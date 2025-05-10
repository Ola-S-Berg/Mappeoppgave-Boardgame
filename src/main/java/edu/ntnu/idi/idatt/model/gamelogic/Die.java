package edu.ntnu.idi.idatt.model.gamelogic;

import java.util.Random;

/**
 * Class representing a single die.
 */
public class Die {
  private int lastRolledValue;
  private final Random random;

  /**
   * The constructor the die class.
   */
  public Die() {
    this.random = new Random();
  }

  /**
   * Rolls a random value between 1 and 6.
   *
   * @return The value rolled.
   */
  public int roll() {
    lastRolledValue = random.nextInt(6) + 1;
    return lastRolledValue;
  }

  /**
   * Gets the value of the last rolled die value.
   *
   * @return The value.
   */
  public int getValue() {
    return lastRolledValue;
  }
}
