package edu.ntnu.idi.idatt.model.gamelogic;

public interface BoardGameObserver {

  /**
   * Called when a player moves to a new tile.
   * @param player The player that moved.
   * @param fromTileId The ID of the tile the player moved from.
   * @param toTileId The ID of the tile the player moved to.
   * @param diceValue The value rolled on the die.
   */
  void onPlayerMove(Player player, int fromTileId, int toTileId, int diceValue);

  /**
   * Called when a player wins the game.
   * @param player The winning player.
   */
  void onGameWon(Player player);

  /**
   * Called when a player must skip their turn.
   * @param player The player that must skip their turn.
   */
  void onPlayerSkipTurn(Player player);

  /**
   * Called when the current player changes.
   * @param player The new current player.
   */
  void onCurrentPlayerChanged(Player player);

  /**
   * Called when a player goes bankrupt.
   *
   * @param player The player who went bankrupt.
   */
  void onPlayerBankrupt(Player player);
}

