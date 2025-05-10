package edu.ntnu.idi.idatt.model.actions.monopolygame;

import edu.ntnu.idi.idatt.model.actions.TileAction;
import edu.ntnu.idi.idatt.model.gamelogic.Player;

/**
 * Class representing action when landing on a pay tile in Monopoly.
 */
public class WealthTaxTileAction implements TileAction {
  private final int amount;

  /**
   * Constructor for PayTileAction.
   *
   * @param amount The amount of money the player has to pay.
   */
  public WealthTaxTileAction(int amount) {
    this.amount = amount;
  }

  /**
   * Performs the action of making the player pay a fixed amount.
   *
   * @param player The player that lands on the tile with this action.
   */
  @Override
  public void perform(Player player) {
    System.out.println(player.getName() + " landed on a wealth tax tile");
    System.out.println(player.getName() + " must pay " + amount);

    boolean paymentSuccessful = player.payMoney(amount);

    if (!paymentSuccessful) {
      System.out.println(player.getName() + " couldn't afford to pay " + amount);
    }
  }

  public int getAmount() {
    return amount;
  }
}