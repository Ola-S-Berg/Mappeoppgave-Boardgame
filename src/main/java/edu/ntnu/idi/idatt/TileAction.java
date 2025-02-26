package edu.ntnu.idi.idatt;

/**
 * Interface that performs a pre-defined action when a player lands on a tile.
 */
public interface TileAction {
  void perform(Player player);
}
