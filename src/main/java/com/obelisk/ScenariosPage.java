package com.obelisk;

import com.obelisk.Scenarios.BudgetEntry;
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
    private Scenarios logic; // Σύνδεση με τη λογική σου
    private TableView<BudgetEntry> table;
    
    // Labels για τα αποτελέσματα
    private Label lblRevenue, lblExpenses, lblBalance, lblStatus;

    public ScenarioPage(GUI mainApp) {
        this.mainApp = mainApp;
        this.logic = new Scenarios(); // Φορτώνει τα δεδομένα αυτόματα
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

        // --- CENTER: TABLE ---
        table = new TableView<>();
        updateTableData(); // Γέμισμα πίνακα
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<BudgetEntry, String> colCode = new TableColumn<>("Code");
        colCode.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().code));

        TableColumn<BudgetEntry, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().type));

        TableColumn<BudgetEntry, String> colMin = new TableColumn<>("Ministry");
        colMin.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().ministry));

        TableColumn<BudgetEntry, String> colDesc = new TableColumn<>("Description");
        colDesc.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().source));

        TableColumn<BudgetEntry, String> colAmount = new TableColumn<>("Amount (€)");
        colAmount.setCellValueFactory(d -> new SimpleStringProperty(String.format("%,d", d.getValue().amount)));
        colAmount.setStyle("-fx-alignment: CENTER-RIGHT;");

        table.getColumns().addAll(colCode, colType, colMin, colDesc, colAmount);
        root.setCenter(table);

        // --- RIGHT SIDEBAR: ACTIONS & RESULTS ---
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(300);
        sidebar.setStyle("-fx-background-color: #e8f0fe; -fx-border-color: #cfd8dc;");

        Label lblActions = new Label("Actions");
        lblActions.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Button btnEdit = new Button("Edit Amount (by Code)");
        btnEdit.setMaxWidth(Double.MAX_VALUE);
        btnEdit.setOnAction(e -> handleEdit());

        Button btnAdd = new Button("Add New Entry");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnAdd.setOnAction(e -> handleAdd());

        Button btnTransfer = new Button("Transfer Between Ministries");
        btnTransfer.setMaxWidth(Double.MAX_VALUE);
        btnTransfer.setOnAction(e -> handleTransfer());

        // Summary Section
        VBox resultsBox = new VBox(10);
        resultsBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 5;");
        Label lblResTitle = new Label("Live Results");
        lblResTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        lblRevenue = new Label();
        lblExpenses = new Label();
        lblBalance = new Label();
        lblStatus = new Label();
        lblStatus.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        updateSummary(); // Αρχικός υπολογισμός

        resultsBox.getChildren().addAll(lblResTitle, new Separator(), lblRevenue, lblExpenses, lblBalance, lblStatus);

        sidebar.getChildren().addAll(lblActions, btnEdit, btnAdd, btnTransfer, new Separator(), resultsBox);
        root.setRight(sidebar);

        // --- FOOTER ---
        HBox footer = new HBox();
        footer.setPadding(new Insets(20));
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            stage.setScene(mainApp.createSecondScene(stage));
            stage.setFullScreen(true);
        });
        footer.getChildren().add(backButton);
        root.setBottom(footer);

        return new Scene(root, 1200, 800);
    }

    // --- BUTTON HANDLERS ---

    private void handleEdit() {
        // Παίρνουμε την επιλεγμένη γραμμή ή ζητάμε κωδικό
        BudgetEntry selected = table.getSelectionModel().getSelectedItem();
        String defaultCode = (selected != null) ? selected.code : "";

        TextInputDialog dialog = new TextInputDialog(defaultCode);
        dialog.setTitle("Edit Entry");
        dialog.setHeaderText("Change Amount");
        dialog.setContentText("Enter Code:");
        
        Optional<String> codeRes = dialog.showAndWait();
        if (codeRes.isPresent()) {
            TextInputDialog amountDialog = new TextInputDialog();
            amountDialog.setContentText("New Amount (€):");
            amountDialog.showAndWait().ifPresent(amountStr -> {
                try {
                    long amount = Long.parseLong(amountStr.trim());
                    boolean success = logic.updateEntryAmount(codeRes.get(), amount);
                    if (success) {
                        refreshAll();
                    } else {
                        showAlert("Error", "Code not found.");
                    }
                } catch (Exception e) { showAlert("Error", "Invalid number."); }
            });
        }
    }

    private void handleAdd() {
        // Απλοϊκός διάλογος για προσθήκη (Μπορείς να φτιάξεις Custom Dialog αν θες)
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Add Entry");
        d.setHeaderText("Format: Type,Ministry,Code,Desc,Amount");
        d.setContentText("e.g.: Έξοδα,Υγείας,NEW1,Νέα ΜΕΘ,50000");
        
        d.showAndWait().ifPresent(input -> {
            String[] parts = input.split(",");
            if (parts.length == 5) {
                try {
                    logic.addNewEntry(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim(), Long.parseLong(parts[4].trim()));
                    refreshAll();
                } catch (Exception e) { showAlert("Error", "Invalid Format"); }
            } else { showAlert("Error", "Use format: Type,Ministry,Code,Desc,Amount"); }
        });
    }

    private void handleTransfer() {
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Transfer");
        d.setHeaderText("Format: From,To,Desc,Amount");
        d.setContentText("e.g.: Υγείας,Άμυνας,Εκτάκτως,10000");

        d.showAndWait().ifPresent(input -> {
            String[] parts = input.split(",");
            if (parts.length == 4) {
                try {
                    logic.transferExpenses(parts[0].trim(), parts[1].trim(), parts[2].trim(), Long.parseLong(parts[3].trim()));
                    refreshAll();
                } catch (Exception e) { showAlert("Error", "Invalid Format"); }
            } else { showAlert("Error", "Use format: From,To,Desc,Amount"); }
        });
    }

    private void refreshAll() {
        updateTableData();
        updateSummary();
    }

    private void updateTableData() {
        ObservableList<BudgetEntry> data = FXCollections.observableArrayList(logic.modifiedEntries);
        table.setItems(data);
        table.refresh();
    }

    private void updateSummary() {
        long rev = logic.getTotalRevenue();
        long exp = logic.getTotalExpenses();
        long bal = logic.getBalance();

        lblRevenue.setText(String.format("Revenue: %,d €", rev));
        lblExpenses.setText(String.format("Expenses: %,d €", exp));
        lblBalance.setText(String.format("Balance: %,d €", bal));
        
        if (bal > 0) {
            lblStatus.setText("SURPLUS");
            lblStatus.setTextFill(Color.GREEN);
        } else if (bal < 0) {
            lblStatus.setText("DEFICIT");
            lblStatus.setTextFill(Color.RED);
        } else {
            lblStatus.setText("BALANCED");
            lblStatus.setTextFill(Color.BLACK);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
