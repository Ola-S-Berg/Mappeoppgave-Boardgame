package edu.ntnu.idi.idatt.model.filehandling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import edu.ntnu.idi.idatt.model.gamelogic.BoardGame;
import edu.ntnu.idi.idatt.model.gamelogic.Tile;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Handles file operations for BoardGame objects using JSON.
 */
public class BoardFileHandler implements FileHandler<BoardGame> {

  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  /**
   * Serializes a BoardGame object into a JSON representation, including the board's tiles
   * and their properties such as tile ID, the next tile in the sequence, and their actions.
   *
   * @param boardGame The BoardGame object to be serialized; it contains the board and its tiles
   * @return A JsonObject representing the serialized board game, including the board name,
   *          description, and a list of tiles with their properties
   */
  public JsonObject serializeBoard(BoardGame boardGame) {
    JsonObject boardJson = new JsonObject();
    String variantName = boardGame.getVariantName();

    if ("Monopoly Game".equals(variantName) || "monopolyGame".equals(variantName)) {
      boardJson.addProperty("name", "Monopoly Game");
      boardJson.addProperty("description", "A classic Monopoly game with 40 tiles.");
    } else {
      boardJson.addProperty("name", "Ladder game");
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
        } else if (tile.getAction() instanceof GoToJailAction) {
          tileJson.addProperty("actionType", "gotToJail");
        } else if (tile.getAction() instanceof FreeParkingAction) {
          tileJson.addProperty("actionType", "FreeParking");
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
   * @param filename the name of the file to write to
   * @param boards the list of BoardGame objects to be written; cannot be empty
   * @throws IOException if an I/O error occurs
   * @throws IllegalArgumentException if the list of BoardGame objects is empty
   */
  @Override
  public void writeToFile(String filename, List<BoardGame> boards) throws IOException {
    if (boards.isEmpty()) {
      throw new IllegalArgumentException("Cannot write empty board list to file.");
    }

    JsonObject boardJson = serializeBoard(boards.getFirst());
    try (Writer writer = new FileWriter(filename)) {
      gson.toJson(boardJson, writer);
    }
  }

  /**
   * Reads the content of a JSON file and converts it into a list containing a BoardGame object.
   * The JSON file is expected to have a list of tiles with their properties,
   * which are used to set up the game board and its relationships between tiles.
   *
   * @param filename the name of the file to read from
   * @return a list containing a single BoardGame object initialized based on the JSON file content
   * @throws IOException if an I/O error occurs during file reading
   */
  @Override
  public List<BoardGame> readFromFile(String filename) throws IOException {
    String json = new String(Files.readAllBytes(Paths.get(filename)));
    JsonObject boardJson = JsonParser.parseString(json).getAsJsonObject();

    BoardGame boardGame = new BoardGame();

    String variantName = boardJson.has("variantName")
        ? boardJson.get("variantName").getAsString() : "";
    boardGame.setVariantName(variantName);

    if ("Monopoly Game".equals(variantName) || "monopolyGame".equals(variantName)) {
      boardGame.createMonopolyGameBoard();
    } else {
      boardGame.createLadderGameBoard();
    }

    JsonArray tilesJson = boardJson.getAsJsonArray("tiles");
    for (JsonElement tileElement : tilesJson) {
      JsonObject tileJson = tileElement.getAsJsonObject();
      int id = tileJson.get("id").getAsInt();
      Tile tile = boardGame.getBoard().getTile(id);

      if (tileJson.has("actionType")) {
        String actionType = tileJson.get("actionType").getAsString();

        switch (actionType) {
          case "ladder":
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
            String propertyName = tileJson.get("propertyName").getAsString();
            int cost = tileJson.get("cost").getAsInt();
            String type = tileJson.get("type").getAsString();
            tile.setAction(new PropertyTileAction(propertyName, cost, type));
            break;
          case "chance":
            tile.setAction(new ChanceTileAction());
            break;
          case "jail":
            tile.setAction(new JailTileAction());
            break;
          case "goToJail":
            int jailTileId = tileJson.get("jailTileId").getAsInt();
            tile.setAction(new GoToJailAction(jailTileId));
            break;
          case "freeParking":
            tile.setAction(new FreeParkingAction());
            break;
          case "tax":
            int percentageTax = tileJson.get("percentageTax").getAsInt();
            int fixedTax = tileJson.get("fixedTax").getAsInt();
            tile.setAction(new TaxTileAction(percentageTax, fixedTax));
            break;
          case "wealthTax":
            int amount = tileJson.get("amount").getAsInt();
            tile.setAction(new WealthTaxTileAction(amount));
            break;
          default:
            System.out.println("Could not find action type");
            break;
        }
      }
    }

    return List.of(boardGame);
  }
}