package edu.ntnu.idi.idatt.model.gamelogic;

import static edu.ntnu.idi.idatt.model.gamelogic.Board.setupTileActionsMonopolyGame;

import edu.ntnu.idi.idatt.model.filehandling.BoardFileHandler;
import java.util.List;
import java.util.ArrayList;

/**
 * The class that handles information about the game.
 */
public class BoardGame {

  private Board board;
  private Player currentPlayer;
  private final List<Player> players = new ArrayList<>();
  private Dice dice;
  private String variantName;
  private final List<BoardGameObserver> observers = new ArrayList<>();
  private int currentPlayerIndex;
  private boolean gameOver;
  private boolean isLoadedGame = false;

  public BoardGame() {
    BoardFileHandler fileHandler = new BoardFileHandler();
    this.currentPlayerIndex = 0;
    this.gameOver = false;
  }

  /**
   * Registers an observer to receive game state updates.
   * @param observer The observer to register.
   */
  public void addObserver (BoardGameObserver observer) {
   if (observer == null) {
     throw new NullPointerException("Observer cannot be null");
    }

    observers.add(observer);
  }

  /**
   * Removes an observer from the list of registered observers.
   * @param observer The observer to remove.
   */
  public void removeObserver(BoardGameObserver observer) {
    observers.remove(observer);
  }

  /**
   * Notifies all observers when a player has moved.
   * @param player The player who moved.
   * @param fromTileId The tile ID the player moved from.
   * @param toTileId The tile ID the player moved to.
   * @param diceValue The value rolled on the die.
   */
  public void notifyPlayerMove(Player player, int fromTileId, int toTileId, int diceValue) {
    for (BoardGameObserver observer : observers) {
      observer.onPlayerMove(player, fromTileId, toTileId, diceValue);
    }
  }

  /**
   * Notifies all observers when a player has won the game.
   * @param player The player that won the game.
   */
  public void notifyGameWon(Player player) {
    for (BoardGameObserver observer : observers) {
      observer.onGameWon(player);
    }
  }

  /**
   * Notifies all observers when a player skips their turn.
   * @param player The player who skipped their turn.
   */
  public void notifyPlayerSkipTurn(Player player) {
    for (BoardGameObserver observer : observers) {
      observer.onPlayerSkipTurn(player);
    }
  }

  /**
   * Notifies all observers when the current player changes.
   * @param player The new current player.
   */
  public void notifyCurrentPlayerChanged(Player player) {
    for (BoardGameObserver observer : observers) {
      observer.onCurrentPlayerChanged(player);
    }
  }

  /**
   * Adds a player when called upon.
   * @param player The player to add.
   */
  public void addPlayer(Player player) {
    if (player == null) {
        throw new NullPointerException("Player cannot be null.");
    }
    players.add(player);
}

  /**
   * Creates an instance of the Ladder Game Board.
   * Adds 90 tiles, with unique identifiers ranging from 1 to 90.
   * Links the tiles in sequential order, with exceptions for the last tile.
   * Configures the board's tile actions based on the game variant, if specified.
   */
  public void createLadderGameBoard() {
    board = new Board();

    for (int i = 1; i <= 90; i++) {
      board.addTile(new Tile(i));
    }

    for (int i = 1; i <= 9; i++) {
      for (int j = 1; j <= 10; j++) {
        int currentId = (i - 1) * 10 + j;

        int nextId;
        if (i == 9 && j == 10) {
          nextId = -1;
        } else {
          nextId = currentId + 1;
        }

        if (nextId != -1) {
          Tile currentTile = board.getTile(currentId);
          Tile nextTile = board.getTile(nextId);
          currentTile.setNextTile(nextTile);
        }
      }
    }

    if (variantName != null) {
      switch (variantName) {
        case "ladderGame":
          Board.setupTileActionsLadderGame(board);
          break;
        case "ladderGameAdvanced":
          Board.setupTileActionsLadderGameAdvanced(board);
          break;
        case "ladderGameExtreme":
          Board.setupTileActionsLadderGameExtreme(board);
          break;
        default:
          System.out.println("Unknown game variant: " + variantName);
      }
    }
  }

