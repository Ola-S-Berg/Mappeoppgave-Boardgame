package edu.ntnu.idi.idatt.model.actions.monopolygame;

import edu.ntnu.idi.idatt.model.actions.TileAction;
import edu.ntnu.idi.idatt.model.gamelogic.Player;

/**
 * <h1>Start Tile Action</h1>
 *
 * <p>Implements the behavior of the Start tile in Monopoly, which rewards players with a monetary
 * bonus when they land on- or pass this tile.</p>
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
 */
public class StartTileAction implements TileAction {
  private static final int PASS_REWARD = 20000;

  /**
   * Performs the action of the start tile. Players collect money when
   * landing on or passing this tile.
   *
   * @param player The player that lands on the tile with this action.
   */
  @Override
  public void perform(Player player) {
    System.out.println(player.getName() + " landed on the Start tile");
    System.out.println(player.getName() + " collects " + PASS_REWARD + " for passing start");
    player.addMoney(PASS_REWARD);
  }
}