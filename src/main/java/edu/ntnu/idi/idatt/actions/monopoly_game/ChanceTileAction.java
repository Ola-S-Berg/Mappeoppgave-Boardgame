package edu.ntnu.idi.idatt.actions.monopoly_game;

import edu.ntnu.idi.idatt.actions.TileAction;
import edu.ntnu.idi.idatt.model.Player;
import java.util.Random;

/**
 * Class representing action when landing on a chance tile in Monopoly.
 */
public class ChanceTileAction implements TileAction {
  private static final Random random = new Random();
  private static final String[] CHANCE_ACTIONS = {
      "Move forward 3 spaces",
      "Go back 2 spaces",
      "Collect 5000 from the bank",
      "Pay 3000 to the bank",
      "Get out of jail free",
      "Advance to the nearest landmark",
      "Pay each player 1000",
      "Collect 1000 from each player"
  };

  /**
   * Performs a random chance action when a player lands on a chance tile.
   * TODO:
   * Actually implement the random actions.
   *
   * @param player The player that lands on the tile with this action.
   */
  @Override
  public void perform(Player player) {
    int actionIndex = random.nextInt(CHANCE_ACTIONS.length);
    String action = CHANCE_ACTIONS[actionIndex];

    System.out.println(player.getName() + " draws a chance card: " + action);

    executeChanceAction(player, actionIndex);
  }

  /**
   * Executes the specific chance action based on the action index.

   *
   * @param player The player performing the action.
   * @param actionIndex The index of the action to perform.
   */
  private void executeChanceAction(Player player, int actionIndex) {

    switch (actionIndex) {
      case 0:
        System.out.println(player.getName() + " moves forward 3 spaces");
        break;
      case 1:
        System.out.println(player.getName() + " moves back 2 spaces");
        break;
      case 2:
        System.out.println(player.getName() + " collects 5000 from the bank");
        break;
      case 3:
        System.out.println(player.getName() + " pays 3000 to the bank");
        break;
      case 4:
        System.out.println(player.getName() + " advances to the nearest landmark");
        break;
      case 5:
        System.out.println(player.getName() + " pays 1000 to each player");
        break;
      case 6:
        System.out.println(player.getName() + " collects 1000 from each player");
        break;
      default:
        System.out.println("Unknown chance action");
        break;
    }
  }
}