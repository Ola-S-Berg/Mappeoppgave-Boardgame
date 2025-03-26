package edu.ntnu.idi.idatt.GameLogic;

/**
 * Class representing a player in the game
 */
public class Player {
  private String name;
  private Tile currentTile;
  private BoardGame game;

  /**
   * The constructor for Player with a game instance.
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
   * The constructor for Player without a game instance.
   * Used when reading players from a file and the game will be assigned later.
   * @param name The name of the player.
   */
  public Player(String name) {
    this.name = name;
    this.game = null;
    this.currentTile = null;
  }

  /**
   * Sets the game instance for this player.
   * @param game The game associated with this player.
   */
  public void setGame(BoardGame game) {
    this.game = game;
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
   * Returns a string representation of the Player object, including the player's name
   * and the ID of the tile the player is currently standing on.
   *
   * @return A string representation of the Player object, displaying the name and current tile ID.
   */
  @Override
  public String toString() {
      return "Player{name='" + name + "', tile=" + currentTile.getTileId() + "}";
  }

  /**
   * Accessor method that gets the board game instance a player is playing on.
   * @return The board game.
   */
  public BoardGame getGame() {
    return game;
  }
}

