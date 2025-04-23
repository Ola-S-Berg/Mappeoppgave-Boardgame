package edu.ntnu.idi.idatt.GUI;

import edu.ntnu.idi.idatt.BoardGameApplication;
import edu.ntnu.idi.idatt.Filehandling.BoardFileHandler;
import edu.ntnu.idi.idatt.Filehandling.BoardGameFactory;

import edu.ntnu.idi.idatt.Filehandling.PlayerFileHandler;
import edu.ntnu.idi.idatt.GameLogic.BoardGame;
import edu.ntnu.idi.idatt.GameLogic.Player;
import edu.ntnu.idi.idatt.GameLogic.Tile;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
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

    Label label = new Label("Choose a variation of the ladder game and create a new game:");
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

    Label loadLabel = new Label("Load Saved Game");
    loadLabel.setStyle("-fx-font-size: 24px");
    popupLayout.getChildren().add(loadLabel);

    File savesDir = new File("src/main/resources/Saves");
    if (!savesDir.exists()) savesDir.mkdirs();

    File[] saveFiles = savesDir.listFiles((dir, name) -> name.endsWith("_players.csv"));
    if (saveFiles != null) {
      for (File file : saveFiles) {
        String saveName = file.getName().replace("_players.csv", "");

        Button loadButton = new Button("Load: " + saveName);
        loadButton.setOnAction(e -> {
          popup.close();
          loadSavedGame(saveName);
        });

        Button deleteButton = new Button("Delete: " + saveName);
        deleteButton.setOnAction(e -> {
          new File("src/main/resources/Saves/" + saveName + ".json").delete();
          file.delete();
          popup.close();
          showLadderVariationsPopup(); // refresh list
        });

        HBox saveButtons = new HBox(10, loadButton, deleteButton);
        saveButtons.setAlignment(Pos.CENTER);
        popupLayout.getChildren().add(saveButtons);
      }
    }

    Scene popupScene = new Scene(popupLayout, 500, 500);
    popup.setScene(popupScene);
    popup.showAndWait();
  }

  private void loadSavedGame(String saveName) {
    try {
      BoardGame loadedGame = new BoardFileHandler().readFromFile("src/main/resources/Saves/" + saveName + ".json").get(0);
      List<Player> players = new PlayerFileHandler().readFromFile("src/main/resources/Saves/" + saveName + "_players.csv");

      if (loadedGame.getDice() == null) {
        loadedGame.createDice();
      }

      for (Player player : players) {
        player.setGame(loadedGame);

        String savedTileIdStr = player.getProperty("savedTileId");
        if (savedTileIdStr != null && !savedTileIdStr.trim().isEmpty()) {
          try {
            int tileId = Integer.parseInt(savedTileIdStr.trim());
            Tile tile = loadedGame.getBoard().getTile(tileId);
            if (tile != null) {
              player.placeOnTile(tile);
              System.out.println("Placed " + player.getName() + " on tile " + tileId);
            } else {
              System.out.println("Tile " + tileId + " not found, placing " + player.getName() + " on tile 1");
              player.placeOnTile(loadedGame.getBoard().getTile(1));
            }
          } catch (NumberFormatException e) {
            System.out.println("Invalid tile ID format: " + savedTileIdStr + ", placing " + player.getName() + " on tile 1");
            player.placeOnTile(loadedGame.getBoard().getTile(1));
          }
        } else {
          System.out.println("No saved tile ID for " + player.getName() + ", placing on tile 1");
          player.placeOnTile(loadedGame.getBoard().getTile(1));
        }

        loadedGame.addPlayer(player);
      }

      new LadderGameClassicView(loadedGame, application.getPrimaryStage());
    } catch (IOException e) {
      System.err.println("Failed to load save: " + e.getMessage());
    }
  }

  /**
   * Returns the scene for this view.
   * @return The scene.
   */
  public Scene getScene() {
    return scene;
  }

}
