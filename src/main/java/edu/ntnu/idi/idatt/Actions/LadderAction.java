package edu.ntnu.idi.idatt.Actions;

import edu.ntnu.idi.idatt.GameLogic.Player;
import edu.ntnu.idi.idatt.GameLogic.Tile;

/**
 * Class representing the action of landing on a ladder.
 */
public class LadderAction implements TileAction {
  private final int destinationTileId;
  private final String direction;

  /**
   * The constructor for LadderAction.
   * @param destinationTileId The ID for the tile a player moves to.
   * @param direction The direction of movement (up or down).
   */
  public LadderAction(int destinationTileId, String direction) {
    this.destinationTileId = destinationTileId;
    this.direction = direction;
  }

  /**
   * Performs the action by moving the player to the tile.
   * @param player The player that lands on the tile with this action.
   */
  @Override
  public void perform(Player player) {
    Tile destination = player.getGame().getBoard().getTile(destinationTileId);
    if (destination == null) {
        throw new IllegalStateException("Destination tile does not exist: " + destinationTileId);
    }

    System.out.println(player.getName() + " moves " + direction + " to tile " + destinationTileId);
    player.placeOnTile(destination);
  }

  /**
   * Gets the destination tile ID.
   * @return The ID of the destination.
   */
  public int getDestinationTileId() {
    return destinationTileId;
  }

  /**
   * Gets the direction of the ladder.
   * @return The direction (up or down).
   */
  public String getDirection() {
    return direction;
  }
}
