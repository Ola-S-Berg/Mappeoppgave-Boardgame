package edu.ntnu.idi.idatt.Controllers;

import edu.ntnu.idi.idatt.GameLogic.Player;

public interface GameController {

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

  /**
   * Calculates the offset position for a player token based on its index.
   *
   * @param playerIndex The index of the player.
   * @param totalPlayers The total number of players in the game.
   * @param baseRadius The base radius to use for the offset calculation.
   * @return A double array where the first element is the X offset and the second element is the Y offset.
   */
  double[] calculateTokenOffset(int playerIndex, int totalPlayers, double baseRadius);
}
