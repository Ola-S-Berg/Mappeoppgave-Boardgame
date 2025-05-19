package edu.ntnu.idi.idatt.model.filehandling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.ntnu.idi.idatt.model.actions.monopolygame.PropertyTileAction;
import edu.ntnu.idi.idatt.model.filehandling.exceptions.FileHandlerException;
import edu.ntnu.idi.idatt.model.filehandling.exceptions.GameLoadException;
import edu.ntnu.idi.idatt.model.filehandling.exceptions.GameSaveException;
import edu.ntnu.idi.idatt.model.gamelogic.BoardGame;
import edu.ntnu.idi.idatt.model.gamelogic.Player;
import edu.ntnu.idi.idatt.model.gamelogic.Tile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class BoardGameFactoryTest {

  private static final String TEST_SAVE_NAME = "testSave";
  private BoardGame ladderGameClassic;
  private BoardGame monopolyGame;

  @TempDir
  File tempDir;
  private Path savesDirectory;
  private Path ladderGameSavesDir;
  private Path monopolyGameSavesDir;

  @BeforeEach
  public void setUp() throws IOException {
    savesDirectory = tempDir.toPath().resolve("saves");
    ladderGameSavesDir = savesDirectory.resolve("laddergame");
    monopolyGameSavesDir = savesDirectory.resolve("monopolygame");

    Files.createDirectories(ladderGameSavesDir);
    Files.createDirectories(monopolyGameSavesDir);

    java.lang.reflect.Field field;
    try {
      field = BoardGameFactory.class.getDeclaredField("SAVE_FILES_DIRECTORY");
      field.setAccessible(true);
      field.set(null, savesDirectory.toString());
    } catch (Exception e) {
      System.err.println("Failed to set save directory for testing: " + e.getMessage());
    }

    ladderGameClassic = BoardGameFactory.createLadderGameClassic();
    monopolyGame = BoardGameFactory.createMonopolyGame();

    Player player1 = new Player("Ola", "Red", null, 200000);
    Player player2 = new Player("Markus", "Blue", null, 200000);

    monopolyGame.addPlayer(player1);
    monopolyGame.addPlayer(player2);
    monopolyGame.initializeGame();

    Tile startTile = monopolyGame.getBoard().getTile(1);
    player1.placeOnTile(startTile);
    player2.placeOnTile(startTile);

    PropertyTileAction property = new PropertyTileAction("Testgata", 4000, "Blue");
    property.setOwner(player1);
    player1.addProperty(property);

    Tile propertyTile = monopolyGame.getBoard().getTile(5);
    propertyTile.setAction(property);
  }

  @AfterEach
  public void tearDown() {
    try {
      if (Files.exists(savesDirectory)) {
        Files.walk(savesDirectory).sorted(Comparator.reverseOrder()).forEach(path -> {
          try {
            Files.deleteIfExists(path);
          } catch (IOException e) {
            System.err.println("Failed to delete path: " + path + ": " + e.getMessage());
          }
        });
      }
    } catch (IOException e) {
      System.err.println("Failed to delete saves directory: " + e.getMessage());
    }
  }

  @Test
  @DisplayName("Should create a Ladder Game Classic with correct properties")
  public void testCreateLadderGameClassic() {
    BoardGame game = BoardGameFactory.createLadderGameClassic();

    assertNotNull(game, "Created game should not be null");
    assertEquals("ladderGame", game.getVariantName(), "Game variant should be 'ladderGame'");
    assertNotNull(game.getBoard(), "Game should have a board");
    assertNotNull(game.getDice(), "Game should have a dice");
  }

  @Test
  @DisplayName("Should create a Ladder Game Advanced with correct properties")
  public void testCreateLadderGameAdvanced() {
    BoardGame game = BoardGameFactory.createLadderGameAdvanced();

    assertNotNull(game, "Created game should not be null");
    assertEquals("ladderGameAdvanced", game.getVariantName(),
        "Game variant should be 'ladderGameAdvanced'");
    assertNotNull(game.getBoard(), "Game should have a board");
    assertNotNull(game.getDice(), "Game should have a dice");
  }

  @Test
  @DisplayName("Should create a Ladder Game Extreme with correct properties")
  public void testCreateLadderGameExtreme() {
    BoardGame game = BoardGameFactory.createLadderGameExtreme();

    assertNotNull(game, "Created game should not be null");
    assertEquals("ladderGameExtreme", game.getVariantName(),
        "Game variant should be 'ladderGameExtreme'");
    assertNotNull(game.getBoard(), "Game should have a board");
    assertNotNull(game.getDice(), "Game should have a dice");
  }

  @Test
  @DisplayName("Should create a Monopoly Game with correct properties")
  public void testCreateMonopolyGame() {
    BoardGame game = BoardGameFactory.createMonopolyGame();

    assertNotNull(game, "Created game should not be null");
    assertEquals("Monopoly Game", game.getVariantName(),
        "Game variant should be 'Monopoly Game'");
    assertNotNull(game.getBoard(), "Game should have a board");
    assertNotNull(game.getDice(), "Game should have a dice");
  }

  @Test
  @DisplayName("Should create a board game from specified variant name")
  public void testCreateBoardGame() {
    String[] variants = {
        "Ladder Game Classic",
        "Ladder Game Advanced",
        "Ladder Game Extreme",
        "Monopoly Game",
        "Unknown Game"
    };

    for (String variant : variants) {
      BoardGame game = BoardGameFactory.createBoardGame(variant);
      assertNotNull(game, "Created game for variant '" + variant + "' should not be null");

      switch (variant) {
        case "Ladder Game Classic":
          assertEquals("ladderGame", game.getVariantName(),
              "Game variant should be 'ladderGame'");
          break;
        case "Ladder Game Advanced":
          assertEquals("ladderGameAdvanced", game.getVariantName(),
              "Game variant should be 'ladderGameAdvanced'");
          break;
        case "Ladder Game Extreme":
          assertEquals("ladderGameExtreme", game.getVariantName(),
              "Game variant should be 'ladderGameExtreme'");
          break;
        case "Monopoly Game":
          assertEquals("Monopoly Game", game.getVariantName(),
              "Game variant should be 'Monopoly Game'");
          break;
        default:
          assertEquals("ladderGame", game.getVariantName(),
              "Unknown variant should default to 'Ladder Game Classic'");
      }
    }
  }

  @Test
  @DisplayName("Should successfully save and load a Monopoly Game")
  public void testSaveAndLoadMonopolyGame() {
    BoardGameFactory.saveBoardGame(monopolyGame, TEST_SAVE_NAME);

    Path boardFilePath = monopolyGameSavesDir.resolve(TEST_SAVE_NAME + "_board.json");
    Path playerFilePath = monopolyGameSavesDir.resolve(TEST_SAVE_NAME + "_players.csv");

    assertTrue(Files.exists(boardFilePath), "Board file should be created");
    assertTrue(Files.exists(playerFilePath), "Player file should be created");

    BoardGame loadedGame = BoardGameFactory.loadSavedGame("monopolygame", TEST_SAVE_NAME);

    assertNotNull(loadedGame, "Loaded game should not be null");
    assertEquals("Monopoly Game", loadedGame.getVariantName(), "Should load a Monopoly Game");
    assertEquals(2, loadedGame.getPlayers().size(), "Should have 2 players");

    Player loadedPlayer1 = loadedGame.getPlayers().stream()
        .filter(p -> p.getName().equals("Ola"))
        .findFirst()
        .orElse(null);

    assertNotNull(loadedPlayer1, "Player 1 should be loaded");
    assertNotNull(loadedPlayer1.getCurrentTile(), "Player 1 should have a current tile");
  }

  @Test
  @DisplayName("Should successfully save and load a Ladder Game")
  public void testSaveAndLoadLadderGame() {
    Player player1 = new Player("Ola", "Red", null, 0);
    Player player2 = new Player("Markus", "Blue", null, 0);

    ladderGameClassic.addPlayer(player1);
    ladderGameClassic.addPlayer(player2);
    ladderGameClassic.initializeGame();

    player1.placeOnTile(ladderGameClassic.getBoard().getTile(1));
    player2.placeOnTile(ladderGameClassic.getBoard().getTile(10));

    BoardGameFactory.saveBoardGame(ladderGameClassic, TEST_SAVE_NAME);

    Path boardFilePath = ladderGameSavesDir.resolve(TEST_SAVE_NAME + "_board.json");
    Path playerFilePath = ladderGameSavesDir.resolve(TEST_SAVE_NAME + "_players.csv");

    assertTrue(Files.exists(boardFilePath), "Board file should be created");
    assertTrue(Files.exists(playerFilePath), "Player file should be created");

    BoardGame loadedGame = BoardGameFactory.loadSavedGame("laddergame", TEST_SAVE_NAME);

    assertNotNull(loadedGame, "Loaded game should not be null");
    assertEquals("ladderGame", loadedGame.getVariantName(), "Should load a Ladder game");
    assertEquals(2, loadedGame.getPlayers().size(), "Should have 2 players");

    Player loadedPlayer1 = loadedGame.getPlayers().stream()
        .filter(p -> p.getName().equals("Ola"))
        .findFirst()
        .orElse(null);

    assertNotNull(loadedPlayer1, "Player 'Ola' should be loaded");
    assertNotNull(loadedPlayer1.getCurrentTile(), "Player should be placed on a tile");
  }

  @Test
  @DisplayName("Should throw GameSaveException when saving game with null name")
  public void testSaveGameWithNullName() {
    assertThrows(GameSaveException.class,
        () -> BoardGameFactory.saveBoardGame(monopolyGame, null),
        "Should throw GameSaveException when save name is null");
  }

  @Test
  @DisplayName("Should throw GameLoadException when loading non-existent save")
  public void testLoadNonExistentSave() {
    assertThrows(GameLoadException.class,
        () -> BoardGameFactory.loadSavedGame("laddergame", "nonExistentSave"),
        "Should throw GameLoadException when save does not exist");
  }

  @Test
  @DisplayName("Should throw GameLoadException when loading with null game type")
  public void testLoadSaveWithNullGameType() {
    assertThrows(GameLoadException.class,
        () -> BoardGameFactory.loadSavedGame(null, TEST_SAVE_NAME),
        "Should throw GameLoadException when game type is null");
  }

  @Test
  @DisplayName("Should throw GameLoadException when loading with null save name")
  public void testLoadSaveWithNullSaveName() {
    assertThrows(GameLoadException.class,
        () -> BoardGameFactory.loadSavedGame("laddergame", null),
        "Should throw GameLoadException when save name is null");
  }

  @Test
  @DisplayName("Should properly handle corrupted player save file")
  public void testLoadCorruptedPlayerSaveFile() throws IOException {
    BoardGameFactory.saveBoardGame(monopolyGame, TEST_SAVE_NAME);

    Path playerFilePath = monopolyGameSavesDir.resolve(TEST_SAVE_NAME + "_players.csv");
    Files.write(playerFilePath, "Invalid,Content,That,Will,Break,Parsing".getBytes());

    assertThrows(FileHandlerException.class,
        () -> BoardGameFactory.loadSavedGame("monopolygame", TEST_SAVE_NAME),
        "Should throw exception when player file is corrupted");
  }

  @Test
  @DisplayName("Should correctly identify game type from BoardGame instance")
  public void testGetGameType() throws Exception {
    java.lang.reflect.Method getGameTypeMethod = BoardGameFactory.class
        .getDeclaredMethod("getGameType", BoardGame.class);
    getGameTypeMethod.setAccessible(true);

    String monopolyType = (String) getGameTypeMethod.invoke(null, monopolyGame);
    assertEquals("monopolygame", monopolyType, "Should identify Monopoly game type");

    String ladderType = (String) getGameTypeMethod.invoke(null, ladderGameClassic);
    assertEquals("laddergame", ladderType, "Should identify Ladder game type");
  }

  @Test
  @DisplayName("Should get player save file path correctly")
  public void testGetPlayerSaveFilePath() throws IOException {
    String playerFilePath = BoardGameFactory.getPlayerSaveFilePath("laddergame", TEST_SAVE_NAME);

    assertTrue(playerFilePath.contains("laddergame"),
        "Player save path should contain game type directory");
    assertTrue(playerFilePath.endsWith(TEST_SAVE_NAME + "_players.csv"),
        "Player save path should end with correct filename");
  }

  @Test
  @DisplayName("Should preserve the current player when saving and loading game")
  public void testCurrentPlayerPreservation() {
    Player player1 = monopolyGame.getPlayers().getFirst();

    BoardGameFactory.saveBoardGame(monopolyGame, TEST_SAVE_NAME);
    BoardGame loadedGame = BoardGameFactory.loadSavedGame("monopolygame", TEST_SAVE_NAME);

    assertNotNull(loadedGame.getCurrentPlayer(), "Current player should be set");
    assertEquals(player1.getName(), loadedGame.getCurrentPlayer().getName(),
        "Current player should be preserved");
  }

  @Test
  @DisplayName("Should load game and initialize dice")
  public void testDiceInitializationOnLoad() {
    BoardGameFactory.saveBoardGame(monopolyGame, TEST_SAVE_NAME);
    BoardGame loadedGame = BoardGameFactory.loadSavedGame("monopolygame", TEST_SAVE_NAME);

    assertNotNull(loadedGame.getDice(), "Dice should be initialized in loaded game");
  }
}