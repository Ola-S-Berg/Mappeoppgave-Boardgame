package edu.ntnu.idi.idatt.Filehandling;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.ntnu.idi.idatt.GameLogic.BoardGame;
import edu.ntnu.idi.idatt.GameLogic.Tile;

import com.google.gson.Gson;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.List;
import java.io.*;

/**
 * Handles file operations for BoardGame objects using JSON.
 */
public class BoardFileHandler implements FileHandler<BoardGame> {

  private final Gson gson = new Gson();

  /**
   * Serializes a BoardGame object into a JSON representation, including the board's tiles
   * and their properties such as tile ID and the next tile in the sequence.
   *
   * @param boardGame the BoardGame object to be serialized; it contains the board and its tiles
   * @return a JsonObject representing the serialized board game, including the board name, description,
   *         and a list of tiles with their properties
   */
  public JsonObject serializeBoard(BoardGame boardGame) {
    JsonObject boardJson = new JsonObject();
    boardJson.addProperty("name", "Ladder game 90");
    boardJson.addProperty("description", "A classic ladder game with 90 tiles and 2 ladders.");

    JsonArray tilesArray = new JsonArray();
    for (int i = 1; i <= 90; i++) {
      Tile tile = boardGame.getBoard().getTile(i);
      JsonObject tileJson = new JsonObject();
      tileJson.addProperty("id", tile.getTileId());

      if (tile.getNextTile() != null) {
        tileJson.addProperty("nextTile", tile.getNextTile().getTileId());
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

    JsonObject boardJson = serializeBoard(boards.get(0));
    try (Writer writer = new FileWriter(filename)) {
      gson.toJson(boardJson, writer);
    }
  }

  /**
   * Reads the content of a JSON file and converts it into a list containing a single BoardGame object.
   * The JSON file is expected to have a list of tiles with their properties, which are used to set up
   * the game board and its relationships between tiles.
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
    boardGame.createBoard();

    JsonArray tilesJson = boardJson.getAsJsonArray("tiles");
    for (JsonElement tileElement : tilesJson) {
      JsonObject tileJson = tileElement.getAsJsonObject();
      int id = tileJson.get("id").getAsInt();
      Tile tile = boardGame.getBoard().getTile(id);

      if (tileJson.has("nextTile")) {
        int nextTileId = tileJson.get("nextTile").getAsInt();
        tile.setNextTile(boardGame.getBoard().getTile(nextTileId));
      }
    }

    return List.of(boardGame);
  }
}
