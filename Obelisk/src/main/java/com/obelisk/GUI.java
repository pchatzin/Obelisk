import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;

public class GUI {

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Obelisk - State Budget Monitoring System");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setStyle("-fx-background-color: #f5f8fb;");

        grid.add(createSection("Budget Analysis", "budget.png"), 0, 0);
        grid.add(createSection("Scenario Execution", "scenario.png"), 1, 0);
        grid.add(createSection("Multi-Year Operations", "operations.png"), 0, 1);
        grid.add(createSection("Country Comparison", "comparison.png"), 1, 1);

        Scene scene = new Scene(grid, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createSection(String title, String imageFile) {
        GridPane section = new GridPane();
        section.setPrefSize(350, 200);
        section.setAlignment(Pos.CENTER);
        section.setVgap(10);
        section.setStyle("-fx-border-color: #003476 #d5dee2 #d5dee2 #003476; -fx-border-width: 2;");

        Label label = new Label(title);
        label.setFont(Font.font("Arial", 18));
        label.setTextFill(Color.web("#363636"));

        Button button = createImageButton("/buttons/" + imageFile, "Open " + title);

        section.add(label, 0, 0);
        section.add(button, 0, 1);

        return section;
    }

    private Button createImageButton(String imagePath, String tooltipText) {
        Image img = new Image(getClass().getResourceAsStream(imagePath));
        ImageView view = new ImageView(img);
        view.setFitWidth(64);
        view.setFitHeight(64);

        Button button = new Button("", view);
        button.setStyle("-fx-background-color: #1a8080; -fx-cursor: hand;");
        button.setTooltip(new Tooltip(tooltipText));
        return button;
    }
}