  /**
   * Creates a standard Monopoly game board with 40 tiles arranged in a square.
   */
  public void createMonopolyGameBoard() {
    this.board = new Board();

    for (int i = 1; i <= 40; i++) {
      Tile tile = new Tile(i);
      board.addTile(tile);
    }

    for (int i = 1; i <= 40; i++) {
      Tile currentTile = board.getTile(i);

      if (i < 40) {
        currentTile.setNextTile(board.getTile(i + 1));
      } else {
        currentTile.setNextTile(board.getTile(1));
      }
    }
    setupTileActionsMonopolyGame(board);
  }

  /**
   * Creates a number of dice.
   */
  public void createDice() {

    dice = new Dice(2);
  }

  /**
   * Gets the winner of the game.
   * @return The name of the player that won.
   */
  public Player getWinner() {
    for (Player player : players) {
      if (player.getCurrentTile().getTileId() == 90) {
        return player;
      }
    }
    return null;
  }

  public void setIsLoadedGame(boolean isLoadedGame) { this.isLoadedGame = isLoadedGame; }

  /**
   * Initializes the game by setting up the initial game state and preparing players.
   * This includes defining the starting player, placing all players on the first tile,
   * and notifying observers about the current player.
   */
  public void initializeGame() {
    currentPlayerIndex = 0;
    gameOver = false;

    if (!players.isEmpty()) {
      currentPlayer = players.get(currentPlayerIndex);
    }

    if (!isLoadedGame) {
      for (Player player : players) {
        player.placeOnTile(board.getTile(1));
      }
    }

    if (currentPlayer != null) {
      notifyCurrentPlayerChanged(currentPlayer);
    }
  }

  /**
   * Processes the current player's turn in the game. If the game is over, it ends the turn
   * immediately. If the current player is set to skip their turn, it notifies observers, resets the
   * skip state, and ends the turn. Otherwise, the current player rolls the dice, calculates their
   * movement, and updates their position accordingly. Observers are notified of the player's
   * movement.
   */
  public void processTurn() {
    if (gameOver) {
      return;
    }

    if (currentPlayer.willWaitTurn()) {
      System.out.println(currentPlayer.getName() + " will skip their turn");
      notifyPlayerSkipTurn(currentPlayer);
      currentPlayer.setWaitTurn(false);
      return;
    }

    int steps = dice.roll();
    int fromTileId = currentPlayer.getCurrentTile().getTileId();
    System.out.println(currentPlayer.getName() + " rolled " + steps);

    movePlayer(currentPlayer, steps, fromTileId);
  }

  /**
   * Handles passing Start in the Monopoly game.
   *
   * @param player The player who passed start.
   * @param fromTileId The ID of the tile the player is moving from.
   * @param toTileId The ID of the tile the player is moving to.
   */
  private void checkPassedStart(Player player, int fromTileId, int toTileId) {
    if (fromTileId > toTileId && toTileId != 1) {
      player.addMoney(20000);
      System.out.println(player.getName() + " passed start and received 20000");
    }
  }

  /**
   * Handles a player going bankrupt and being removed from the game.
   *
   * @param player The player who went bankrupt.
   */
  public void playerBankrupt(Player player) {
    if(!players.contains(player)) {
      return;
    }

    notifyPlayerBankrupt(player);

    checkGameOver();
  }

  /**
   * Notifies all observers when a player goes bankrupt.
   *
   * @param player The player who went bankrupt.
   */
  public void notifyPlayerBankrupt(Player player) {
    for (BoardGameObserver observer : observers) {
      observer.onPlayerBankrupt(player);
    }
  }

  /**
   * Checks if the game is over by checking if only one player is left in the game.
   * If so, declares that player as the winner.
   */
  public void checkGameOver() {
    List<Player> activePlayers = getActivePlayers();

    if (activePlayers.size() == 1) {
      Player winner = activePlayers.getFirst();
      gameOver = true;
      notifyGameWon(winner);
    }
  }

