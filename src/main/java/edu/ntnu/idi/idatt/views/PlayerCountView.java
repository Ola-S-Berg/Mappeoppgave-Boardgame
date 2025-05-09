package edu.ntnu.idi.idatt.views;

import edu.ntnu.idi.idatt.BoardGameApplication;
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
  private final BoardGameApplication application;
  private final String selectedGame;
  private Scene scene;

  /**
   * Constructor that creates the player count view.
   * @param application The main application instance.
   * @param selectedGame The game selected by the user.
   */
  public PlayerCountView(BoardGameApplication application, String selectedGame) {
    this.application = application;
    this.selectedGame = selectedGame;
    createView();
  }

  /**
   * Creates the player count view components.
   */
  private void createView() {
    BorderPane root = new BorderPane();
    root.setPadding(new Insets(10));

    VBox layout = new VBox(30);
    layout.setAlignment(Pos.CENTER);
    layout.setPadding(new Insets(10));

    Label titleLabel = new Label("Select Number of Players");
    titleLabel.setStyle("-fx-font-size: 24px");
    layout.getChildren().add(titleLabel);

    for (int i = 2; i <= 5; i++) {
      Button playerCountButton = new Button(i + " Player" + (i > 2 ? "s" : ""));
      final int count = i;
      playerCountButton.setOnAction(event -> application.showPlayerNameView(selectedGame, count));
      layout.getChildren().add(playerCountButton);
    }
    scene = new Scene(layout, 800, 600);

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
