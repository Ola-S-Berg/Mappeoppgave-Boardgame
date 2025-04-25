package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.GUI.GameSelectionView;
import edu.ntnu.idi.idatt.GUI.LadderGameClassicView;
import edu.ntnu.idi.idatt.GUI.PlayerCountView;
import edu.ntnu.idi.idatt.GUI.PlayerNameView;
import edu.ntnu.idi.idatt.GUI.TokenSelectionView;
import edu.ntnu.idi.idatt.GameLogic.BoardGame;
import edu.ntnu.idi.idatt.GameLogic.Player;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application that serves as the entry point for the JavaFX application.
 * Manages the different views.
 */
public class BoardGameApplication extends Application {
  private Stage primaryStage;
  private BoardGame boardGame;
  private String selectedGame;

  /**
   * Initializes and starts the primary stage of the application.
   *
   * @param primaryStage The primary stage for this application, provided by the JavaFX framework.
   */
  @Override
  public void start(Stage primaryStage) {

    this.primaryStage = primaryStage;
    this.boardGame = new BoardGame();
    boardGame.createBoard();
    boardGame.createDice();

    primaryStage.setTitle("Board Game");

    showGameSelectionView();

    primaryStage.show();
  }

  /**
   * Shows the game selection view.
   */
  public void showGameSelectionView() {
    GameSelectionView gameSelectionView = new GameSelectionView(this);
    Scene scene = gameSelectionView.getScene();
    primaryStage.setScene(scene);
  }

  /**
   * Shows the player count selection view.
   * @param selectedGame The game selected by the user.
   */
  public void showPlayerCountView(String selectedGame) {
    this.selectedGame = selectedGame;
    PlayerCountView playerCountView = new PlayerCountView(this, selectedGame);
    Scene scene = playerCountView.getScene();
    primaryStage.setScene(scene);
  }

  /**
   * Shows the player name input view.
   * @param selectedGame The game selected by the user.
   * @param playerCount The number of players.
   */
  public void showPlayerNameView(String selectedGame, int playerCount) {
    PlayerNameView playerNameView = new PlayerNameView(this, selectedGame, playerCount);
    Scene scene = playerNameView.getScene();
    primaryStage.setScene(scene);
  }

  /**
   * Shows the token selection view.
   * @param selectedGame The game selected by the user.
   * @param playerNames Array of player names.
   */
  public void showTokenSelectionView(String selectedGame, String[] playerNames) {
    TokenSelectionView tokenSelectionView = new TokenSelectionView(this, selectedGame, playerNames);
    Scene scene = tokenSelectionView.getScene();
    primaryStage.setScene(scene);
  }

  /**
   * Starts the game and shows the game view.
   * @param playerNames Array of player names.
   * @param playerTokens Array of player token paths.
   */
  public void startGame(String[] playerNames, String[] playerTokens) {
    boardGame = new BoardGame();
    boardGame.setVariantName(getVariantNameFromGameType(selectedGame));
    boardGame.createBoard();
    boardGame.createDice();

    for (int i = 0; i < playerNames.length; i++) {
      Player player = new Player(playerNames[i], playerTokens[i], boardGame);
      boardGame.addPlayer(player);
    }

    LadderGameClassicView ladderGameClassicView = new LadderGameClassicView(boardGame, primaryStage, selectedGame);
  }

  /**
   * Determines the variant name of a game based on the selected game type.
   *
   * @param selectedGame The name of the selected game type.
   * @return The variant name corresponding to the selected game type. Defaults to "ladderGame1" if the input is unrecognized.
   */
  private String getVariantNameFromGameType(String selectedGame) {
    switch (selectedGame) {
      case "Classic Ladder Game":
        return "ladderGame";
      case "Classic Ladder Game Advanced":
        return "ladderGameAdvanced";
      case "Classic Ladder Game Extreme":
        return "ladderGameExtreme";
      default:
        return "ladderGame";  // Default variant if not recognized
    }
  }

  /**
   * Returns the primary stage.
   * @return The primary stage.
   */
  public Stage getPrimaryStage() {
    return primaryStage;
  }

  /**
   * Returns the board game instance.
   * @return The board game.
   */
  public BoardGame getBoardGame() {
    return boardGame;
  }

  public static void main(String[] args) {
    launch(args);
  }
}