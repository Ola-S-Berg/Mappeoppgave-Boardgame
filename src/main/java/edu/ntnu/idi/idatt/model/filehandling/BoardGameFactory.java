package edu.ntnu.idi.idatt.model.filehandling;

import edu.ntnu.idi.idatt.model.actions.monopolygame.PropertyTileAction;
import edu.ntnu.idi.idatt.model.filehandling.exceptions.BoardFileException;
import edu.ntnu.idi.idatt.model.filehandling.exceptions.FileExceptionUtil;
import edu.ntnu.idi.idatt.model.filehandling.exceptions.FileHandlerException;
import edu.ntnu.idi.idatt.model.filehandling.exceptions.FileNotFoundException;
import edu.ntnu.idi.idatt.model.filehandling.exceptions.GameLoadException;
import edu.ntnu.idi.idatt.model.filehandling.exceptions.GameSaveException;
import edu.ntnu.idi.idatt.model.filehandling.exceptions.PlayerFileException;
import edu.ntnu.idi.idatt.model.gamelogic.BoardGame;
import edu.ntnu.idi.idatt.model.gamelogic.Player;
import edu.ntnu.idi.idatt.model.gamelogic.Tile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <h1>Board Game Factory</h1>
 *
 * <p>A factory class responsible for creating, loading and saving different variations of
 * board games. This class follows the Factory design pattern, providing centralized
 * creation methods for various board game types and handling game state operations.</p>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Creating predefined board game variants (Ladder game, Monopoly game)</li>
 *   <li>Saving game state to persistent storage</li>
 *   <li>Loading previously saved game states</li>
 *   <li>Managing game configuration and initialization</li>
 *   <li>Handling file operations for game storage</li>
 * </ul>
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
 */
public class BoardGameFactory {
  //Cannot be final due to tests:
  private static String SAVE_FILES_DIRECTORY = "src/main/resources/saves";
  static Logger LOGGER = Logger.getLogger(BoardGameFactory.class.getName());

  /**
   * Creates a classic ladder game.
   *
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
   *
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
   *
   * @return A configured board game.
   */
  public static BoardGame createLadderGameExtreme() {
    BoardGame boardGame = new BoardGame();
    boardGame.setVariantName("ladderGameExtreme");
    boardGame.createLadderGameBoard();
    boardGame.createDice();

    return boardGame;
  }

  /**
   * Creates a monopoly game variant.
   *
   * @return A configured board game.
   */
  public static BoardGame createMonopolyGame() {
    BoardGame boardGame = new BoardGame();
    boardGame.setVariantName("Monopoly Game");
    boardGame.createMonopolyGameBoard();
    boardGame.createDice();

    return boardGame;
  }

  /**
   * Gets a list of available board game variants.
   *
   * @return List of predefined game variants.
   */
  public static List<String> getAvailableVariants() {
    return Stream.of("Ladder Game Classic", "Ladder Game Advanced", "Ladder Game Extreme")
        .collect(Collectors.toList());
  }

  /**
   * Creates a board game from a predefined type.
   *
   * @param boardName The name of the board variant to create.
   * @return A configured board game.
   */
  public static BoardGame createBoardGame(String boardName) {
    return switch (boardName) {
      case "Ladder Game Advanced" -> createLadderGameAdvanced();
      case "Ladder Game Extreme" -> createLadderGameExtreme();
      case "Monopoly Game" -> createMonopolyGame();
      default -> createLadderGameClassic();
    };
  }

  /**
   * Save a board game to a file.
   *
   * @param boardGame The board game to save.
   * @param boardName The name to save the board game as.
   * @throws GameSaveException If an error occurs during saving the game.
   */
  public static void saveBoardGame(BoardGame boardGame, String boardName) {
    if (boardName == null) {
      throw new GameSaveException("Cannot save game with null name", null);
    }

    try {
      String gameType = getGameType(boardGame);
      String boardFilename = getBoardSaveFilePath(gameType, boardName);
      String playerFilename = getPlayerSaveFilePath(gameType, boardName);

      if (!boardGame.getPlayers().isEmpty() && boardGame.getCurrentPlayer() != null) {
        String currentPlayerName = boardGame.getCurrentPlayer().getName();
        boardGame.getPlayers().getFirst().setProperty("currentPlayerName", currentPlayerName);

        PlayerFileHandler playerFileHandler = new PlayerFileHandler();
        playerFileHandler.writeToFile(playerFilename, boardGame.getPlayers());
      }

      BoardFileHandler fileHandler = new BoardFileHandler();
      fileHandler.writeToFile(boardFilename, List.of(boardGame));
    } catch (IOException e) {
      throw FileExceptionUtil.createSaveException(boardName, e);
    } catch (GameSaveException e) {
      throw e;
    } catch (Exception e) {
      throw FileExceptionUtil.createSaveException(boardName,
          boardGame != null ? boardGame.getVariantName() : "unknown",
          "An unexpected error occurred while saving the game: " + e.getMessage());
    }
  }

