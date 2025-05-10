package edu.ntnu.idi.idatt.model.actions.monopoly_game;

import edu.ntnu.idi.idatt.model.actions.TileAction;
import edu.ntnu.idi.idatt.model.gamelogic.Player;
import edu.ntnu.idi.idatt.controllers.MonopolyGameController;
import edu.ntnu.idi.idatt.views.DialogService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.stage.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class representing action when landing on a property tile in Monopoly Game.
 */
public class PropertyTileAction implements TileAction {
  private final String propertyName;
  private final String propertyType;
  private final int cost;
  private Player owner;
  private MonopolyGameController controller;
  private static final Logger LOGGER = Logger.getLogger(PropertyTileAction.class.getName());
  private static final Map<String, Integer> propertyTypeCounts = new HashMap<>();

  static {
    propertyTypeCounts.put("blue", 2);
    propertyTypeCounts.put("pink", 3);
    propertyTypeCounts.put("green", 3);
    propertyTypeCounts.put("gray", 3);
    propertyTypeCounts.put("red", 3);
    propertyTypeCounts.put("yellow", 3);
    propertyTypeCounts.put("purple", 3);
    propertyTypeCounts.put("orange", 2);
    propertyTypeCounts.put("landmark", 4);
  }

  /**
   * Constructor for PropertyTileAction.
   *
   * @param propertyName The name of the property.
   * @param cost The cost to purchase the property.
   * @param propertyType The type/color of the property.
   */
  public PropertyTileAction(String propertyName, int cost, String propertyType) {
    this.propertyName = propertyName;
    this.propertyType = propertyType;
    this.cost = cost;
    this.owner = null;
  }

  /**
   * Sets the game controller for this action.
   *
   * @param controller The controller for this action.
   */
  public void setController(MonopolyGameController controller) {
    this.controller = controller;
  }

  /**
   * Performs the action of a property tile. If the property is not owned,
   * the player can purchase it. If it is owned by another player, the player
   * must pay rent. Rent is equal to property cost if a player owns all properties of a type.
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
                controller.updatePlayerProperty(player);
                controller.updatePlayerMoney(player);
              }

              System.out.println(player.getName() + " purchased " + propertyName + " for " + cost);
            }
            }, () -> System.out.println(player.getName() + " declined to purchase " + propertyName));
        } catch (Exception e) {
          LOGGER.log(Level.SEVERE, "Error showing property purchase dialog", e);
        }
      } else if (owner != player) {
        String freeParking = player.getProperty("freeParking");
        if (freeParking != null && freeParking.equals("true")) {
          System.out.println(player.getName() + " landed on " + propertyName + " owned by " + owner.getName() + " but has Free Parking");
          System.out.println(player.getName() + " doesn't need to pay rent this turn");

          player.setProperty("freeParking", null);

          if (controller != null) {
            controller.updatePlayerMoney(player);
          }
        } else {
          int rent = cost*2/10;
          boolean ownsAllOfType = ownsAllPropertiesOfType(owner, propertyType);

          if (ownsAllOfType) {
            rent = cost;
            System.out.println(player.getName() + " landed on " + propertyName +
                " owned by " + owner.getName() + " (Monopoly bonus: rent = cost)");
          } else {
            System.out.println(
                player.getName() + " landed on " + propertyName + " owned by " + owner.getName());
          }

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
        }
      } else {
        System.out.println(player.getName() + " landed on their own property: " + propertyName);
      }
    });
  }

  /**
   * Checks if a player owns all properties of a specific type.
   *
   * @param player The player to check.
   * @param type The property to check.
   * @return True if the player owns all properties of the specified type, false otherwise.
   */
  private boolean ownsAllPropertiesOfType(Player player, String type) {
    if (!propertyTypeCounts.containsKey(type)) {
      return false;
    }

    int totalPropertiesOfTpe = propertyTypeCounts.get(type);
    int playerPropertiesOfType = 0;

    List<PropertyTileAction> playerProperties = player.getOwnedProperties();
    for (PropertyTileAction property : playerProperties) {
      if (property.getPropertyType().equals(type)) {
        playerPropertiesOfType++;
      }
    }

    return playerPropertiesOfType == totalPropertiesOfTpe;
  }

  /**
   * Sets the owner of this property.
   *
   * @param player The player to set ownership of the property to.
   */
  public void setOwner(Player player) {
    this.owner = player;
  }

  /**
   * Gets the owner of this property.
   * @return The player who owns this property.
   */
  public Player getOwner() {
    return this.owner;
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
   * Gets the property type of this property.
   * @return The property type.
   */
  public String getPropertyType() {
    return propertyType;
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