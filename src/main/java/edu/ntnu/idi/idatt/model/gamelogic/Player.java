package edu.ntnu.idi.idatt.model.gamelogic;

import edu.ntnu.idi.idatt.model.actions.monopolygame.PropertyTileAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h1>Player Class</h1>
 *
 * <p>Represents a player in a board game. The Player class contains all player-related
 * characteristics and behaviors including movement across the game board, financial transactions,
 * property ownership and game state tracking</p>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Player identification through name and token</li>
 *   <li>Movement mechanics on the game board</li>
 *   <li>System for financial transactions</li>
 *   <li>Property ownership tracking</li>
 *   <li>Game state tracking (bankruptcy, jail status, etc.)</li>
 *   <li>Turn management mechanics</li>
 * </ul>
 *
 * @author Ola Syrstad Berg
 * @author Markus Øyen Lund
 * @since v1.1.0
 */
public class Player {
  private final String name;
  private Tile currentTile;
  private BoardGame game;
  private final String token;
  private boolean waitTurn = false;
  private boolean bankrupt = false;
  private final Map<String, String> properties = new HashMap<>();
  private int money;
  private final List<PropertyTileAction> ownedProperties = new ArrayList<>();

  /**
   * Constructs a new player with specified attributes and connects them to a game instance.
   *
   * @param name The name of the player.
   * @param token The player's token identifier.
   * @param game The board game instance this player is participating in.
   * @param startingMoney The initial amount of money the player begins with.
   */
  public Player(String name, String token, BoardGame game, int startingMoney) {
    this.name = name;
    this.game = game;
    this.currentTile = null;
    this.token = (token != null) ? token : "Default";
    this.money = startingMoney;
  }

  /**
   * Sets the game instance for this player.
   *
   * @param game The game associated with this player.
   */
  public void setGame(BoardGame game) {
    this.game = game;
  }

  /**
   * Places the player on a specific tile on the game board and removes the player
   * from the previous tile if it exists.
   *
   * @param tile The tile to place the player on.
   */
  public void placeOnTile(Tile tile) {
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
   *
   * @param steps The number of steps the player moves.
   */
  public void move(int steps) {
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
   * Adds money to the player's balance.
   *
   * @param amount The amount of money to add.
   */
  public void addMoney(int amount) {
    this.money += amount;
    System.out.println(name + " received " + amount + ". New balance: " + money);
  }

  /**
   * Deducts money from the player's balance.
   *
   * @param amount The amount of money to deduct.
   * @return True if payment successful, false otherwise.
   */
  public boolean payMoney(int amount) {
    if (money >= amount) {
      money -= amount;
      System.out.println(name + " paid " + amount + ". New balance: " + money);
      return true;
    } else {
      System.out.println(name + " can't afford to pay " + amount + ". Current balance: " + money);
      declareBankrupt();
      return false;
    }
  }

  /**
   * Transfers money from this player to another player.
   *
   * @param recipient The player to transfer money to.
   * @param amount The amount of money to transfer.
   * @return True if payment successful, false otherwise.
   */
  public boolean payPlayer(Player recipient, int amount) {
    if (payMoney(amount)) {
      recipient.addMoney(amount);
      return true;
    }

    declareBankrupt();
    return false;
  }

  /**
   * Adds a property to the player's owned properties.
   *
   * @param property The property to add.
   */
  public void addProperty(PropertyTileAction property) {
    ownedProperties.add(property);
    property.setOwner(this);
  }

  /**
   * Releases the player from jail.
   */
  public void releaseFromJail() {
    setProperty("inJail", "false");
    System.out.println(name + " has been released from jail.");
  }

  /**
   * Checks if the player is bankrupt.
   *
   * @return True if the player is bankrupt, false otherwise.
   */
  public boolean isBankrupt() {
    return bankrupt;
  }

  /**
   * Declares the player bankrupt, updates their status, releases owned properties,
   * notifies the game and removes them from the game.
   */
  public void declareBankrupt() {
    this.bankrupt = true;
    System.out.println(name + " has gone bankrupt and is out of the game");

    for (PropertyTileAction property : ownedProperties) {
      property.setOwner(null);
    }

    ownedProperties.clear();

    if (game != null) {
      game.playerBankrupt(this);
    }
  }

  /**
   * Gets the current tile the player is standing on.
   *
   * @return The current tile.
   */
  public Tile getCurrentTile() {
    return currentTile;
  }

  /**
   * Gets the name of the player.
   *
   * @return The name of the player.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets whether the player should skip their next turn.
   *
   * @param skip True if the player has landed on a skip turn tile.
   */
  public void setWaitTurn(boolean skip) {
    this.waitTurn = skip;
  }

  /**
   * Checks if the player will skip their next turn.
   *
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
   * Gets the board game instance a player is participating in.
   *
   * @return The board game.
   */
  public BoardGame getGame() {
    return game;
  }

  /**
   * Gets the player's token identifier.
   *
   * @return The token.
   */
  public String getToken() {
    return token;
  }

  /**
   * Stores property value in the player's property map.
   *
   * @param key The property key.
   * @param value The property value.
   */
  public void setProperty(String key, String value) {
    properties.put(key, value);
  }

  /**
   * Gets a property value from the player's property map.
   *
   * @param key The property key.
   * @return The property value, or null if not found.
   */
  public String getProperty(String key) {
    return properties.get(key);
  }

  /**
   * Gets a list of all properties owned by this player.
   *
   * @return The list of properties owned by this player.
   */
  public List<PropertyTileAction> getOwnedProperties() {
    return new ArrayList<>(ownedProperties);
  }

  /**
   * Gets the player's current money balance.
   *
   * @return The player's current money.
   */
  public int getMoney() {
    return money;
  }
}

