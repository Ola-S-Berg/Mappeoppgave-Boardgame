package edu.ntnu.idi.idatt.controllers;

import edu.ntnu.idi.idatt.model.gamelogic.Player;

/**
 * <h1>Board Game Controller Interface</h1>
 *
 * <p>This interface defines the core functionality required for controllers. It establishes
 * a standard contract for all game controllers to implement, ensuring consistent behavior
 * across different game variations.</p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Manging player turns and game state transitions</li>
 *   <li>Handling dice rolls and player movement</li>
 *   <li>Coordinating save/load functionality</li>
 *   <li>Managing game lifecycle events (restart, quit)</li>
 *   <li>Converting logical game positions to visual representations</li>
 * </ul>
 *
 * <h2>Design Patterns</h2>
 *
 * <p>This interface follows the Model-View-Controller (MVC) pattern, where the controller
 * acts as an intermediary between the game model and the UI views. Implementations of this
 * interface are responsible for maintaining the application's business logic while keeping
 * the view and model layers separate.</p>
 *
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
 */
public interface BoardGameController {

  /**
   * Get the current player whose turn it is.
   *
   * @return The current player.
   */
  Player getCurrentPlayer();

  /**
   * Handles the dice roll and player movement logic.
   * This method is called from the view when the roll button is clicked.
   */
  void rollDice();

  /**
   * Saves the current game state.
   *
   * @return true if save was successful, false otherwise.
   */
  boolean saveGame();

  /**
   * Quits the current game and returns to the main menu.
   * Shows a confirmation dialog before quitting.
   */
  void quitToMenu();

  /**
   * Restarts the game with the same players.
   */
  void restartGame();

  /**
   * Returns the game variation.
   *
   * @return The game variation.
   */
  String getGameVariation();

  /**
   * Converts a tile ID to its corresponding grid coordinates on the game board.
   *
   * @param tileId The ID of the tile to be converted.
   * @return An integer array containing the grid coordinates where
   *         the first element is the row index and the second element is the column index.
   */
  int[] convertTileIdToGridCoordinates(int tileId);
}
