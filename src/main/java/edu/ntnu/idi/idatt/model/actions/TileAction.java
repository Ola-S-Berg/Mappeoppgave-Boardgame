package edu.ntnu.idi.idatt.model.actions;

import edu.ntnu.idi.idatt.model.gamelogic.Player;

/**
 * Interface that performs a pre-defined action when a player lands on a tile.
 */
public interface TileAction {
  void perform(Player player);
}
