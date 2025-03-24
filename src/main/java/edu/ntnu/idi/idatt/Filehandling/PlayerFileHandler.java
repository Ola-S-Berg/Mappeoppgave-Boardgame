package edu.ntnu.idi.idatt.Filehandling;

import edu.ntnu.idi.idatt.GameLogic.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * File handler for managing player data in CSV files.
 * Implements the FileHandler interface for Player objects.
 */
public class PlayerFileHandler implements FileHandler<Player> {

  /**
   * Writes a list of players to a CSV file.
   * Each player is written on a new line with the format:
   * playerName, game
   * @param filename The name of the file to write to.
   * @param players The list of players to write to the file.
   * @throws IOException If an error occurs during file writing.
   */
  @Override
  public void writeToFile(String filename, List<Player> players) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
      for (Player player : players) {
        writer.write(player.getName() + ", " + player.getGame().toString());
        writer.newLine();
      }
    }
  }

  /**
   * Reads a list of players from a CSV file.
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
        if (tokens.length == 1) {
          players.add(new Player(tokens[0].trim()));
        }
      }
    }
    return players;
  }
}
