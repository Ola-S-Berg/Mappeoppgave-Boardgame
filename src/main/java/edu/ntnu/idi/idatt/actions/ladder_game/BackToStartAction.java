package edu.ntnu.idi.idatt.actions.ladder_game;

import edu.ntnu.idi.idatt.actions.TileAction;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.Tile;

/**
 * Class representing action when landing on a move back to start tile.
 */
public class BackToStartAction implements TileAction {

  /**
   * Performs the action of moving the player back to start.
   * @param player The player that lands on the tile with this action.
   */
  @Override
  public void perform(Player player) {
    System.out.println(player.getName() + " moved back to start");
    Tile startTile = player.getGame().getBoard().getTile(1);
    player.placeOnTile(startTile);
  }
}
