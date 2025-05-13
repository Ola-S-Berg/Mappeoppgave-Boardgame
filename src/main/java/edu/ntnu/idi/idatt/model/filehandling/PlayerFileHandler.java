package edu.ntnu.idi.idatt.model.filehandling;

import edu.ntnu.idi.idatt.model.actions.monopolygame.PropertyTileAction;
import edu.ntnu.idi.idatt.model.gamelogic.Player;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>Player File Handler</h1>
 *
 * <p>File handler for managing player data using CSV format. Provides functionality to save and
 * load player information, including their game state, position, owned properties and money</p>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>CSV-based handling of player data</li>
 *   <li>Support for tracking the current active player in saved games</li>
 *   <li>Preservation of player properties, money and game positions</li>
 *   <li>Complete player state reconstruction during loading</li>
 * </ul>
 *
 * <h2>CSV format structure</h2>
 *
 * <p>The CSV file format used by this handler follows this pattern:</p>
 * <ul>
 *   <li>CURRENT_PLAYER: [player name]</li>
 *   <li>[name], [token], [current tile ID], [money], [owned properties]</li>
 *   <li>[name], [token], [current tile ID], [money], [owned properties]</li>
 *   <li>...</li>
 * </ul>
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
 */
public class PlayerFileHandler implements FileHandler<Player> {

  /**
   * Writes a list of players to a CSV file. Each player is written on a new line with the format:
   * playerName, game
   *
   * @param filename The name of the file to write to.
   * @param players  The list of players to write to the file.
   * @throws IOException If an error occurs during file writing.
   */
  @Override
  public void writeToFile(String filename, List<Player> players) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
      String currentPlayerName = null;
      for (Player player : players) {
        if (player.getProperty("currentPlayerName") != null) {
          currentPlayerName = player.getProperty("currentPlayerName");
          break;
        }
      }

      if (currentPlayerName != null) {
        writer.write("CURRENT_PLAYER:" + currentPlayerName);
        writer.newLine();
      }

      for (Player player : players) {
        int currentTileId =
            (player.getCurrentTile() != null) ? player.getCurrentTile().getTileId() : 1;
        String properties = player.getOwnedProperties().stream().map(
            PropertyTileAction::getPropertyName).collect(
            Collectors.joining(";"));

        writer.write(player.getName() + ", "
            + player.getToken() + ", "
            + currentTileId + ", "
            + player.getMoney() + ", "
            + properties);

        writer.newLine();
      }
    }
  }

  /**
   * Reads a list of players from a CSV file.
   *
   * @param filename The name of the file to read from.
   * @return A list of player objects.
   * @throws IOException If an error occurs during file reading.
   */
  @Override
  public List<Player> readFromFile(String filename) throws IOException {
    List<Player> players = new ArrayList<>();
    String currentPlayerName = null;

    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.startsWith("CURRENT_PLAYER:")) {
          currentPlayerName = line.substring("CURRENT_PLAYER:".length()).trim();
          System.out.println("Current player from file: " + currentPlayerName);
          continue;
        }

        String[] tokens = line.split(",");
        if (tokens.length >= 4) {
          String name = tokens[0].trim();
          String token = tokens[1].trim();
          String tileId = tokens[2].trim();
          int money = Integer.parseInt(tokens[3].trim());

          Player player = new Player(name, token, null, money);
          player.setProperty("savedTileId", tileId);

          if (tokens.length >= 5 && !tokens[4].trim().isEmpty()) {
            player.setProperty("savedProperties", tokens[4].trim());
          }

          if (name.equals(currentPlayerName)) {
            player.setProperty("isCurrentPlayer", "true");
          }

          players.add(player);
          System.out.println("Read player: " + name
              + ", Token: " + token
              + ", tileId: " + tileId
              + ", money: " + money
              + (tokens.length >= 5 ? ", properties: " + tokens[4].trim() : ""));
        }
      }

      if (currentPlayerName == null && !players.isEmpty()) {
        players.getFirst().setProperty("isCurrentPlayer", "true");
        System.out.println("No current player found, defaulting to first player: "
            + players.getFirst().getName());
      }
    }

    return players;
  }
}