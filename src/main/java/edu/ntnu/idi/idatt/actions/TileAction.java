package edu.ntnu.idi.idatt.actions;

import edu.ntnu.idi.idatt.model.Player;

/**
 * Interface that performs a pre-defined action when a player lands on a tile.
 */
public interface TileAction {
  void perform(Player player);
}
