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
  private List<Player> players = new ArrayList<>();
  private Dice dice;
  private BoardFileHandler fileHandler;
  private String variantName;
  private List<BoardGameObserver> observers = new ArrayList<>();

  public BoardGame() {
    fileHandler = new BoardFileHandler();
  }

  /**
   * Registers an observer to receive game state updates.
   * @param observer The observer to register.
   */
  public void addObserver (BoardGameObserver observer) {
    if (observer != null) {
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
  private void notifyPlayerMove(Player player, int fromTileId, int toTileId, int diceValue) {
    for (BoardGameObserver observer : observers) {
      observer.onPlayerMove(player, fromTileId, toTileId, diceValue);
    }
  }

  /**
   * Notifies all observers when a player has won the game.
   * @param player The player that won the game.
   */
  private void notifyGameWon(Player player) {
    for (BoardGameObserver observer : observers) {
      observer.onGameWon(player);
    }
  }

  /**
   * Notifies all observers when a player skips their turn.
   * @param player The player who skipped their turn.
   */
  private void notifyPlayerSkipTurn(Player player) {
    for (BoardGameObserver observer : observers) {
      observer.onPlayerSkipTurn(player);
    }
  }

  /**
   * Notifies all observers when the current player changes.
   * @param player The new current player.
   */
  private void notifyCurrentPlayerChanged(Player player) {
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
   * Creates a new board. The board takes the form of a 9x10 snake game board.
   */
  public void createBoard() {
    board = new Board();

    //Adds 90 tiles
    for (int i = 1; i <= 90; i++) {
      board.addTile(new Tile(i));
    }

    if (variantName != null) {
      switch (variantName) {
        case "ladderGame1":
          Board.setupTileActionsLadderGame1(board);
          break;
        case "ladderGame2":
          Board.setupTileActionsLadderGame2(board);
          break;
        case "ladderGame3":
          Board.setupTileActionsLadderGame3(board);
          break;
        default:
          System.out.println("Unknown game variant: " + variantName);
      }
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
  }

  /**
   * Creates a number of dice.
   */
  public void createDice() {

    dice = new Dice(2);
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
   * Plays the game by iterating over players until a winner is found.
   */
  public void play () {
    int roundNumber = 1;

    for (Player player : players) {
      player.placeOnTile(board.getTile(1));
    }

    while (getWinner() == null) {
      System.out.println("Round " + roundNumber);

      for (Player player : players) {
        currentPlayer = player;
        int steps = dice.roll();
        System.out.println(currentPlayer.getName() + " rolled " + steps);
        player.move(steps);

        System.out.println(player.getName() + " is now on tile " + player.getCurrentTile().getTileId());

        if (getWinner() != null) {
          break;
        }
      }
      roundNumber++;
    }
  }

  public void saveBoard(String filename) {
    try {
      fileHandler.writeToFile(filename, List.of(this));
      System.out.println("Board saved to file: " + filename);
    } catch (IOException e) {
      System.err.println("Error saving board to file: " + filename);
    }
  }

  public void loadBoard(String filename) {
    try {
      List<BoardGame> loadedBoards = fileHandler.readFromFile(filename);
      if (!loadedBoards.isEmpty()) {
        BoardGame loadedGame = loadedBoards.get(0);
        this.board = loadedGame.getBoard();
        System.out.println("Board loaded from file: " + filename);
      }
    } catch (IOException e) {
      System.err.println("Error loading board from file: " + filename);
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

  public String getVariantName() {
    return variantName;
  }

  public void setVariantName(String variantName) {
    this.variantName = variantName;
  }
}



