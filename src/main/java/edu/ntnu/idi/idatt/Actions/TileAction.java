package edu.ntnu.idi.idatt.Actions;

import edu.ntnu.idi.idatt.GameLogic.Player;

/**
 * Interface that performs a pre-defined action when a player lands on a tile.
 */
public interface TileAction {
  void perform(Player player);
}
