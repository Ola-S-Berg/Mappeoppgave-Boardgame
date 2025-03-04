package edu.ntnu.idi.idatt;

/**
 * Class representing a tile on the game board.
 */
public class Tile {
  private Tile nextTile;
  private int tileId;
  //private TileAction landAction;

  public Tile(int tileId) {
    this.tileId = tileId;
  }

  /**
   *
   * @param player
   */
  public void landPlayer(Player player) {
    System.out.println(player.);
  }
  public void leavePlayer(Player player){

  }

  public void setNextTile (Tile nextTile ) {
    this.nextTile = nextTile;
  }

  public int getTileId() {
    return tileId;
  }

  public Tile getNextTile() {
    return nextTile;
  }
}
