package edu.ntnu.idi.idatt.model.actions.monopolygame;

import edu.ntnu.idi.idatt.model.actions.TileAction;
import edu.ntnu.idi.idatt.model.gamelogic.Player;

/**
 * <h1>Free Parking Action</h1>
 *
 * <p>Represents the action that occurs when a player lands on the Free Parking tile in Monopoly.
 * Sets a property flag that on the player that grants them a temporary benefit for their next turn,
 * being the exemption from paying for parking/rent next turn.</p>
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
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

