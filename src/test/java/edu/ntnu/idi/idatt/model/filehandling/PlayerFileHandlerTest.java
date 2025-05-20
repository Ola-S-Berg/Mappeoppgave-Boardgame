package edu.ntnu.idi.idatt.model.filehandling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.ntnu.idi.idatt.model.actions.monopolygame.PropertyTileAction;
import edu.ntnu.idi.idatt.model.filehandling.exceptions.PlayerDataFormatException;
import edu.ntnu.idi.idatt.model.filehandling.exceptions.PlayerFileWriteException;
import edu.ntnu.idi.idatt.model.gamelogic.Player;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/* Test class for the PlayerFileHandler class. */
public class PlayerFileHandlerTest {

  private PlayerFileHandler playerFileHandler;
  private Player player1;
  private Player player2;
  private Player player3;
  private List<Player> players;

  /* Create a temporary directory for the test */
  @TempDir
  File tempDir;
  private String testFilePath;

  /* Set up the player file handler before each test by creating a new player file handler, and creating 3 players with 200000 money */
  @BeforeEach
  public void SetUp() {
    playerFileHandler = new PlayerFileHandler();
    player1 = new Player("Ola", "Red", null, 200000);
    player2 = new Player("Markus", "Blue", null, 200000);
    player3 = new Player("Kari", "Green", null, 200000);

    player1.setProperty("currentPlayerName", "Ola");

    PropertyTileAction property = new PropertyTileAction(
        "Testgata", 2000, "Blue");
    player2.addProperty(property);

    player3.addProperty(new PropertyTileAction("Testgata2", 3000, "Green"));
    player3.addProperty(new PropertyTileAction("Testgata3", 4000, "Red"));

    players = new ArrayList<>();
    players.add(player1);
    players.add(player2);
    players.add(player3);

    testFilePath = tempDir.getAbsolutePath() + File.separator + "players.csv";
  }

  /* Delete the test file after each test */
  @AfterEach
  public void tearDown() {
    try {
      Files.deleteIfExists(Paths.get(testFilePath));
    } catch (Exception e) {
      System.err.println("Failed to delete test file: " + e.getMessage());
    }
  }

  /* Test that the writeToFile method works by writing player data to a file and reading it back */
  @Test
  @DisplayName("Should successfully write player data to file and read it back")
  public void testWriteAndReadPlayerData() {
    playerFileHandler.writeToFile(testFilePath, players);

    File file = new File(testFilePath);
    assertTrue(file.exists(), "Player file should have been created");

    List<Player> loadedPlayers = playerFileHandler.readFromFile(testFilePath);

    assertNotNull(loadedPlayers, "Loaded players list should not be null");
    assertEquals(3, loadedPlayers.size(), "Loaded players list should have 3 elements");

    Player loadedPlayer1 = loadedPlayers.stream().filter(p -> p.getName().equals("Ola")).findFirst().orElse(null);
    assertNotNull(loadedPlayer1, "First player should be loaded");
    assertEquals("Red", loadedPlayer1.getToken(), "First player's token should be Red");
    assertEquals(200000, loadedPlayer1.getMoney(), "First player should have 200000 money");
    assertEquals("true", loadedPlayer1.getProperty("isCurrentPlayer"),
        "First player should be marked as current player");

    Player loadedPlayer2 = loadedPlayers.stream().filter(p -> p.getName().equals("Markus")).findFirst().orElse(null);
    assertNotNull(loadedPlayer2, "Second player should be loaded");
    assertEquals("Blue", loadedPlayer2.getToken(), "Second player's token should be Blue");
    assertEquals(200000, loadedPlayer2.getMoney(), "Second player should have 200000 money");
    assertEquals("false", loadedPlayer2.getProperty("isCurrentPlayer"),
        "Second player should not be marked as current player");

    Player loadedPlayer3 = loadedPlayers.stream().filter(p -> p.getName().equals("Kari")).findFirst().orElse(null);
    assertNotNull(loadedPlayer3, "Third player should be loaded");
    assertEquals("Green", loadedPlayer3.getToken(), "Third player's token should be Green");
    assertEquals(200000, loadedPlayer3.getMoney(), "Third player should have 200000 money");
    assertEquals("false", loadedPlayer3.getProperty("isCurrentPlayer"),
        "Third player should not be marked as current player");
  }

  /* Test that the handleCurrentPlayer method works by setting the current player to Markus and writing the data to a file and reading it back */
  @Test
  @DisplayName("Should handle current player correctly")
  public void testHandleCurrentPlayer() {
    player1.setProperty("currentPlayerName", null);
    player2.setProperty("currentPlayerName", "Markus");

    playerFileHandler.writeToFile(testFilePath, players);
    List<Player> loadedPlayers = playerFileHandler.readFromFile(testFilePath);

    Player currentPlayer = loadedPlayers.stream().filter(p -> "true".equals(p.getProperty("isCurrentPlayer"))).findFirst().orElse(null);

    assertNotNull(currentPlayer, "Current player should be loaded");
    assertEquals("Markus", currentPlayer.getName(), "Current player should be Markus");
  }

