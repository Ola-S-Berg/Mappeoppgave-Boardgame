package edu.ntnu.idi.idatt;
import java.util.List;

/**
 * Class responsible for the game. Sets up the board, sets up dice, registers players
 * and plays the game by iterating over the players, letting them roll dice and move.
 */
public class BoardGame {
  private Board board;
  private Player currentPlayer;
  private List<Player> players;
  private Dice dice;

  public BoardGame() {
    createDice();
  }

  //Fill in methods as
  public void addPlayer(String name) {
  }

  public void createBoard() {

  }

  public void createDice() {
    this.dice = new Dice(2);
  }

  public void play() {

  }

  public Player getWinner() {
    return null;
  }
}
