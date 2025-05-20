package edu.ntnu.idi.idatt.model.filehandling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import edu.ntnu.idi.idatt.model.actions.laddergame.BackToStartAction;
import edu.ntnu.idi.idatt.model.actions.laddergame.LadderAction;
import edu.ntnu.idi.idatt.model.actions.laddergame.WaitAction;
import edu.ntnu.idi.idatt.model.actions.monopolygame.ChanceTileAction;
import edu.ntnu.idi.idatt.model.actions.monopolygame.FreeParkingAction;
import edu.ntnu.idi.idatt.model.actions.monopolygame.GoToJailAction;
import edu.ntnu.idi.idatt.model.actions.monopolygame.JailTileAction;
import edu.ntnu.idi.idatt.model.actions.monopolygame.PropertyTileAction;
import edu.ntnu.idi.idatt.model.actions.monopolygame.StartTileAction;
import edu.ntnu.idi.idatt.model.actions.monopolygame.TaxTileAction;
import edu.ntnu.idi.idatt.model.actions.monopolygame.WealthTaxTileAction;
import edu.ntnu.idi.idatt.model.filehandling.exceptions.BoardFileException;
import edu.ntnu.idi.idatt.model.filehandling.exceptions.DataFormatException;
import edu.ntnu.idi.idatt.model.filehandling.exceptions.FileReadException;
import edu.ntnu.idi.idatt.model.filehandling.exceptions.FileWriteException;
import edu.ntnu.idi.idatt.model.gamelogic.BoardGame;
import edu.ntnu.idi.idatt.model.gamelogic.Tile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class BoardFileHandlerTest {

  private BoardFileHandler boardFileHandler;
  private BoardGame monopolyGame;
  private BoardGame ladderGame;
  private static final String TEST_FILE_NAME = "testBoard.json";

  /* Create a temporary directory for the test */
  @TempDir
  File tempDir;
  private String testFilePath;

  /* Set up the board file handler and the monopoly game board before each test */
  @BeforeEach
  public void setUp() {
    boardFileHandler = new BoardFileHandler();
    monopolyGame = new BoardGame();
    monopolyGame.setVariantName("Monopoly Game");
    monopolyGame.createMonopolyGameBoard();

    Tile tile = monopolyGame.getBoard().getTile(1);
    tile.setAction(new StartTileAction());

    tile = monopolyGame.getBoard().getTile(11);
    tile.setAction(new JailTileAction());

    tile = monopolyGame.getBoard().getTile(21);
    tile.setAction(new FreeParkingAction());

    tile = monopolyGame.getBoard().getTile(31);
    tile.setAction(new GoToJailAction(11));

    tile = monopolyGame.getBoard().getTile(2);
    tile.setAction(new PropertyTileAction("Testgata", 2000, "Blue"));

    tile = monopolyGame.getBoard().getTile(5);
    tile.setAction(new TaxTileAction(10, 20000));

    tile = monopolyGame.getBoard().getTile(37);
    tile.setAction(new WealthTaxTileAction(10000));

    tile = monopolyGame.getBoard().getTile(3);
    tile.setAction(new ChanceTileAction());

    ladderGame = new BoardGame();
    ladderGame.setVariantName("Ladder Game");
    ladderGame.createLadderGameBoard();

    tile = ladderGame.getBoard().getTile(25);
    tile.setAction(new LadderAction(7, "down"));

    tile = ladderGame.getBoard().getTile(37);
    tile.setAction(new WaitAction());

    tile = ladderGame.getBoard().getTile(10);
    tile.setAction(new BackToStartAction());

    testFilePath = tempDir.getAbsolutePath() + File.separator + TEST_FILE_NAME;
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

  @Test
  @DisplayName("Should successfully serialize a Ladder Game board")
  public void serializeLadderGameBoard() {
    JsonObject boardJson = boardFileHandler.serializeBoard(ladderGame);

    assertNotNull(boardJson, "Serialized JSON should not be null");
    assertEquals("Ladder Game", boardJson.get("name").getAsString());
    assertEquals("Ladder Game", boardJson.get("variantName").getAsString());
    assertFalse(boardJson.getAsJsonArray("tiles").isEmpty(),
        "Board should have tiles");
  }

  @Test
  @DisplayName("Should successfully serialize a Monopoly game board")
  public void serializeMonopolyGameBoard() {
    JsonObject boardJson = boardFileHandler.serializeBoard(monopolyGame);

    assertNotNull(boardJson, "Serialized JSON should not be null");
    assertEquals("Monopoly Game", boardJson.get("name").getAsString());
    assertEquals("Monopoly Game", boardJson.get("variantName").getAsString());
    assertFalse(boardJson.getAsJsonArray("tiles").isEmpty(),
        "Board should have tiles");
  }

  @Test
  @DisplayName("Should throw exception when serializing null board game")
  public void testSerializeBoardWithNullGame() {

    assertThrows(BoardFileException.class, () ->
        boardFileHandler.serializeBoard(null),
        "Should throw BoardFileException when serializing null board game");
  }

  @Test
  @DisplayName("Should throw exception when serializing board game with null variant name")
  public void testSerializeBoardWithNullVariantName() {
    BoardGame invalidGame = new BoardGame();

    assertThrows(BoardFileException.class, () ->
        boardFileHandler.serializeBoard(invalidGame),
        "Should throw BoardFileException when serializing board game with null variant name");
  }

  @Test
  @DisplayName("Should successfully write Monopoly game to file and read it back")
  public void writeAndReadMonopolyGameBoard() throws FileWriteException, BoardFileException,
                                                     FileReadException, DataFormatException {
    boardFileHandler.writeToFile(testFilePath, List.of(monopolyGame));

    File file = new File(testFilePath);
    assertTrue(file.exists(), "Board game file should have been created");

    List <BoardGame> readGames = boardFileHandler.readFromFile(testFilePath);

    assertNotNull(readGames, "Read board games should not be null");
    assertEquals(1, readGames.size(), "Should have read one board game");
    BoardGame readGame = readGames.getFirst();
    assertEquals("Monopoly Game", readGame.getVariantName(),
        "Should have read Monopoly game");

    Tile tile = readGame.getBoard().getTile(1);
    assertInstanceOf(StartTileAction.class, tile.getAction(),
        "Tile 1 should have StartTileAction");

    tile = readGame.getBoard().getTile(2);
    assertInstanceOf(PropertyTileAction.class, tile.getAction(),
        "Tile 2 should have PropertyTileAction");
    PropertyTileAction propertyAction = (PropertyTileAction) tile.getAction();
    assertEquals("Testgata", propertyAction.getPropertyName());
    assertEquals(2000, propertyAction.getCost());
    assertEquals("Blue", propertyAction.getPropertyType());
  }

  @Test
  @DisplayName("Should successfully write Ladder game to file and read it back")
  public void writeAndReadLadderGameBoard() throws FileWriteException, BoardFileException,
                                                   FileReadException, DataFormatException {
    boardFileHandler.writeToFile(testFilePath, List.of(ladderGame));

    File file = new File(testFilePath);
    assertTrue(file.exists(), "Board game file should have been created");

    List <BoardGame> readGames = boardFileHandler.readFromFile(testFilePath);

    assertNotNull(readGames, "Read board games should not be null");
    assertEquals(1, readGames.size(), "Should have read one board game");
    BoardGame readGame = readGames.getFirst();
    assertEquals("Ladder Game", readGame.getVariantName(),
        "Should have read Ladder Game");

    Tile tile = readGame.getBoard().getTile(25);
    assertInstanceOf(LadderAction.class, tile.getAction(),
        "Tile 25 should have LadderAction");
    LadderAction ladderAction = (LadderAction) tile.getAction();
    assertEquals(7, ladderAction.getDestinationTileId());
    assertEquals("down", ladderAction.getDirection());

    tile = readGame.getBoard().getTile(37);
    assertInstanceOf(WaitAction.class, tile.getAction(), "Tile should have WaitAction");

    tile = readGame.getBoard().getTile(10);
    assertInstanceOf(BackToStartAction.class, tile.getAction(),
        "Tile 10 should have BackToStartAction");
  }

  @Test
  @DisplayName("Should throw exception when writing to null filename")
  public void testWriteToFileWithNullName() {

    assertThrows(BoardFileException.class, () ->
        boardFileHandler.writeToFile(null, List.of(monopolyGame)),
        "Should throw FileWriteException when writing to null filename");
  }

  @Test
  @DisplayName("Should throw exception when writing to empty filename")
  public void testWriteToFileWithEmptyName() {

    assertThrows(BoardFileException.class, () ->
        boardFileHandler.writeToFile("", List.of(monopolyGame)),
        "Should throw FileWriteException when writing to empty filename");
  }

  @Test
  @DisplayName("Should throw exception when writing null board list")
  public void testWriteToFileWithNullBoardList() {

    assertThrows(BoardFileException.class, () ->
        boardFileHandler.writeToFile(testFilePath, null),
        "Should throw FileWriteException when writing null board list");
  }

  @Test
  @DisplayName("Should throw exception when reading from null filename")
  public void testReadFromFileWithNullFilename() {

    assertThrows(FileReadException.class, () ->
            boardFileHandler.readFromFile(null),
        "Should throw FileReadException when filename is null");
  }

  @Test
  @DisplayName("Should throw exception when reading from empty filename")
  public void testReadFromFileWithEmptyFilename() {
    String emptyFilename = "";

    assertThrows(FileReadException.class, () ->
            boardFileHandler.readFromFile(emptyFilename),
        "Should throw FileReadException when filename is empty");
  }

  @Test
  @DisplayName("Should throw exception when reading non-existent file")
  public void testReadFromNonExistentFile() {
    String nonExistentFile = tempDir.getAbsolutePath() + File.separator + "non_existent.json";

    assertThrows(FileReadException.class, () ->
        boardFileHandler.readFromFile(nonExistentFile),
        "Should throw FileReadException when file doesn't exist");
  }

  @Test
  @DisplayName("Should throw exception when reading invalid JSON file")
  public void testReadInvalidJsonFile() throws IOException {
    String invalidContent = "This is not valid JSON";
    Files.write(Paths.get(testFilePath), invalidContent.getBytes());

    assertThrows(DataFormatException.class, () ->
        boardFileHandler.readFromFile(testFilePath),
        "Should throw DataFormatException when file contains invalid JSON");
  }

  @Test
  @DisplayName("Should throw exception when reading JSON without required properties")
  public void testReadJsonWithMissingProperties() throws IOException {
    String invalidContent = "{ \"name\": \"Test Game\" }";
    Files.write(Paths.get(testFilePath), invalidContent.getBytes());

    assertThrows(DataFormatException.class, () ->
        boardFileHandler.readFromFile(testFilePath),
        "Should throw DataFormatException when JSON is missing required properties");
  }
}
