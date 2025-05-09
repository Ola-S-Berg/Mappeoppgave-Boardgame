package edu.ntnu.idi.idatt.filehandling;

import edu.ntnu.idi.idatt.model.BoardGame;
import edu.ntnu.idi.idatt.model.Player;

import edu.ntnu.idi.idatt.model.Tile;
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
  public static BoardGame createLadderGameClassic() {
    BoardGame boardGame = new BoardGame();
    boardGame.setVariantName("ladderGame");
    boardGame.createLadderGameBoard();
    boardGame.createDice();

    return boardGame;
  }

  /**
   * Creates a second version of the classic ladder game with mixed tile actions.
   * @return A configured board game.
   */
  public static BoardGame createLadderGameAdvanced() {
    BoardGame boardGame = new BoardGame();
    boardGame.setVariantName("ladderGameAdvanced");
    boardGame.createLadderGameBoard();
    boardGame.createDice();

    return boardGame;
  }

  /**
   * Creates a third version of the classic ladder game with mixed tile actions.
   * @return A configured board game.
   */
  public static BoardGame createLadderGameExtreme() {
    BoardGame boardGame = new BoardGame();
    boardGame.setVariantName("ladderGameExtreme");
    boardGame.createLadderGameBoard();
    boardGame.createDice();

    return boardGame;
  }

  public static BoardGame createMonopolyGame() {
    BoardGame boardGame = new BoardGame();
    boardGame.setVariantName("Monopoly Game");
    boardGame.createMonopolyGameBoard();
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
      case "Ladder Game Classic" -> createLadderGameClassic();
      case "Ladder Game Advanced" -> createLadderGameAdvanced();
      case "Ladder Game Extreme" -> createLadderGameExtreme();
      case "Monopoly Game" -> createMonopolyGame();
      default -> createLadderGameClassic(); // Default to classic game
    };
  }

  /**
   * Save a board game to a file.
   * @param boardGame The board game to save.
   * @param boardName The name to save the board game as.
   * @throws IOException If an error occurs during file writing.
   */
  public static void saveBoardGame(BoardGame boardGame, String boardName) throws IOException {
    String filename = getBoardSaveFilePath(boardName);
    BoardFileHandler fileHandler = new BoardFileHandler();
    fileHandler.writeToFile(filename, List.of(boardGame));
  }


  /**
   * Loads a saved game from specified save files. This method reads the board
   * configuration and player data from the corresponding save files, restores
   * the game state, initializes any missing components, and positions players
   * on the appropriate tiles.
   *
   * @param saveName The name of the save file to load the game from, excluding file extensions.
   * @return The loaded BoardGame instance with restored state and player positions.
   * @throws IOException If an error occurs while reading the save files.
   */
  public static BoardGame loadSavedGame(String saveName) throws IOException {
    String boardFilename = getBoardSaveFilePath(saveName);
    String playerFilename = getPlayerSaveFilePath(saveName);

    BoardFileHandler fileHandler = new BoardFileHandler();
    BoardGame loadedGame = fileHandler.readFromFile(boardFilename).getFirst();

    if (loadedGame.getDice() == null) {
      loadedGame.createDice();
    }

    PlayerFileHandler playerFileHandler = new PlayerFileHandler();
    List<Player> players = playerFileHandler.readFromFile(playerFilename);

    for (Player player : players) {
      player.setGame(loadedGame);

      String savedTileIdStr = player.getProperty("savedTileId");
      if (savedTileIdStr != null && !savedTileIdStr.trim().isEmpty()) {
        try {
          int tileId = Integer.parseInt(savedTileIdStr.trim());
          Tile tile = loadedGame.getBoard().getTile(tileId);
          if (tile != null) {
            player.placeOnTile(tile);
          } else {
            player.placeOnTile(loadedGame.getBoard().getTile(1));
          }
        } catch (NumberFormatException e) {
          player.placeOnTile(loadedGame.getBoard().getTile(1));
        }
      } else {
        player.placeOnTile(loadedGame.getBoard().getTile(1));
      }

      loadedGame.addPlayer(player);
    }
    return loadedGame;
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
  private static String getBoardSaveFilePath(String saveName) throws IOException {
    Path savesDir = ensureSavesDirectory();
    return savesDir.resolve(saveName + "_board.json").toString();
  }

  /**
   * Resolves and returns the full file path for a player save file, using the specified save name.
   * Ensures that the directory for saving files exists before constructing the file path.
   *
   * @param saveName The name of the save file (without extension).
   * @return The full path to the player save file, as a string.
   * @throws IOException If an I/O error occurs while ensuring the "saves" directory exists.
   */
  private static String getPlayerSaveFilePath(String saveName) throws IOException {
    Path savesDir = ensureSavesDirectory();
    return savesDir.resolve(saveName + "_players.csv").toString();
  }
}