  /* Test that the default current player method works by setting the current player to null and writing the data to a file and reading it back */
  @Test
  @DisplayName("Should set first player as current when no current player specified")
  public void testDefaultCurrentPlayer() {
    player1.setProperty("currentPlayerName", null);
    player2.setProperty("currentPlayerName", null);
    player3.setProperty("currentPlayerName", null);

    playerFileHandler.writeToFile(testFilePath, players);
    List<Player> loadedPlayers = playerFileHandler.readFromFile(testFilePath);

    boolean hasCurrentPlayer = loadedPlayers.stream().anyMatch(p ->
        "true".equals(p.getProperty("isCurrentPlayer")));
    assertTrue(hasCurrentPlayer, "Current player should be set");

    assertEquals("true", loadedPlayers.getFirst().getProperty("isCurrentPlayer"),
        "First player should be marked as current by default");
  }

  /* Test that the writeToFile method throws an exception when writing to a null filename */
  @Test
  @DisplayName("Should throw exception when writing to null filename")
  public void testWriteToFileWithNullFilename() {

    assertThrows(PlayerFileWriteException.class, () ->
        playerFileHandler.writeToFile(null, players),
        "Should throw PlayerFileWriteException when filename is null");
  }

  /* Test that the writeToFile method throws an exception when writing to an empty player list */
  @Test
  @DisplayName("Should throw exception when writing empty player list")
  public void testWriteToFileWithEmptyPlayerList() {
    List<Player> emptyPlayerList = new ArrayList<>();

    assertThrows(PlayerFileWriteException.class, () ->
        playerFileHandler.writeToFile(testFilePath, emptyPlayerList),
        "Should throw PlayerFileWriteException when player list is empty");
  }

  /* Test that the readFromFile method throws an exception when reading from a null filename */
  @Test
  @DisplayName("Should throw exception when reading from null filename")
  public void testReadFromFileWithNullFilename() {

    assertThrows(PlayerFileWriteException.class, () ->
        playerFileHandler.readFromFile(null),
        "Should throw PlayerFileWriteException when filename is null");
  }

  /* Test that the readFromFile method throws an exception when reading from an empty filename */
  @Test
  @DisplayName("Should throw exception when reading from empty filename")
  public void testReadFromFileWithEmptyFilename() {

    assertThrows(PlayerFileWriteException.class, () ->
        playerFileHandler.readFromFile(""),
        "Should throw PlayerFileWriteException when filename is empty");
  }

  /* Test that the readFromFile method throws an exception when reading from a non-existent file */
  @Test
  @DisplayName("Should throw exception when reading non-existent file")
  public void testReadFromNonExistentFile() {

    assertThrows(PlayerFileWriteException.class, () ->
        playerFileHandler.readFromFile("non-existent-file.csv"),
        "Should throw PlayerFileWriteException when file does not exist");
  }

  /* Test that the readFromFile method throws an exception when reading from a file with an invalid CSV format */
  @Test
  @DisplayName("Should throw exception when reading file with invalid CSV file format")
  public void testReadFromFileWithInvalidCSVFormat() throws IOException {
    String invalidContent = "Ola, Red, InvalidTileID, 200000\n" + "Markus, Blue, missing fields";
    Files.write(Paths.get(testFilePath), invalidContent.getBytes());

    assertThrows(PlayerDataFormatException.class, () ->
        playerFileHandler.readFromFile(testFilePath),
        "Should throw PlayerFileWriteException when file has invalid CSV format");
  }

  /* Test that the readFromFile method throws an exception when reading from a CSV file with an invalid money value */
  @Test
  @DisplayName("Should throw exception when reading CSV with invalid money value")
  public void testReadCSVWithInvalidMoneyValue() throws IOException {
    String invalidContent = "Ola, Red, 1, not a number";
    Files.write(Paths.get(testFilePath), invalidContent.getBytes());

    assertThrows(PlayerDataFormatException.class, () ->
        playerFileHandler.readFromFile(testFilePath),
        "Should throw PlayerFileWriteException when CSV has invalid money value");
  }

  /* Test that the readFromFile method throws an exception when reading from an empty CSV file */
  @Test
  @DisplayName("Should throw exception when reading empty CSV file")
  public void testReadEmptyCSVFile() throws IOException {
    Files.write(Paths.get(testFilePath), new byte[0]);

    assertThrows(PlayerDataFormatException.class, () ->
        playerFileHandler.readFromFile(testFilePath),
        "Should throw PlayerFileWriteException when CSV file is empty");
  }
}