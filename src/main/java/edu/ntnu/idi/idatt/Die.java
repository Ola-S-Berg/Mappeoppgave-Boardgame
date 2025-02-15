package edu.ntnu.idi.idatt;
import java.util.Random;

/**
 * Class representing a single die. Rolls a random value between 1 and 6.
 */
public class Die {
    private int lastRolledValue;
    private final Random random;

    public Die() {
      this.random = new Random();
    }

    public int roll() {
      lastRolledValue = random.nextInt(6) + 1;
      return lastRolledValue;
    }

    public int getValue() {
      return lastRolledValue;
    }
}
