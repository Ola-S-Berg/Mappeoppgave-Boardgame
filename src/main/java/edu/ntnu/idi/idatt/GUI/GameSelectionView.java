package edu.ntnu.idi.idatt.GUI;

import edu.ntnu.idi.idatt.BoardGameApplication;
import edu.ntnu.idi.idatt.Controllers.LadderGameController;
import edu.ntnu.idi.idatt.Filehandling.BoardGameFactory;
import edu.ntnu.idi.idatt.GameLogic.BoardGame;
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
  private final BoardGameApplication application;
  private Scene scene;
  private static final String SAVE_FILES_DIRECTORY = "src/main/resources/Saves";

  /**
   * Constructor that creates the game selection view.
   * @param application The application to create.
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
   * Displays a popup for selecting a variant of the ladder game or loading a previously saved game.
   * This method creates a modal popup window where users can:
   * 1. Choose from a list of predefined or available ladder game variations to start a new game.
   * 2. View saved game files, load a selected saved game, or delete a saved game file.
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

    List<String> variations = BoardGameFactory.getAvailableVariants();

    for (String variation : variations) {
      Button variationButton = new Button(variation);
      variationButton.setOnAction(event -> {
        popup.close();
        application.showPlayerCountView(variation);
      });
      popupLayout.getChildren().add(variationButton);

    }

    Label loadLabel = new Label("Load Saved Game");
    loadLabel.setStyle("-fx-font-size: 24px");
    popupLayout.getChildren().add(loadLabel);

    File savesDir = new File(SAVE_FILES_DIRECTORY);
    if (!savesDir.exists()) {
      boolean dirCreated = savesDir.mkdirs();
      if (!dirCreated) {
        System.err.println("Failed to create save directory: " + savesDir.getAbsolutePath());
      }
    }

    File[] playerSaveFiles = savesDir.listFiles((dir, name) -> name.endsWith("_players.csv"));

    if (playerSaveFiles != null) {
      for (File playerFile : playerSaveFiles) {
        String saveName = playerFile.getName().replace("_players.csv", "");

        File boardFile = new File(SAVE_FILES_DIRECTORY + "/" + saveName + "_board.json");
        if (boardFile.exists()) {
          Button loadButton = new Button("Load: " + saveName);
          loadButton.setOnAction(e -> {
            popup.close();
            loadGame(saveName);
          });

          Button deleteButton = new Button("Delete: " + saveName);
          deleteButton.setOnAction(e -> {
            boolean boardFileDeleted = boardFile.delete();
            boolean playerFileDeleted = playerFile.delete();
            if (!boardFileDeleted || !playerFileDeleted) {
              System.err.println("Failed to delete save files");
              return;
            }
            popup.close();
            showLadderVariationsPopup();
          });

          HBox saveButtons = new HBox(10, loadButton, deleteButton);
          saveButtons.setAlignment(Pos.CENTER);
          popupLayout.getChildren().add(saveButtons);
        }
      }
    }

    Scene popupScene = new Scene(popupLayout, 500, 500);
    popup.setScene(popupScene);
    popup.showAndWait();
  }

  /**
   * Loads a saved game and initializes the appropriate game view based on the variant of the game.
   * If the saved game cannot be loaded due to an error, it logs an error message to the console.
   *
   * @param saveName The name of the saved game file to load.
   */
  private void loadGame(String saveName) {
    try {
      BoardGame loadedGame = BoardGameFactory.loadSavedGame(saveName);

      String gameVariation = loadedGame.getVariantName();
      String displayName = switch (gameVariation) {
        case "Ladder Game Advanced", "ladderGameAdvanced" -> "Ladder Game Advanced";
        case "Ladder Game Extreme", "ladderGameExtreme" -> "Ladder Game Extreme";
        default -> "Ladder Game Classic";
      };

      new LadderGameController(loadedGame, application.getPrimaryStage(), displayName);
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
