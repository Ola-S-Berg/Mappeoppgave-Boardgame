package edu.ntnu.idi.idatt.controllers;

import edu.ntnu.idi.idatt.MainApp;
import edu.ntnu.idi.idatt.model.actions.TileAction;
import edu.ntnu.idi.idatt.model.actions.monopolygame.ChanceTileAction;
import edu.ntnu.idi.idatt.model.actions.monopolygame.JailTileAction;
import edu.ntnu.idi.idatt.model.actions.monopolygame.PropertyTileAction;
import edu.ntnu.idi.idatt.model.actions.monopolygame.StartTileAction;
import edu.ntnu.idi.idatt.model.actions.monopolygame.TaxTileAction;
import edu.ntnu.idi.idatt.model.filehandling.BoardGameFactory;
import edu.ntnu.idi.idatt.model.filehandling.PlayerFileHandler;
import edu.ntnu.idi.idatt.model.gamelogic.BoardGame;
import edu.ntnu.idi.idatt.model.gamelogic.Player;
import edu.ntnu.idi.idatt.model.gamelogic.Tile;
import edu.ntnu.idi.idatt.views.DialogService;
import edu.ntnu.idi.idatt.views.gameviews.MonopolyGameView;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Controller for the Monopoly Game that handles game logic and coordinates
 * between the model (BoardGame) and view (MonopolyGameView).
 */
public class MonopolyGameController implements BoardGameController {
  private final BoardGame boardGame;
  private final MonopolyGameView view;
  private final String gameVariation;
  private final Stage stage;
  private static final Logger LOGGER = Logger.getLogger(MonopolyGameController.class.getName());
  private final ExecutorService executorService;
  private boolean isShutDown = false;

  /**
   * Constructor for the controller.
   * Creates a thread factory for daemon threads to ensure the application doesn't hang on shutdown.
   *
   * @param boardGame The game model.
   * @param stage The JavaFX stage.
   * @param gameVariation The variation of the monopoly game.
   */
  public MonopolyGameController(BoardGame boardGame, Stage stage, String gameVariation) {
    this.boardGame = boardGame;
    this.stage = stage;
    this.gameVariation = gameVariation;

    ThreadFactory daemonFactory = r -> {
      Thread t = new Thread(r);
      t.setDaemon(true);
      return t;
    };

    this.executorService = Executors.newCachedThreadPool(daemonFactory);

    stage.setOnCloseRequest(event -> shutdown());

    this.view = new MonopolyGameView(boardGame, stage, this);

    if (boardGame.isLoadedGame()) {
      boardGame.initializeGame();
    }
  }

  /**
   * Shutdown method to clean up resources.
   */
  public void shutdown() {
    if (isShutDown) {
      return;
    }

    isShutDown = true;
    executorService.shutdown();
    LOGGER.info("Shutting down MonopolyGameController");
  }

  /**
   * Get the current player whose turn it is.
   *
   * @return The current player.
   */
  @Override
  public Player getCurrentPlayer() {
    return boardGame.getCurrentPlayer();
  }

  /**
   * Handles the dice roll and player movement logic.
   * This method is called from the view when the roll button is clicked.
   */
  @Override
  public void rollDice() {
    Player currentPlayer = getCurrentPlayer();

    view.disableRollButton(true);

    if (isPlayerInJail(currentPlayer)) {
      view.showActionMessage(currentPlayer, "InJail");

      if (currentPlayer.getCurrentTile().getAction() instanceof JailTileAction jailAction) {
        jailAction.setOwnerStage(this.stage);
        jailAction.setController(this);
        jailAction.perform(currentPlayer);
      } else {
        boardGame.notifyPlayerSkipTurn(currentPlayer);
        advanceToNextPlayer();
      }
      return;
    } else if (currentPlayer.willWaitTurn()) {
      boardGame.notifyPlayerSkipTurn(currentPlayer);
      currentPlayer.setWaitTurn(false);
      advanceToNextPlayer();
      return;
    }

    int[] diceValues = boardGame.rollDice();
    view.updateDiceDisplay(diceValues[0], diceValues[1]);

    int fromTileId = currentPlayer.getCurrentTile().getTileId();
    Tile destinationTile = calculateDestinationTile(currentPlayer, diceValues[0] + diceValues[1]);
    int toTileId = destinationTile.getTileId();

    checkIfPassedStart(currentPlayer, fromTileId, toTileId);

    currentPlayer.placeOnTile(destinationTile);

    boardGame.notifyPlayerMove(currentPlayer, fromTileId, toTileId, diceValues[0] + diceValues[1]);

    handlePlayerMove(currentPlayer);
  }

