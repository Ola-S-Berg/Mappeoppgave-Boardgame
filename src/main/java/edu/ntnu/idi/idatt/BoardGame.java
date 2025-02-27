package edu.ntnu.idi.idatt;
import java.util.List;
import java.util.ArrayList;

/**
 * Class responsible for the game. Sets up the board, sets up dice, registers players
 * and plays the game by iterating over the players, letting them roll dice and move.
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
   * Creates a new board.
   */
  public void createBoard() {
    board = new Board();
  }

  /**
   * Creates a number of dice.
   */
  public void createDice() {
    dice = new Dice(2);
  }

  public void play() {

  }

  public Player getWinner() {
    return null;
  }
}
