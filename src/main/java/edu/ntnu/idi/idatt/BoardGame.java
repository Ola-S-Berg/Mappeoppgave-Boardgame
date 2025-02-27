package edu.ntnu.idi.idatt;
import java.util.List;
import java.util.ArrayList;

/**
 * The class that holds
 */
public class BoardGame {
  private Board board;
  private Player currentPlayer;
  private List<Player> players = new ArrayList<>();
  private Dice dice;

  public void addPlayer (Player player) {
    players.add(player);
  }

  public void createBoard() {
    board = new Board();
  }

  public void createDice() {
    dice = new Dice(2);
  }
}
