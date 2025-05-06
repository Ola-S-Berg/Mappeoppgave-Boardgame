package edu.ntnu.idi.idatt.actions.monopoly_game;

import edu.ntnu.idi.idatt.actions.TileAction;
import edu.ntnu.idi.idatt.model.Player;

/**
 * Class representing action when landing on a pay tile in Monopoly.
 */
public class PayTileAction implements TileAction {
  private final int amount;

  /**
   * Constructor for PayTileAction.
   *
   * @param amount The amount of money the player has to pay.
   */
  public PayTileAction(int amount) {
    this.amount = amount;
  }

  /**
   * Performs the action of making the player pay a fixed amount.
   * TODO:
   * Actually subtract money from a player.
   *
   * @param player The player that lands on the tile with this action.
   */
  @Override
  public void perform(Player player) {
    System.out.println(player.getName() + " landed on a Pay tile");
    System.out.println(player.getName() + " must pay " + amount);
  }

  /**
   * Gets the payment amount.
   *
   * @return The amount to pay.
   */
  public int getAmount() {
    return amount;
  }
}