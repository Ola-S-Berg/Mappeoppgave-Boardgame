package edu.ntnu.idi.idatt.Filehandling;

import edu.ntnu.idi.idatt.GameLogic.BoardGame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory class for creating different variants of board games.
 * Can create predefined game types or load games from files.
 */
public class BoardGameFactory {
  private static final String GAME_FILES_DIRECTORY = "src/main/resources/Games";
  private static final String SAVE_FILES_DIRECTORY = "src/main/resources/Saves";

  /**
   * Creates a classic ladder game.
   * @return A configured board game.
   */
  public static BoardGame createClassicLadderGame1() {
    BoardGame boardGame = new BoardGame();
    boardGame.setVariantName("ladderGame1");
    boardGame.createBoard();
    boardGame.createDice();

    return boardGame;
  }

  /**
   * Creates a second version of the classic ladder game with mixed tile actions.
   * @return A configured board game.
   */
  public static BoardGame createClassicLadderGame2() {
    BoardGame boardGame = new BoardGame();
    boardGame.setVariantName("ladderGame2");
    boardGame.createBoard();
    boardGame.createDice();

    return boardGame;
  }

  /**
   * Creates a third version of the classic ladder game with mixed tile actions.
   * @return A configured board game.
   */
  public static BoardGame createClassicLadderGame3() {
    BoardGame boardGame = new BoardGame();
    boardGame.setVariantName("ladderGame3");
    boardGame.createBoard();
    boardGame.createDice();

    return boardGame;
  }

  /**
   * Gets a list of available board game files.
   * @return List of board game file names.
   */
  public static List<String> getAvailableBoardGames() {
    List<String> boardNames = new ArrayList<>();

    boardNames.add("Classic Ladder Game 1");
    boardNames.add("Classic Ladder Game 2");
    boardNames.add("Classic Ladder Game 3");

    try {
      Path gamesDir = ensureGamesDirectory();
      List<String> fileBoards = Files.list(gamesDir)
          .filter(path -> path.toString().endsWith(".json"))
          .map(path -> path.getFileName().toString().replace(".json", ""))
          .collect(Collectors.toList());

      boardNames.addAll(fileBoards);
    } catch (IOException e) {
      System.err.println("Error reading game files: " + e.getMessage());
    }

    return boardNames;
  }

  /**
   * Creates a board game from a file or predefined type.
   *
   * @param boardName The name of the board to create.
   * @return A configured board game.
   */
  public static BoardGame createBoardGame(String boardName) {
    if (boardName.equals("Classic Ladder Game 1")) {
      return createClassicLadderGame1();
    } else if (boardName.equals("Classic Ladder Game 2")) {
      return createClassicLadderGame2();
    } else if (boardName.equals("Classic Ladder Game 3")) {
      return createClassicLadderGame3();
    } else {

      try {
        String filename = getGameFilePath(boardName);
        BoardFileHandler fileHandler = new BoardFileHandler();
        List<BoardGame> games = fileHandler.readFromFile(filename);
        if (!games.isEmpty()) {
          return games.get(0);
        }
      } catch (IOException e) {
        System.err.println("Error loading game from file: " + e.getMessage());
      }
    }

    return createClassicLadderGame1();
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
   * Ensures the "Games" directory exists and returns its path.
   * @return Path to the "Games" directory.
   * @throws IOException If directory creation fails.
   */
  private static Path ensureGamesDirectory() throws IOException {
    Path gamesDir = Paths.get(GAME_FILES_DIRECTORY);
    if (!Files.exists(gamesDir)) {
      Files.createDirectories(gamesDir);
    }
    return gamesDir;
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
   * Gets the full file path for a game board file.
   * @param boardName The name of the board.
   * @return The full file path.
   */
  private static String getGameFilePath(String boardName) throws IOException {
    Path gamesDir = ensureGamesDirectory();
    return gamesDir.resolve(boardName + ".json").toString();
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