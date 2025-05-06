package edu.ntnu.idi.idatt.actions.monopoly_game;

import edu.ntnu.idi.idatt.actions.TileAction;
import edu.ntnu.idi.idatt.model.Player;


/**
 * Class representing action when landing on a jail tile in Monopoly.
 */
public class JailTileAction implements TileAction {

  /**
   * Performs the action of the jail tile.
   * Player in this tile must wait until all players have passed once.
   * TODO:
   * Make work with observer to check when all players have passed.
   *
   * @param player The player that lands on the tile with this action.
   */
  @Override
  public void perform(Player player) {

    System.out.println(player.getName() + " is just visiting the jail");
  }
}