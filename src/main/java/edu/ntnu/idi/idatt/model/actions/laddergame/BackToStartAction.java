package edu.ntnu.idi.idatt.model.actions.laddergame;

import edu.ntnu.idi.idatt.model.actions.TileAction;
import edu.ntnu.idi.idatt.model.gamelogic.Player;
import edu.ntnu.idi.idatt.model.gamelogic.Tile;

/**
 * <h1>Back To Start Action</h1>
 *
 * <p>Implements a game mechanic that forces a player to return to the starting position
 * of the game board when landing on a specific tile with this action.</p>
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
 */
public class BackToStartAction implements TileAction {

  /**
   * Performs the action of moving the player back to start.
   *
   * @param player The player that lands on the tile with this action.
   */
  @Override
  public void perform(Player player) {
    System.out.println(player.getName() + " moved back to start");
    Tile startTile = player.getGame().getBoard().getTile(1);
    player.placeOnTile(startTile);
  }
}
