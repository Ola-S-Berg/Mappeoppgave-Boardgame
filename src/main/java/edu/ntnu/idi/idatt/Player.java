package edu.ntnu.idi.idatt;

/**
 * Class representing a player in the game
 */
public class Player {
  private String name;
  private Tile currentTile;
  private BoardGame game;

  /**
   * The constructor for Player.
   *
   * @param name name The name of the player.
   * @param game The game the player is connected to.
   */
  public Player(String name, BoardGame game) {
    this.name = name;
    this.game = game;
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
    if (currentTile == null) {
      currentTile = game.getBoard().getTile(1);
    }

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

  public String getName() {
    return name;
  }

  /**
   * Accessor method that gets the board game instance a player is playing on.
   * @return The board game.
   */
  public BoardGame getGame() {
    return game;
  }
}
