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
   *                    Default value is 'null' as the player starts outside the board.
   */
  public Player(String name, Tile currentTile) {
    this.name = name;
    this.currentTile = null;
  }

  public void placeOnTile (Tile tile) {

  }

  public void move (int steps) {
    
  }
}
