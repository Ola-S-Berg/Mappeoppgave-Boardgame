package edu.ntnu.idi.idatt.model.actions;

import edu.ntnu.idi.idatt.model.gamelogic.Player;

/**
 * Interface that performs a pre-defined action when a player lands on a tile.
 */
public interface TileAction {

  /**
   * Makes the player perform the defined action.
   *
   * @param player The player to perform the action.
   */
  void perform(Player player);
}
