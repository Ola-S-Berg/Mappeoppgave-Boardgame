package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.controllers.LadderGameController;
import edu.ntnu.idi.idatt.controllers.MonopolyGameController;
import edu.ntnu.idi.idatt.model.filehandling.BoardGameFactory;
import edu.ntnu.idi.idatt.model.gamelogic.BoardGame;
import edu.ntnu.idi.idatt.model.gamelogic.Player;
import edu.ntnu.idi.idatt.views.menuviews.GameSelectionView;
import edu.ntnu.idi.idatt.views.menuviews.PlayerCountView;
import edu.ntnu.idi.idatt.views.menuviews.PlayerNameView;
import edu.ntnu.idi.idatt.views.menuviews.TokenSelectionView;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * <h1>Board Game Application</h1>
 *
 * <p>The main application class that serves as the entry point for the javaFX board game platform.
 * This class manages the flow between different views and controllers, handling the overall
 * lifecycle of the application.</p>
 *
 * <h2>Application flow</h2>
 * <ol>
 * <li>Game selection (Monopoly or Ladder Game</li>
 * <li>Player count selection</li>
 * <li>Player name input</li>
 * <li>Player token selection</li>
 * <li>Game initialization and start</li>
 * </ol>
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
 */
public class MainApp extends Application {
  private Stage primaryStage;
  private BoardGame boardGame;
  private String selectedGame;
  private static final Logger LOGGER = Logger.getLogger(MainApp.class.getName());

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

    primaryStage.setOnCloseRequest(e -> {
      LOGGER.info("Application window closing");
      Platform.exit();
    });

    showGameSelectionView();

    primaryStage.show();
  }

  /**
   * Clean up resources when the application is stopped.
   */
  @Override
  public void stop() {
    try {
      LOGGER.info("Application stopping, cleaning up resources");
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error during application shutdown", e);
    } finally {
      Platform.exit();
    }
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
   *
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
   *
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
   *
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
   *
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
   *
   * @return The primary stage.
   */
  public Stage getPrimaryStage() {
    return primaryStage;
  }

  /**
   *Main method for the application.
   *
   * @param args The command line arguments.
   */
  public static void main(String[] args) {
    launch(args);
  }
}