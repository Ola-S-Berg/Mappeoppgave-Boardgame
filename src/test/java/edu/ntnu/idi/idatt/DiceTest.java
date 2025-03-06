package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.GameLogic.Dice;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test-class for class Dice. Sets up two dice and rolls them. Asserts true if value is between 2 and 12.
 */
class DiceTest {
  private Dice dice;

  @BeforeEach
  void setUp() {
    dice = new Dice(2);
  }

  @Test
  void testRollDice() {
    int result = dice.roll();
    assertTrue(result >= 2 && result <= 12);
  }
}