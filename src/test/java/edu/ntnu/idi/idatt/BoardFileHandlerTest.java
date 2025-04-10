package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.GameLogic.BoardGame;

public class BoardFileHandlerTest {
  public static void main(String[] args) {
    BoardGame game = new BoardGame();
    game.createBoard();

    String filename = "board.json";
    game.saveBoard(filename);

    BoardGame loadGame = new BoardGame();
    loadGame.loadBoard(filename);

    if (loadGame.getBoard() != null) {
      System.out.println("Board loaded successfully");
      new java.io.File(filename).delete();
    }
  }
}
