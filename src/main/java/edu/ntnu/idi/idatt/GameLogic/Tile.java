package edu.ntnu.idi.idatt.GameLogic;

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
   * Checks which tile the player has landed on.
   * @param player The player that lands.
   */
  public void landPlayer(Player player) {
    System.out.println(player.getName() + " lands at " + tileId);
  }

  public void leavePlayer(Player player){
    System.out.println(player.getName() + " leaves at " + tileId);
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
