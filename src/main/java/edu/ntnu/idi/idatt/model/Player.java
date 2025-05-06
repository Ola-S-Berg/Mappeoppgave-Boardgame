package edu.ntnu.idi.idatt.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a player in the game
 */
public class Player {
  private final String name;
  private Tile currentTile;
  private BoardGame game;
  private final String token;
  private boolean waitTurn = false;
  private final Map<String, String> properties = new HashMap<>();


  /**
   * The constructor for Player with a game instance.
   *
   * @param name name The name of the player.
   * @param token The player's token.
   */
  public Player(String name, String token, BoardGame game) {
    this.name = name;
    this.game = game;
    this.currentTile = null;
    this.token = (token != null) ? token : "Default";
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
    if (currentTile != null) {
      currentTile.leavePlayer(this);
    }

    this.currentTile = tile;

    if (tile != null) {
      tile.landPlayer(this);
    }
  }

  /**
   * Moves the player a certain amount of tile based on pips rolled from dice.
   * Notifies BoardGame about movement and game win.
   * @param steps The number of steps the player moves.
   */
  public void move (int steps) {
    if (waitTurn) {
      System.out.println(name + " Skips this turn");
      waitTurn = false;
      return;
    }

    if (currentTile == null) {
      placeOnTile(game.getBoard().getTile(1));
      return;
    }

    int fromTileId = currentTile.getTileId();

    Tile destinationTile = currentTile;
    for (int i = 0; i < steps; i++) {
      if (destinationTile.getNextTile() != null) {
        destinationTile = destinationTile.getNextTile();
      }
    }

    placeOnTile(destinationTile);

    game.notifyPlayerMove(this, fromTileId, currentTile.getTileId(), steps);

    if (currentTile.getTileId() == 90) {
      game.notifyGameWon(this);
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
   * Sets whether the player should skip their next turn.
   * @param skip True if the player has landed on a skip turn tile.
   */
  public void setWaitTurn(boolean skip) {
    this.waitTurn = skip;
  }

  /**
   * Checks if the player skips their next turn.
   * @return True if the player skips their next turn.
   */
  public boolean willWaitTurn() {
    return waitTurn;
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

  public String getToken() {
    return token;
  }

  /**
   * Stores a temporary property for the player.
   * @param key The property key.
   * @param value The property value.
   */
  public void setProperty(String key, String value) {
    properties.put(key, value);
  }

  /**
   * Gets a temporary property value.
   * @param key The property key.
   * @return The property value, or null if not found.
   */
  public String getProperty(String key) {
    return properties.get(key);
  }
}

