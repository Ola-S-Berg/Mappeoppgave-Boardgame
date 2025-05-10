package edu.ntnu.idi.idatt.model.actions.monopolygame;

import edu.ntnu.idi.idatt.model.actions.TileAction;
import edu.ntnu.idi.idatt.model.gamelogic.Player;

/**
 * Class representing action when landing on or passing the Start tile in Monopoly.
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