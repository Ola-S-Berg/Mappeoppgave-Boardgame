package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.model.actions.laddergame.LadderAction;

import edu.ntnu.idi.idatt.model.gamelogic.BoardGame;
import edu.ntnu.idi.idatt.model.gamelogic.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LadderActionTest {

    private Player player;
    private BoardGame game;

    @BeforeEach
    void setUp() {
        game = new BoardGame();
        game.createLadderGameBoard();
        player = new Player("Markus", "Blue", game);
    }

    @Test
    void ladderMovesPlayer() {
        player.placeOnTile(game.getBoard().getTile(10));
        LadderAction ladder = new LadderAction(20, "Climb to 20");
        ladder.perform(player);
        assertEquals(20, player.getCurrentTile().getTileId());
    }

    @Test
    void ladderToWinningTile() {
    player.placeOnTile(game.getBoard().getTile(89));
      game.addPlayer(player);
      LadderAction ladder = new LadderAction(90, "Climb to finish line");
      ladder.perform(player);
    
      assertEquals(90, player.getCurrentTile().getTileId());
      assertEquals(player, game.getWinner());
}

    @Test
    void ladderToInvalidTileThrows() {
      player.placeOnTile(game.getBoard().getTile(5));
      LadderAction ladder = new LadderAction(999, "Invalid destination");
  
      assertThrows(IllegalStateException.class, () -> ladder.perform(player));
  }
}
