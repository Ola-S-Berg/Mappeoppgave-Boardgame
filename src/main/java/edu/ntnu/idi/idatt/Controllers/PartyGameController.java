package edu.ntnu.idi.idatt.Controllers;

import edu.ntnu.idi.idatt.GameLogic.Player;

/**
 * Controller for the Party Game that implements the GameController interface.
 */
public class PartyGameController implements GameController {

  @Override
  public Player getCurrentPlayer() {
    return null;
  }

  @Override
  public void rollDice() {
  }

  @Override
  public boolean saveGame() {
    return false;
  }

  @Override
  public void quitToMenu() {
  }

  @Override
  public void restartGame() {
  }

  @Override
  public String getGameVariation() {
    return null;
  }

  @Override
  public int[] convertTileIdToGridCoordinates(int tileId) {
    return new int[0];
  }

  @Override
  public double[] calculateTokenOffset(int playerIndex, int totalPlayers, double baseRadius) {
    return new double[0];
  }
}
