package edu.ntnu.idi.idatt.views.gameviews;

import edu.ntnu.idi.idatt.controllers.LadderGameController;
import edu.ntnu.idi.idatt.model.gamelogic.BoardGame;
import edu.ntnu.idi.idatt.model.gamelogic.Player;
import javafx.stage.Stage;

/**
 * View for the "Ladder Game" game.
 * Displays the game board, handles dice rolling, and shows player movement.
 * Communicates with the controller "MonopolyGameController" to handle game logic.
 * Extends AbstractGameView to inherit common game view functionality.
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
}