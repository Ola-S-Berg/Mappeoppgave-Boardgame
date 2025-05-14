package edu.ntnu.idi.idatt.model.actions.laddergame;

import edu.ntnu.idi.idatt.model.actions.TileAction;
import edu.ntnu.idi.idatt.model.gamelogic.Player;

/**
 * <h1>Wait Action</h1>
 *
 * <p>Implements a turn-delay mechanism that temporarily prevents a player from rolling/moving.
 * Forces a player who lands on a tile with this action to skip a turn.</p>
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
 */
public class WaitAction implements TileAction {

  /**
   * Performs the action of making the player wait a turn before rolling again.
   *
   * @param player The player that lands on the tile with this action.
   */
  @Override
  public void perform(Player player) {

    System.out.println(player.getName() + " must wait a turn before rolling");
    player.setWaitTurn(true);
  }
}
