package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.GameLogic.Tile;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TileTest {

    private Tile tile;

    @BeforeEach
    void setUp() {
        tile = new Tile(42);
    }

    @Test
    void tileIdIsSetCorrectly() {
        assertEquals(42, tile.getTileId());
    }

    @Test
    void canSetAndGetNextTile() {
        Tile next = new Tile(43);
        tile.setNextTile(next);
        assertEquals(43, tile.getNextTile().getTileId());
    }

    
}
