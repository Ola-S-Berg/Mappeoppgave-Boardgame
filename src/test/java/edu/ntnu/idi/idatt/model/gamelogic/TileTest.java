package edu.ntnu.idi.idatt.model.gamelogic;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/* Test class for the Tile class. */
class TileTest {

    private Tile tile;

    /* Set up the tile before each test */
    @BeforeEach
    void setUp() {
        tile = new Tile(42);
    }
    
    /* Test that the tile id can be set and retrieved correctly */
    @Test
    void tileIdIsSetCorrectly() {
        assertEquals(42, tile.getTileId());
    }
    /* Test that the next tile can be set and retrieved correctly */
    @Test
    void canSetAndGetNextTile() {
        Tile next = new Tile(43);
        tile.setNextTile(next);
        assertEquals(43, tile.getNextTile().getTileId());
    }

    
}
