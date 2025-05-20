package edu.ntnu.idi.idatt.model.actions;

import edu.ntnu.idi.idatt.model.actions.laddergame.LadderAction;

import edu.ntnu.idi.idatt.model.gamelogic.BoardGame;
import edu.ntnu.idi.idatt.model.gamelogic.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LadderActionTest {

  private Player player;
  private BoardGame game;

  /* Set up the ladder action and the game board before each test */
  @BeforeEach
  void setUp() {
    game = new BoardGame();
    game.createLadderGameBoard();
    player = new Player("Markus", "Blue", game, 200000);
  }

  @Test
  @DisplayName("Should move player from tile 10 to tile 20")
  void ladderMovesPlayer() {
    player.placeOnTile(game.getBoard().getTile(10));
    LadderAction ladder = new LadderAction(20, "up");
    ladder.perform(player);
    assertEquals(20, player.getCurrentTile().getTileId());
  }
    @Test
    @DisplayName("Should move player from tile 89 to tile 90 and set winner")
    void ladderToWinningTile() {
      player.placeOnTile(game.getBoard().getTile(89));
      game.addPlayer(player);
      LadderAction ladder = new LadderAction(90, "up");
      ladder.perform(player);
    
      assertEquals(90, player.getCurrentTile().getTileId());
      assertEquals(player, game.getWinner());
  }

    @Test
    @DisplayName("Should handle invalid destination tile IDs")
    void ladderToInvalidTileThrows() {
      player.placeOnTile(game.getBoard().getTile(5));
      LadderAction ladder = new LadderAction(999, "up");
  
      assertThrows(IllegalStateException.class, () -> ladder.perform(player));
  }
}
