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
   * Sets up all the tile actions on the board.
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

  public static void setupTileActionsLadderGame2(Board board) {
    board.getTile(6).setAction(new LadderAction(17, "up"));
    board.getTile(13).setAction(new LadderAction(49, "up"));
    board.getTile(22).setAction(new LadderAction(41, "up"));
    board.getTile(44).setAction(new LadderAction(61, "up"));
    board.getTile(56).setAction(new LadderAction(87, "up"));
    board.getTile(66).setAction(new LadderAction(84, "up"));

    board.getTile(26).setAction(new LadderAction(7, "down"));
    board.getTile(39).setAction(new LadderAction(1, "down"));
    board.getTile(49).setAction(new LadderAction(13, "down"));
    board.getTile(71).setAction(new LadderAction(30, "down"));
    board.getTile(77).setAction(new LadderAction(27, "down"));
    board.getTile(88).setAction(new LadderAction(53, "down"));

    board.getTile(37).setAction(new WaitAction());
    board.getTile(54).setAction(new WaitAction());
    board.getTile(71).setAction(new WaitAction());

    board.getTile(10).setAction(new BackToStartAction());
    board.getTile(81).setAction(new BackToStartAction());
  }

  /**
   * Sets up the actions for the third variant of the Ladder game.
   * @param board
   */
  public static void setupTileActionsLadderGame3(Board board) {
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
}

