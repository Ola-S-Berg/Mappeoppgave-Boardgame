package edu.ntnu.idi.idatt.model.actions.monopolygame;

import edu.ntnu.idi.idatt.model.actions.TileAction;
import edu.ntnu.idi.idatt.model.gamelogic.Player;

/**
 * <h1>Wealth Tax Tile Action</h1>
 *
 * <p>Implements a tax mechanism in Monopoly where players must pay a fixed amount when landing
 * on this tile</p>
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
 */
public class WealthTaxTileAction implements TileAction {
  private final int amount;

  /**
   * Constructs a new WealthTaxTileAction with a fixed tax amount.
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

  /**
   * Gets the amount of money the player has to pay.
   *
   * @return The amount of money the player has to pay.
   */
  public int getAmount() {
    return amount;
  }
}