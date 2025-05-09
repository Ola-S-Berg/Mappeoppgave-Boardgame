package edu.ntnu.idi.idatt.model.filehandling;

import edu.ntnu.idi.idatt.model.actions.monopoly_game.PropertyTileAction;
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
 * File handler for managing player data in CSV files.
 * Implements the FileHandler interface for Player objects.
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
      for (Player player : players) {
        int currentTileId =
            (player.getCurrentTile() != null) ? player.getCurrentTile().getTileId() : 1;
        String properties = player.getOwnedProperties().stream().map(
            PropertyTileAction::getPropertyName).collect(
            Collectors.joining(";"));

        writer.write(player.getName() + ", " +
            player.getToken() + ", " +
            currentTileId + ", " +
            player.getMoney() + ", " +
            properties);

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

    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
      String line;
      while ((line = reader.readLine()) != null) {
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

          players.add(player);
          System.out.println("Read player: " + name +
              ", Token: " + token +
              ", tileId: " + tileId +
              ", money: " + money +
              ", properties: " + tokens[4].trim());
        }
      }
    }
    return players;
  }
}