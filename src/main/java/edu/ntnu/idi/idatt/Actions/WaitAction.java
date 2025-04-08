package edu.ntnu.idi.idatt.Actions;

import edu.ntnu.idi.idatt.GameLogic.Player;

/**
 * Class representing action when landing on a wait tile.
 */
public class WaitAction implements TileAction {

  /**
   * Performs the action of making the player wait a turn before rolling again.
   * @param player The player that lands on the tile with this action.
   */
  @Override
  public void perform(Player player) {

    System.out.println(player.getName() + " must wait a turn before rolling");
    player.setWaitTurn(true);
  }
}
