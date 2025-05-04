package edu.ntnu.idi.idatt.GameLogic;

import edu.ntnu.idi.idatt.Filehandling.BoardFileHandler;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * The class that handles information about the game.
 */
public class BoardGame {
  private Board board;
  private Player currentPlayer;
  private final List<Player> players = new ArrayList<>();
  private Dice dice;
  private final BoardFileHandler fileHandler;
  private String variantName;
  private final List<BoardGameObserver> observers = new ArrayList<>();

  public BoardGame() {
    fileHandler = new BoardFileHandler();
  }

  /**
   * Registers an observer to receive game state updates.
   * @param observer The observer to register.
   */
  public void addObserver (BoardGameObserver observer) {
   if (observer == null) {
     throw new NullPointerException("Observer cannot be null");
    }

    observers.add(observer);
  }

  /**
   * Removes an observer from the list of registered observers.
   * @param observer The observer to remove.
   */
  public void removeObserver (BoardGameObserver observer) {
    observers.remove(observer);
  }

  /**
   * Notifies all observers when a player has moved.
   * @param player The player who moved.
   * @param fromTileId The tile ID the player moved from.
   * @param toTileId The tile ID the player moved to.
   * @param diceValue The value rolled on the die.
   */
  public void notifyPlayerMove(Player player, int fromTileId, int toTileId, int diceValue) {
    for (BoardGameObserver observer : observers) {
      observer.onPlayerMove(player, fromTileId, toTileId, diceValue);
    }
  }

  /**
   * Notifies all observers when a player has won the game.
   * @param player The player that won the game.
   */
  public void notifyGameWon(Player player) {
    for (BoardGameObserver observer : observers) {
      observer.onGameWon(player);
    }
  }

  /**
   * Notifies all observers when a player skips their turn.
   * @param player The player who skipped their turn.
   */
  public void notifyPlayerSkipTurn(Player player) {
    for (BoardGameObserver observer : observers) {
      observer.onPlayerSkipTurn(player);
    }
  }

  /**
   * Notifies all observers when the current player changes.
   * @param player The new current player.
   */
  public void notifyCurrentPlayerChanged(Player player) {
    for (BoardGameObserver observer : observers) {
      observer.onCurrentPlayerChanged(player);
    }
  }

  /**
   * Adds a player when called upon.
   * @param player The player to add.
   */
  public void addPlayer(Player player) {
    if (player == null) {
        throw new NullPointerException("Player cannot be null.");
    }
    players.add(player);
}

  /**
   * Creates an instance of the Board.
   * Adds 90 tiles, with unique identifiers ranging from 1 to 90.
   * Links the tiles in sequential order, with exceptions for the last tile.
   * Configures the board's tile actions based on the game variant, if specified.
   */
  public void createBoard() {
    board = new Board();

    //Adds 90 tiles
    for (int i = 1; i <= 90; i++) {
      board.addTile(new Tile(i));
    }

    //Creates a 9x10 board and gives each tile a unique tile number (ID).
    for (int i = 1; i <= 9; i++) {
      for (int j = 1; j <= 10; j++) {
        int currentId = (i - 1) * 10 + j; //Equation that finds the tile number of the tile created.

        //If statement that finds if the next tile is 90, if not sets the next tile to ID to the current tile ID +1.
        int nextId;
        if (i == 9 && j == 10) {
          nextId = -1;
        } else {
          nextId = currentId + 1;
        }

        //Links the tiles in order if the tile reached is not 90.
        if (nextId != -1) {
          Tile currentTile = board.getTile(currentId);
          Tile nextTile = board.getTile(nextId);
          currentTile.setNextTile(nextTile);
        }
      }
    }

    if (variantName != null) {
      switch (variantName) {
        case "ladderGame":
          Board.setupTileActionsLadderGame(board);
          break;
        case "ladderGameAdvanced":
          Board.setupTileActionsLadderGameAdvanced(board);
          break;
        case "ladderGameExtreme":
          Board.setupTileActionsLadderGameExtreme(board);
          break;
        default:
          System.out.println("Unknown game variant: " + variantName);
      }
    }

  }

  /**
   * Creates a number of dice.
   */
  public void createDice() {

    dice = new Dice(1);
  }

  /**
   * Gets the winner of the game.
   * @return The name of the player that won.
   */
  public Player getWinner() {
    for (Player player : players) {
      if (player.getCurrentTile().getTileId() == 90) {
        return player;
      }
    }
    return null;
  }

  /**
   * Iterates through players and managing game rounds until a winner is determined. Handles player
   * movements, skipping turns, and notifications to observers for updates
   * such as player moves, turn changes, and game completion.
   */
  public void play () {
    int roundNumber = 1;

    for (Player player : players) {
      player.placeOnTile(board.getTile(1));
    }

    while (getWinner() == null) {
      System.out.println("Round " + roundNumber);

      for (Player player : players) {
        Player previousPlayer = currentPlayer;
        currentPlayer = player;

        if (previousPlayer != currentPlayer) {
          notifyCurrentPlayerChanged(currentPlayer);
        }

        if (player.willWaitTurn()) {
          System.out.println(player.getName() + " will skip their turn");
          notifyPlayerSkipTurn(player);
          player.setWaitTurn(false);
          continue;
        }

        int steps = dice.roll();
        int fromTileId = player.getCurrentTile().getTileId();
        System.out.println(currentPlayer.getName() + " rolled " + steps);
        player.move(steps);
        int toTileId = player.getCurrentTile().getTileId();

        notifyPlayerMove(player, fromTileId, toTileId, steps);

        System.out.println(player.getName() + " is now on tile " + player.getCurrentTile().getTileId());

        if (getWinner() != null) {
          notifyGameWon(getWinner());
          break;
        }
      }
      roundNumber++;
    }
  }

  /**
   * Accessor method that gets the dice.
   * @return The dice.
   */
  public Dice getDice() {

    return dice;
  }

  /**
   * Accessor method that gets the current player.
   * @return The current player.
   */
  public List<Player> getPlayers() {

    return players;
  }

  /**
   * Accessor method that gets the board.
   * @return The board.
   */
  public Board getBoard() {
    return board;
  }

  /**
   * Retrieves the name of the game variant currently in use.
   *
   * @return The name of the variant as a String.
   */
  public String getVariantName() {
    return variantName;
  }

  /**
   * Sets the name of the game variant to the specified value.
   *
   * @param variantName The name of the variant to set.
   */
  public void setVariantName(String variantName) {
    this.variantName = variantName;
  }
}