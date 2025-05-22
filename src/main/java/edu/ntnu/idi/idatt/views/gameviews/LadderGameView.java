package edu.ntnu.idi.idatt.views.gameviews;

import edu.ntnu.idi.idatt.controllers.LadderGameController;
import edu.ntnu.idi.idatt.model.gamelogic.BoardGame;
import edu.ntnu.idi.idatt.model.gamelogic.Player;
import javafx.stage.Stage;

/**
 * <h1>Ladder Game View</h1>
 *
 * <p>An implementation of the AbstractBoardGameView that specifically renders and manages the
 * UI for the "Ladder Game". This class handles the specific visual representations and UI
 * elements unique to this board game.</p>
 *
 * <h2>Game Variation</h2>
 *
 * <p>The Ladder Game View supports three different game variations:</p>
 * <ul>
 *   <li><strong>Classic</strong> - The classic Ladder Game experience</li>
 *   <li><strong>Advanced</strong> - Features additional action tiles</li>
 *   <li><strong>Extreme</strong> - Flips all up ladders to down</li>
 * </ul>
 *
 * <h2>Game Board Layout</h2>
 *
 * <p>The game board uses a 9x10 grid layout, with tiles organized in the movement pattern of
 * the Ladder Game. Grid coordinates are used to map logical tile positions to visual positions.</p>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Action tiles - Unique visual indicators for special actions:</li>
 *   <li>Ladder climbs/descents - Animated movement when a player lands on a ladder</li>
 *   <li>Sent back to start - Animates a player's movement back to start</li>
 *   <li>Wait action - Indicates when a player must skip their next turn</li>
 * </ul>
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
 */
public class LadderGameView extends AbstractBoardGameView {

  private final String gameVariation;
  private static final int GRID_ROWS = 9;
  private static final int GRID_COLS = 10;

  /**
   * Constructor that initializes the game view with a controller.
   *
   * @param boardGame The game logic
   * @param stage The JavaFX stage to display the game on
   * @param controller The controller that handles game logic
   */
  public LadderGameView(BoardGame boardGame, Stage stage, LadderGameController controller) {
    super(boardGame, stage, controller);
    this.gameVariation = controller.getGameVariation();
    setupGameView();
  }

  /**
   * Gets the appropriate board image path based on the game variation.
   *
   * @return The path to the board image.
   */
  @Override
  protected String getBoardImagePath() {
    return switch (gameVariation) {
      case "Ladder Game Advanced" -> "/images/Games/LadderGameAdvanced.png";
      case "Ladder Game Extreme" -> "/images/Games/LadderGameExtreme.png";
      default -> "/images/Games/LadderGame.png";
    };
  }

  /**
   * Gets the number of rows in the grid.
   *
   * @return The number of rows.
   */
  @Override
  protected int getGridRows() {
    return GRID_ROWS;
  }

  /**
   * Gets the number of columns in the grid.
   *
   * @return The number of columns.
   */
  @Override
  protected int getGridCols() {
    return GRID_COLS;
  }

  /**
   * Gets the title for the game window.
   *
   * @return The game title.
   */
  @Override
  protected String getGameTitle() {
    return "Ladder Game";
  }

  /**
   * Gets the game-specific win message.
   *
   * @return The win message.
   */
  @Override
  protected String getWinMessage() {
    return "reached the final square!";
  }

  /**
   * Displays a game-specific action message.
   *
   * @param player The player performing the action.
   * @param actionType The type of action being performed.
   */
  @Override
  public void showActionMessage(Player player, String actionType) {
    String message = switch (actionType) {
      case "LadderAction" -> player.getName() + " landed on a ladder";
      case "BackToStartAction" -> player.getName() + " must go back to start";
      case "WaitAction" -> player.getName() + " must wait a turn";
      default -> player.getName() + " landed on a tile action";
    };

    actionLabel.setText(message);
    actionLabel.setVisible(true);
  }

  /**
   * Gets the game-specific information to display in the game info dialog.
   * This method provides detailed information about the Ladder Game, including its rules,
   * objectives, and specific game variation features.
   *
   * @return A string containing the Ladder Game information.
   */
  @Override
  protected String getGameInformation() {
    StringBuilder info = new StringBuilder();
    info.append("""
        The ladder game is a classic board game where players move along a path with\
         ladders, wait and back-to-start actions.
        
        """);
    info.append("Game Objective:\n");
    info.append("• Reach the final tile (tile 90) before your opponents.\n");
    info.append("• The first player to reach the final tile wins!\n\n");

    info.append("Game Rules:\n");
    info.append("• Players take turns rolling a die and moving their token forward.\n");
    info.append("• Landing on a ladder allows you to climb up or down depending on the type.\n");
    info.append("• Special action tiles can send you back to start or make you skip a turn.\n\n");

    info.append("Game Variations: ").append(gameVariation).append("\n");
    switch (gameVariation) {
      case "Ladder Game Advanced":
        info.append("• Advanced game mode includes additional action tiles.\n");
        info.append("• Players will encounter more actions that will impact their progress.\n");
        break;
      case "Ladder Game Extreme":
        info.append("• Extreme mode flips all up ladders to down.\n");
        info.append("• This creates a more challenging experience as progress is harder to make.\n");
        break;
      default:
        info.append("• Classic mode offers the traditional Ladder Game experience.\n");
        info.append("• Offers a balanced amount of ladders and actions for fair gameplay.\n");
        break;
    }

    return info.toString();
  }
}