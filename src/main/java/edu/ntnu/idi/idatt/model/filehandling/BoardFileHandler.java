package edu.ntnu.idi.idatt.model.filehandling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
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
import edu.ntnu.idi.idatt.model.filehandling.exceptions.FileExceptionUtil;
import edu.ntnu.idi.idatt.model.filehandling.exceptions.FileReadException;
import edu.ntnu.idi.idatt.model.filehandling.exceptions.FileWriteException;
import edu.ntnu.idi.idatt.model.gamelogic.BoardGame;
import edu.ntnu.idi.idatt.model.gamelogic.Tile;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * <h1>Board File Handler</h1>
 *
 * <p>Specialized file handler that manages the serialization and deserialization of
 * board game objects using JSON format. This class provides functionality to persist the complete
 * state of a board game, including its tiles and their associated actions.</p>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>JSON serialization of complete board game structures using GSON</li>
 *   <li>Preservation of game variant information</li>
 *   <li>Handling of tile relationships and action types</li>
 *   <li>Full reconstruction of game boards from saved files</li>
 * </ul>
 *
 * <h2>JSON format structure</h2>
 *
 * <p>The JSON file format follows the following structure:</p>
 * <ul>
 *   <li>Game metadata (name, description, variant)</li>
 *   <li>Complete tile collection with IDs</li>
 *   <li>Tile action configurations with type-specific properties</li>
 *   <li>Inter-tile relationships and connections</li>
 * </ul>
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
 */
public class BoardFileHandler implements FileHandler<BoardGame> {

  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  /**
   * Serializes a BoardGame object into a JSON representation, including the board's tiles
   * and their properties such as tile ID, the next tile in the sequence, and their actions.
   *
   * @param boardGame The BoardGame object to be serialized.
   * @return A JsonObject representing the serialized board game.
   */
  public JsonObject serializeBoard(BoardGame boardGame) {
    if (boardGame == null) {
      throw FileExceptionUtil.createBoardFileException("Cannot serialize null board game object.");
    }

    if (boardGame.getVariantName() == null || boardGame.getVariantName().isEmpty()) {
      throw FileExceptionUtil.createBoardFileException("Invalid board game: missing variant name");
    }

    JsonObject boardJson = new JsonObject();
    String variantName = boardGame.getVariantName();

    if ("Monopoly Game".equals(variantName) || "monopolyGame".equals(variantName)) {
      boardJson.addProperty("name", "Monopoly Game");
      boardJson.addProperty("description", "A classic Monopoly game with 40 tiles.");
    } else {
      boardJson.addProperty("name", "Ladder Game");
      boardJson.addProperty("description", "A ladder game with 90 tiles.");
    }

    boardJson.addProperty("variantName", variantName);

    JsonArray tilesArray = new JsonArray();

    int maxTileId = ("Monopoly Game".equals(variantName)
        || "monopolyGame".equals(variantName)) ? 40 : 90;

    for (int i = 1; i <= maxTileId; i++) {
      Tile tile = boardGame.getBoard().getTile(i);
      if (tile == null) {
        continue;
      }

      JsonObject tileJson = new JsonObject();
      tileJson.addProperty("id", tile.getTileId());

      if (tile.getNextTile() != null) {
        if (tile.getAction() instanceof LadderAction ladderAction) {
          tileJson.addProperty("actionType", "ladder");
          tileJson.addProperty("destination", ladderAction.getDestinationTileId());
          tileJson.addProperty("direction", ladderAction.getDirection());
        } else if (tile.getAction() instanceof BackToStartAction) {
          tileJson.addProperty("actionType", "backToStart");
        } else if (tile.getAction() instanceof WaitAction) {
          tileJson.addProperty("actionType", "wait");
        } else if (tile.getAction() instanceof PropertyTileAction propertyTileAction) {
          tileJson.addProperty("actionType", "property");
          tileJson.addProperty("propertyName", propertyTileAction.getPropertyName());
          tileJson.addProperty("cost", propertyTileAction.getCost());
          tileJson.addProperty("type", propertyTileAction.getPropertyType());
        } else if (tile.getAction() instanceof ChanceTileAction) {
          tileJson.addProperty("actionType", "chance");
        } else if (tile.getAction() instanceof StartTileAction) {
          tileJson.addProperty("actionType", "start");
        } else if (tile.getAction() instanceof JailTileAction) {
          tileJson.addProperty("actionType", "jail");
        } else if (tile.getAction() instanceof GoToJailAction goToJailAction) {
          tileJson.addProperty("actionType", "goToJail");
          tileJson.addProperty("jailTileId", goToJailAction.getJailTileId());
        } else if (tile.getAction() instanceof FreeParkingAction) {
          tileJson.addProperty("actionType", "freeParking");
        } else if (tile.getAction() instanceof TaxTileAction taxTileAction) {
          tileJson.addProperty("actionType", "tax");
          tileJson.addProperty("percentageTax", taxTileAction.getPercentageTax());
          tileJson.addProperty("fixedTax", taxTileAction.getFixedTax());
        } else if (tile.getAction() instanceof WealthTaxTileAction wealthTaxTileAction) {
          tileJson.addProperty("actionType", "wealthTax");
          tileJson.addProperty("amount", wealthTaxTileAction.getAmount());
        }
      }

      tilesArray.add(tileJson);
    }

    boardJson.add("tiles", tilesArray);
    return boardJson;
  }

