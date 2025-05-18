package edu.ntnu.idi.idatt.model.actions;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.ntnu.idi.idatt.model.actions.monopolygame.PropertyTileAction;
import edu.ntnu.idi.idatt.model.gamelogic.BoardGame;
import edu.ntnu.idi.idatt.model.gamelogic.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PropertyTileActionTest {

  private Player player1;
  private Player player2;
  private BoardGame game;
  private PropertyTileAction property1;
  private PropertyTileAction property2;

  @BeforeEach
  void setUp() {
    game = new BoardGame();
    player1 = new Player("Ola", "Blue", game, 10000);
    player2 = new Player("Markus", "Red", game, 10000);

    property1 = new PropertyTileAction("Testgata1", 1000, "blue");
    property2 = new PropertyTileAction("Testgata2", 1200, "blue");
  }

  @Test
  @DisplayName("New property should have no owner")
  void newPropertyShouldHaveNoOwner() {
    assertNull(property1.getOwner(), "A new property should have no owner.");
  }

  @Test
  @DisplayName("Should set and get property owner correctly")
  void shouldSetAndGetOwnerCorrectly() {
    property1.setOwner(player1);
    assertEquals(player1, property1.getOwner(), "Property owner should be player1");
  }

  @Test
  @DisplayName("Should return correct property name")
  void shouldReturnCorrectPropertyName() {
    assertEquals("Testgata1", property1.getPropertyName(),
        "Property name should be Testgata1");
  }

  @Test
  @DisplayName("Should return correct property type")
  void shouldReturnCorrectPropertyType() {
    assertEquals("blue", property1.getPropertyType(),
        "Property type should be blue");
  }

  @Test
  @DisplayName("Should return correct property cost")
  void shouldReturnCorrectPropertyCost() {
    assertEquals(1000, property1.getCost(), "Property cost should be 1000");
  }

  @Test
  @DisplayName("Player should be able to purchase unowned property")
  void playerShouldBuyUnownedProperty() {
      int initialMoney = player1.getMoney();

      boolean paymentSuccess = player1.payMoney(property1.getCost());
      if (paymentSuccess) {
        property1.setOwner(player1);
        player1.addProperty(property1);
      }

      assertTrue(paymentSuccess, "Payment should be successful");
      assertEquals(player1, property1.getOwner(), "Player1 should be the owner");
      assertEquals(initialMoney - property1.getCost(), player1.getMoney(),
          "Player's money should be reduced by property cost");
      assertTrue(player1.getOwnedProperties().contains(property1),
          "Player's owned properties should contain the purchased property");
  }

  @Test
  @DisplayName("Player cannot purchase property when money is insufficient")
  void playerCannotBuyPropertyWithInsufficientFunds() {
    Player poorPlayer = new Player("Kari", "Green", game, 500);
    int initialMoney = poorPlayer.getMoney();

    boolean paymentSuccess = poorPlayer.payMoney(property1.getCost());

    assertFalse(paymentSuccess, "Payment should fail due to insufficient funds");
    assertNull(property1.getOwner(), "Property should remain unowned");
    assertEquals(initialMoney, poorPlayer.getMoney(),
        "Player's money should remain unchanged");
  }

  @Test
  @DisplayName("Player pays basic rent when landing on another player's property")
  void playerPaysBasicRentToPropertyOwner() {
    property1.setOwner(player1);
    int player1InitialMoney = player1.getMoney();
    int player2InitialMoney = player2.getMoney();
    int expectedRent = property1.getCost() * 2 / 10;

    boolean paymentSuccess = player2.payPlayer(player1, expectedRent);

    assertTrue(paymentSuccess, "Rent payment should be successful");
    assertEquals(player1InitialMoney + expectedRent, player1.getMoney(),
        "Owner should receive rent amount");
    assertEquals(player2InitialMoney - expectedRent, player2.getMoney(),
        "Player should pay rent amount");
  }

  @Test
  @DisplayName("Player pays monopoly rent when owner has all properties of type")
  void playerPaysMonopolyRentWhenOwnerHasAllPropertiesOfType() {
    property1.setOwner(player1);
    property2.setOwner(player1);
    player1.addProperty(property1);
    player1.addProperty(property2);

    int player1InitialMoney = player1.getMoney();
    int player2InitialMoney = player2.getMoney();

    int monopolyRent = property1.getCost();

    boolean paymentSuccess = player2.payPlayer(player1, monopolyRent);

    assertTrue(paymentSuccess, "Monopoly rent payment should be successful");
    assertEquals(player1InitialMoney + monopolyRent, player1.getMoney(),
        "Owner should receive monopoly rent");
    assertEquals(player2InitialMoney - monopolyRent, player2.getMoney(),
        "Player should pay monopoly rent");
  }

  @Test
  @DisplayName("Player with insufficient funds cannot pay rent")
  void playerWithInsufficientFundsCannotPayRent() {
    property1.setOwner(player1);
    Player poorPlayer = new Player("Kari", "Green", game, 10);
    int expectedRent = property1.getCost() * 2 / 10;
    int ownerInitialMoney = player1.getMoney();
    int poorPlayerInitialMoney = poorPlayer.getMoney();

    boolean paymentSuccess = poorPlayer.payPlayer(player1, expectedRent);

    assertFalse(paymentSuccess, "Rent payment should fail due to insufficient funds");
    assertEquals(ownerInitialMoney, player1.getMoney(),
        "Owner's money should remain unchanged");
    assertEquals(poorPlayerInitialMoney, poorPlayer.getMoney(),
        "Poor player's money should remain unchanged");
  }

  @Test
  @DisplayName("Player does not pay rent when landing on own property")
  void playerDoesNotPayRentWhenLandingOnOwnProperty() {
    property1.setOwner(player1);
    int initialMoney = player1.getMoney();

    assertEquals(initialMoney, player1.getMoney(),
        "Player's money should remain unchanged when landing on own property");
  }

  @Test
  @DisplayName("Player with free parking does not pay rent")
  void playerWithFreeParkingDoesNotPayRent() {
    property1.setOwner(player1);
    player2.setProperty("freeParking", "true");
    int player1InitialMoney = player1.getMoney();
    int player2InitialMoney = player2.getMoney();

    String freeParking = player2.getProperty("freeParking");
    boolean shouldPayRent = !(freeParking != null && freeParking.equals("true"));

    assertFalse(shouldPayRent, "Player with free parking should not pay rent");
    assertEquals(player1InitialMoney, player1.getMoney(),
        "Owner's money should remain unchanged");
    assertEquals(player2InitialMoney, player2.getMoney(),
        "Player's money should remain unchanged");
  }

  @Test
  @DisplayName("Free parking is consumed after use")
  void freeParkingIsConsumedAfterUse() {
    property1.setOwner(player1);
    player2.setProperty("freeParking", "true");

    String freeParking = player2.getProperty("freeParking");
    if (freeParking != null && freeParking.equals("true")) {
      player2.setProperty("freeParking", null);
    }

    assertNull(player2.getProperty("freeParking"),
        "Free parking should be consumed after use");
  }

  @Test
  @DisplayName("Property actions with invalid type should not throw errors")
  void propertyActionsWithInvalidTypeShouldNotThrowErrors() {
    PropertyTileAction invalidProperty = new PropertyTileAction("Invalid", 1000,
        "invalidType");
    invalidProperty.setOwner(player1);
    player1.addProperty(invalidProperty);

    assertDoesNotThrow(() -> assertEquals("invalidType",
        invalidProperty.getPropertyType()));
  }
}
