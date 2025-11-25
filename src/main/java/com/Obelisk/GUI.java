import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.geometry.Pos;

public class GUI extends Application {

    @Override
    public void start(Stage primaryStage) {

        // colors
        String baseColor = "#f5f8fb";     // background page
        String headerColor = "#003476";   // top header
        String footerColor = "#d5dee2";   // bottom area
        String headerTextColor = "#ffffff"; // white text in header
        String mainTextColor = "#363636";   // classic text

        // header
        HBox header = new HBox();
        header.setStyle("-fx-background-color: " + headerColor + ";");
        header.setPrefHeight(80);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("State Budget Monitoring");
        title.setStyle("-fx-text-fill: " + headerTextColor + "; -fx-font-size: 24px;");
        header.getChildren().add(title);

        // main content (empty)
        VBox mainContent = new VBox();
        mainContent.setStyle("-fx-background-color: " + baseColor + ";");
        mainContent.setAlignment(Pos.CENTER);

        // footer
        HBox footer = new HBox();
        footer.setStyle("-fx-background-color: " + footerColor + ";");
        footer.setPrefHeight(40);
        footer.setAlignment(Pos.CENTER);

        Label footerText = new Label("© 2025 Your Application");
        footerText.setStyle("-fx-text-fill: " + mainTextColor + ";");
        footer.getChildren().add(footerText);

        // ROOT LAYOUT (BorderPane)
        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(mainContent);
        root.setBottom(footer);

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("My Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