  /**
   * Loads a saved game from specified save files. This method reads the board
   * configuration and player data from the corresponding save files, restores
   * the game state, initializes any missing components, and positions players
   * on the appropriate tiles.
   *
   * @param gameType The type of game to load (monopolygame, laddergame).
   * @param saveName The name of the save file to load the game from, excluding file extensions.
   * @return The loaded BoardGame instance with restored state and player positions.
   * @throws GameLoadException If an error occurs while loading the game.
   * @throws BoardFileException If an error occurs with the board file.
   * @throws PlayerFileException If an error occurs with the player file.
   */
  public static BoardGame loadSavedGame(String gameType, String saveName) {
    if (gameType == null) {
      throw new GameLoadException("null", "unknown", "Game type cannot be null");
    }

    if (saveName == null) {
      throw new GameLoadException("null", gameType, "Save name cannot be null");
    }

    try {
      String boardFilename = getBoardSaveFilePath(gameType, saveName);
      String playerFilename = getPlayerSaveFilePath(gameType, saveName);

      Path boardFilePath = Paths.get(boardFilename);
      Path playerFilePath = Paths.get(playerFilename);

      if (!Files.exists(boardFilePath)) {
        throw new FileNotFoundException(boardFilename);
      }

      if (!Files.exists(playerFilePath)) {
        throw new FileHandlerException("Player data file does not exist: " + playerFilename);
      }

      PlayerFileHandler playerFileHandler = new PlayerFileHandler();
      List<Player> players = playerFileHandler.readFromFile(playerFilename);

      BoardFileHandler fileHandler = new BoardFileHandler();
      List<BoardGame> loadedGames = fileHandler.readFromFile(boardFilename);

      if (loadedGames.isEmpty()) {
        throw FileExceptionUtil.createBoardFileException(gameType,
            "Board file exists but contains no valid board game data");
      }

      BoardGame loadedGame = loadedGames.getFirst();
      loadedGame.setIsLoadedGame(true);

      if (loadedGame.getDice() == null) {
        loadedGame.createDice();
      }

      if (players.isEmpty()) {
        throw FileExceptionUtil.createPlayerFileException(saveName,
            "Player file contains no valid player data");
      }

      int currentPlayerIndex = -1;

      for (int index = 0; index < players.size(); index++) {
        Player player = players.get(index);
        player.setGame(loadedGame);

        String savedTileIdStr = player.getProperty("savedTileId");
        if (savedTileIdStr != null && !savedTileIdStr.trim().isEmpty()) {
          try {
            int tileId = Integer.parseInt(savedTileIdStr.trim());
            Tile tile = loadedGame.getBoard().getTile(tileId);
            if (tile != null) {
              player.placeOnTile(tile);
              LOGGER.log(Level.INFO, "Player {0} placed on tile {1}",
                  new Object[]{player.getName(), tileId});
            } else {
              LOGGER.log(Level.WARNING, "Could not find saved tile ID {0}", tileId);
              player.placeOnTile(loadedGame.getBoard().getTile(1));
            }
          } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Could not parse saved tile ID: {0}", savedTileIdStr);
            player.placeOnTile(loadedGame.getBoard().getTile(1));
          }
        } else {
          LOGGER.log(Level.INFO, "No saved tile ID found for player {0}", player.getName());
          player.placeOnTile(loadedGame.getBoard().getTile(1));
        }

        String savedPropertiesStr = player.getProperty("savedProperties");
        if (savedPropertiesStr != null && !savedPropertiesStr.trim().isEmpty()) {
          String[] propertyNames = savedPropertiesStr.split(";");
          for (String propertyName : propertyNames) {
            if (!propertyName.trim().isEmpty()) {
              addPropertyToPlayer(loadedGame, player, propertyName.trim());
            }
          }
        }

        if ("true".equals(player.getProperty("isCurrentPlayer"))) {
          currentPlayerIndex = index;
        }

        loadedGame.addPlayer(player);
      }

      if (currentPlayerIndex >= 0 && currentPlayerIndex < players.size()) {
        loadedGame.initializeGameWithCurrentPlayer(currentPlayerIndex);
      } else {
        LOGGER.log(Level.WARNING, "Could not find current player in saved game,"
            + " starting with first player instead.");
        loadedGame.initializeGame();
      }

      return loadedGame;
    } catch (FileNotFoundException e) {
      throw FileExceptionUtil.createLoadException(saveName, gameType, "Save file not found: "
          + e.getFilename());
    } catch (FileHandlerException e) {
      throw FileExceptionUtil.createLoadException(saveName, gameType, e.getMessage());
    } catch (Exception e) {
      throw FileExceptionUtil.createLoadException(saveName, gameType,
          "An unexpected error occurred: "
              + e.getMessage());
    }
  }

  /**
   * Associates a property with a player in a Monopoly game.
   *
   * @param game The game containing the properties.
   * @param player The player to assign the property to.
   * @param propertyName The name of the property to assign.
   */
  private static void addPropertyToPlayer(BoardGame game, Player player, String propertyName) {
    if (!game.getVariantName().contains("Monopoly")) {
      return;
    }

    for (int i = 1; i <= 40; i++) {
      Tile tile = game.getBoard().getTile(i);
      if (tile != null && tile.getAction() instanceof PropertyTileAction propertyAction) {
        if (propertyAction.getPropertyName().equals(propertyName)) {
          player.addProperty(propertyAction);
          propertyAction.setOwner(player);
          break;
        }
      }
    }
  }

  /**
   * Ensures that the directory for saving files exists.
   * If the directory does not exist, it creates it.
   *
   * @param gameType The type of game.
   * @return The path to the game-specific save directory.
   */
  private static Path ensureSavesDirectory(String gameType) {
    Path savesBaseDir = Paths.get(SAVE_FILES_DIRECTORY);
    if (!Files.exists(savesBaseDir)) {
      try {
        Files.createDirectories(savesBaseDir);
      } catch (IOException e) {
        throw FileExceptionUtil.wrapWriteException(savesBaseDir.toString(), e);
      }
    }

    Path gameTypeDir = savesBaseDir.resolve(gameType);
    if (!Files.exists(gameTypeDir)) {
      try {
        Files.createDirectories(gameTypeDir);
      } catch (IOException e) {
        throw FileExceptionUtil.wrapWriteException(gameTypeDir.toString(), e);
      }
    }
    return gameTypeDir;
  }

  /**
   * Determines the appropriate game type from a BoardGame object.
   *
   * @param boardGame The board game to get the game type from.
   * @return A string identifying the game type (monopoly or ladder).
   */
  private static String getGameType(BoardGame boardGame) {
    if (boardGame == null) {
      throw new IllegalArgumentException("Board game cannot be null");
    }

    String variantName = boardGame.getVariantName();
    if (variantName != null && variantName.toLowerCase().contains("monopoly")) {
      return "monopolygame";
    } else {
      return "laddergame";
    }
  }

  /**
   * Resolves and returns the full file path for a save file, using the specified save name.
   * Ensures that the directory for saving files exists before constructing the file path.
   *
   * @param gameType The type of game to get the save path for.
   * @param saveName The name of the save file (without extension).
   * @return The full path to the save file, as a string.
   * @throws IOException If an I/O error occurs while ensuring the "saves" directory exists.
   */
  private static String getBoardSaveFilePath(String gameType, String saveName) throws IOException {
    Path savesDir = ensureSavesDirectory(gameType);
    return savesDir.resolve(saveName + "_board.json").toString();
  }

  /**
   * Resolves and returns the full file path for a player save file, using the specified save name.
   * Ensures that the directory for saving files exists before constructing the file path.
   *
   * @param gameType The type of game to get the save path for.
   * @param saveName The name of the save file (without extension).
   * @return The full path to the player save file, as a string.
   * @throws IOException If an I/O error occurs while ensuring the "saves" directory exists.
   */
  public static String getPlayerSaveFilePath(String gameType, String saveName) throws IOException {
    Path savesDir = ensureSavesDirectory(gameType);
    return savesDir.resolve(saveName + "_players.csv").toString();
  }
}