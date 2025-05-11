package edu.ntnu.idi.idatt;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.ntnu.idi.idatt.model.gamelogic.BoardGame;
import edu.ntnu.idi.idatt.model.gamelogic.Player;

class BoardGameTest {
  
  private BoardGame boardGame;

  @BeforeEach
  void setUp() {
    boardGame = new BoardGame();
  }

  @Test
  void addPlayer_Positive() {
   
    Player player = new Player("Alice", "Blue", boardGame, 200000);

    boardGame.addPlayer(player);

    assertEquals(player, boardGame.getPlayers().get(0), "Player should be added successfully.");
  }

  @Test
  void addPlayer_Negative_NullPlayer() {
    
    assertThrows(NullPointerException.class, () -> boardGame.addPlayer(null), "Should not allow adding a null player.");
  }

  @Test
  void createLadderGameBoard_Positive() {
   
    boardGame.createLadderGameBoard();

    assertNotNull(boardGame.getBoard(), "Board should be initialized.");
    assertEquals(90, boardGame.getBoard().getTile(90).getTileId(), "Tile 90 should exist.");
  }

  @Test
  void createLadderGameBoard_Negative_BeforeInitialization() {
    
    assertThrows(NullPointerException.class, () -> boardGame.getBoard().getTile(1), "Should not allow accessing board before initialization.");
  }

  @Test
  void createDice_Positive() {
    
    boardGame.createDice();
    assertNotNull(boardGame.getDice(), "Dice should be initialized.");
}

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
