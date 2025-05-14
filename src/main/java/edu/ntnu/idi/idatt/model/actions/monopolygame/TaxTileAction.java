package edu.ntnu.idi.idatt.model.actions.monopolygame;

import edu.ntnu.idi.idatt.controllers.MonopolyGameController;
import edu.ntnu.idi.idatt.model.actions.TileAction;
import edu.ntnu.idi.idatt.model.gamelogic.Player;
import edu.ntnu.idi.idatt.views.DialogService;
import javafx.stage.Stage;

/**
 * <h1>Tax Tile Action</h1>
 *
 * <p>Implements the behavior when a player lands on a tax tile in Monopoly. Presents the player
 * with a strategic choice between two taxation methods: paying a percentage of their current
 * money or a fixed amount.</p>
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
 */
public class TaxTileAction implements TileAction {
  private final int percentageTax;
  private final int fixedTax;
  private Stage ownerStage;
  private MonopolyGameController controller;

  /**
   * Constructs a new TaxTileAction tile two taxation options.
   *
   * @param percentageTax The percentage of the player's money to pay as tax.
   * @param fixedTax The fixed amount to pay as tax.
   */
  public TaxTileAction(int percentageTax, int fixedTax) {
    this.percentageTax = percentageTax;
    this.fixedTax = fixedTax;
  }

  public void setController(MonopolyGameController controller) {
    this.controller = controller;
  }

  /**
   * Performs the action of a tax tile. The player must pay either a percentage
   * of their money or a fixed amount, whichever is specified.
   *
   * @param player The player that lands on the tile with this action.
   */
  @Override
  public void perform(Player player) {
    System.out.println(player.getName() + " landed on a tax tile");
    System.out.println("Options: Pay " + percentageTax
        + "% of money or " + fixedTax + " fixed tax");

    if (ownerStage == null && controller != null) {
      ownerStage = controller.getStage();
    }

    int percentageAmount = (int) (player.getMoney() * (percentageTax / 100.0));

    DialogService.showTaxPaymentDialog(ownerStage, percentageTax, fixedTax, player, () -> {
      if (player.payMoney(percentageAmount)) {
        System.out.println(player.getName() + " paid " + percentageAmount
            + " as " + percentageTax + "% tax");

        if (controller != null) {
          controller.updatePlayerMoney(player);
        }
      }
    }, () -> {
      if (player.payMoney(fixedTax)) {
        System.out.println(player.getName() + " paid " + fixedTax + " as fixed tax");

        if (controller != null) {
          controller.updatePlayerMoney(player);
        }
      }
    });
  }

  public int getPercentageTax() {
    return percentageTax;
  }

  public int getFixedTax() {
    return fixedTax;
  }
}