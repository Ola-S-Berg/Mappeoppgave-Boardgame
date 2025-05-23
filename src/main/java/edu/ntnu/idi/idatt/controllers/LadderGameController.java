package edu.ntnu.idi.idatt.controllers;

import edu.ntnu.idi.idatt.MainApp;
import edu.ntnu.idi.idatt.model.actions.TileAction;
import edu.ntnu.idi.idatt.model.filehandling.BoardGameFactory;
import edu.ntnu.idi.idatt.model.filehandling.PlayerFileHandler;
import edu.ntnu.idi.idatt.model.gamelogic.BoardGame;
import edu.ntnu.idi.idatt.model.gamelogic.Player;
import edu.ntnu.idi.idatt.model.gamelogic.Tile;
import edu.ntnu.idi.idatt.views.DialogService;
import edu.ntnu.idi.idatt.views.SoundUtil;
import edu.ntnu.idi.idatt.views.gameviews.LadderGameView;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * <h1>Ladder Game Controller</h1>
 *
 * <p>A specialized controller implementation for the Ladder Game. This controller manages the
 * complete game lifecycle, coordinates between the game model and the visual representation,
 * and implements game-specific rules and mechanics.</p>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Turn-based player management with automated progression</li>
 *   <li>Dice rolling and movement calculation with support for special actions</li>
 *   <li>Multithreaded animation coordination for a smooth gameplay experience</li>
 *   <li>Game state persistence through save/load functionality</li>
 *   <li>Position conversion between logical game state and visual grid representation</li>
 * </ul>
 *
 * @author Ola Syrstad Berg
 * @author Markus Øyen Lund
 * @since v1.1.0
 */
public class LadderGameController implements BoardGameController {
  private final BoardGame boardGame;
  private final LadderGameView view;
  private final String gameVariation;
  private final Stage stage;
  private static final Logger LOGGER = Logger.getLogger(LadderGameController.class.getName());


  /**
   * Constructor for the controller.
   *
   * @param boardGame The game model.
   * @param stage The JavaFX stage.
   * @param gameVariation The variation of the ladder game.
   */
  public LadderGameController(BoardGame boardGame, Stage stage, String gameVariation) {
    this.boardGame = boardGame;
    this.stage = stage;
    this.gameVariation = gameVariation;

    this.view = new LadderGameView(boardGame, stage, this);

    if (boardGame.isLoadedGame()) {
      boardGame.initializeGame();
    }
  }

  /**
   * Get the current player whose turn it is.
   *
   * @return The current player.
   */
  public Player getCurrentPlayer() {
    return boardGame.getCurrentPlayer();
  }

  /**
   * Handles the dice roll and player movement logic.
   * This method is called from the view when the roll button is clicked.
   */
  public void rollDice() {
    Player currentPlayer = getCurrentPlayer();

    view.disableRollButton(true);

    if (currentPlayer.willWaitTurn()) {
      boardGame.notifyPlayerSkipTurn(currentPlayer);
      currentPlayer.setWaitTurn(false);
      advanceToNextPlayer();
      return;
    }

    // Play dice roll sound for each roll of the dice
    SoundUtil.playDiceRollSound();

    int[] diceValues = boardGame.rollDice();

    int dice1 = (diceValues.length > 0) ? diceValues[0] : 1;
    int dice2 = (diceValues.length > 1) ? diceValues[1] : 1;
    view.updateDiceDisplay(dice1, dice2);

    int diceTotal = (diceValues.length > 1) ? dice1 + dice2 : dice1;

    int fromTileId = currentPlayer.getCurrentTile().getTileId();
    Tile destinationTile = calculateDestinationTile(currentPlayer, diceTotal);
    int toTileId = destinationTile.getTileId();

    currentPlayer.placeOnTile(destinationTile);

    boardGame.notifyPlayerMove(currentPlayer, fromTileId, toTileId, diceTotal);

    handlePlayerMove(currentPlayer);
  }

