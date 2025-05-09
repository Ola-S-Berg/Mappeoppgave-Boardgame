package edu.ntnu.idi.idatt.model;
import edu.ntnu.idi.idatt.actions.ladder_game.BackToStartAction;
import edu.ntnu.idi.idatt.actions.monopoly_game.ChanceTileAction;
import edu.ntnu.idi.idatt.actions.monopoly_game.FreeParkingAction;
import edu.ntnu.idi.idatt.actions.monopoly_game.GoToJailAction;
import edu.ntnu.idi.idatt.actions.monopoly_game.JailTileAction;
import edu.ntnu.idi.idatt.actions.ladder_game.LadderAction;
import edu.ntnu.idi.idatt.actions.monopoly_game.PayTileAction;
import edu.ntnu.idi.idatt.actions.monopoly_game.PropertyTileAction;
import edu.ntnu.idi.idatt.actions.monopoly_game.StartTileAction;
import edu.ntnu.idi.idatt.actions.monopoly_game.TaxTileAction;
import edu.ntnu.idi.idatt.actions.ladder_game.WaitAction;
import java.util.Map;
import java.util.HashMap;

/**
 * Class representing the game board. Consists of a collection of tiles.
 * Also keeps a complete overview of all tiles.
 */
public class Board {
  private final Map<Integer, Tile> tiles = new HashMap<>();

  /**
   * Adds a tile to the board.
   * @param tile The tile to add.
   */
  public void addTile(Tile tile) {
    tiles.put(tile.getTileId(), tile);
  }

  /**
   * Accessor that gets the ID (number) of the tile.
   * @param tileId The tile ID represented by an integer.
   * @return The ID.
   */
  public Tile getTile(int tileId) {
    return tiles.get(tileId);
  }

  /**
   * Sets up the common actions for all ladder game tiles.
   *
   * @param board The board game to set up.
   */
  private static void setupCommonTileActions(Board board) {
    board.getTile(25).setAction(new LadderAction(7, "down"));
    board.getTile(38).setAction(new LadderAction(1, "down"));
    board.getTile(48).setAction(new LadderAction(13, "down"));
    board.getTile(70).setAction(new LadderAction(30, "down"));
    board.getTile(79).setAction(new LadderAction(27, "down"));
    board.getTile(89).setAction(new LadderAction(53, "down"));


    board.getTile(37).setAction(new WaitAction());
    board.getTile(54).setAction(new WaitAction());
    board.getTile(71).setAction(new WaitAction());

    board.getTile(10).setAction(new BackToStartAction());
    board.getTile(81).setAction(new BackToStartAction());
  }

  /**
   * Sets up actions for the first variation of the ladder game.
   * A classic balanced ladder game with a normal number of actions.
   * @param board The game board to set up.
   */
  public static void setupTileActionsLadderGame(Board board) {
    board.getTile(5).setAction(new LadderAction(17, "up"));
    board.getTile(12).setAction(new LadderAction(49, "up"));
    board.getTile(21).setAction(new LadderAction(41, "up"));
    board.getTile(43).setAction(new LadderAction(61, "up"));
    board.getTile(55).setAction(new LadderAction(87, "up"));
    board.getTile(65).setAction(new LadderAction(84, "up"));

    setupCommonTileActions(board);
  }

  /**
   * Sets up actions for the second variation of the ladder game.
   * Increased number of actions from the first variation.
   * @param board The board game to set up.
   */
  public static void setupTileActionsLadderGameAdvanced(Board board) {
    board.getTile(5).setAction(new LadderAction(17, "up"));
    board.getTile(12).setAction(new LadderAction(49, "up"));
    board.getTile(14).setAction(new LadderAction(47, "up"));
    board.getTile(21).setAction(new LadderAction(41, "up"));
    board.getTile(43).setAction(new LadderAction(61, "up"));
    board.getTile(52).setAction(new LadderAction(72, "up"));
    board.getTile(65).setAction(new LadderAction(84, "up"));

    board.getTile(42).setAction(new LadderAction(2, "down"));
    board.getTile(46).setAction(new LadderAction(15, "down"));
    board.getTile(64).setAction(new LadderAction(24, "down"));

    board.getTile(18).setAction(new WaitAction());
    board.getTile(28).setAction(new WaitAction());
    board.getTile(45).setAction(new WaitAction());
    board.getTile(58).setAction(new WaitAction());
    board.getTile(75).setAction(new WaitAction());
    board.getTile(88).setAction(new WaitAction());

    board.getTile(34).setAction(new BackToStartAction());
    board.getTile(56).setAction(new BackToStartAction());
    board.getTile(68).setAction(new BackToStartAction());

    setupCommonTileActions(board);
  }

