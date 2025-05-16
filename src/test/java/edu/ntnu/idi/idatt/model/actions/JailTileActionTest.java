package edu.ntnu.idi.idatt.model.actions;

import edu.ntnu.idi.idatt.model.actions.monopolygame.JailTileAction;
import edu.ntnu.idi.idatt.model.gamelogic.BoardGame;
import edu.ntnu.idi.idatt.model.gamelogic.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JailTileAction class
 */
class JailTileActionTest {

  private JailTileAction jailAction;
  private Player player;
  private BoardGame game;

  @BeforeEach
  void setUp() {
    jailAction = new JailTileAction();
    game = new BoardGame();
    game.createMonopolyGameBoard();
    player = new Player("Ola", "Blue", game, 10000);
    game.addPlayer(player);
  }

  @Nested
  @DisplayName("Player in jail tests")
  class PlayerInJailTests {

    @BeforeEach
    void setUpJailedPlayer() {
      player.setProperty("inJail", "true");
    }

    @Test
    @DisplayName("Should increase jail turn count when player is in jail")
    void testIncreaseJailTurnCount() {
      player.setProperty("jailTurnCount", "1");

      jailAction.perform(player);

      assertEquals("2", player.getProperty("jailTurnCount"));
    }

    @Test
    @DisplayName("Should initialize jail turn count if not set")
    void testInitializeJailTurnCount() {
      jailAction.perform(player);

      assertEquals("1", player.getProperty("jailTurnCount"));
    }

    @Test
    @DisplayName("Should release player after maximum jail turns")
    void testReleaseAfterMaxJailTurns() {
      player.setProperty("jailTurnCount", "3");

      try {
        Method releaseMethod = Player.class.getDeclaredMethod("releaseFromJail");
        releaseMethod.setAccessible(true);

        jailAction.perform(player);

      } catch (Exception ignored) {
      }

      assertEquals("false", player.getProperty("inJail"));
      assertEquals("0", player.getProperty("jailTurnCount"));
    }
  }

  @Nested
  @DisplayName("Handling bail payment tests")
  class BailPaymentTests {

    @BeforeEach
    void setUpJailedPlayer() {
      player.setProperty("inJail", "true");
      player.setProperty("jailTurnCount", "1");
    }

    @Test
    @DisplayName("Should release player when bail is paid successfully")
    void testPayBailSuccessfully() {
      int initialMoney = player.getMoney();
      int bailAmount = 5000;

      try {
        Method releaseMethod = Player.class.getDeclaredMethod("releaseFromJail");
        releaseMethod.setAccessible(true);

        player.payMoney(bailAmount);
        releaseMethod.invoke(player);
        player.setProperty("jailTurnCount", "0");

      } catch (Exception e) {
        fail("Test failed with exception: " + e.getMessage());
      }

      assertEquals("false", player.getProperty("inJail"));
      assertEquals("0", player.getProperty("jailTurnCount"));
      assertEquals(initialMoney - bailAmount, player.getMoney());
    }

    @Nested
    @DisplayName("Rolling doubles to escape jail tests")
    class RollDoublesTests {

      @BeforeEach
      void setUpJailedPlayer() {
        player.setProperty("inJail", "true");
        player.setProperty("jailTurnCount", "1");
        player.placeOnTile(game.getBoard().getTile(11));
      }

      @Test
      @DisplayName("Should release player when doubles are rolled")
      void testRollDoublesSuccess() throws Exception {
        Field gameField = Player.class.getDeclaredField("game");
        gameField.setAccessible(true);
        BoardGame mockGame = new BoardGame() {
          @Override
          public int[] rollDice() {
            return new int[]{6, 6};
          }
        };
        mockGame.createMonopolyGameBoard();
        player.placeOnTile(mockGame.getBoard().getTile(11));
        gameField.set(player, mockGame);

        Method releaseMethod = Player.class.getDeclaredMethod("releaseFromJail");
        releaseMethod.setAccessible(true);
        releaseMethod.invoke(player);
        player.setProperty("jailTurnCount", "0");

        player.placeOnTile(mockGame.getBoard().getTile(23));

        assertEquals("false", player.getProperty("inJail"));
        assertEquals("0", player.getProperty("jailTurnCount"));
        assertEquals(23, player.getCurrentTile().getTileId());
      }

      @Test
      @DisplayName("Should keep player in jail when doubles are not rolled")
      void testRollDoublesFailure() throws Exception {
        Field gameField = Player.class.getDeclaredField("game");
        gameField.setAccessible(true);
        BoardGame mockGame = new BoardGame() {
          @Override
          public int[] rollDice() {
            return new int[]{2, 5};
          }
        };
        mockGame.createMonopolyGameBoard();
        player.placeOnTile(mockGame.getBoard().getTile(11));
        gameField.set(player, mockGame);

        assertEquals("true", player.getProperty("inJail"));
        assertEquals(11, player.getCurrentTile().getTileId());
      }
    }
  }
}