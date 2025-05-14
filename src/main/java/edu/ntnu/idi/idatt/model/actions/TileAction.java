package edu.ntnu.idi.idatt.model.actions;

import edu.ntnu.idi.idatt.model.gamelogic.Player;

/**
 * <h1>Tile Action Interface</h1>
 *
 * <p>Defines the core behavior for actions that occur when a player lands on a specific tile
 * in a board game. This interface serves as the foundation for the action design pattern
 * implementation across the game's tile action system.</p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Executing game logic when a player lands on a specific tile</li>
 *   <li>Modifying player state according to tile-specific rules</li>
 *   <li>Enforcing game mechanics through specialized implementations</li>
 *   <li>Providing a consistent interface for all tile-based actions</li>
 *   <li>Supporting extension of the game with new tile types</li>
 * </ul>
 *
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
 */
public interface TileAction {

  /**
   * Makes the player perform the defined action.
   *
   * @param player The player to perform the action.
   */
  void perform(Player player);
}
