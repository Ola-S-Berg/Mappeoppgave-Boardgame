package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.model.gamelogic.Die;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Die class.
 */

class DieTest {


    /* Test that the roll method returns a value within the expected range. */

    @Test
    void rollGivesValueInRange() {
        Die die = new Die();
        for (int i = 0; i < 100; i++) {
            int result = die.roll();
            assertTrue(result >= 1 && result <= 6);
        }
    }
    
    /* Test that the getValue method returns the last rolled value. */

    @Test
    void getValueReturnsLastRolled() {
        Die die = new Die();
        int rolled = die.roll();
        assertEquals(rolled, die.getValue());
    }

    /* Test that the value is zero before the die is rolled. */

    @Test
    void valueIsZeroBeforeRoll() {
        Die die = new Die();
        assertEquals(0, die.getValue());
    }
}
