package edu.ntnu.idi.idatt.model.gamelogic;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Dice Class</h1>
 *
 * <p>Represents a collection of dice used in board games. This class manages multiple
 * individual die objects and provides methods for rolling all dice simultaneously and
 * retrieving their values.</p>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Creates and manages a customizable number of six-sided dice</li>
 *   <li>Provides rolling functionality for all dice at once</li>
 *   <li>Calculates the sum of all dice values automatically</li>
 *   <li>Allows access to individual die values when needed</li>
 * </ul>
 *
 * @author Ola Syrstad Berg
 * @author Markus Øyen Lund
 * @since v1.1.0
 */
public class Dice {
  private final List<Die> dice;

  /**
   * Creates an array list of dice and adds a pre-defined number of dice.
   *
   * @param numberOfDice The number of dice to add.
   */
  public Dice(int numberOfDice) {
    dice = new ArrayList<>();
    for (int i = 0; i < numberOfDice; i++) {
      dice.add(new Die());
    }
  }

  /**
   * Method for summarizing the values of all die rolled.
   *
   * @return Returns the sum of pips.
   */
  public int roll() {
    int sum = 0;
    for (Die die : dice) {
      sum += die.roll();
    }
    return sum;
  }

  /**
   * Retrieves the value of a specified die in the collection.
   *
   * @param dieNumber The index of the die to retrieve. Must be between 0 (inclusive)
   *                  and the number of dice in the collection (exclusive).
   * @return The value of the specified die.
   * @throws IllegalArgumentException If the given dieNumber is out of range.
   */
  public int getDie(int dieNumber) {
    if (dieNumber < 0 || dieNumber >= dice.size()) {
      throw new IllegalArgumentException("Invalid dieNumber.");
    }
    return dice.get(dieNumber).getValue();
  }

  /**
   * Retrieves the number of dice in the collection.
   *
   * @return The total number of dice.
   */
  public int getNumberOfDice() {
    return dice.size();
  }
}
