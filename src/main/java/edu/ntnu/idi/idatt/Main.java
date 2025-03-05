package edu.ntnu.idi.idatt;
import java.util.Scanner;

/**
 * The class that plays the game. (Name subject to change).
 */
public class Main {

  /**
   * Creates a new board game, board and dice.
   * Asks for a number of players and their names.
   * Plays the game.
   * @param args The arguments.
   */
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    BoardGame game = new BoardGame();
    game.createBoard();
    game.createDice();

    System.out.println("How many players are playing?");
    int players = scanner.nextInt();
    scanner.nextLine();

    for (int i = 1; i <= players; i++) {
      System.out.println("Player " + i + ":");
      String playerName = scanner.nextLine();
      Player player = new Player(playerName, game);
      game.addPlayer(player);
    }

    System.out.println("Starting game...");
    game.play();

    Player winner = game.getWinner();
    System.out.println("Winner is " + winner.getName());

    scanner.close();
  }
}