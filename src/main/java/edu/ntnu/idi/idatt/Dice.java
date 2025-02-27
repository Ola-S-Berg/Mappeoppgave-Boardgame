package edu.ntnu.idi.idatt;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a collection of die. Creates a list of die and stores them in an ArrayList.
 */
public class Dice {
  private final List<Die> dice;

  /**
   * Creates an array list of dice and adds a pre-defined number of dice.
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
   * @return Returns the sum of pips.
   */
  public int roll() {
    int sum = 0;
    for (Die die : dice) {
      sum += die.roll();
    }
    return sum;
  }

  public int getDie(int dieNumber) {
    if (dieNumber < 0 && dieNumber < dice.size()) {
      throw new IllegalArgumentException("Invalid dieNumber.");
    }
    return dice.get(dieNumber).getValue();
  }


}
