package edu.ntnu.idi.idatt;
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

  /**
   * Adds a player when called upon.
   * @param player The player to add.
   */
  public void addPlayer (Player player) {
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

    //Creates a 9x10 board and gives each tile a unique tile number (ID).
    for (int i = 1; i <= 9; i++) {
      for (int j = 1; j <= 10; j++) {
        int currentId = (i - 1) * 10 + j; //Equation that finds the tile number of the tile created.

        //If statement that finds if the next tile is 90, if not sets the next tile to ID to the current til ID +1.
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
    for (Player player : players) {
      player.placeOnTile(board.getTile(1));
    }

    while (getWinner() == null) {
      for (Player player : players) {
        currentPlayer = player;
        int steps = dice.roll();
        player.move(steps);

        if (getWinner() != null) {
          break;
        }
      }
    }
  }

  /**
   * Accessor method that gets the board.
   * @return The board.
   */
  public Board getBoard() {
    return board;
  }
}
