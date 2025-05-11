package edu.ntnu.idi.idatt.model.gamelogic;

import java.util.Random;

/**
 * <h1>Die Class</h1>
 *
 * <p>Represents a single six-sided die used in the board games. This class provides functionality
 * for rolling a die and keeping track of the most recently rolled value.</p>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Random number generation between 1 and 6</li>
 *   <li>Maintains the last rolled value for reference</li>
 * </ul>
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
 */
public class Die {
  private int lastRolledValue;
  private final Random random;

  /**
   * Constructs a new die object.
   * Initializes the random number generator used for die rolls.
   */
  public Die() {
    this.random = new Random();
  }

  /**
   * Rolls a random value between 1 and 6.
   * Updates the stored last rolled value and returns it.
   *
   * @return The value rolled.
   */
  public int roll() {
    lastRolledValue = random.nextInt(6) + 1;
    return lastRolledValue;
  }

  /**
   * Gets the last value rolled by this die.
   *
   * @return The value.
   */
  public int getValue() {
    return lastRolledValue;
  }
}
