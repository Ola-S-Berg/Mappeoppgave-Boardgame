package edu.ntnu.idi.idatt.GUI;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * View class for the game selection view.
 * Allows players to choose which game to play.
 */
public class GameSelectionView {
  private BoardGameApplication application;
  private Scene scene;

  /**
   * Constructor that creates the game selection view.
   * @param application
   */
  public GameSelectionView(BoardGameApplication application) {
    this.application = application;
    createView();
  }

  /**
   * Creates the game selection view components.
   */
  private void createView() {
    VBox layout = new VBox(20);
    layout.setAlignment(Pos.CENTER);

    Label titleLabel = new Label("Select a game to play");
    titleLabel.setStyle("-fx-font-size: 24px");
    layout.getChildren().add(titleLabel);

    String[] games = {"Ladder Game"};

    for (String game : games) {
      Button gameButton = new Button(game);
      gameButton.setOnAction(event -> {
        application.showPlayerCountView(game);
      });
      layout.getChildren().add(gameButton);
    }

    scene = new Scene(layout, 300, 400);
  }

  /**
   * Returns the scene for this view.
   * @return The scene.
   */
  public Scene getScene() {
    return scene;
  }

}
