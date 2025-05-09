package edu.ntnu.idi.idatt.views;

import edu.ntnu.idi.idatt.BoardGameApplication;
import edu.ntnu.idi.idatt.controllers.LadderGameController;
import edu.ntnu.idi.idatt.controllers.MonopolyGameController;
import edu.ntnu.idi.idatt.filehandling.BoardGameFactory;
import edu.ntnu.idi.idatt.model.BoardGame;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
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
  private static final String LADDER_GAME_DIRECTORY = "src/main/resources/saves/ladder_game";
  private static final String MONOPOLY_GAME_DIRECTORY = "src/main/resources/saves/monopoly_game";

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
    BorderPane root = new BorderPane();
    root.setPadding(new Insets(10));

    VBox layout = new VBox(30);
    layout.setAlignment(Pos.CENTER);
    layout.setPadding(new Insets(10));

    Label titleLabel = new Label("Select a game to play");
    titleLabel.setStyle("-fx-font-size: 24px");
    layout.getChildren().add(titleLabel);

    VBox ladderGameSection = createGameSection("Ladder Game", "Ladder Game");
    layout.getChildren().add(ladderGameSection);

    VBox monopolyGameSection = createGameSection("Monopoly Game", "Monopoly Game");
    layout.getChildren().add(monopolyGameSection);

    root.setCenter(layout);

    scene = new Scene(root, 800, 600);

    Stage stage = application.getPrimaryStage();
    stage.setMinWidth(600);
    stage.setMinHeight(600);
    stage.centerOnScreen();
  }

  /**
   * Creates a section for a specific game with play and save handling options.
   *
   * @param displayName The display name of the game.
   * @param gameType The internal game type identifier.
   * @return A VBox containing the same section UI elements.
   */
  private VBox createGameSection(String displayName, String gameType) {
    VBox gameSection = new VBox(15);
    gameSection.setAlignment(Pos.CENTER);
    gameSection.setStyle("-fx-padding: 10; -fx-border-color: #cccccc; -fx-border-radius: 5;");
    gameSection.setMaxWidth(400);

    Label gameLabel = new Label(displayName);
    gameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

    Button playButton = new Button("Play " + displayName);
    playButton.setMinWidth(250);
    playButton.setOnAction(event -> {
      if (gameType.equals("Ladder Game")) {
        showLadderVariationsPopup();
      } else {
        application.showPlayerCountView(gameType);
      }
    });

    HBox saveSection = new HBox(10);
    saveSection.setAlignment(Pos.CENTER);

    String saveDirectory = gameType.equals("Ladder Game") ? LADDER_GAME_DIRECTORY : MONOPOLY_GAME_DIRECTORY;
    String saveName = gameType.equals("Ladder Game") ? "LadderGameSave" : "MonopolyGameSave";

    File boardFile = new File(saveDirectory + "/" + saveName + "_board.json");
    File playerFile = new File(saveDirectory + "/" + saveName + "_players.csv");

    boolean hasSave = boardFile.exists() && playerFile.exists();

    Button loadSaveButton = new Button("Load Save");
    loadSaveButton.setDisable(!hasSave);
    loadSaveButton.setOnAction(event -> loadGame(saveName, gameType));

    Button deleteSaveButton = new Button("Delete Save");
    deleteSaveButton.setDisable(!hasSave);
    deleteSaveButton.setOnAction(event -> {
      if (showDeleteConfirmation(displayName)) {
        if (deleteSave(boardFile, playerFile)) {
          loadSaveButton.setDisable(true);
          deleteSaveButton.setDisable(true);}
      }
    });

    saveSection.getChildren().addAll(loadSaveButton, deleteSaveButton);

    gameSection.getChildren().addAll(gameLabel, playButton, saveSection);

    return gameSection;
  }

  /**
   * Displays a popup for selecting which variation of the ladder game to play.
   */
  private void showLadderVariationsPopup() {
    Stage popup = new Stage();
    popup.initModality(Modality.APPLICATION_MODAL);
    popup.setTitle("Select Ladder Game Version");

    VBox popupLayout = new VBox(15);
    popupLayout.setAlignment(Pos.CENTER);
    popupLayout.setPadding(new Insets(20));

    Label label = new Label("Choose a variation of the ladder game and create a new game:");
    label.setStyle("-fx-font-size: 18px");
    popupLayout.getChildren().add(label);

    List<String> variations = BoardGameFactory.getAvailableVariants();

    for (String variation : variations) {
      if (variation.startsWith("Ladder Game")) {
        Button variationButton = new Button(variation);
        variationButton.setOnAction(event -> {
          popup.close();
          application.showPlayerCountView(variation);
        });
        popupLayout.getChildren().add(variationButton);
      }
    }

    Scene popupScene = new Scene(popupLayout, 700, 500);
    popup.setScene(popupScene);
    popup.setMinWidth(700);
    popup.setMinHeight(500);
    popup.centerOnScreen();
    popup.showAndWait();
  }

  /**
   * Displays a confirmation dialog before deleting a save file.
   *
   * @param gameType The type of game whose save is being deleted.
   * @return true if the user confirms deletion, false otherwise.
   */
  private boolean showDeleteConfirmation(String gameType) {
    Alert confirmDialog = new Alert(AlertType.CONFIRMATION);
    confirmDialog.setTitle("Confirm Deletion");
    confirmDialog.setHeaderText("Delete" + gameType + " save?");
    confirmDialog.setContentText("Are you sure you want to delete this saved game? This action cannot be undone.");

    return confirmDialog.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
  }

  /**
   * Deletes the save files for a game.
   *
   * @param boardFile The board file to delete.
   * @param playerFile The player file to delete.
   * @return true if the deletion was successful, false otherwise.
   */
  private boolean deleteSave(File boardFile, File playerFile) {
    boolean boardFileDeleted = boardFile.delete();
    boolean playerFileDeleted = playerFile.delete();

    if (!boardFileDeleted || !playerFileDeleted) {
      System.err.println("Failed to delete save files");
      return false;
    }

    return true;
  }

  /**
   * Loads a saved game and initializes the appropriate game view based on the variant of the game.
   * If the saved game cannot be loaded due to an error, it logs an error message to the console.
   *
   * @param saveName The name of the saved game file to load.
   */
  private void loadGame(String saveName, String gameType) {
    try {
      String gameDirectoryType = gameType.equals("Ladder Game") ? "ladder_game" : "monopoly_game";
      BoardGame loadedGame = BoardGameFactory.loadSavedGame(gameDirectoryType, saveName);
      String gameVariation = loadedGame.getVariantName();

      if (gameType.equals("Ladder Game")) {
        String displayName = switch (gameVariation) {
          case "Ladder Game Advanced", "ladderGameAdvanced" -> "Ladder Game Advanced";
          case "Ladder Game Extreme", "ladderGameExtreme" -> "Ladder Game Extreme";
          default -> "Ladder Game Classic";
        };
        new LadderGameController(loadedGame, application.getPrimaryStage(), displayName);
      } else {
        new MonopolyGameController(loadedGame, application.getPrimaryStage(), "Monopoly Game");
      }
    } catch  (IOException e) {
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