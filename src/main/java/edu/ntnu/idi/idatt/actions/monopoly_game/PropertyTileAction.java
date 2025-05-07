package edu.ntnu.idi.idatt.actions.monopoly_game;

import edu.ntnu.idi.idatt.actions.TileAction;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.controllers.MonopolyGameController;
import edu.ntnu.idi.idatt.views.DialogService;
import javafx.application.Platform;
import javafx.stage.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class representing action when landing on a property tile in Monopoly Game.
 */
public class PropertyTileAction implements TileAction {
  private final String propertyName;
  private final int cost;
  private Player owner;
  private MonopolyGameController controller;
  private static final Logger LOGGER = Logger.getLogger(PropertyTileAction.class.getName());

  /**
   * Constructor for PropertyTileAction.
   *
   * @param propertyName The name of the property.
   * @param cost The cost to purchase the property.
   * @param propertyType The type/color of the property.
   */
  public PropertyTileAction(String propertyName, int cost, String propertyType) {
    this.propertyName = propertyName;
    this.cost = cost;
    this.owner = null;
  }

  public void setController(MonopolyGameController controller) {
    this.controller = controller;
  }

  /**
   * Performs the action of a property tile. If the property is not owned,
   * the player can purchase it. If it is owned by another player, the player
   * must pay rent.
   *
   * @param player The player that lands on the tile with this action.
   */
  @Override
  public void perform(Player player) {

    Platform.runLater(() -> {
      if (owner == null) {
        System.out.println(player.getName() + " landed on an unowned property: " + propertyName);
        System.out.println("Price: " + cost);

        if (player.getMoney() < cost) {
          System.out.println(player.getName() + " doesn't have enough money to purchase " + propertyName);
          if (controller != null) {
            controller.updatePlayerMoney(player);
          }
          return;
        }

        try {
          Stage stage = controller.getStage();
          DialogService.showPropertyPurchaseDialog(stage, this, () -> {
            if (player.payMoney(cost)) {
              owner = player;
              player.addProperty(this);

              if (controller != null) {
                controller.updatePlayerProperty(player, propertyName);
                controller.updatePlayerMoney(player);
              }

              System.out.println(player.getName() + " purchased " + propertyName + " for " + cost);
            }
            }, () -> System.out.println(player.getName() + " declined to purchase " + propertyName));
        } catch (Exception e) {
          LOGGER.log(Level.SEVERE, "Error showing property purchase dialog", e);
        }
      } else if (owner != player) {
        int rent = calculateRent();
        System.out.println(
            player.getName() + " landed on " + propertyName + " owned by " + owner.getName());
        System.out.println("Rent: " + rent);

        if (player.payPlayer(owner, rent)) {
          if (controller != null) {
            controller.updatePlayerMoney(player);
            controller.updatePlayerMoney(owner);
          }
          System.out.println(player.getName() + " paid " + owner.getName() + " " + rent);
        } else {
          System.out.println(player.getName() + " cannot afford rent");
        }
      } else {
        System.out.println(player.getName() + " landed on their own property: " + propertyName);
      }
    });
  }

  /**
   * Calculates the rent for this property.
   *
   * @return The rent amount.
   */
  private int calculateRent() {
    return cost / 10;
  }

  /**
   * Sets the owner of this property.
   *
   * @param player The player who owns the property.
   */
  public void setOwner(Player player) {
    this.owner = player;
  }

  /**
   * Gets the owner of this property.
   *
   * @return The player who owns the property, or null if unowned.
   */
  public Player getOwner() {
    return owner;
  }

  /**
   * Gets the name of this property.
   *
   * @return The property name.
   */
  public String getPropertyName() {
    return propertyName;
  }

  /**
   * Gets the cost of this property.
   *
   * @return The property cost.
   */
  public int getCost() {
    return cost;
  }
}