package edu.ntnu.idi.idatt.model.gamelogic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/* Test class for the BoardGame class. */
class BoardGameTest {
  
  private BoardGame boardGame;

  /* Set up the board game before each test */
  @BeforeEach
  void setUp() {
    boardGame = new BoardGame();
  }

  /* Test that the addPlayer method works by adding a player and checking if the player is added successfully */
  @Test
  void addPlayer_Positive() {
   
    Player player = new Player("Alice", "Blue", boardGame, 200000);

    boardGame.addPlayer(player);

    assertEquals(player, boardGame.getPlayers().get(0), "Player should be added successfully.");
  }

  /* Test that the addPlayer method throws an exception when adding a null player */
  @Test
  void addPlayer_Negative_NullPlayer() {
    
    assertThrows(NullPointerException.class, () -> boardGame.addPlayer(null), "Should not allow adding a null player.");
  }

  /* Test that the createLadderGameBoard method works by getting a board and checking if the tile 90 exists */
  @Test
  void createLadderGameBoard_Positive() {
   
    boardGame.createLadderGameBoard();

    assertNotNull(boardGame.getBoard(), "Board should be initialized.");
    assertEquals(90, boardGame.getBoard().getTile(90).getTileId(), "Tile 90 should exist.");
  }

  /* Test that the createLadderGameBoard method throws an exception when accessing the board before initialization */
  @Test
  void createLadderGameBoard_Negative_BeforeInitialization() {
    
    assertThrows(NullPointerException.class, () -> boardGame.getBoard().getTile(1), "Should not allow accessing board before initialization.");
  }

  /* Test that the createDice method works */
  @Test
  void createDice_Positive() {
    
    boardGame.createDice();
    assertNotNull(boardGame.getDice(), "Dice should be initialized.");
  }

  /* Test that the playGame method works */
  @Test
  void playGame_Positive() {
    
    boardGame.createLadderGameBoard();
    boardGame.createDice();
    
    Player player1 = new Player("Alice", "Blue", boardGame, 200000);
    Player player2 = new Player("Bob", "Red", boardGame, 200000);
    
    boardGame.addPlayer(player1);
    boardGame.addPlayer(player2);
    
    
    boardGame.play();
    
    
    assertNotNull(boardGame.getWinner(), "There should be a winner at the end of the game.");
  }
}
