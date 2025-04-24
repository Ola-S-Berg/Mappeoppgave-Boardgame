package edu.ntnu.idi.idatt.GameLogic;
import edu.ntnu.idi.idatt.Actions.BackToStartAction;
import edu.ntnu.idi.idatt.Actions.LadderAction;
import edu.ntnu.idi.idatt.Actions.WaitAction;
import java.util.Map;
import java.util.HashMap;

/**
 * Class representing the game board. Consists of a collection of tiles.
 * Also keeps a complete overview of all tiles.
 */
public class Board {
  private Map<Integer, Tile> tiles = new HashMap<>();

  /**
   * Adds a tile to the board.
   * @param tile The tile to add.
   */
  public void addTile(Tile tile) {
    tiles.put(tile.getTileId(), tile);
  }

  /**
   * Accessor that gets the ID (number) of the tile.
   * @param tileId The tile ID represented by an integer.
   * @return The ID.
   */
  public Tile getTile(int tileId) {
    return tiles.get(tileId);
  }

  /**
   * Sets up actions for the first variation of the ladder game.
   * A classic balanced ladder game with a normal amount of actions.
   * @param board The game board to set up.
   */
  public static void setupTileActionsLadderGame1(Board board) {
    board.getTile(5).setAction(new LadderAction(17, "up"));
    board.getTile(12).setAction(new LadderAction(49, "up"));
    board.getTile(21).setAction(new LadderAction(41, "up"));
    board.getTile(43).setAction(new LadderAction(61, "up"));
    board.getTile(55).setAction(new LadderAction(87, "up"));
    board.getTile(65).setAction(new LadderAction(84, "up"));

    board.getTile(25).setAction(new LadderAction(7, "down"));
    board.getTile(38).setAction(new LadderAction(1, "down"));
    board.getTile(48).setAction(new LadderAction(13, "down"));
    board.getTile(70).setAction(new LadderAction(30, "down"));
    board.getTile(79).setAction(new LadderAction(27, "down"));
    board.getTile(89).setAction(new LadderAction(53, "down"));

    board.getTile(37).setAction(new WaitAction());
    board.getTile(54).setAction(new WaitAction());
    board.getTile(71).setAction(new WaitAction());

    board.getTile(10).setAction(new BackToStartAction());
    board.getTile(81).setAction(new BackToStartAction());
  }

  /**
   * Sets up actions for the second variation of the ladder game.
   * Increased amount of actions from the first variation.
   * @param board The board game to set up.
   */
  public static void setupTileActionsLadderGame2(Board board) {
    board.getTile(5).setAction(new LadderAction(17, "up"));
    board.getTile(12).setAction(new LadderAction(49, "up"));
    board.getTile(14).setAction(new LadderAction(47, "up"));
    board.getTile(21).setAction(new LadderAction(41, "up"));
    board.getTile(43).setAction(new LadderAction(61, "up"));
    board.getTile(52).setAction(new LadderAction(72, "up"));
    board.getTile(55).setAction(new LadderAction(87, "up"));
    board.getTile(65).setAction(new LadderAction(84, "up"));

    board.getTile(25).setAction(new LadderAction(7, "down"));
    board.getTile(38).setAction(new LadderAction(1, "down"));
    board.getTile(42).setAction(new LadderAction(2, "down"));
    board.getTile(46).setAction(new LadderAction(15, "down"));
    board.getTile(48).setAction(new LadderAction(13, "down"));
    board.getTile(64).setAction(new LadderAction(24, "down"));
    board.getTile(70).setAction(new LadderAction(30, "down"));
    board.getTile(79).setAction(new LadderAction(27, "down"));
    board.getTile(82).setAction(new LadderAction(63, "down"));
    board.getTile(89).setAction(new LadderAction(53, "down"));

    board.getTile(18).setAction(new WaitAction());
    board.getTile(28).setAction(new WaitAction());
    board.getTile(37).setAction(new WaitAction());
    board.getTile(45).setAction(new WaitAction());
    board.getTile(54).setAction(new WaitAction());
    board.getTile(58).setAction(new WaitAction());
    board.getTile(71).setAction(new WaitAction());
    board.getTile(75).setAction(new WaitAction());
    board.getTile(88).setAction(new WaitAction());

    board.getTile(10).setAction(new BackToStartAction());
    board.getTile(34).setAction(new BackToStartAction());
    board.getTile(56).setAction(new BackToStartAction());
    board.getTile(68).setAction(new BackToStartAction());
    board.getTile(81).setAction(new BackToStartAction());
  }

  /**
   * Sets up the actions for the third variant of the Ladder game.
   * Flips all up ladders from the second variation to down.
   * A diabolical version of the classic ladder game.
   * @param board The board game to set up.
   */
  public static void setupTileActionsLadderGame3(Board board) {

    board.getTile(17).setAction(new LadderAction(5, "down"));
    board.getTile(25).setAction(new LadderAction(7, "down"));
    board.getTile(38).setAction(new LadderAction(1, "down"));
    board.getTile(41).setAction(new LadderAction(21, "down"));
    board.getTile(42).setAction(new LadderAction(2, "down"));
    board.getTile(46).setAction(new LadderAction(15, "down"));
    board.getTile(48).setAction(new LadderAction(13, "down"));
    board.getTile(47).setAction(new LadderAction(14, "down"));
    board.getTile(49).setAction(new LadderAction(12, "down"));
    board.getTile(61).setAction(new LadderAction(43, "down"));
    board.getTile(64).setAction(new LadderAction(24, "down"));
    board.getTile(70).setAction(new LadderAction(30, "down"));
    board.getTile(72).setAction(new LadderAction(52, "down"));
    board.getTile(79).setAction(new LadderAction(27, "down"));
    board.getTile(82).setAction(new LadderAction(63, "down"));
    board.getTile(84).setAction(new LadderAction(65, "down"));
    board.getTile(87).setAction(new LadderAction(55, "down"));
    board.getTile(89).setAction(new LadderAction(53, "down"));

    board.getTile(18).setAction(new WaitAction());
    board.getTile(28).setAction(new WaitAction());
    board.getTile(37).setAction(new WaitAction());
    board.getTile(45).setAction(new WaitAction());
    board.getTile(54).setAction(new WaitAction());
    board.getTile(58).setAction(new WaitAction());
    board.getTile(71).setAction(new WaitAction());
    board.getTile(75).setAction(new WaitAction());
    board.getTile(88).setAction(new WaitAction());

    board.getTile(10).setAction(new BackToStartAction());
    board.getTile(34).setAction(new BackToStartAction());
    board.getTile(56).setAction(new BackToStartAction());
    board.getTile(68).setAction(new BackToStartAction());
    board.getTile(81).setAction(new BackToStartAction());
  }
}