  /**
   * Writes a list of BoardGame objects to a file in JSON format.
   * Only the first BoardGame in the list is serialized and written to the file.
   *
   * @param filename The name of the file to write to.
   * @param boards The list of BoardGame objects to be written; cannot be empty.
   * @throws FileWriteException If an I/O error occurs.
   * @throws BoardFileException If the list of BoardGame objects is empty or invalid.
   */
  @Override
  public void writeToFile(String filename, List<BoardGame> boards)
      throws FileWriteException, BoardFileException {

    if (filename == null || filename.isEmpty()) {
      throw FileExceptionUtil.createBoardFileException("Cannot write a null or empty file name.");
    }

    if (boards == null || boards.isEmpty()) {
      throw FileExceptionUtil.createBoardFileException("Cannot write empty board list to file.");
    }

    JsonObject boardJson = serializeBoard(boards.getFirst());
    try (Writer writer = new FileWriter(filename)) {
      gson.toJson(boardJson, writer);
    } catch (IOException e) {
      throw FileExceptionUtil.wrapWriteException(filename, e);
    } catch (BoardFileException e) {
      throw e;
    } catch (Exception e) {
      throw FileExceptionUtil.createBoardFileException("Error serializing board:" + e.getMessage());
    }
  }

  /**
   * Deserializes a JsonObject into a BoardGame object, populating the board's tiles
   * and their actions based on the JSON representation.
   *
   * @param boardJson The JsonObject containing the board game data.
   * @param filename The name of the source file (used for error reporting).
   * @return A BoardGame object initialized based on the JSON content.
   * @throws DataFormatException If the JSON content is not in the expected format.
   * @throws BoardFileException If there is an error related to the board structure.
   */
  private BoardGame deserializeBoard(JsonObject boardJson, String filename)
      throws DataFormatException, BoardFileException {
    BoardGame boardGame = new BoardGame();
    String variantName = boardJson.get("variantName").getAsString();
    boardGame.setVariantName(variantName);

    if ("Monopoly Game".equals(variantName) || "monopolyGame".equals(variantName)) {
      boardGame.createMonopolyGameBoard();
    } else {
      boardGame.createLadderGameBoard();
    }

    JsonArray tilesJson = boardJson.getAsJsonArray("tiles");
    int lineNumber = 1;

    for (JsonElement tileElement : tilesJson) {
      lineNumber++;
      JsonObject tileJson = tileElement.getAsJsonObject();

      if (!tileJson.has("id")) {
        throw FileExceptionUtil.createDataFormatException(filename, lineNumber,
            "Tile is missing required 'id' property");
      }

      int id = tileJson.get("id").getAsInt();
      Tile tile = boardGame.getBoard().getTile(id);

      if (tile == null) {
        throw FileExceptionUtil.createBoardFileException(variantName, "Invalid tile ID: " + id);
      }

      if (tileJson.has("actionType")) {
        String actionType = tileJson.get("actionType").getAsString();

        try {
          switch (actionType) {
            case "ladder":
              if (!tileJson.has("destination")
                  || !tileJson.has("direction")) {
                throw FileExceptionUtil.createDataFormatException(filename, lineNumber,
                    "Ladder action missing required properties (destination and/or direction)");
              }
              int destination = tileJson.get("destination").getAsInt();
              String direction = tileJson.get("direction").getAsString();
              tile.setAction(new LadderAction(destination, direction));
              break;
            case "backToStart":
              tile.setAction(new BackToStartAction());
              break;
            case "wait":
              tile.setAction(new WaitAction());
              break;
            case "property":
              if (!tileJson.has("propertyName") || !tileJson.has("cost")
                  || !tileJson.has("type")) {
                throw FileExceptionUtil.createDataFormatException(filename, lineNumber,
                    "Property action missing required properties (propertyName, cost, or type)");
              }
              String propertyName = tileJson.get("propertyName").getAsString();
              int cost = tileJson.get("cost").getAsInt();
              String type = tileJson.get("type").getAsString();
              tile.setAction(new PropertyTileAction(propertyName, cost, type));
              break;
            case "start":
              tile.setAction(new StartTileAction());
              break;
            case "chance":
              tile.setAction(new ChanceTileAction());
              break;
            case "jail":
              tile.setAction(new JailTileAction());
              break;
            case "goToJail":
              if (!tileJson.has("jailTileId")) {
                throw FileExceptionUtil.createDataFormatException(filename, lineNumber,
                    "GoToJail action missing required property: jailTileId");
              }
              int jailTileId = tileJson.get("jailTileId").getAsInt();
              tile.setAction(new GoToJailAction(jailTileId));
              break;
            case "freeParking":
              tile.setAction(new FreeParkingAction());
              break;
            case "tax":
              if (!tileJson.has("percentageTax")
                  || !tileJson.has("fixedTax")) {
                throw FileExceptionUtil.createDataFormatException(filename, lineNumber,
                    "Tax action missing required properties (percentageTax and/or fixedTax)");
              }
              int percentageTax = tileJson.get("percentageTax").getAsInt();
              int fixedTax = tileJson.get("fixedTax").getAsInt();
              tile.setAction(new TaxTileAction(percentageTax, fixedTax));
              break;
            case "wealthTax":
              if (!tileJson.has("amount")) {
                throw FileExceptionUtil.createDataFormatException(filename, lineNumber,
                    "WealthTax action missing required property: amount");
              }
              int amount = tileJson.get("amount").getAsInt();
              tile.setAction(new WealthTaxTileAction(amount));
              break;
            default:
              throw FileExceptionUtil.createDataFormatException(filename, lineNumber,
                  "Unknown action type: " + actionType);
          }
        } catch (IllegalArgumentException e) {
          throw FileExceptionUtil.createBoardFileException(variantName,
              "Invalid value for action type '" + actionType
                  + "' at tile " + id + ": " + e.getMessage());
        }
      }
    }

    return boardGame;
  }

