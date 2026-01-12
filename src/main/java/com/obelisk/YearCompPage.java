package com.obelisk;

import com.obelisk.YearComp.YearData;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class YearCompPage {

    private GUI mainApp;
    private ComboBox<Integer> comboYear1;
    private ComboBox<Integer> comboYear2;
    private TableView<ComparisonRow> table;
    private BarChart<String, Number> barChart;
    private Label lblError;

    public YearCompPage(GUI mainApp) {
        this.mainApp = mainApp;
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

        Label pageTitle = new Label("Multiple Year Operations");
        pageTitle.setFont(Font.font("Arial", 40));
        pageTitle.setTextFill(Color.web("#f5f8fb"));

        header.getChildren().addAll(logoView, pageTitle);
        root.setTop(header);

        // --- CONTROLS ---
        HBox controls = new HBox(20);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(20));
        controls.setStyle("-fx-background-color: #e8f0fe; -fx-border-color: #cfd8dc;");

        // Διαθέσιμα έτη (βεβαιώσου ότι έχεις τα αντίστοιχα CSV)
        ObservableList<Integer> years = FXCollections.observableArrayList(2020, 2021, 2022, 2023, 2024, 2025);
        
        comboYear1 = new ComboBox<>(years);
        comboYear1.setPromptText("Year 1");
        comboYear1.setValue(2024);

        comboYear2 = new ComboBox<>(years);
        comboYear2.setPromptText("Year 2");
        comboYear2.setValue(2025);

        Button btnCompare = new Button("Compare");
        btnCompare.setStyle("-fx-background-color: #1a8080; -fx-text-fill: white; -fx-font-size: 16px;");
        btnCompare.setOnAction(e -> performComparison());

        lblError = new Label();
        lblError.setTextFill(Color.RED);

        controls.getChildren().addAll(new Label("Select Years:"), comboYear1, new Label("vs"), comboYear2, btnCompare, lblError);
        
        // --- CENTER (Chart + Table) ---
        VBox centerContent = new VBox(30);
        centerContent.setPadding(new Insets(30));
        centerContent.setAlignment(Pos.TOP_CENTER);
        
        // Γράφημα
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount (€)");
        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Revenue & Expenses Comparison");
        barChart.setAnimated(false);

        // Πίνακας
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(200);

        TableColumn<ComparisonRow, String> colMetric = new TableColumn<>("Metric");
        colMetric.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().metric));

        TableColumn<ComparisonRow, String> colY1 = new TableColumn<>("Year 1");
        colY1.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().val1));
        colY1.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<ComparisonRow, String> colY2 = new TableColumn<>("Year 2");
        colY2.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().val2));
        colY2.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<ComparisonRow, String> colDiff = new TableColumn<>("Change (%)");
        colDiff.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().diff));
        colDiff.setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold;");

        table.getColumns().addAll(colMetric, colY1, colY2, colDiff);

        centerContent.getChildren().addAll(controls, barChart, table);
        
        // ScrollPane για να χωράει σε μικρές οθόνες
        ScrollPane scroll = new ScrollPane(centerContent);
        scroll.setFitToWidth(true);
        root.setCenter(scroll);

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

    private void performComparison() {
        Integer y1 = comboYear1.getValue();
        Integer y2 = comboYear2.getValue();

        if (y1 == null || y2 == null) {
            lblError.setText("Please select two years.");
            return;
        }

        try {
            YearData data1 = YearComp.loadYearData(y1);
            YearData data2 = YearComp.loadYearData(y2);
            lblError.setText("");

            // Update Table
            ObservableList<ComparisonRow> rows = FXCollections.observableArrayList();
            rows.add(new ComparisonRow("Total Revenue", data1.totalRevenue, data2.totalRevenue));
            rows.add(new ComparisonRow("Total Expenses", data1.totalExpenses, data2.totalExpenses));
            rows.add(new ComparisonRow("Balance", data1.balance, data2.balance));
            table.setItems(rows);
            
            table.getColumns().get(1).setText(String.valueOf(y1));
            table.getColumns().get(2).setText(String.valueOf(y2));

            // Update Chart
            barChart.getData().clear();
            
            XYChart.Series<String, Number> series1 = new XYChart.Series<>();
            series1.setName(String.valueOf(y1));
            series1.getData().add(new XYChart.Data<>("Revenue", data1.totalRevenue));
            series1.getData().add(new XYChart.Data<>("Expenses", data1.totalExpenses));
            
            XYChart.Series<String, Number> series2 = new XYChart.Series<>();
            series2.setName(String.valueOf(y2));
            series2.getData().add(new XYChart.Data<>("Revenue", data2.totalRevenue));
            series2.getData().add(new XYChart.Data<>("Expenses", data2.totalExpenses));

            barChart.getData().addAll(series1, series2);

        } catch (Exception e) {
            lblError.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static class ComparisonRow {
        String metric;
        String val1;
        String val2;
        String diff;

        public ComparisonRow(String metric, long v1, long v2) {
            this.metric = metric;
            this.val1 = String.format("%,d €", v1);
            this.val2 = String.format("%,d €", v2);
            
            if (v1 != 0) {
                double delta = ((double) (v2 - v1) / Math.abs(v1)) * 100.0;
                this.diff = String.format("%+.2f%%", delta);
            } else {
                this.diff = "N/A";
            }
        }
    }
}
