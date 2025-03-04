package edu.ntnu.idi.idatt;

/**
 * Class representing the action of landing on a ladder.
 */
public class LadderAction implements TileAction {
  private int destinationTileId;
  private String description;

  /**
   * The constructor for LadderAction.
   * @param destinationTileId The ID for the tile a player moves to.
   * @param description A description of the action.
   */
  public LadderAction(int destinationTileId, String description) {
    this.destinationTileId = destinationTileId;
    this.description = description;
  }

  /**
   * Performs the action by moving the player to the tile.
   * @param player The player that lands on the tile with this action.
   */
  @Override
  public void perform(Player player) {
    System.out.println(player.getName() + " Took a ladder to tile " + destinationTileId);
    player.placeOnTile(player.getGame().getBoard().getTile(destinationTileId));
  }
}
