package edu.ntnu.idi.idatt.model.gamelogic;

import edu.ntnu.idi.idatt.model.actions.TileAction;
import edu.ntnu.idi.idatt.model.actions.monopoly_game.PropertyTileAction;

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
   * Checks which tile the player has landed on. Only performs actions for Monopoly.
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
