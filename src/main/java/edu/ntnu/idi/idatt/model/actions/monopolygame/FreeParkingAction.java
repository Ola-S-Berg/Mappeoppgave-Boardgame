package edu.ntnu.idi.idatt.model.actions.monopolygame;

import edu.ntnu.idi.idatt.model.actions.TileAction;
import edu.ntnu.idi.idatt.model.gamelogic.Player;

/**
 * Class representing action when landing on the Free Parking tile in Monopoly.
 */
public class FreeParkingAction implements TileAction {

  /**
   * Performs the action of the free parking tile. The player who lands on this
   * doesn't need to pay for parking on their next turn.
   *
   * @param player The player that lands on the tile with this action.
   */
  @Override
  public void perform(Player player) {
    System.out.println(player.getName() + " landed on Free Parking");
    player.setProperty("freeParking", "true");
    System.out.println(player.getName() + " won't need to pay for parking next turn!");
  }
}

