package edu.ntnu.idi.idatt;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.Tile;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void addTile_Works() {
        
        Tile tile = new Tile(1);

        
        board.addTile(tile);

        
        assertEquals(tile, board.getTile(1), "Tile should be added successfully.");
    }

    @Test
    void getTile_ReturnsCorrectTile() {
       
        Tile tile1 = new Tile(1);
        Tile tile2 = new Tile(2);
        board.addTile(tile1);
        board.addTile(tile2);

        
        assertEquals(tile2, board.getTile(2), "Should return the correct tile.");
    }

    @Test
    void getTile_ReturnsNullForNonExistentTile() {
       
        assertNull(board.getTile(140), "Should return null for a tile that does not exist.");
    }

    @Test
    void addMultipleTiles_Works() {
        
        for (int i = 1; i <= 5; i++) {
            board.addTile(new Tile(i));
        }

        
        for (int i = 1; i <= 5; i++) {
            assertNotNull(board.getTile(i), "Tile " + i + " should exist on the board.");
        }
    }
}
