package edu.ntnu.idi.idatt.model.actions;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import edu.ntnu.idi.idatt.model.actions.monopolygame.ChanceTileAction;
import edu.ntnu.idi.idatt.model.gamelogic.BoardGame;
import edu.ntnu.idi.idatt.model.gamelogic.Player;
import edu.ntnu.idi.idatt.model.gamelogic.Tile;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ChanceTileActionTest {

  private ChanceTileAction chanceAction;
  private Player player;
  private BoardGame game;

  /* Set up the chance tile action and the game board before each test */
  @BeforeEach
  void setUp() {
    chanceAction = new ChanceTileAction();
    game = new BoardGame();
    game.createMonopolyGameBoard();
    player = new Player("Ola", "Blue", game, 200000);
    game.addPlayer(player);

    Player player2 = new Player("Markus", "Blue", game, 200000);
    Player player3 = new Player("Kari", "Red", game, 200000);
    game.addPlayer(player2);
    game.addPlayer(player3);
  }

  @Nested
  @DisplayName("Random Chance Action tests")
  class RandomChanceActionTests {
    private Method executeChanceActionMethod;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
      executeChanceActionMethod = ChanceTileAction.class.getDeclaredMethod(
          "executeChanceAction", Player.class, int.class);
      executeChanceActionMethod.setAccessible(true);
    }

    @Test
    @DisplayName("Action 0: Should move player forward 3 spaces")
    void testForward3Spaces() {
      Tile starTile = game.getBoard().getTile(5);
      player.placeOnTile(starTile);

      try {
        executeChanceActionMethod.invoke(chanceAction, player, 0);
        assertEquals(8, player.getCurrentTile().getTileId());
      } catch (Exception e) {
        fail("Exception occurred while executing action: " + e.getMessage());
      }
    }

    @Test
    @DisplayName("Action 0: Should handle null nextTile")
    void testForward3SpacesNullNextTile() {
      Tile mockTile = new Tile(5);
      mockTile.setNextTile(null);

      player.placeOnTile(mockTile);

      try {
        executeChanceActionMethod.invoke(chanceAction, player, 0);
        assertEquals(mockTile, player.getCurrentTile());
      } catch (Exception e) {
        fail("Exception occurred while handling null nextTile: " + e.getMessage());
      }
    }

    @Test
    @DisplayName("Action 1: Should add 5000 to player's money")
    void testCollect5000FromBank() {
      int initialMoney = player.getMoney();

      try {
        executeChanceActionMethod.invoke(chanceAction, player, 1);
        assertEquals(initialMoney + 5000, player.getMoney());
      } catch (Exception e) {
        fail("Exception occurred while executing action: " + e.getMessage());
      }
    }

    @Test
    @DisplayName("Action 2: Should remove 3000 from player's money")
    void testPay3000ToBank() {
      int initialMoney = player.getMoney();

      try {
        executeChanceActionMethod.invoke(chanceAction, player, 2);
        assertEquals(initialMoney - 3000, player.getMoney());
      } catch (Exception e) {
        fail("Exception occurred while executing action: " + e.getMessage());
      }
    }

    @Test
    @DisplayName("Action 2: Should handle insufficient funds and bankrupt player")
    void testPay3000ToBankInsufficientFunds() {
      player = new Player("Ola", "Blue", game, 1000);

      try {
        executeChanceActionMethod.invoke(chanceAction, player, 2);
        assertTrue(player.isBankrupt());
      } catch (Exception e) {
        fail("Exception occurred while handling insufficient funds: " + e.getMessage());
      }
    }

    @Test
    @DisplayName("Action 3: Should move player to nearest landmark")
    void testAdvanceToNearestLandmark() {
      player.placeOnTile(game.getBoard().getTile(13));

      try {
        executeChanceActionMethod.invoke(chanceAction, player, 3);
        assertEquals(16, player.getCurrentTile().getTileId());
      } catch (Exception ignored) {
      }
    }

    @Test
    @DisplayName("Action 3: Should handle null landmark")
    void testAdvanceToNonexistentLandmark() {
      player.placeOnTile(game.getBoard().getTile(21));

      BoardGame testGame = new BoardGame();
      player.setGame(testGame);

      try {
        executeChanceActionMethod.invoke(chanceAction, player, 3);
        assertEquals(21, player.getCurrentTile().getTileId());
      } catch (Exception ignored) {
      }
    }

    @Test
    @DisplayName("Action 4: Should pay each player 1000")
    void testPayEachPlayer1000() {
      Player player = game.getPlayers().get(0);
      Player player2 = game.getPlayers().get(1);
      Player player3 = game.getPlayers().get(2);
      int initialMoney = player.getMoney();
      int initialMoney2 = player2.getMoney();
      int initialMoney3 = player3.getMoney();

      try {
        executeChanceActionMethod.invoke(chanceAction, player, 4);

        assertEquals(initialMoney - 2000, player.getMoney());
        assertEquals(initialMoney2 + 1000, player2.getMoney());
        assertEquals(initialMoney3 + 1000, player3.getMoney());
      } catch (Exception ignored) {
      }
    }

    @Test
    @DisplayName("Action 4: Should handle insufficient funds when paying others and bankrupt player")
    void testPayEachPlayer1000InsufficientFund() {
      player = new Player("Ola", "Blue", game, 500);
      game.getPlayers().set(0, player);

      Player player2 = game.getPlayers().get(1);
      Player player3 = game.getPlayers().get(2);
      int initialMoney2 = player2.getMoney();
      int initialMoney3 = player3.getMoney();

      try {
        executeChanceActionMethod.invoke(chanceAction, player, 4);

        assertTrue(player.isBankrupt());

        assertTrue(player2.getMoney() >= initialMoney2);
        assertTrue(player3.getMoney() >= initialMoney3);
      } catch (Exception ignored) {

      }
    }

    @Test
    @DisplayName("Action 5: Should collect 1000 from each player")
    void testCollect1000FromEachPlayer() {
      Player player2 = game.getPlayers().get(1);
      Player player3 = game.getPlayers().get(2);
      int initialMoney = player.getMoney();
      int initialMoney2 = player2.getMoney();
      int initialMoney3 = player3.getMoney();

      try {
        executeChanceActionMethod.invoke(chanceAction, player, 5);

        assertEquals(initialMoney + 2000, player.getMoney());
        assertEquals(initialMoney2 - 1000, player2.getMoney());
        assertEquals(initialMoney3 - 1000, player3.getMoney());
      } catch (Exception ignored) {
      }
    }

    @Test
    @DisplayName("Action 5: Should handle others with insufficient funds")
    void testCollect1000FromEachPlayerWithInsufficientFunds() {
      Player player2 = new Player("Ola", "Yellow", game, 500);
      game.getPlayers().set(1, player2);

      int initialMoney = player.getMoney();
      int initialMoney3 = game.getPlayers().get(2).getMoney();

      try {
        executeChanceActionMethod.invoke(chanceAction, player, 5);

        assertTrue(player.getMoney() >= initialMoney);

        assertTrue(player2.getMoney() >= 0);

        assertEquals(initialMoney3 - 1000, game.getPlayers().get(2).getMoney());
      } catch (Exception ignored) {
      }
    }

    @Test
    @DisplayName("Should handle invalid action index")
    void testInvalidActionIndex() {
      int initialMoney = player.getMoney();
      Tile initialTile = player.getCurrentTile();

      try {
        executeChanceActionMethod.invoke(chanceAction, player, 999);

        assertEquals(initialMoney, player.getMoney());
        assertEquals(initialTile, player.getCurrentTile());
      } catch (Exception e) {
        fail("Exception occurred while handling invalid action index: " + e.getMessage());
      }
    }
  }

  @Test
  @DisplayName("Should perform random action when perform is called")
  void testPerformSelectsRandomAction() throws Exception {
    Field randomField = ChanceTileAction.class.getDeclaredField("random");
    randomField.setAccessible(true);
    Random originalRandom = (Random) randomField.get(null);

    Random mockRandom = new Random() {
      @Override
      public int nextInt(int bound) {
        return 1;
      }
    };

    try {
      randomField.set(null, mockRandom);

      int initialMoney = player.getMoney();

      chanceAction.perform(player);

      assertEquals(initialMoney + 5000, player.getMoney());
    } finally {
      randomField.set(null, originalRandom);
    }
  }

  @Test
  @DisplayName("Should handle null player")
  void testPerformWithNullPlayer() {
    assertDoesNotThrow(() -> chanceAction.perform(null));
  }
}