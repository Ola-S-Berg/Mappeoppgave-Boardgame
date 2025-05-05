package edu.ntnu.idi.idatt.GameLogic;

import edu.ntnu.idi.idatt.Actions.TileAction;

/**
 * Class representing a tile on the game board.
 */
public class Tile {
  private Tile nextTile;
  private final int tileId;
  private TileAction action;

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

  /**
   * Logs when a player leaves a tile.
   * @param player The player that leaves.
   */
  public void leavePlayer(Player player){
    System.out.println(player.getName() + " leaves at " + tileId);
  }

  /**
   * Sets the tile in sequence.
   * @param nextTile The next tile.
   */
  public void setNextTile (Tile nextTile ) {
    this.nextTile = nextTile;
  }

  /**
   * Gets the tileID.
   * @return The tileID.
   */
  public int getTileId() {
    return tileId;
  }

  /**
   * Gets the next tile in sequence.
   * @return The next tile.
   */
  public Tile getNextTile() {
    return nextTile;
  }

  /**
   * Sets an action for this tile.
   * @param action The action to be performed.
   */
  public void setAction(TileAction action) {
    this.action = action;
  }

  /**
   * Gets the action associated with this tile.
   * @return The tile action.
   */
  public TileAction getAction() {
    return action;
  }
}
