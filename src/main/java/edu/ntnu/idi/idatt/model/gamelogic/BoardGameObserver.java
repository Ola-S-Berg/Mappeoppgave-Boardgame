package edu.ntnu.idi.idatt.model.gamelogic;

/**
 * <h1>Board Game Observer Interface</h1>
 *
 * <p>Defines the interface for objects that wish to observe and react to game state changes
 * in a board game. Implements the Observer pattern to decouple game logic from UI
 * and other components that need to respond to game events.</p>
 *
 * <h2>Event types</h2>
 * <ul>
 *   <li>Player movement: Triggered when a player moves from one tile to another</li>
 *   <li>Game Won: Triggered when a player wins the game</li>
 *   <li>Turn skip: Triggered when a player must skip their turn</li>
 *   <li>Player change: Triggered when the active player changes</li>
 *   <li>Player bankruptcy: Triggered when a player goes bankrupt</li>
 * </ul>
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
 */
public interface BoardGameObserver {

  /**
   * Called when a player moves to a new tile.
   *
   * @param player The player that moved.
   * @param fromTileId The ID of the tile the player moved from.
   * @param toTileId The ID of the tile the player moved to.
   * @param diceValue The value rolled on the die.
   */
  void onPlayerMove(Player player, int fromTileId, int toTileId, int diceValue);

  /**
   * Called when a player wins the game.
   *
   * @param player The winning player.
   */
  void onGameWon(Player player);

  /**
   * Called when a player must skip their turn.
   *
   * @param player The player that must skip their turn.
   */
  void onPlayerSkipTurn(Player player);

  /**
   * Called when the current player changes.
   *
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

