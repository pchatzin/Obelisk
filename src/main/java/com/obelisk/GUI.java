package com.obelisk;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GUI {

    public void start(Stage primaryStage) {
        Scene blankScene = createBlankScene(primaryStage);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f8fb;");

        // Header
        HBox header = new HBox(30);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(40, 80, 40, 80)); 
        header.setStyle("-fx-background-color: #003476;");

        ImageView logoView;
        try {
            Image logo = new Image(getClass().getResourceAsStream("/GUI/logo.png"));
            logoView = new ImageView(logo);
            logoView.setFitHeight(220); 
            logoView.setPreserveRatio(true);
        } catch (Exception e) {
            logoView = new ImageView();
        }

        VBox titleBox = new VBox(10);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Label obeliskLabel = new Label("Obelisk");
        obeliskLabel.setFont(Font.font("Arial", 72));
        obeliskLabel.setTextFill(Color.web("#f5f8fb"));

        Label tagline = new Label("Navigate complexity with precision and purpose");
        tagline.setFont(Font.font("Arial", 36));
        tagline.setTextFill(Color.web("#f5f8fb"));

        titleBox.getChildren().addAll(obeliskLabel, tagline);
        header.getChildren().addAll(logoView, titleBox);
        root.setTop(header);

        // Main content
        VBox centerContent = new VBox(40);
        centerContent.setAlignment(Pos.TOP_LEFT);
        centerContent.setPadding(new Insets(60, 80, 60, 80)); 

        Label title = new Label("State Budget Monitoring & Processing System");
        title.setFont(Font.font("Arial", 48));
        title.setTextFill(Color.web("#003476"));

        Label description = new Label(
            "The State Budget Monitoring & Processing System enables comprehensive access to and analysis\n" +
            "of the government’s financial data, ensuring transparency and data-driven fiscal management."
        );
        description.setFont(Font.font("Arial", 32));
        description.setTextFill(Color.web("#363636"));
        description.setWrapText(true);
        description.setMaxWidth(2000);

        Button accessButton = new Button("Access the application");
        accessButton.setFont(Font.font("Arial", 28));
        accessButton.setStyle("-fx-background-color: #1a8080; -fx-text-fill: #ffffff;");
        accessButton.setOnAction(e -> {
            Scene secondScene = createSecondScene(primaryStage);
            primaryStage.setScene(secondScene);
            primaryStage.setFullScreen(true); 
        });

        centerContent.getChildren().addAll(title, description, accessButton);
        root.setCenter(centerContent);

        // Footer
        VBox footerBox = new VBox(10);
        footerBox.setAlignment(Pos.BOTTOM_LEFT);
        footerBox.setPadding(new Insets(20, 80, 20, 80)); 
        footerBox.setStyle("-fx-background-color: #d5dee2;");

        Label copyright = new Label("© 2025 Obelisk - State Budget Monitoring & Processing System");
        copyright.setFont(Font.font("Arial", 24));
        copyright.setTextFill(Color.web("#363636"));

        Label website = new Label("www.obelisk.gr");
        website.setFont(Font.font("Arial", 24));
        website.setTextFill(Color.web("#363636"));

        footerBox.getChildren().addAll(copyright, website);
        root.setBottom(footerBox);

        Scene landingScene = new Scene(root, 1200, 800);
        primaryStage.setScene(landingScene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    private Scene createBlankScene(Stage primaryStage) {
        BorderPane blank = new BorderPane();
        blank.setStyle("-fx-background-color: #ffffff;");
        return new Scene(blank, 1200, 800);
    }

    private Scene createSecondScene(Stage primaryStage) {

    BorderPane root = new BorderPane();
    root.setStyle("-fx-background-color: #f5f8fb;");

    // Header
    HBox header = new HBox(30);
    header.setAlignment(Pos.CENTER_LEFT);
    header.setPadding(new Insets(40, 80, 40, 80));
    header.setStyle("-fx-background-color: #003476;");

    ImageView logoView;
    try {
        Image logo = new Image(getClass().getResourceAsStream("/GUI/logo.png"));
        logoView = new ImageView(logo);
        logoView.setFitHeight(220);
        logoView.setPreserveRatio(true);
    } catch (Exception e) {
        logoView = new ImageView();
    }

    VBox titleBox = new VBox(10);
    titleBox.setAlignment(Pos.CENTER_LEFT);

    Label obeliskLabel = new Label("Obelisk");
    obeliskLabel.setFont(Font.font("Arial", 72));
    obeliskLabel.setTextFill(Color.web("#f5f8fb"));

    Label tagline = new Label("Navigate complexity with precision and purpose");
    tagline.setFont(Font.font("Arial", 36));
    tagline.setTextFill(Color.web("#f5f8fb"));

    titleBox.getChildren().addAll(obeliskLabel, tagline);
    header.getChildren().addAll(logoView, titleBox);
    root.setTop(header);

    // GRID ΜΕ 2 ΓΡΑΜΜΕΣ – 3 ΣΤΗΛΕΣ
    VBox menuGrid = new VBox(40);
    menuGrid.setAlignment(Pos.CENTER);
    menuGrid.setPadding(new Insets(60, 80, 60, 80));

    HBox row1 = new HBox(40);
    row1.setAlignment(Pos.CENTER_LEFT);

    row1.getChildren().addAll(
        createMenuButton(
            "Budget Analysis",
             "/GUI/button1.png",
              e -> {
                BudgetAnalyzerPage page = new BudgetAnalyzerPage();
                primaryStage.setScene(page.createScene(primaryStage));
            }
        ),
        createMenuButton(
             "Scenario Execution",
              "/GUI/button4.png",
              e -> primaryStage.setScene(createScenarioPage(primaryStage))
            ),
        createMenuButton(
             "Multiple Year Operations",
             "/GUI/button3.png",
             e -> primaryStage.setScene(createMultiplePage(primaryStage))
            )
        );


    HBox row2 = new HBox(40);
    row2.setAlignment(Pos.CENTER_LEFT);

    row2.getChildren().add(
        createMenuButton("Country Comparison", "/GUI/button2.png", 
        e -> primaryStage.setScene(createCountryPage(primaryStage)))
    );

    menuGrid.getChildren().addAll(row1, row2);
    root.setCenter(menuGrid);



    // Footer
    VBox footerBox = new VBox(10);
    footerBox.setAlignment(Pos.BOTTOM_LEFT);
    footerBox.setPadding(new Insets(20, 80, 20, 80));
    footerBox.setStyle("-fx-background-color: #d5dee2;");

    Label copyright = new Label(
        "© 2025 Obelisk - State Budget Monitoring & Processing System"
    );
    copyright.setFont(Font.font("Arial", 24));
    copyright.setTextFill(Color.web("#363636"));

    Label website = new Label("www.obelisk.gr");
    website.setFont(Font.font("Arial", 24));
    website.setTextFill(Color.web("#363636"));

    footerBox.getChildren().addAll(copyright, website);
    root.setBottom(footerBox);

    return new Scene(root, 1200, 800);
   }

   private Scene createBudgetPage(Stage stage) {
    BorderPane root = new BorderPane();
    root.setStyle("-fx-background-color: white;");
    root.setCenter(new Label("Budget Analysis Page"));
    return new Scene(root, 1200, 800);
   }

   private Scene createScenarioPage(Stage stage) {
    BorderPane root = new BorderPane();
    root.setStyle("-fx-background-color: white;");
    root.setCenter(new Label("Scenario Execution Page"));
    return new Scene(root, 1200, 800);
   }

   private Scene createMultiplePage(Stage stage) {
    BorderPane root = new BorderPane();
    root.setStyle("-fx-background-color: white;");
    root.setCenter(new Label("Multiple Year Operations Page"));
    return new Scene(root, 1200, 800);
   }

   private Scene createCountryPage(Stage stage) {
    BorderPane root = new BorderPane();
    root.setStyle("-fx-background-color: white;");
    root.setCenter(new Label("Country Comparison Page"));
    return new Scene(root, 1200, 800);
   }

   private Button createMenuButton(String text, String iconPath, javafx.event.EventHandler<javafx.event.ActionEvent> action) {

    ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
    icon.setFitHeight(100);
    icon.setPreserveRatio(true);

    Button b = new Button(text, icon);
    b.setFont(Font.font("Arial", 36));
    b.setPrefWidth(500);
    b.setAlignment(Pos.CENTER_LEFT);
    b.setPadding(new Insets(10, 20, 10, 20));

    b.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #1a8080; -fx-border-color: #1a8080; -fx-border-width: 2;");

    b.setOnAction(action);

    return b;
   }
 }
