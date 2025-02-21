package edu.ntnu.idi.idatt;
import java.util.Map;
import java.util.HashMap;

/**
 * Class representing the game board. Consists of a collection of tiles.
 * Also keeps a complete overview of all tiles.
 * Note: Wait with implementing until the creation and implementation of class Tile and TileAction.
 */
public class Board {
  private Map<Integer, Tile> tiles;

  public void addTile() {

  }

  public Tile getTile(int tileId) {
    return tiles.get(tileId);
  }

}
