package edu.ntnu.idi.idatt.Filehandling;

import edu.ntnu.idi.idatt.GameLogic.BoardGame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory class for creating different variants of board games.
 * Can create predefined game types or load games from saved files.
 */
public class BoardGameFactory {
  private static final String SAVE_FILES_DIRECTORY = "src/main/resources/Saves";

  /**
   * Creates a classic ladder game.
   * @return A configured board game.
   */
  public static BoardGame createClassicLadderGame() {
    BoardGame boardGame = new BoardGame();
    boardGame.setVariantName("ladderGame");
    boardGame.createBoard();
    boardGame.createDice();

    return boardGame;
  }

  /**
   * Creates a second version of the classic ladder game with mixed tile actions.
   * @return A configured board game.
   */
  public static BoardGame createClassicLadderGameAdvanced() {
    BoardGame boardGame = new BoardGame();
    boardGame.setVariantName("ladderGameAdvanced");
    boardGame.createBoard();
    boardGame.createDice();

    return boardGame;
  }

  /**
   * Creates a third version of the classic ladder game with mixed tile actions.
   * @return A configured board game.
   */
  public static BoardGame createClassicLadderGameExtreme() {
    BoardGame boardGame = new BoardGame();
    boardGame.setVariantName("ladderGameExtreme");
    boardGame.createBoard();
    boardGame.createDice();

    return boardGame;
  }

  /**
   * Gets a list of available board game variants.
   * @return List of predefined game variants.
   */
  public static List<String> getAvailableVariants() {
    List<String> boardNames = new ArrayList<>();

    boardNames.add("Ladder Game Classic");
    boardNames.add("Ladder Game Advanced");
    boardNames.add("Ladder Game Extreme");

    return boardNames;
  }

  /**
   * Creates a board game from a predefined type.
   *
   * @param boardName The name of the board variant to create.
   * @return A configured board game.
   */
  public static BoardGame createBoardGame(String boardName) {
    return switch (boardName) {
      case "Ladder Game Classic" -> createClassicLadderGame();
      case "Ladder Game Advanced" -> createClassicLadderGameAdvanced();
      case "Ladder Game Extreme" -> createClassicLadderGameExtreme();
      default -> createClassicLadderGame(); // Default to classic game
    };
  }

  /**
   * Save a board game to a file.
   * @param boardGame The board game to save.
   * @param boardName The name to save the board game as.
   * @throws IOException If an error occurs during file writing.
   */
  public static void saveBoardGame(BoardGame boardGame, String boardName) throws IOException {
    String filename = getSaveFilePath(boardName);
    BoardFileHandler fileHandler = new BoardFileHandler();
    fileHandler.writeToFile(filename, List.of(boardGame));
  }

  /**
   * Ensures that the directory for saving files exists. If the directory does not exist, it creates it.
   *
   * @return The path to the "saves" directory.
   * @throws IOException If an I/O error occurs while checking for or creating the directory.
   */
  private static Path ensureSavesDirectory() throws IOException {
    Path savesDir = Paths.get(SAVE_FILES_DIRECTORY);
    if (!Files.exists(savesDir)) {
      Files.createDirectories(savesDir);
    }
    return savesDir;
  }

  /**
   * Resolves and returns the full file path for a save file, using the specified save name.
   * Ensures that the directory for saving files exists before constructing the file path.
   *
   * @param saveName The name of the save file (without extension).
   * @return The full path to the save file, as a string.
   * @throws IOException If an I/O error occurs while ensuring the "saves" directory exists.
   */
  private static String getSaveFilePath(String saveName) throws IOException {
    Path savesDir = ensureSavesDirectory();
    return savesDir.resolve(saveName + ".json").toString();
  }
}