  /**
   * Gets a list of active (non-bankrupt) players.
   *
   * @return List of active players.
   */
  public List<Player> getActivePlayers() {
    List<Player> activePlayers = new ArrayList<>();
    for (Player player: players) {
      if (!player.isBankrupt()) {
        activePlayers.add(player);
      }
    }
    return activePlayers;
  }

  /**
   * Moves the specified player a given number of steps on the game board, starting from a specified
   * tile. Notifies observers about the move and checks for game completion.
   *
   * @param player     The player to be moved.
   * @param steps      The number of steps the player will move.
   * @param fromTileId The ID of the tile the player is moving from.
   */
  public void movePlayer(Player player, int steps, int fromTileId) {
    player.move(steps);
    int toTileId = player.getCurrentTile().getTileId();

    if (variantName != null && variantName.equals("monopolyGame")) {
      checkPassedStart(player, fromTileId, toTileId);
    }

    notifyPlayerMove(player, fromTileId, toTileId, steps);

    System.out.println(player.getName() + " is now on tile " + toTileId);

    if (getWinner() != null) {
      gameOver = true;
      notifyGameWon(getWinner());
    }

  }

  /**
   * Rolls all the dice in the game and retrieves their rolled values.
   *
   * @return An array of integers representing the values rolled for each die.
   */
  public int[] rollDice() {
    int numberOfDice = dice.getNumberOfDice();
    int[] values = new int[numberOfDice];

    dice.roll();

    for (int i = 0; i < numberOfDice; i++) {
      values[i] = dice.getDie(i);
    }

    return values;
  }


  /**
   * Advances the game to the next player's turn. If the game is over or there are no players, the
   * method returns null. Otherwise, it updates the current player to the next player in the
   * sequence and notifies observers about the change if applicable.
   */
  public void advanceToNextPlayer() {
    if (players.isEmpty() || gameOver) {
      return;
    }

    Player previousPlayer = currentPlayer;

    int startingIndex = currentPlayerIndex;
    do {
      currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
      if (startingIndex == currentPlayerIndex) {
        break;
      }
    } while (players.get(currentPlayerIndex).isBankrupt());

    if (!players.get(currentPlayerIndex).isBankrupt()) {
      currentPlayer = players.get(currentPlayerIndex);
    }

    if (previousPlayer != currentPlayer) {
      notifyCurrentPlayerChanged(currentPlayer);
    }

     checkGameOver();
  }

  /**
   * Play-method that runs a game automatically. Kept for backwards compatibility with tests.
   * Initializes the game, processes turns, and advances players until a winner is found.
   */
  public void play() {
    int roundNumber = 1;

    initializeGame();

    while (getWinner() == null) {
      System.out.println("Round " + roundNumber);

      for (int i = 0; i < players.size() && !gameOver; i++) {
        processTurn();

        if (!gameOver) {
          advanceToNextPlayer();
        }
      }

      roundNumber++;
    }
  }

  /**
   * Retrieves the current player in the game.
   *
   * @return The Player object representing the current player.
   */
  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  /**
   * Accessor method that gets the dice.
   * @return The dice.
   */
  public Dice getDice() {

    return dice;
  }

  /**
   * Accessor method that gets the current player.
   * @return The current player.
   */
  public List<Player> getPlayers() {

    return players;
  }

  /**
   * Accessor method that gets the board.
   * @return The board.
   */
  public Board getBoard() {
    return board;
  }

  /**
   * Retrieves the name of the game variant currently in use.
   *
   * @return The name of the variant as a String.
   */
  public String getVariantName() {
    return variantName;
  }

  /**
   * Sets the name of the game variant to the specified value.
   *
   * @param variantName The name of the variant to set.
   */
  public void setVariantName(String variantName) {
    this.variantName = variantName;
  }
}