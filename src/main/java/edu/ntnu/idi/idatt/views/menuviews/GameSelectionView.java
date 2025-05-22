package edu.ntnu.idi.idatt.views.menuviews;

import edu.ntnu.idi.idatt.MainApp;
import edu.ntnu.idi.idatt.controllers.LadderGameController;
import edu.ntnu.idi.idatt.controllers.MonopolyGameController;
import edu.ntnu.idi.idatt.model.filehandling.BoardGameFactory;
import edu.ntnu.idi.idatt.model.gamelogic.BoardGame;
import edu.ntnu.idi.idatt.views.CssUtil;
import java.io.File;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * <h1>Game Selection View</h1>
 *
 *
 * <p>This view represents the main menu screen where players can choose which game to play,
 * Ladder Game with its variations or a simplified version of a Monopoly Game. Serves as the
 * central navigation hub of the application.</p>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Displays available games with intuitive UI elements</li>
 *   <li>Provides options to start new games or load saved games</li>
 *   <li>Handles game variation selection for Ladder Game</li>
 *   <li>Manages save file operations (loading and deletion)</li>
 *   <li>Implements confirmation dialogs for destructive operations</li>
 * </ul>
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
 */
public class GameSelectionView extends AbstractMenuView {
  private static final String LADDER_GAME_DIRECTORY = "src/main/resources/saves/laddergame";
  private static final String MONOPOLY_GAME_DIRECTORY = "src/main/resources/saves/monopolygame";

  /**
   * Constructor that creates the game selection view.
   *
   * @param application The application to create.
   */
  public GameSelectionView(MainApp application) {
    super(application);
    createView();
  }

  /**
   * Creates the game selection view components.
   */
  private void createView() {
    Label titleLabel = new Label("Select a game to play");
    titleLabel.getStyleClass().add("heading-large");
    layout.getChildren().add(titleLabel);

    VBox ladderGameSection = createGameSection("Ladder Game", "Ladder Game");
    layout.getChildren().add(ladderGameSection);

    VBox monopolyGameSection = createGameSection("Monopoly Game", "Monopoly Game");
    layout.getChildren().add(monopolyGameSection);

    Button exitButton = new Button("Exit Game");
    exitButton.getStyleClass().add("button");
    exitButton.setOnAction(event -> application.getPrimaryStage().close());

    layout.getChildren().add(exitButton);
  }

  /**
   * Creates a section for a specific game with play and save handling options.
   *
   * @param displayName The display name of the game.
   * @param gameType The internal game type identifier.
   * @return A VBox containing the same section of UI elements.
   */
  private VBox createGameSection(String displayName, String gameType) {
    VBox gameSection = new VBox(15);
    gameSection.setAlignment(Pos.CENTER);
    gameSection.getStyleClass().add("game-selection-panel");
    gameSection.setMaxWidth(400);

    Label gameLabel = new Label(displayName);
    gameLabel.getStyleClass().add("heading-medium");

    Button playButton = new Button("Play " + displayName);
    playButton.getStyleClass().add("button");
    playButton.getStyleClass().add("button-primary");
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

    String saveDirectory = gameType.equals("Ladder Game")
        ? LADDER_GAME_DIRECTORY : MONOPOLY_GAME_DIRECTORY;
    String saveName = gameType.equals("Ladder Game") ? "LadderGameSave" : "MonopolyGameSave";

    File boardFile = new File(saveDirectory + "/" + saveName + "_board.json");
    File playerFile = new File(saveDirectory + "/" + saveName + "_players.csv");

    boolean hasSave = boardFile.exists() && playerFile.exists();

    Button loadSaveButton = new Button("Load Save");
    loadSaveButton.getStyleClass().add("button");
    loadSaveButton.getStyleClass().add("button-secondary");
    if (!hasSave) {
      loadSaveButton.getStyleClass().add("button-disabled");
    }
    loadSaveButton.setDisable(!hasSave);
    loadSaveButton.setOnAction(event -> loadGame(saveName, gameType));

    Button deleteSaveButton = new Button("Delete Save");
    deleteSaveButton.getStyleClass().add("button");
    deleteSaveButton.getStyleClass().add("button-danger");
    if (!hasSave) {
      deleteSaveButton.getStyleClass().add("button-disabled");
    }
    deleteSaveButton.setDisable(!hasSave);
    deleteSaveButton.setOnAction(event -> {
      if (showDeleteConfirmation(displayName)) {
        if (deleteSave(boardFile, playerFile)) {
          loadSaveButton.setDisable(true);
          loadSaveButton.getStyleClass().add("button-disabled");
          deleteSaveButton.setDisable(true);
          deleteSaveButton.getStyleClass().add("button-disabled");
        }
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
    popupLayout.getStyleClass().add("dialog-pane");

    Label label = new Label("Choose a variation of the ladder game and create a new game:");
    label.getStyleClass().add("dialog-header");
    popupLayout.getChildren().add(label);

    List<String> variations = BoardGameFactory.getAvailableVariants();

    for (String variation : variations) {
      if (variation.startsWith("Ladder Game")) {
        Button variationButton = new Button(variation);
        variationButton.getStyleClass().add("button");
        variationButton.getStyleClass().add("button-primary");
        variationButton.setOnAction(event -> {
          popup.close();
          application.showPlayerCountView(variation);
        });
        popupLayout.getChildren().add(variationButton);
      }
    }

    Button backButton = new Button("Back To Game Selection");
    backButton.getStyleClass().add("button");
    backButton.getStyleClass().add("button-secondary");
    backButton.setOnAction(event -> popup.close());
    popupLayout.getChildren().add(backButton);

    Scene popupScene = new Scene(popupLayout, 700, 500);
    CssUtil.applyStyleSheet(popupScene);

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
    confirmDialog.setHeaderText("Delete " + gameType + " save?");
    confirmDialog.setContentText(
        "Are you sure you want to delete this saved game? This action cannot be undone.");

    if (confirmDialog.getDialogPane() != null) {
      confirmDialog.getDialogPane().getStyleClass().add("dialog-pane");
      Scene dialogScene = confirmDialog.getDialogPane().getScene();
      if (dialogScene != null) {
        CssUtil.applyStyleSheet(dialogScene);
      }
    }

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
    String gameDirectoryType = gameType.equals("Ladder Game") ? "laddergame" : "monopolygame";
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
      new MonopolyGameController(loadedGame, application.getPrimaryStage(),
          "Monopoly Game");
    }
  }

  /**
   * Returns the scene for this view.
   *
   * @return The scene.
   */
  public Scene getScene() {
    return scene;
  }
}