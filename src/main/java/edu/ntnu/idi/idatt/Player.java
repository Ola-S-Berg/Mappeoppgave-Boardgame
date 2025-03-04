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

  /**
   * Moves the player a certain amount of tile based on pips rolled from dice.
   * @param steps The amount of steps the player moves.
   */
  public void move (int steps) {
    for (int i = 0; i < steps; i++) {
      if (currentTile.getNextTile() != null) {
        currentTile = currentTile.getNextTile();
      }
    }
  }

  /**
   * Gets the current tile the player is standing on.
   * @return The current tile.
   */
  public Tile getCurrentTile() {
    return currentTile;
  }
}
