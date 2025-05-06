package edu.ntnu.idi.idatt.actions.monopoly_game;

import edu.ntnu.idi.idatt.actions.TileAction;
import edu.ntnu.idi.idatt.model.Player;

/**
 * Class representing action when landing on a tax tile in Monopoly.
 */
public class TaxTileAction implements TileAction {
  private final int percentageTax;
  private final int fixedTax;

  /**
   * Constructor for TaxTileAction.
   *
   * @param percentageTax The percentage of the player's money to pay as tax.
   * @param fixedTax The fixed amount to pay as tax.
   */
  public TaxTileAction(int percentageTax, int fixedTax) {
    this.percentageTax = percentageTax;
    this.fixedTax = fixedTax;
  }

  /**
   * Performs the action of a tax tile. The player must pay either a percentage
   * of their money or a fixed amount, whichever is specified.
   * TODO:
   * Make the player actually choose.
   *
   * @param player The player that lands on the tile with this action.
   */
  @Override
  public void perform(Player player) {
    System.out.println(player.getName() + " landed on a tax tile");
    System.out.println("Options: Pay " + percentageTax + "% of money or " + fixedTax + " fixed tax");

    System.out.println(player.getName() + " must pay tax");
  }

  /**
   * Gets the percentage tax rate.
   *
   * @return The percentage tax rate.
   */
  public int getPercentageTax() {
    return percentageTax;
  }

  /**
   * Gets the fixed tax amount.
   *
   * @return The fixed tax amount.
   */
  public int getFixedTax() {
    return fixedTax;
  }
}