  /**
   * Checks if a player is in jail.
   *
   * @param player The player to check.
   * @return true if the player is in jail, false otherwise.
   */
  private boolean isPlayerInJail(Player player) {
    return player.getProperty("inJail") != null && player.getProperty("inJail").equals("true");
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

      switch (action) {
        case PropertyTileAction propertyTileAction -> propertyTileAction.setController(this);
        case ChanceTileAction chanceTileAction -> chanceTileAction.setController(this);
        case TaxTileAction taxTileAction -> taxTileAction.setController(this);
        default -> {
        }
      }

      executorService.submit(() -> {
        try {
          Thread.sleep(1000);

          if (isShutDown) {
            return;
          }

          Platform.runLater(() -> {
            if (isShutDown) {
              return;
            }

            try {
              int fromTileId = player.getCurrentTile().getTileId();
              action.perform(player);

              if (player.isBankrupt()) {
                handlePlayerBankrupt(player);
                boardGame.playerBankrupt(player);
                return;
              }

              int toTileId = player.getCurrentTile().getTileId();

              if (fromTileId != toTileId) {
                boardGame.notifyPlayerMove(player, fromTileId, toTileId, 0);
              }

              view.updatePlayerMoney(player);
              view.updatePlayerProperties(player);
            } catch (Exception e) {
              LOGGER.log(Level.WARNING, "Error during player action", e);
            }
          });

          Thread.sleep(1000);

          if (isShutDown) {
            return;
          }

          Platform.runLater(() -> {
            if (!isShutDown) {
              advanceToNextPlayer();
            }
          });
        } catch (InterruptedException e) {
          LOGGER.log(Level.WARNING, "Thread was interrupted during game action delay", e);
          Thread.currentThread().interrupt();
        }
      });
    } else {
      executorService.submit(() -> {
        try {
          Thread.sleep(2000);
          if (!isShutDown) {
            Platform.runLater(() -> {
              if (!isShutDown) {
                advanceToNextPlayer();
              }
            });
          }
        } catch (InterruptedException e) {
          LOGGER.log(Level.WARNING, "Thread was interrupted during game action delay", e);
          Thread.currentThread().interrupt();
        }
      });
    }
  }

  /**
   * Notifies the view that a player has bankrupted.
   *
   * @param player The player that has bankrupted.
   */
  public void handlePlayerBankrupt(Player player) {
    view.onPlayerBankrupt(player);

    List<Player> activePlayers = boardGame.getActivePlayers();
    if (activePlayers.size() <= 1) {
      if (activePlayers.size() == 1) {
        Player winner = activePlayers.getFirst();
        view.showGameWonMessage(winner);
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
   * Checks whether a player has passed start during the current player turn.
   * If so, performs startAction and updates the view.
   *
   * @param player The player whose turn it is.
   * @param fromTileId Starting tile ID.
   * @param toTileId Destination tile ID.
   */
  private void checkIfPassedStart(Player player, int fromTileId, int toTileId) {
    final int startTileId = 1;

    if (fromTileId > toTileId && toTileId != startTileId) {
      Tile startTile = boardGame.getBoard().getTile(startTileId);
      if (startTile != null && startTile.getAction() instanceof StartTileAction startAction) {
        startAction.perform(player);
        view.updatePlayerMoney(player);
      }
    }
  }

  /**
   * Advances the game to the next player's turn.
   */
  public void advanceToNextPlayer() {
    boardGame.advanceToNextPlayer();
    view.prepareForNextTurn();
    view.disableRollButton(false);
  }

  /**
   * Notifies the view that a property change has occurred for the specified player.
   *
   * @param player The player whose property is being updated.
   */
  public void updatePlayerProperty(Player player) {
    view.onPropertyChange(player);
  }

  /**
   * Notifies the view of the change in the player's money.
   *
   * @param player The player whose money is being updated.
   */
  public void updatePlayerMoney(Player player) {
    view.onMoneyChange(player);
  }

  /**
   * Enables the roll button.
   *
   * @param disabled Not true if the roll button is to be enabled.
   */
  public void enableRollButton(boolean disabled) {
    view.disableRollButton(!disabled);
  }

  /**
   * Returns the stage instance used by this controller.
   *
   * @return The stage.
   */
  public Stage getStage() {
    return stage;
  }

  /**
   * Gets the property action at a specific tileId.
   *
   * @param tileId The tile ID to get the property action from.
   * @return The property action.
   */
  public PropertyTileAction getPropertyAtTile(int tileId) {
    Tile tile = boardGame.getBoard().getTile(tileId);
    if (tile != null && tile.getAction() instanceof PropertyTileAction propertyAction) {
      return propertyAction;
    }
    return null;
  }

  /**
   * Updates the action label with a message string.
   *
   * @param message The message to update the label with.
   */
  public void updateActionLabel(String message) {
    if (view != null) {
      Platform.runLater(() -> view.setActionLabelText(message));
    }
  }

  /**
   * Saves the current game state.
   *
   * @return true if save was successful, false otherwise.
   */
  @Override
  public boolean saveGame() {
    try {
      String saveName = "MonopolyGameSave";
      BoardGameFactory.saveBoardGame(boardGame, saveName);
      new PlayerFileHandler().writeToFile(
          BoardGameFactory.getPlayerSaveFilePath("monopolygame", saveName),
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
  @Override
  public void quitToMenu() {
    DialogService.showQuitConfirmationDialog(stage, () -> {
      stage.close();
      new MainApp().start(new Stage());
    });
  }

  /**
   * Restarts the game with the same players.
   */
  @Override
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
      Player player = new Player(playerNames[i], playerTokens[i], newGame, 200000);
      newGame.addPlayer(player);
    }

    new MonopolyGameController(newGame, newStage, gameVariation);
  }

  /**
   * Returns the game variation.
   *
   * @return The game variation.
   */
  @Override
  public String getGameVariation() {
    return gameVariation;
  }

  /**
   * Converts a tile ID to its corresponding grid coordinates on the game board.
   *
   * @param tileId The ID of the tile to be converted.
   * @return An integer array containing the grid coordinates where
   *         the first element is the row index and the second element is the column index.
   */
  @Override
  public int[] convertTileIdToGridCoordinates(int tileId) {
    final int boardSize = 11;
    tileId = Math.max(1, Math.min(40, tileId));

    if (tileId < 11) {
      return new int[] {boardSize - 1 - (tileId - 1), 0};
    } else if (tileId < 21) {
      return new int[] {0, tileId - 11};
    } else if (tileId < 31) {
      return new int[] {tileId - 21, boardSize - 1};
    } else {
      return new int[] {boardSize - 1, boardSize - 1 - (tileId - 31)};
    }
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
  @Override
  public double[] calculateTokenOffset(int playerIndex, int totalPlayers, double baseRadius) {
    if (totalPlayers <= 0) {
      return new double[] {0, 0};
    }

    playerIndex = Math.max(0, Math.min(totalPlayers - 1, playerIndex));

    int row = playerIndex / 2;
    int col = playerIndex % 2;

    double offsetX = (col * 2 - 0.5) * baseRadius * 0.8;
    double offsetY = (row * 2 - 0.5) * baseRadius * 0.8;

    return new double[] {offsetX, offsetY};
  }
}