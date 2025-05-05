package edu.ntnu.idi.idatt.Controllers;

import edu.ntnu.idi.idatt.Actions.TileAction;
import edu.ntnu.idi.idatt.Filehandling.BoardGameFactory;
import edu.ntnu.idi.idatt.Filehandling.PlayerFileHandler;
import edu.ntnu.idi.idatt.GameLogic.BoardGame;
import edu.ntnu.idi.idatt.GameLogic.Player;
import edu.ntnu.idi.idatt.GameLogic.Tile;
import edu.ntnu.idi.idatt.GUI.LadderGameView;
import edu.ntnu.idi.idatt.BoardGameApplication;

import java.io.IOException;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Controller for the Ladder Game that handles game logic and coordinates
 * between the model (BoardGame) and view (LadderGameClassicView).
 */
public class LadderGameController {
  private final BoardGame boardGame;
  private final LadderGameView view;
  private final String gameVariation;
  private int currentPlayerIndex = 0;
  private final Stage stage;

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

    initializePlayers();

    boardGame.notifyCurrentPlayerChanged(getCurrentPlayer());
  }

  /**
   * Initialize all players on the starting tile if they're not already on a tile.
   */
  private void initializePlayers() {
    for (Player player : boardGame.getPlayers()) {
      if (player.getCurrentTile() == null) {
        player.placeOnTile(boardGame.getBoard().getTile(1));
      }
    }
  }

  /**
   * Get the current player whose turn it is.
   *
   * @return The current player.
   */
  public Player getCurrentPlayer() {
    return boardGame.getPlayers().get(currentPlayerIndex);
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

    int dice1 = boardGame.getDice().roll();
    int dice2 = boardGame.getDice().roll();
    int diceValue = dice1 + dice2;

    view.updateDiceDisplay(dice1, dice2);

    int fromTileId = currentPlayer.getCurrentTile().getTileId();
    Tile destinationTile = calculateDestinationTile(currentPlayer, diceValue);
    int toTileId = destinationTile.getTileId();

    currentPlayer.placeOnTile(destinationTile);

    boardGame.notifyPlayerMove(currentPlayer, fromTileId, toTileId, diceValue);

    handlePlayerMove(currentPlayer);
  }

  private void handlePlayerMove(Player player) {
    Tile currentTile = player.getCurrentTile();
    TileAction action = currentTile.getAction();

    if (action != null) {
      int beforeActionId = currentTile.getTileId();

      String actionType = action.getClass().getSimpleName();
      view.showActionMessage(player, actionType);

      action.perform(player);
      int afterActionId = player.getCurrentTile().getTileId();

      if (afterActionId != beforeActionId) {
        boardGame.notifyPlayerMove(player, beforeActionId, afterActionId, 0);
      }
    }

    if (player.getCurrentTile().getTileId() == 90) {
      boardGame.notifyGameWon(player);
    } else {
      new Thread(() -> {
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        Platform.runLater(this::advanceToNextPlayer);
      }).start();
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
    currentPlayerIndex = (currentPlayerIndex + 1) % boardGame.getPlayers().size();
    Player nextPlayer = boardGame.getPlayers().get(currentPlayerIndex);
    boardGame.notifyCurrentPlayerChanged(nextPlayer);
    view.prepareForNextTurn();
  }

  /**
   * Saves the current game state.
   *
   * @return true if save was successful, false otherwise
   */
  public boolean saveGame() {
    try {
      String saveName = "SavedGame";
      BoardGameFactory.saveBoardGame(boardGame, saveName);
      new PlayerFileHandler().writeToFile("src/main/resources/Saves/" + saveName + "_players.csv",
          boardGame.getPlayers());
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Quits the current game and returns to the main menu.
   */
  public void quitToMenu() {
    stage.close();
    new BoardGameApplication().start(new Stage());
  }

  /**
   * Restarts the game with the same players.
   */
  public void restartGame() {
    BoardGame newGame = BoardGameFactory.createBoardGame(gameVariation);

    String[] playerNames = new String[boardGame.getPlayers().size()];
    String[] playerTokens = new String[boardGame.getPlayers().size()];

    for (int i = 0; i < boardGame.getPlayers().size(); i++) {
      Player player = boardGame.getPlayers().get(i);
      playerNames[i] = player.getName();
      playerTokens[i] = player.getToken();
    }

    stage.close();

    Stage newStage = new Stage();
    BoardGameApplication application = new BoardGameApplication();
    application.start(newStage);

    for (int i = 0; i < playerNames.length; i++) {
      Player player = new Player(playerNames[i], playerTokens[i], newGame);
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
   * Returns the board game.
   *
   * @return The board game.
   */
  public BoardGame getBoardGame() {
    return boardGame;
  }

  /**
   * Returns the stage.
   *
   * @return The JavaFX stage.
   */
  public Stage getStage() {
    return stage;
  }

  /**
   * Converts a tile ID to its corresponding grid coordinates on the game board.
   * This method has been moved from the view to the controller to follow MVC principles.
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
   * This is now a controller method since it involves calculation logic.
   *
   * @param playerIndex The index of the player.
   * @param totalPlayers The total number of players in the game.
   * @param baseRadius The base radius to use for the offset calculation.
   * @return A double array where the first element is the X offset and the second element is the Y offset.
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