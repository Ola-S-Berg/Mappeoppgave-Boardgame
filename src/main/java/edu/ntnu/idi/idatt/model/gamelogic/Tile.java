package edu.ntnu.idi.idatt.model.gamelogic;

import edu.ntnu.idi.idatt.model.actions.TileAction;
import edu.ntnu.idi.idatt.model.actions.monopolygame.PropertyTileAction;

/**
 * <h1>Tile Class</h1>
 *
 * <p>Represents a single tile on a board game. Each tile has a unique identifier, may have an
 * associated action that executes when a player lands on it, and is connected to other tiles
 * to form the game board path.</p>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Unique identification through tileId</li>
 *   <li>Sequential linking to form the game board path</li>
 *   <li>Flexible action system that triggers when players land on tiles</li>
 *   <li>Support for both generic and game-specific behaviors</li>
 *   <li>Named representation for monopoly tile types</li>
 * </ul>
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
 */
public class Tile {
  private Tile nextTile;
  private final int tileId;
  private TileAction action;

  /**
   * Constructs a new tile with the specified ID.
   *
   * @param tileId The unique ID for this tile.
   */
  public Tile(int tileId) {
    this.tileId = tileId;
  }

  /**
   * Processes a player landing on this tile. Only performs actions for Monopoly.
   *
   * @param player The player that lands.
   */
  public void landPlayer(Player player) {
    System.out.println(player.getName() + " lands at " + tileId);
    if (action != null) {
      BoardGame game = player.getGame();
      String variantName = game != null ? game.getVariantName() : null;

      if (variantName != null && variantName.equals("monopolyGame")) {
        action.perform(player);
      }
    }
  }

  /**
   * Processes when a player leaves this tile.
   *
   * @param player The player that leaves.
   */
  public void leavePlayer(Player player) {
    System.out.println(player.getName() + " leaves at " + tileId);
  }

  /**
   * Sets the tile in the game board sequence.
   *
   * @param nextTile The next tile.
   */
  public void setNextTile(Tile nextTile) {
    this.nextTile = nextTile;
  }

  /**
   * Gets the unique ID for this tile.
   *
   * @return The tileID.
   */
  public int getTileId() {
    return tileId;
  }

  /**
   * Gets the tile name for a Monopoly board action tile.
   *
   * @param tile The tile to get the name for.
   * @return A string representation of the action tile.
   */
  public static String getTileName(Tile tile) {
    if (tile == null) {
      return "unknown";
    }

    if (tile.getAction() == null) {
      return "Tile " + tile.getTileId();
    }

    if (tile.getAction() instanceof PropertyTileAction propertyAction) {
      return propertyAction.getPropertyName();
    }

    String actionType = tile.getAction().getClass().getSimpleName();
    return switch (actionType) {
      case "StartTileAction" -> "Start";
      case "JailTileAction" -> "Jail";
      case "FreeParkingAction" -> "Free Parking";
      case "GoToJailAction" -> "Go To Jail";
      case "TaxTileAction" -> "Income Tax";
      case "WealthTaxTileAction" -> "Wealth Tax";
      case "ChanceTileAction" -> "Chance";
      default -> "Tile " + tile.getTileId();
    };
  }

  /**
   * Gets the next tile in the game board sequence.
   *
   * @return The next tile.
   */
  public Tile getNextTile() {
    return nextTile;
  }

  /**
   * Sets an action for this tile that will be executed when the player lands on it.
   *
   * @param action The action to be performed.
   */
  public void setAction(TileAction action) {
    this.action = action;
  }

  /**
   * Gets the action associated with this tile.
   *
   * @return The tile action.
   */
  public TileAction getAction() {
    return action;
  }
}
