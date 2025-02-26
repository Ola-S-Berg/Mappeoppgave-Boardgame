package edu.ntnu.idi.idatt;
import java.util.Map;
import java.util.HashMap;

/**
 * Class representing the game board. Consists of a collection of tiles.
 * Also keeps a complete overview of all tiles.
 */

public class Board {
  private Map<Integer, Tile> tiles = new HashMap<>();

  public void addTile(Tile tile) {
    tiles.put(tile.getTileId(), tile);
  }

  public Tile getTile(int tileId) {
    return tiles.get(tileId);
  }

}


