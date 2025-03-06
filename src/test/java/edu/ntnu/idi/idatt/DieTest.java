package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.GameLogic.Die;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Test-class for class Die. Tests rolling a die and asserts true if the value is between 1 and 6.
 */
class DieTest {

  @Test
  void testRollDie() {
    Die die = new Die();
    int result = die.roll();
    assertTrue(result >= 1 && result <= 6);
  }

  @Test
  void getValue() {
  }
}