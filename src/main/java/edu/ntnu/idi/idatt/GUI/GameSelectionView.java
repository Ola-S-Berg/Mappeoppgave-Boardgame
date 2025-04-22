package edu.ntnu.idi.idatt.GUI;

import edu.ntnu.idi.idatt.Filehandling.BoardGameFactory;

import edu.ntnu.idi.idatt.GameLogic.BoardGame;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

    String[] games = {"Ladder Game", "WIP", "WIP"};

    for (String game : games) {
      Button gameButton = new Button(game);
      gameButton.setOnAction(event -> {
        if (game.equals("Ladder Game")) {
          showLadderVariationsPopup();
        } else {
          application.showPlayerCountView(game);
        }
      });
      layout.getChildren().add(gameButton);
    }

    scene = new Scene(layout, 800, 800);
  }

  /**
   * Shows a popup for selecting ladder game variations.
   */
  private void showLadderVariationsPopup() {
    Stage popup = new Stage();
    popup.initModality(Modality.APPLICATION_MODAL);
    popup.setTitle("Select Ladder Game Version");

    VBox popupLayout = new VBox(15);
    popupLayout.setAlignment(Pos.CENTER);

    Label loadLabel = new Label("Load Saved Game");
    loadLabel.setStyle("-fx-font-size: 24px");
    popupLayout.getChildren().add(loadLabel);

    Label label = new Label("Choose a variation:");
    label.setStyle("-fx-font-size: 18px");
    popupLayout.getChildren().add(label);

    List<String> variations = BoardGameFactory.getAvailableBoardGames();

    for (String variation : variations) {
      Button variationButton = new Button(variation);
      variationButton.setOnAction(event -> {
        popup.close();
        BoardGame selectedGame = BoardGameFactory.createBoardGame(variation);
        application.showPlayerCountView(variation);
      });
      popupLayout.getChildren().add(variationButton);
    }

    Scene popupScene = new Scene(popupLayout, 500, 500);
    popup.setScene(popupScene);
    popup.showAndWait();
  }

  /**
   * Returns the scene for this view.
   * @return The scene.
   */
  public Scene getScene() {
    return scene;
  }

}
