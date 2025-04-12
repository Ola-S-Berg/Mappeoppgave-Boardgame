package edu.ntnu.idi.idatt.GUI;

import edu.ntnu.idi.idatt.GameLogic.BoardGame;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * View for the "Ladder Game Classic" game.
 * Displays the game board, handles dice rolling, and shows player movement.
 */
public class LadderGameClassicView {
  private Stage stage;
  private BoardGame boardGame;
  private GridPane boardGridPane;
  private VBox mainLayout;

  public LadderGameClassicView(BoardGame boardGame, Stage stage) {
    this.boardGame = boardGame;
    this.stage = stage;

  }
}
