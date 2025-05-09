package edu.ntnu.idi.idatt.views.menuviews;

import edu.ntnu.idi.idatt.MainApp;
import edu.ntnu.idi.idatt.views.CssUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * View class for the player count selection screen.
 * Allows selection of how many players will participate in the game.
 */
public class PlayerCountView {
  private final MainApp application;
  private final String selectedGame;
  private Scene scene;

  /**
   * Constructor that creates the player count view.
   * @param application The main application instance.
   * @param selectedGame The game selected by the user.
   */
  public PlayerCountView(MainApp application, String selectedGame) {
    this.application = application;
    this.selectedGame = selectedGame;
    createView();
  }

  /**
   * Creates the player count view components.
   */
  private void createView() {
    BorderPane root = new BorderPane();
    root.getStyleClass().add("root");
    root.setPadding(new Insets(10));

    VBox layout = new VBox(30);
    layout.setAlignment(Pos.CENTER);
    layout.setPadding(new Insets(10));
    layout.getStyleClass().add("content-box");

    Label titleLabel = new Label("Select Number of Players");
    titleLabel.getStyleClass().add("heading-large");
    layout.getChildren().add(titleLabel);

    for (int i = 2; i <= 5; i++) {
      Button playerCountButton = new Button(i + " Player" + "s");
      playerCountButton.getStyleClass().add("button");
      playerCountButton.getStyleClass().add("button-primary");
      final int count = i;
      playerCountButton.setOnAction(event -> application.showPlayerNameView(selectedGame, count));
      layout.getChildren().add(playerCountButton);
    }

    Button backButton = new Button("Back To Game Selection");
    backButton.getStyleClass().add("button");
    backButton.getStyleClass().add("button-secondary");
    backButton.setOnAction(event -> application.showGameSelectionView());
    layout.getChildren().add(backButton);

    root.setCenter(layout);
    scene = new Scene(root, 800, 600);
    CssUtil.applyStyleSheet(scene);

    Stage stage = application.getPrimaryStage();
    stage.setMinWidth(600);
    stage.setMinHeight(600);
    stage.centerOnScreen();
  }

  /**
   * Returns the scene for this view.
   * @return The scene.
   */
  public Scene getScene() {
    return scene;
  }
}