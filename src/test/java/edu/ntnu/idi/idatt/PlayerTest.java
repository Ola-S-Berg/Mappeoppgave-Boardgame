package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.GameLogic.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private BoardGame game;
    private Player player;


    /* Arranging a boardgame and a player for each test */

    @BeforeEach
    void setUp() {
        game = new BoardGame();
        game.createBoard();
        player = new Player("Markus", "Blue", game);
    }

    /* A test that makes sure the name is returned properly */

    @Test
    void nameIsSetCorrectly() {
        assertEquals("Markus", player.getName());
    }

    /* Tests that the player is placed on a tile */

    @Test
    void placeOnTileWorks() {
        Tile tile = game.getBoard().getTile(5);
        player.placeOnTile(tile);
        assertEquals(5, player.getCurrentTile().getTileId());
    }

    /* Tests that the player moves the correct amount of steps */

    @Test
    void moveStepsForward() {
        player.placeOnTile(game.getBoard().getTile(1));
        player.move(4);
        assertEquals(5, player.getCurrentTile().getTileId());
    }

    /* Tests that the player moves from the first tile if not placed */

    @Test
    void moveStartsFromTileOneIfNotPlaced() {
        player.move(3);
        assertEquals(4, player.getCurrentTile().getTileId());
    }

    /* Tests that the player stops at the last tile */
    
    @Test
    void moveStopsAtLastTile() {
        player.placeOnTile(game.getBoard().getTile(89)); 
        player.move(5); // shouldn't go beyond 90
        assertEquals(90, player.getCurrentTile().getTileId());
    }

    /* Tests that the player info is returned by ToString method */

    @Test
    void toStringReturnsUsefulInfo() {
        player.placeOnTile(game.getBoard().getTile(10));
        String output = player.toString();
        assertTrue(output.contains("Markus"));
        assertTrue(output.contains("10"));
    }

    /* Tests that the Boardgame is the current game the player is playing */

    @Test
    void getGameReturnsBoardGame() {
        assertEquals(game, player.getGame());
    }
}
