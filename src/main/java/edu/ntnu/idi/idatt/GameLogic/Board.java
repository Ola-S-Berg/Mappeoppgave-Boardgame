package edu.ntnu.idi.idatt.GameLogic;
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
}