  /**
   * Sets up the actions for the third variant of the Ladder game.
   * Flips all up ladders from the second variation to down.
   * A diabolical version of the classic ladder game.
   * @param board The board game to set up.
   */
  public static void setupTileActionsLadderGameExtreme(Board board) {

    board.getTile(17).setAction(new LadderAction(5, "down"));
    board.getTile(41).setAction(new LadderAction(21, "down"));
    board.getTile(42).setAction(new LadderAction(2, "down"));
    board.getTile(46).setAction(new LadderAction(15, "down"));
    board.getTile(47).setAction(new LadderAction(14, "down"));
    board.getTile(49).setAction(new LadderAction(12, "down"));
    board.getTile(61).setAction(new LadderAction(43, "down"));
    board.getTile(64).setAction(new LadderAction(24, "down"));
    board.getTile(72).setAction(new LadderAction(52, "down"));
    board.getTile(82).setAction(new LadderAction(63, "down"));
    board.getTile(84).setAction(new LadderAction(65, "down"));
    board.getTile(87).setAction(new LadderAction(55, "down"));

    board.getTile(18).setAction(new WaitAction());
    board.getTile(28).setAction(new WaitAction());
    board.getTile(45).setAction(new WaitAction());
    board.getTile(58).setAction(new WaitAction());
    board.getTile(75).setAction(new WaitAction());
    board.getTile(88).setAction(new WaitAction());

    board.getTile(34).setAction(new BackToStartAction());
    board.getTile(56).setAction(new BackToStartAction());
    board.getTile(68).setAction(new BackToStartAction());

    setupCommonTileActions(board);
  }

  /**
   * Sets up actions for the Monopoly game tiles.
   *
   * @param board The game board to set up tiles for.
   */
  public static void setupTileActionsMonopolyGame(Board board) {
    //Start tile.
    board.getTile(1).setAction(new StartTileAction());

    //Property Tiles - Blue.
    board.getTile(2).setAction(new PropertyTileAction("Skolegata", 6000, "blue"));
    board.getTile(4).setAction(new PropertyTileAction("Brattorgata", 6000, "blue"));

    //Property Tiles - Pink.
    board.getTile(7).setAction(new PropertyTileAction("Sverres gate", 10000, "pink"));
    board.getTile(9).setAction(new PropertyTileAction("Tormods gate", 10000, "pink"));
    board.getTile(10).setAction(new PropertyTileAction("Olav Kyrres gate", 12000, "pink"));

    //Property Tiles - Green.
    board.getTile(12).setAction(new PropertyTileAction("Ragnhilds gate", 14000, "green"));
    board.getTile(14).setAction(new PropertyTileAction("St. Olavs gate", 14000, "green"));
    board.getTile(15).setAction(new PropertyTileAction("Guttorms gate", 16000, "green"));

    //Property Tiles - Gray.
    board.getTile(17).setAction(new PropertyTileAction("Nygata", 18000, "gray"));
    board.getTile(19).setAction(new PropertyTileAction("Bakkegata", 18000, "gray"));
    board.getTile(20).setAction(new PropertyTileAction("Kirkegata", 20000, "gray"));

    //Property Tiles - Red.
    board.getTile(22).setAction(new PropertyTileAction("Krambugata", 22000, "red"));
    board.getTile(24).setAction(new PropertyTileAction("Fjordgata", 22000, "red"));
    board.getTile(25).setAction(new PropertyTileAction("Sandgata", 24000, "red"));

    //Property Tiles - Yellow.
    board.getTile(27).setAction(new PropertyTileAction("Klostergata", 26000, "yellow"));
    board.getTile(28).setAction(new PropertyTileAction("Munkegata", 26000, "yellow"));
    board.getTile(30).setAction(new PropertyTileAction("Bispegata", 28000, "yellow"));

    //Property Tiles - Purple.
    board.getTile(32).setAction(new PropertyTileAction("Sondre gate", 30000, "purple"));
    board.getTile(33).setAction(new PropertyTileAction("Erling Skakkes gate", 30000, "purple"));
    board.getTile(35).setAction(new PropertyTileAction("Kongens gate", 32000, "purple"));

    //Property Tiles - Orange.
    board.getTile(38).setAction(new PropertyTileAction("Prinsens gate", 35000, "orange"));
    board.getTile(40).setAction(new PropertyTileAction("Dronningens gate", 40000, "orange"));

    //Landmark Tiles.
    board.getTile(6).setAction(new PropertyTileAction("Nidarosdomen", 20000, "landmark"));
    board.getTile(16).setAction(new PropertyTileAction("Gamle Bybro", 20000, "landmark"));
    board.getTile(26).setAction(new PropertyTileAction("Kristiansen Festning", 20000, "landmark"));
    board.getTile(36).setAction(new PropertyTileAction("Gloshaugen", 20000, "landmark"));

    //Chance Tiles.
    board.getTile(3).setAction(new ChanceTileAction());
    board.getTile(8).setAction(new ChanceTileAction());
    board.getTile(13).setAction(new ChanceTileAction());
    board.getTile(18).setAction(new ChanceTileAction());
    board.getTile(23).setAction(new ChanceTileAction());
    board.getTile(29).setAction(new ChanceTileAction());
    board.getTile(34).setAction(new ChanceTileAction());
    board.getTile(39).setAction(new ChanceTileAction());

    //Tax Tile.
    board.getTile(5).setAction(new TaxTileAction(10, 20000));

    //Jail Tile.
    board.getTile(11).setAction(new JailTileAction());

    //Free Parking.
    board.getTile(21).setAction(new FreeParkingAction());

    //Go To Jail Tile.
    board.getTile(31).setAction(new GoToJailAction(11));

    //Pay Tile.
    board.getTile(37).setAction(new PayTileAction(10000));
  }
}