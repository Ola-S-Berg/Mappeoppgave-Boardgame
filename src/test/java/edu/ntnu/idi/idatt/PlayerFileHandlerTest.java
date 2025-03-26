package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.Filehandling.PlayerFileHandler;
import edu.ntnu.idi.idatt.GameLogic.Player;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

public class PlayerFileHandlerTest {

  private static final String FILE_NAME = "players.csv";

  @Test
  public void testWriteToFile() throws IOException {
    PlayerFileHandler playerFileHandler = new PlayerFileHandler();
    Player player1 = new Player("Ola", "Red", null);
    Player player2 = new Player("Markus", "Blue", null);

    List<Player> players = List.of(player1, player2);

    try {
      playerFileHandler.writeToFile(FILE_NAME, players);
      System.out.println("File written successfully.");

      List<Player> loadedPlayers = playerFileHandler.readFromFile(FILE_NAME);

      assert loadedPlayers.size() == 2 : "The loaded players should be 2.";
      assert loadedPlayers.get(0).getName().equals("Ola") : "First player should be Ola.";
      assert loadedPlayers.get(1).getName().equals("Markus") : "Second player should be Markus.";

      assert loadedPlayers.get(0).getToken().equals("Red") : "First player's token should be Red.";
      assert loadedPlayers.get(1).getToken().equals("Blue") : "Second player's token should be Blue.";

      assert loadedPlayers.get(0).getGame() == null : "First player should not have a game.";
      assert loadedPlayers.get(1).getGame() == null : "Second player should not have a game.";

      new File(FILE_NAME).delete();
    } catch (IOException e) {
      System.out.println("Error writing or reading file: " + e.getMessage());
    }
  }
}

