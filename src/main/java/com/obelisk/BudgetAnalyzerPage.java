package com.obelisk;

import com.obelisk.BudgetAnalyzer.Entry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class BudgetAnalyzerPage {

    private GUI mainApp;
    private ObservableList<Entry> allData; 
    private TableView<Entry> table; 

    public BudgetAnalyzerPage(GUI mainApp) {
        this.mainApp = mainApp;
    }

    public Scene createScene(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f8fb;");

        HBox header = new HBox(30);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20, 80, 20, 80));
        header.setStyle("-fx-background-color: #003476;");

        ImageView logoView = new ImageView();
        try {
            logoView.setImage(new Image(getClass().getResourceAsStream("/GUI/logo.png")));
            logoView.setFitHeight(100);
            logoView.setPreserveRatio(true);
        } catch (Exception e) { }

        Label pageTitle = new Label("Budget Analysis Dashboard");
        pageTitle.setFont(Font.font("Arial", 40));
        pageTitle.setTextFill(Color.web("#f5f8fb"));

        header.getChildren().addAll(logoView, pageTitle);
        root.setTop(header);

        List<Entry> rawData;
        long totalRev = 0;
        long totalExp = 0;
        
        try {
            rawData = BudgetAnalyzer.loadEntries("/budget/budget-2025.csv");
            
            for (Entry e : rawData) {
                if ("Έσοδα".equals(e.getType())) totalRev += e.getAmount();
                if ("Έξοδα".equals(e.getType())) totalExp += e.getAmount();
            }
        } catch (Exception e) {
            rawData = List.of();
            System.err.println("Error loading CSV: " + e.getMessage());
        }
        
        allData = FXCollections.observableArrayList(rawData);

        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(30));
        sidebar.setStyle("-fx-background-color: #dbe4ea;");
        sidebar.setPrefWidth(250);

        Label filterLabel = new Label("Filters");
        filterLabel.setFont(Font.font("Arial", 22));

        Button btnShowAll = createFilterButton("Show All");
        Button btnRevenue = createFilterButton("Only Revenue");
        Button btnExpenses = createFilterButton("Only Expenses");

        Label ministryLabel = new Label("Filter by Ministry:");
        ministryLabel.setPadding(new Insets(20, 0, 5, 0));
        
        ComboBox<String> ministryCombo = new ComboBox<>();
        ministryCombo.setPrefWidth(200);
        
        ministryCombo.setItems(FXCollections.observableArrayList(
            rawData.stream()
                   .map(Entry::getMinistry)
                   .filter(m -> m != null && !m.equals("-") && !m.trim().isEmpty())
                   .distinct()
                   .sorted()
                   .collect(Collectors.toList())
        ));
        ministryCombo.setPromptText("Select Ministry");

        btnShowAll.setOnAction(e -> table.setItems(allData));
        
        btnRevenue.setOnAction(e -> {
            ObservableList<Entry> filtered = allData.filtered(en -> "Έσοδα".equals(en.getType()));
            table.setItems(filtered);
        });
        
        btnExpenses.setOnAction(e -> {
            ObservableList<Entry> filtered = allData.filtered(en -> "Έξοδα".equals(en.getType()));
            table.setItems(filtered);
        });

        ministryCombo.setOnAction(e -> {
            String selected = ministryCombo.getValue();
            if (selected != null) {
                ObservableList<Entry> filtered = allData.filtered(en -> selected.equals(en.getMinistry()));
                table.setItems(filtered);
            }
        });

        sidebar.getChildren().addAll(filterLabel, btnShowAll, btnRevenue, btnExpenses, ministryLabel, ministryCombo);
        root.setLeft(sidebar);

        VBox centerBox = new VBox(20);
        centerBox.setPadding(new Insets(30));

        HBox summaryBox = new HBox(40);
        summaryBox.setAlignment(Pos.CENTER);
        
        summaryBox.getChildren().add(createSummaryCard("Total Revenue", totalRev, "#1a8080"));
        summaryBox.getChildren().add(createSummaryCard("Total Expenses", totalExp, "#c0392b"));
        
        long balance = totalRev - totalExp;
        String status = balance >= 0 ? "SURPLUS" : "DEFICIT";
        String color = balance >= 0 ? "#27ae60" : "#e74c3c";
        summaryBox.getChildren().add(createSummaryCard("Budget " + status, balance, color));

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Entry, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<Entry, String> ministryCol = new TableColumn<>("Ministry");
        ministryCol.setCellValueFactory(new PropertyValueFactory<>("ministry"));

        TableColumn<Entry, String> sourceCol = new TableColumn<>("Source / Description");
        sourceCol.setCellValueFactory(new PropertyValueFactory<>("source"));

        TableColumn<Entry, Long> amountCol = new TableColumn<>("Amount (€)");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setStyle("-fx-alignment: CENTER-RIGHT;");

        table.getColumns().addAll(typeCol, ministryCol, sourceCol, amountCol);
        table.setItems(allData);

        centerBox.getChildren().addAll(summaryBox, table);
        root.setCenter(centerBox);

        // --- FOOTER ---
        HBox footer = new HBox();
        footer.setPadding(new Insets(20, 80, 20, 80));
        footer.setStyle("-fx-background-color: #d5dee2;");
        
        Button backButton = new Button("<-Back to Main Menu");
        backButton.setStyle("-fx-font-size: 16px; -fx-background-color: #363636; -fx-text-fill: white;");
        backButton.setOnAction(e -> {
            stage.setScene(mainApp.createSecondScene(stage));
            stage.setFullScreen(true);
        });

        footer.getChildren().add(backButton);
        root.setBottom(footer);

        return new Scene(root, 1200, 800);
    }

    private Button createFilterButton(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("-fx-font-size: 16px; -fx-background-color: white; -fx-border-color: #aaaaaa;");
        return b;
    }

    private VBox createSummaryCard(String title, long amount, String colorHex) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0); -fx-background-radius: 5;");
        card.setPrefWidth(300);
        card.setAlignment(Pos.CENTER);

        Label titleLbl = new Label(title);
        titleLbl.setFont(Font.font("Arial", 18));
        titleLbl.setTextFill(Color.web("#7f8c8d"));

        Label amountLbl = new Label(String.format("%,d €", amount));
        amountLbl.setFont(Font.font("Arial", 24)); 
        amountLbl.setStyle("-fx-font-weight: bold;");
        amountLbl.setTextFill(Color.web(colorHex));

        card.getChildren().addAll(titleLbl, amountLbl);
        return card;
    }
}
