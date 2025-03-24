package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.GameLogic.Dice;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


/* Test class for the Dice class. */

class DiceTest {


  /* Test that the roll method returns a value within the expected range when generating several dice objects. */

    @Test
    void rollWithinRange() {
        Dice dice = new Dice(3);
        int sum = dice.roll();
        assertTrue(sum >= 3 && sum <= 18);
    }


    /* Test that the getDie method returns a value within the expected range. */

    @Test
    void getDieValue() {
        Dice dice = new Dice(2);
        dice.roll(); 
        int value = dice.getDie(1);
        assertTrue(value >= 1 && value <= 6);
    }

    /* Test that the getDie method throws an exception when given an invalid index. */

    @Test
    void invalidIndexThrows() {
        Dice dice = new Dice(2);
        assertThrows(IllegalArgumentException.class, () -> dice.getDie(-1));
        assertThrows(IllegalArgumentException.class, () -> dice.getDie(2));
    }

    /* Test that the roll method returns a value within the expected range for different number of dice. */

    @Test
    void rollDifferentCounts() {
        Dice one = new Dice(1);
        Dice five = new Dice(5);
        int sum1 = one.roll();
        int sum5 = five.roll();

        assertTrue(sum1 >= 1 && sum1 <= 6);
        assertTrue(sum5 >= 5 && sum5 <= 30);
    }
}
