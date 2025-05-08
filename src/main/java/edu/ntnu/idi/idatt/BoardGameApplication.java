package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.controllers.LadderGameController;
import edu.ntnu.idi.idatt.controllers.MonopolyGameController;
import edu.ntnu.idi.idatt.views.GameSelectionView;
import edu.ntnu.idi.idatt.views.PlayerCountView;
import edu.ntnu.idi.idatt.views.PlayerNameView;
import edu.ntnu.idi.idatt.views.TokenSelectionView;
import edu.ntnu.idi.idatt.model.BoardGame;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.filehandling.BoardGameFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Application that serves as the entry point for the JavaFX application.
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
    this.boardGame = BoardGameFactory.createLadderGameClassic();

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
   * Shows the player-count selection view.
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
    boardGame = BoardGameFactory.createBoardGame(selectedGame);

    for (int i = 0; i < playerNames.length; i++) {
      Player player = new Player(playerNames[i], playerTokens[i], boardGame, 200000);
      boardGame.addPlayer(player);
    }

    if (selectedGame.equals("Monopoly Game")) {
      new MonopolyGameController(boardGame, primaryStage, selectedGame);
    } else {
      new LadderGameController(boardGame, primaryStage, selectedGame);
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