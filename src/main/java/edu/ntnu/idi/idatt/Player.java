package edu.ntnu.idi.idatt;

/**
 * Class representing a player in the game
 */
public class Player {
  private String name;
  private Tile currentTile;

  /**
   * The constructor for Player.
   * @param name The name of the player.
   * @param currentTile The current tile the player is on.
   *                    Default value is 'null' until the game starts.
   */
  public Player(String name, Tile currentTile) {
    this.name = name;
    this.currentTile = null;
  }

  /**
   * Places the player on a specific tile.
   * @param tile The tile to place the player on.
   */
  public void placeOnTile (Tile tile) {
    this.currentTile = tile;
  }

  public void move (int steps) {
    for (int i = 0; i < steps; i++) {
      if (currentTile.getNextTile() != null) {
        currentTile = currentTile.getNextTile();
      }
    }
  }
}
