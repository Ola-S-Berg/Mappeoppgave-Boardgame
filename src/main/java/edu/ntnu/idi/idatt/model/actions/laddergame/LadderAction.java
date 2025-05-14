package edu.ntnu.idi.idatt.model.actions.laddergame;

import edu.ntnu.idi.idatt.model.actions.TileAction;
import edu.ntnu.idi.idatt.model.gamelogic.Player;
import edu.ntnu.idi.idatt.model.gamelogic.Tile;

/**
 * <h1>Ladder Action</h1>
 *
 * <p>Implements the signature mechanic of the ladder game, allowing players to move up or down
 * the game board when landing on ladder action tiles. The player moves up when landing on a ladder
 * with a green tile and down when landing on a ladder with a red tile.</p>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Bidirectional movement capability (up/down ladders)</li>
 *   <li>Configurable destination points for adaptable board design</li>
 *   <li>UI integration for event notification</li>
 *   <li>Exception handling for missing destination tiles</li>
 * </ul>
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
 */
public class LadderAction implements TileAction {
  private final int destinationTileId;
  private final String direction;

  /**
   * The constructor for LadderAction.
   *
   * @param destinationTileId The ID for the tile a player moves to.
   * @param direction The direction of movement (up or down).
   */
  public LadderAction(int destinationTileId, String direction) {
    this.destinationTileId = destinationTileId;
    this.direction = direction;
  }

  /**
   * Performs the action by moving the player to the tile.
   *
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
   *
   * @return The ID of the destination.
   */
  public int getDestinationTileId() {
    return destinationTileId;
  }

  /**
   * Gets the direction of the ladder.
   *
   * @return The direction (up or down).
   */
  public String getDirection() {
    return direction;
  }
}
