<<<<<<< HEAD
package com.obelisk;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class BudgetAnalyzerPage {

    public Scene createScene(Stage primaryStage) {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f8fb;");

        /* ================= HEADER ================= */
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(30, 60, 30, 60));
        header.setStyle("-fx-background-color: #003476;");

        Label title = new Label("Budget Analysis");
        title.setFont(Font.font("Arial", 48));
        title.setTextFill(Color.web("#f5f8fb"));

        header.getChildren().add(title);
        root.setTop(header);

        /* ================= CENTER CONTENT ================= */
        VBox content = new VBox(30);
        content.setPadding(new Insets(60));
        content.setAlignment(Pos.TOP_LEFT);

        Label description = new Label(
                "Here you will be able to analyze budget data,\n" +
                "view reports and process financial information."
        );
        description.setFont(Font.font("Arial", 28));
        description.setTextFill(Color.web("#363636"));

        Button analyzeButton = new Button("Run Budget Analysis");
        analyzeButton.setFont(Font.font("Arial", 24));
        analyzeButton.setStyle(
                "-fx-background-color: #1a8080; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 10 20 10 20;"
        );

        analyzeButton.setOnAction(e -> {
            // Εδώ ΑΡΓΟΤΕΡΑ μπορείς να καλέσεις
            // τον παλιό BudgetAnalyzer (λογική / υπολογισμούς)
            System.out.println("Budget analysis started...");
        });

        content.getChildren().addAll(description, analyzeButton);
        root.setCenter(content);

        /* ================= FOOTER ================= */
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(20, 60, 20, 60));
        footer.setStyle("-fx-background-color: #d5dee2;");

        Button backButton = new Button("← Back to Menu");
        backButton.setFont(Font.font("Arial", 20));

        backButton.setOnAction(e -> {
            GUI gui = new GUI();
            gui.start(primaryStage);
        });

        footer.getChildren().add(backButton);
        root.setBottom(footer);

        return new Scene(root, 1200, 800);
    }
}
=======

>>>>>>> db4275d2c6009b4a5651069673b8557eaf9923be
