package edu.ntnu.idi.idatt;

import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.ntnu.idi.idatt.Filehandling.PlayerFileHandler;
import edu.ntnu.idi.idatt.GameLogic.Player;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

public class PlayerFileHandlerTest {

  private static final String Players = "players.json";

  @Test
  public void testWriteToFile() throws IOException {
    PlayerFileHandler playerFileHandler = new PlayerFileHandler();
    Player player1 = new Player("Ola");
    Player player2 = new Player("Markus");

    List<Player> players = List.of(player1, player2);

    String filename = "players.csv";

    try {
      playerFileHandler.writeToFile(filename, players);
      System.out.println("File written successfully.");

      List<Player> loadedPlayers = playerFileHandler.readFromFile(filename);
      assertTrue(loadedPlayers.size() == 2, "The loaded players should be 2.");
      new java.io.File(filename).delete();

    } catch (IOException e) {
      System.out.println("Error writing file:" + e.getMessage());
    }
  }
}
