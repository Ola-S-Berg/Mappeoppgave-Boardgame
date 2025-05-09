package edu.ntnu.idi.idatt.model.filehandling;

import edu.ntnu.idi.idatt.model.actions.monopoly_game.PropertyTileAction;
import edu.ntnu.idi.idatt.model.gamelogic.BoardGame;
import edu.ntnu.idi.idatt.model.gamelogic.Player;

import edu.ntnu.idi.idatt.model.gamelogic.Tile;
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
  private static final String SAVE_FILES_DIRECTORY = "src/main/resources/saves";

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
   *
   * @param boardGame The board game to save.
   * @param boardName The name to save the board game as.
   * @throws IOException If an error occurs during file writing.
   */
  public static void saveBoardGame(BoardGame boardGame, String boardName) throws IOException {
    String gameType = getGameType(boardGame);
    String filename = getBoardSaveFilePath(gameType, boardName);

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
  public static BoardGame loadSavedGame(String gameType, String saveName) throws IOException {
    String boardFilename = getBoardSaveFilePath(gameType, saveName);
    String playerFilename = getPlayerSaveFilePath(gameType, saveName);

    BoardFileHandler fileHandler = new BoardFileHandler();
    BoardGame loadedGame = fileHandler.readFromFile(boardFilename).getFirst();

    loadedGame.setIsLoadedGame(true);

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
            System.out.println("Player " + player.getName() + " placed on tile " + tileId);
          } else {
            System.out.println("Could not find saved tile ID" + tileId);
            player.placeOnTile(loadedGame.getBoard().getTile(1));
          }
        } catch (NumberFormatException e) {
          System.out.println("Could not parse saved tile ID: " + savedTileIdStr);
          e.printStackTrace();
          player.placeOnTile(loadedGame.getBoard().getTile(1));
        }
      } else {
        System.out.println("No saved tile ID found for player " + player.getName());
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

      loadedGame.addPlayer(player);
    }

    loadedGame.initializeGame();
    return loadedGame;
  }

  /**
   * Associates a property with a player in a Monopoly game.
   *
   * @param game The game containing the properties.
   * @param player The player to assign the property to.
   * @param propertyName The name of the property to assign.
   */
  private static void addPropertyToPlayer(BoardGame game, Player player, String propertyName) {
    if(!game.getVariantName().contains("Monopoly")) {
      return;
    }

    for (int i = 1 ; i <= 40 ; i++) {
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
   * Ensures that the directory for saving files exists. If the directory does not exist, it creates it.
   *
   * @return The path to the "saves" directory.
   * @throws IOException If an I/O error occurs while checking for or creating the directory.
   */
  private static Path ensureSavesDirectory(String gameType) throws IOException {
    Path savesBaseDir = Paths.get(SAVE_FILES_DIRECTORY);
    if (!Files.exists(savesBaseDir)) {
      Files.createDirectories(savesBaseDir);
    }

    Path gameTypeDir = savesBaseDir.resolve(gameType);
    if (!Files.exists(gameTypeDir)) {
      Files.createDirectories(gameTypeDir);
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
    String variantName = boardGame.getVariantName();
    if (variantName != null && variantName.toLowerCase().contains("monopoly")) {
      return "monopoly_game";
    } else {
      return "ladder_game";
    }
  }

  /**
   * Resolves and returns the full file path for a save file, using the specified save name.
   * Ensures that the directory for saving files exists before constructing the file path.
   *
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
   * @param saveName The name of the save file (without extension).
   * @return The full path to the player save file, as a string.
   * @throws IOException If an I/O error occurs while ensuring the "saves" directory exists.
   */
  public static String getPlayerSaveFilePath(String gameType, String saveName) throws IOException {
    Path savesDir = ensureSavesDirectory(gameType);
    return savesDir.resolve(saveName + "_players.csv").toString();
  }
}