  /**
   * Reads the content of a JSON file and converts it into a list containing a BoardGame object.
   * The JSON file is expected to have a list of tiles with their properties,
   * which are used to set up the game board and its relationships between tiles.
   *
   * @param filename The name of the file to read from.
   * @return A list containing a single BoardGame object initialized based on the JSON file content.
   * @throws FileReadException If an I/O error occurs during file reading.
   * @throws DataFormatException If the file content is not in the expected format.
   * @throws BoardFileException If there is an error related to the board structure.
   */
  @Override
  public List<BoardGame> readFromFile(String filename)
      throws FileReadException, DataFormatException, BoardFileException {
    if (filename == null || filename.isEmpty()) {
      throw new FileReadException("Cannot read from a null or empty filename");
    }

    try {
      String json = new String(Files.readAllBytes(Paths.get(filename)));

      JsonObject boardJson;
      try {
        boardJson = JsonParser.parseString(json).getAsJsonObject();
      } catch (JsonSyntaxException e) {
        throw FileExceptionUtil.createDataFormatException(filename, 1,
            "Invalid JSON syntax: " + e.getMessage());
      }

      if (!boardJson.has("variantName")) {
        throw FileExceptionUtil.createDataFormatException(filename, 1,
            "Missing required property: variantName");
      }

      if (!boardJson.has("tiles")) {
        throw FileExceptionUtil.createDataFormatException(filename, 1,
            "Missing required property: tiles");
      }

      BoardGame boardGame = deserializeBoard(boardJson, filename);
      return List.of(boardGame);
    } catch (IOException e) {
      throw FileExceptionUtil.wrapReadException(filename, e);
    } catch (DataFormatException | BoardFileException e) {
      throw e;
    } catch (Exception e) {
      throw FileExceptionUtil.createBoardFileException("Failed to parse board file: "
          + e.getMessage());
    }
  }
}