  /**
   * Handles player movement and performs actions, updating views accordingly.
   *
   * @param player The player that moves.
   */
  private void handlePlayerMove(Player player) {
    Tile currentTile = player.getCurrentTile();
    TileAction action = currentTile.getAction();

    if (action != null) {
      String actionType = action.getClass().getSimpleName();
      view.showActionMessage(player, actionType);

      new Thread(() -> {
        try {
          Thread.sleep(1000);

          Platform.runLater(() -> {
            int fromTileId = player.getCurrentTile().getTileId();
            action.perform(player);
            int toTileId = player.getCurrentTile().getTileId();

            if (fromTileId != toTileId) {
              boardGame.notifyPlayerMove(player, fromTileId, toTileId, 0);
            }
          });

          Thread.sleep(1000);

          Platform.runLater(() -> {
            if (player.getCurrentTile().getTileId() == 90) {
              SoundUtil.playVictorySound();
              boardGame.notifyGameWon(player);
            } else {
              advanceToNextPlayer();
            }
          });
        } catch (InterruptedException e) {
          LOGGER.log(Level.WARNING, "Thread was interrupted during game action delay", e);
          Thread.currentThread().interrupt();
        }
      }).start();
    } else {
      if (player.getCurrentTile().getTileId() == 90) {
        SoundUtil.playVictorySound();
        boardGame.notifyGameWon(player);
      } else {
        new Thread(() -> {
          try {
            Thread.sleep(2000);
          } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Thread was interrupted during game action delay", e);
            Thread.currentThread().interrupt();
          }
          Platform.runLater(this::advanceToNextPlayer);
        }).start();
      }
    }
  }


  /**
   * Calculates the destination tile based on dice value.
   *
   * @param player The player to move.
   * @param diceValue The combined value of the dice.
   * @return The destination tile.
   */
  private Tile calculateDestinationTile(Player player, int diceValue) {
    Tile destinationTile = player.getCurrentTile();
    for (int i = 0; i < diceValue; i++) {
      if (destinationTile.getNextTile() != null) {
        destinationTile = destinationTile.getNextTile();
      }
    }
    return destinationTile;
  }

  /**
   * Advances the game to the next player's turn.
   */
  private void advanceToNextPlayer() {
    boardGame.advanceToNextPlayer();

    view.prepareForNextTurn();
  }

  /**
   * Saves the current game state.
   *
   * @return true if save was successful, false otherwise.
   */
  public boolean saveGame() {
    try {
      String saveName = "LadderGameSave";
      BoardGameFactory.saveBoardGame(boardGame, saveName);
      new PlayerFileHandler().writeToFile(
          BoardGameFactory.getPlayerSaveFilePath("laddergame", saveName),
          boardGame.getPlayers());
      return true;
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Failed to save game", e);
      return false;
    }
  }

  /**
   * Quits the current game and returns to the main menu.
   * Shows a confirmation dialog before quitting.
   */
  public void quitToMenu() {
    DialogService.showQuitConfirmationDialog(stage, () -> {
      stage.close();
      new MainApp().start(new Stage());
    });
  }

  /**
   * Restarts the game with the same players.
   */
  public void restartGame() {
    String[] playerNames = new String[boardGame.getPlayers().size()];
    String[] playerTokens = new String[boardGame.getPlayers().size()];

    for (int i = 0; i < boardGame.getPlayers().size(); i++) {
      Player player = boardGame.getPlayers().get(i);
      playerNames[i] = player.getName();
      playerTokens[i] = player.getToken();
    }

    stage.close();

    Stage newStage = new Stage();
    MainApp application = new MainApp();
    application.start(newStage);

    BoardGame newGame = BoardGameFactory.createBoardGame(gameVariation);

    for (int i = 0; i < playerNames.length; i++) {
      Player player = new Player(playerNames[i], playerTokens[i], newGame, 100000);
      newGame.addPlayer(player);
    }

    new LadderGameController(newGame, newStage, gameVariation);
  }

  /**
   * Returns the game variation.
   *
   * @return The game variation.
   */
  public String getGameVariation() {
    return gameVariation;
  }

  /**
   * Converts a tile ID to its corresponding grid coordinates on the game board.
   *
   * @param tileId The ID of the tile to be converted. Expected range: 1-90.
   * @return An integer array containing the grid coordinates where
   *         the first element is the row index and the second element is the column index.
   */
  public int[] convertTileIdToGridCoordinates(int tileId) {
    final int gridRows = 9;
    final int gridCols = 10;

    tileId = Math.max(1, Math.min(90, tileId));

    int adjustedId = tileId - 1;

    int row = gridRows - 1 - (adjustedId / gridCols);

    int col;

    if ((gridRows - 1 - row) % 2 == 0) {
      col = adjustedId % gridCols;
    } else {
      col = gridCols - 1 - (adjustedId % gridCols);
    }

    return new int[] {row, col};
  }

  /**
   * Calculates the offset position for a player token based on its index.
   *
   * @param playerIndex The index of the player.
   * @param totalPlayers The total number of players in the game.
   * @param baseRadius The base radius to use for the offset calculation.
   * @return A double array where the first element is the X offset
   *         and the second element is the Y offset.
   */
  public double[] calculateTokenOffset(int playerIndex, int totalPlayers, double baseRadius) {
    if (totalPlayers <= 0) {
      return new double[] {0, 0};
    }

    playerIndex = Math.max(0, Math.min(totalPlayers - 1, playerIndex));

    double offsetAngle = (playerIndex * (360.0 / totalPlayers)) * Math.PI / 180;

    double offsetX = baseRadius * Math.cos(offsetAngle);
    double offsetY = baseRadius * Math.sin(offsetAngle);

    return new double[] {offsetX, offsetY};
  }
}