package edu.ntnu.idi.idatt.actions.monopoly_game;

import edu.ntnu.idi.idatt.actions.TileAction;
import edu.ntnu.idi.idatt.model.Player;

/**
 * Class representing action when landing on a property tile in Monopoly Game.
 */
public class PropertyTileAction implements TileAction {
  private final String propertyName;
  private final int cost;
  private final String propertyType;
  private Player owner;

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
    this.propertyType = propertyType;
    this.owner = null;
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
    System.out.println(player.getName() + " landed on " + propertyName +
        " (" + propertyType + ") - Cost: " + cost);

    if (owner == null) {
      System.out.println(propertyName + " is available for purchase");
      player.getGame().buyProperty(player, this);
    } else if (owner != player) {
      int rent = calculateRent();
      System.out.println(player.getName() + " must pay " + rent + " to " + owner.getName());
      player.payPlayer(owner, rent);
    } else {
      System.out.println(player.getName() + " owns this property");
    }
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

  /**
   * Gets the type of this property.
   *
   * @return The property type/color.
   */
  public String getPropertyType() {
    return propertyType;
  }
}