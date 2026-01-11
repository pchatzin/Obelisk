package com.obelisk;

import com.obelisk.Scenarios.BudgetEntry;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Optional;

public class ScenariosPage {

    private GUI mainApp;
    private TableView<BudgetEntry> table;
    private ObservableList<BudgetEntry> data;
    
    // Labels για τα σύνολα
    private Label lblTotalRev;
    private Label lblTotalExp;
    private Label lblBalance;
    private Label lblStatus;

    public ScenarioPage(GUI mainApp) {
        this.mainApp = mainApp;
        // Φόρτωση δεδομένων από τη νέα δομή του Scenarios
        this.data = FXCollections.observableArrayList(Scenarios.loadBaseScenario());
    }

    public Scene createScene(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f8fb;");

        // --- HEADER ---
        HBox header = new HBox(30);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20, 80, 20, 80));
        header.setStyle("-fx-background-color: #003476;");

        ImageView logoView = new ImageView();
        try {
            logoView.setImage(new Image(getClass().getResourceAsStream("/GUI/logo.png")));
            logoView.setFitHeight(80);
            logoView.setPreserveRatio(true);
        } catch (Exception e) {}

        Label pageTitle = new Label("Scenario Execution");
        pageTitle.setFont(Font.font("Arial", 36));
        pageTitle.setTextFill(Color.WHITE);

        header.getChildren().addAll(logoView, pageTitle);
        root.setTop(header);

        // --- CENTER (TABLE) ---
        table = new TableView<>();
        table.setItems(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<BudgetEntry, String> colCode = new TableColumn<>("Code");
        colCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().code));

        TableColumn<BudgetEntry, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().type));

        TableColumn<BudgetEntry, String> colMinistry = new TableColumn<>("Ministry");
        colMinistry.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().ministry));

        TableColumn<BudgetEntry, String> colDesc = new TableColumn<>("Description");
        colDesc.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().source));

        TableColumn<BudgetEntry, String> colAmount = new TableColumn<>("Amount (€)");
        colAmount.setCellValueFactory(c -> new SimpleStringProperty(String.format("%,d", c.getValue().amount)));
        colAmount.setStyle("-fx-alignment: CENTER-RIGHT;");

        table.getColumns().addAll(colCode, colType, colMinistry, colDesc, colAmount);
        
        // --- RIGHT SIDEBAR (ACTIONS) ---
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: #e8f0fe; -fx-border-color: #cfd8dc;");

        Label lblActions = new Label("Actions");
        lblActions.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Button btnEdit = new Button("Edit Selected Amount");
        btnEdit.setMaxWidth(Double.MAX_VALUE);
        btnEdit.setOnAction(e -> editSelectedEntry());

        Button btnAdd = new Button("Add New Entry");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnAdd.setOnAction(e -> addNewEntryDialog());

        // Σύνολα (Live Update)
        VBox summaryBox = new VBox(10);
        summaryBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 5;");
        
        lblTotalRev = new Label();
        lblTotalExp = new Label();
        lblBalance = new Label();
        lblStatus = new Label();
        lblBalance.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        updateSummary(); // Υπολογισμός αρχικών τιμών

        summaryBox.getChildren().addAll(new Label("Summary:"), lblTotalRev, lblTotalExp, new Separator(), lblBalance, lblStatus);

        sidebar.getChildren().addAll(lblActions, btnEdit, btnAdd, new Separator(), summaryBox);

        root.setCenter(table);
        root.setRight(sidebar);

        // --- FOOTER ---
        HBox footer = new HBox();
        footer.setPadding(new Insets(20, 80, 20, 80));
        footer.setStyle("-fx-background-color: #d5dee2;");
        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(e -> {
            stage.setScene(mainApp.createSecondScene(stage));
            stage.setFullScreen(true);
        });
        footer.getChildren().add(backButton);
        root.setBottom(footer);

        return new Scene(root, 1200, 800);
    }

    // --- LOGIC METHODS ---

    private void updateSummary() {
        long revenue = data.stream().filter(e -> "Έσοδα".equals(e.type)).mapToLong(e -> e.amount).sum();
        long expenses = data.stream().filter(e -> "Έξοδα".equals(e.type)).mapToLong(e -> e.amount).sum();
        long balance = revenue - expenses;

        lblTotalRev.setText(String.format("Revenue: %,d €", revenue));
        lblTotalExp.setText(String.format("Expenses: %,d €", expenses));
        lblBalance.setText(String.format("Balance: %,d €", balance));
        
        if (balance > 0) {
            lblStatus.setText("SURPLUS");
            lblStatus.setTextFill(Color.GREEN);
        } else if (balance < 0) {
            lblStatus.setText("DEFICIT");
            lblStatus.setTextFill(Color.RED);
        } else {
            lblStatus.setText("BALANCED");
            lblStatus.setTextFill(Color.BLACK);
        }
    }

    private void editSelectedEntry() {
        BudgetEntry selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a row to edit.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(String.valueOf(selected.amount));
        dialog.setTitle("Edit Amount");
        dialog.setHeaderText("Editing: " + selected.source);
        dialog.setContentText("New Amount (€):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(amountStr -> {
            try {
                long newAmount = Long.parseLong(amountStr.trim());
                selected.setAmount(newAmount);
                table.refresh(); // Ανανέωση πίνακα
                updateSummary(); // Ανανέωση συνόλων
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid number.");
            }
        });
    }

    private void addNewEntryDialog() {
        // Απλοποιημένος διάλογος για προσθήκη (κανονικά θα ήθελε custom dialog)
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Entry");
        dialog.setHeaderText("Format: Type,Ministry,Description,Amount");
        dialog.setContentText("Example: Έξοδα,Υγείας,Νέα ΜΕΘ,500000");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input -> {
            String[] parts = input.split(",");
            if (parts.length == 4) {
                try {
                    BudgetEntry entry = new BudgetEntry();
                    entry.type = parts[0].trim();
                    entry.ministry = parts[1].trim();
                    entry.source = parts[2].trim();
                    entry.amount = Long.parseLong(parts[3].trim());
                    entry.code = "NEW";
                    
                    data.add(entry);
                    updateSummary();
                    table.scrollTo(entry);
                } catch (Exception e) {
                    showAlert("Error", "Invalid format or number.");
                }
            } else {
                showAlert("Error", "Please use the correct format separated by commas.");
            }
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
