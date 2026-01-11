package com.obelisk;

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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

import com.obelisk.CountryComp.CountryData;

public class CountryCompPage {

    private GUI mainApp;
    private List<CountryData> allCountries;
    private CountryData greeceData;
    
    private Label lblSelectedCountry;
    private TableView<ComparisonRow> tableAbsolute;
    private TableView<ComparisonRow> tablePercent;
    private Text txtExplanation;
    private Label lblCurrencyWarning;

    public CountryCompPage(GUI mainApp) {
        this.mainApp = mainApp;
        this.allCountries = CountryComp.loadCountries();
        
        // Η Ελλάδα είναι πάντα στο index 4
        if (allCountries.size() > 4) {
            this.greeceData = allCountries.get(4);
        }
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
            logoView.setFitHeight(100);
            logoView.setPreserveRatio(true);
        } catch (Exception e) { /* ignore */ }

        Label pageTitle = new Label("International Comparison");
        pageTitle.setFont(Font.font("Arial", 40));
        pageTitle.setTextFill(Color.web("#f5f8fb"));

        header.getChildren().addAll(logoView, pageTitle);
        root.setTop(header);

        // --- LEFT SIDEBAR ---
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(30));
        sidebar.setStyle("-fx-background-color: #dbe4ea;");
        sidebar.setPrefWidth(280);

        Label lblInstruct = new Label("Select Country:");
        lblInstruct.setFont(Font.font("Arial", 18));
        
        ListView<CountryData> listCountries = new ListView<>();
        ObservableList<CountryData> others = FXCollections.observableArrayList();
        if (allCountries != null) {
            for (CountryData c : allCountries) {
                if (!c.country.equals("Ελλάδα")) {
                    others.add(c);
                }
            }
        }
        listCountries.setItems(others);
        listCountries.setPrefHeight(200);
        
        Button btnCompare = new Button("Compare");
        btnCompare.setMaxWidth(Double.MAX_VALUE);
        btnCompare.setStyle("-fx-background-color: #1a8080; -fx-text-fill: white; -fx-font-size: 16px;");
        
        lblCurrencyWarning = new Label("");
        lblCurrencyWarning.setWrapText(true);
        lblCurrencyWarning.setTextFill(Color.RED);

        sidebar.getChildren().addAll(lblInstruct, listCountries, btnCompare, lblCurrencyWarning);
        root.setLeft(sidebar);

        // --- CENTER ---
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f5f8fb; -fx-border-color: transparent;");

        VBox centerContent = new VBox(30);
        centerContent.setPadding(new Insets(30));
        centerContent.setAlignment(Pos.TOP_LEFT);

        lblSelectedCountry = new Label("Select a country to compare with Greece");
        lblSelectedCountry.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblSelectedCountry.setTextFill(Color.web("#003476"));

        Label lblTab1 = new Label("Absolute Values (Million €)");
        lblTab1.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        tableAbsolute = createComparisonTable();
        
        Label lblTab2 = new Label("Key Metrics (% GDP or % Revenue)");
        lblTab2.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        tablePercent = createComparisonTable();

        VBox analysisBox = new VBox(10);
        analysisBox.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        Label lblAnalysisTitle = new Label("AI Analysis / Justification");
        lblAnalysisTitle.setTextFill(Color.web("#1a8080"));
        lblAnalysisTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        txtExplanation = new Text("Analysis will appear here...");
        txtExplanation.setFont(Font.font("Arial", 16));
        txtExplanation.setWrappingWidth(700);
        
        analysisBox.getChildren().addAll(lblAnalysisTitle, txtExplanation);

        centerContent.getChildren().addAll(lblSelectedCountry, lblTab1, tableAbsolute, lblTab2, tablePercent, analysisBox);
        scrollPane.setContent(centerContent);
        root.setCenter(scrollPane);

        // --- EVENTS ---
        btnCompare.setOnAction(e -> {
            CountryData selected = listCountries.getSelectionModel().getSelectedItem();
            if (selected != null && greeceData != null) {
                updateComparison(selected);
            }
        });

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

    private void updateComparison(CountryData other) {
        lblSelectedCountry.setText("Greece vs " + other.country);
        
        if ("ΗΠΑ".equals(other.country)) {
            lblCurrencyWarning.setText("Note: USA data is in Dollars ($). Comparisons based on % of GDP.");
        } else {
            lblCurrencyWarning.setText("");
        }

        ObservableList<ComparisonRow> absData = FXCollections.observableArrayList();
        absData.add(new ComparisonRow("Total Revenue", greeceData.totalRevenue, other.totalRevenue));
        absData.add(new ComparisonRow("Total Expenditure", greeceData.totalExpenditure, other.totalExpenditure));
        absData.add(new ComparisonRow("Balance", greeceData.balance, other.balance));
        absData.add(new ComparisonRow("GDP", greeceData.gdp, other.gdp));
        absData.add(new ComparisonRow("Public Debt", greeceData.publicDebt, other.publicDebt));
        absData.add(new ComparisonRow("Health Exp.", greeceData.healthExp, other.healthExp));
        absData.add(new ComparisonRow("Defense Exp.", greeceData.defenseExp, other.defenseExp));
        absData.add(new ComparisonRow("Education Exp.", greeceData.educationExp, other.educationExp));
        tableAbsolute.setItems(absData);
        tableAbsolute.setPrefHeight(280);

        ObservableList<ComparisonRow> pctData = FXCollections.observableArrayList();
        pctData.add(new ComparisonRow("Revenue % GDP", greeceData.revenuePctGDP, other.revenuePctGDP));
        pctData.add(new ComparisonRow("Expenditure % GDP", greeceData.expenditurePctGDP, other.expenditurePctGDP));
        pctData.add(new ComparisonRow("Balance % GDP", greeceData.balancePctGDP, other.balancePctGDP));
        pctData.add(new ComparisonRow("Debt % Revenue", greeceData.debtPctRevenue, other.debtPctRevenue));
        pctData.add(new ComparisonRow("Direct Taxes Share", greeceData.directTaxesShare, other.directTaxesShare));
        pctData.add(new ComparisonRow("Health Share", greeceData.healthShare, other.healthShare));
        pctData.add(new ComparisonRow("Defense Share", greeceData.defenseShare, other.defenseShare));
        tablePercent.setItems(pctData);
        tablePercent.setPrefHeight(250);

        txtExplanation.setText(CountryComp.explanationForCountry(other.country));
    }

    private TableView<ComparisonRow> createComparisonTable() {
        TableView<ComparisonRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ComparisonRow, String> colMetric = new TableColumn<>("Indicator");
        colMetric.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().metric));

        TableColumn<ComparisonRow, String> colGreece = new TableColumn<>("Greece");
        colGreece.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().greeceVal));
        colGreece.setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold;");

        TableColumn<ComparisonRow, String> colOther = new TableColumn<>("Selected Country");
        colOther.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().otherVal));
        colOther.setStyle("-fx-alignment: CENTER-RIGHT;");

        table.getColumns().addAll(colMetric, colGreece, colOther);
        return table;
    }

    public static class ComparisonRow {
        String metric;
        String greeceVal;
        String otherVal;

        public ComparisonRow(String metric, double g, double o) {
            this.metric = metric;
            this.greeceVal = String.format("%,.1f", g);
            this.otherVal = String.format("%,.1f", o);
        }
    }
}
