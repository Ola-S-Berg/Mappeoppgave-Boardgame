package edu.ntnu.idi.idatt.GUI;

import edu.ntnu.idi.idatt.GameLogic.BoardGame;
import edu.ntnu.idi.idatt.GameLogic.Player;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * View for the "Ladder Game Classic" game.
 * Displays the game board, handles dice rolling, and shows player movement.
 */
public class LadderGameClassicView {

  private Stage stage;
  private BoardGame boardGame;
  private GridPane boardGridPane;
  private VBox mainLayout;
  private Label statusLabel;
  private Button rollButton;
  private Map<Player, ImageView> playerTokenViews;
  private Image boardImage;
  private ImageView boardImageView;
  private int currentPlayerIndex = 0;

  private static final int CELL_SIZE = 60;
  private static final int GRID_SIZE = 10;

  /**
   * Constructor that initializes the game view.
   * @param boardGame The game logic.
   * @param stage The JavaFX stage to display the game on.
   */
  public LadderGameClassicView(BoardGame boardGame, Stage stage) {
    this.boardGame = boardGame;
    this.stage = stage;
    this.playerTokenViews = new HashMap<>();

    setupGameView();
  }

  /**
   * Sets up the game view with board image, player tokens, and controls.
   */
  private void setupGameView() {
    mainLayout = new VBox(20);
    mainLayout.setPadding(new Insets(20));
    mainLayout.setAlignment(Pos.CENTER);

    statusLabel = new Label(
        "Game Started! " + boardGame.getPlayers().get(0).getName() + "'s Turn To Roll");
    statusLabel.setStyle("-fx-font-size: 18px");

    StackPane boardPane = new StackPane();
    boardPane.setMinSize(600, 600);
    boardPane.setMaxSize(600, 600);

    boardImage = new Image(getClass().getResourceAsStream("/images/Games/LadderGameClassic.png"));
    boardImageView = new ImageView(boardImage);
    boardImageView.setFitWidth(600);
    boardImageView.setFitHeight(600);
    boardImageView.setPreserveRatio(true);

    boardGridPane = new GridPane();
    boardGridPane.setMinSize(600, 600);
    boardGridPane.setMaxSize(600, 600);

    for (int row = 0; row < GRID_SIZE; row++) {
      for (int col = 0; col < GRID_SIZE; col++) {
        StackPane cell = new StackPane();
        cell.setMinSize(CELL_SIZE, CELL_SIZE);
        cell.setMaxSize(CELL_SIZE, CELL_SIZE);
        boardGridPane.add(cell, col, row);
      }
    }

    boardPane.getChildren().addAll(boardImageView, boardGridPane);

    setupPlayerTokens();

    rollButton = new Button("Roll Die");
    rollButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px 20px");
    rollButton.setOnAction(event -> rollDice());

    mainLayout.getChildren().addAll(statusLabel, boardPane, rollButton);

    Scene scene = new Scene(mainLayout, 700, 750);
    stage.setScene(scene);
    stage.setTitle("Ladder Game Classic");
    stage.show();

    for (Player player : boardGame.getPlayers()) {
      player.placeOnTile(boardGame.getBoard().getTile(1));
    }

    for (int i = 0; i < boardGame.getPlayers().size(); i++) {
      Player player = boardGame.getPlayers().get(i);
      ImageView tokenView = playerTokenViews.get(player);
      positionTokenAtTile(tokenView, 1, i);
    }
  }

  /**
   * Sets up player tokens on the board.
   */
  private void setupPlayerTokens() {
    playerTokenViews.clear();

    int playerOffset = 0;
    for (Player player : boardGame.getPlayers()) {
      Image tokenImage = new Image(getClass().getResourceAsStream(player.getToken()));
      ImageView tokenView = new ImageView(tokenImage);
      tokenView.setFitHeight(40);
      tokenView.setFitWidth(40);
      tokenView.setPreserveRatio(true);

      playerTokenViews.put(player, tokenView);

      playerOffset++;
    }
  }

  /**
   * Handles the roll dice button click event.
   */
  private void rollDice() {
    rollButton.setDisable(true);

    Player currentPlayer = boardGame.getPlayers().get(currentPlayerIndex);

    statusLabel.setText(currentPlayer.getName() + "'s Turn To Roll");

    new Thread(() -> {
      int diceValue = boardGame.getDice().roll();

      int oldTileId = currentPlayer.getCurrentTile().getTileId();

      final boolean willSkipTurn = currentPlayer.willWaitTurn();

      currentPlayer.move(diceValue);

      int newTileId = currentPlayer.getCurrentTile().getTileId();

      Platform.runLater(() -> {
        StringBuilder statusText = new StringBuilder();
        statusText.append(currentPlayer.getName()).append(" Rolled ").append(diceValue); // Added space after name

        if (newTileId != oldTileId) {
          statusText.append(". Moving from ").append(oldTileId).append(" to ").append(newTileId);

          if (newTileId > oldTileId + diceValue) {
            statusText.append(" (Climbed Up A Ladder)");
          } else if (newTileId < oldTileId + diceValue && newTileId != 1) {
            statusText.append(" (Climbed Down A Ladder)");
          } else if (newTileId == 1 && oldTileId != 1) {
            statusText.append(" (went back to start)");
          }

          if (currentPlayer.willWaitTurn() && !willSkipTurn) {
            statusText.append(" (Must skip next turn)");
          }
        } else {
          statusText.append(" (No movement)");
        }
        statusLabel.setText(statusText.toString());

        ImageView tokenView = playerTokenViews.get(currentPlayer);
        animateTokenMovement(tokenView, oldTileId, newTileId, currentPlayerIndex, () -> {
          if (newTileId == 90) {
            endGame(currentPlayer);
          } else {
            currentPlayerIndex = (currentPlayerIndex + 1) % boardGame.getPlayers().size();
            Player nextPlayer = boardGame.getPlayers().get(currentPlayerIndex);

            if (nextPlayer.willWaitTurn()) {
              statusLabel.setText(nextPlayer.getName() + " must skip their turn!");

              new Thread(() -> {
                try {
                  Thread.sleep(2000);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }

                Platform.runLater(() -> {
                  currentPlayerIndex = (currentPlayerIndex + 1) % boardGame.getPlayers().size();
                  final Player updatedNextPlayer = boardGame.getPlayers().get(currentPlayerIndex);
                  statusLabel.setText(updatedNextPlayer.getName() + "'s turn to roll.");
                  rollButton.setDisable(false);
                });
              }).start();
            } else {
              statusLabel.setText(nextPlayer.getName() + "'s turn to roll.");
              rollButton.setDisable(false);
            }
          }
        });
      });
    }).start();
  }

  /**
   * Animates token movement from old position to new position.
   * @param tokenView The token image view to animate.
   * @param fromTileId Starting tile ID.
   * @param toTileId Ending tile ID.
   * @param playerIndex The player's index (for offset positioning).
   * @param onFinished Runnable to execute after animation completes.
   */
  private void animateTokenMovement(ImageView tokenView, int fromTileId, int toTileId, int playerIndex, Runnable onFinished) {
    if (fromTileId == toTileId) {
      if (onFinished != null) {
        onFinished.run();
      }
      return;
    }

    int[] fromCoords = tileIdToGridCoordinates(fromTileId);
    int[] toCoords = tileIdToGridCoordinates(toTileId);

    StackPane fromCell = getStackPaneAt(fromCoords[0], fromCoords[1]);
    StackPane toCell = getStackPaneAt(toCoords[0], toCoords[1]);

    if (fromCell != null) {
      fromCell.getChildren().remove(tokenView);
    }
    if (toCell != null) {
      toCell.getChildren().add(tokenView);
    }

    double offsetX = (playerIndex % 2) * 20;
    double offsetY = (playerIndex / 2) * 20;

    tokenView.setTranslateX(0);
    tokenView.setTranslateY(0);

    TranslateTransition transition = new TranslateTransition(Duration.millis(1000), tokenView);
    transition.setFromX(0);
    transition.setFromY(0);
    transition.setToX((toCoords[1] - fromCoords[1]) * CELL_SIZE + (offsetX));
    transition.setToY((toCoords[0] - fromCoords[0]) * CELL_SIZE + (offsetY));

    transition.setOnFinished(event -> {
      tokenView.setTranslateX(offsetX);
      tokenView.setTranslateY(offsetY);
      if (onFinished != null) {
        onFinished.run();
      }
    });

    transition.play();
  }

  /**
   * Positions a token at the specified tile location.
   * @param tokenView The token image view.
   * @param tileId The tile ID.
   * @param playerIndex The player's index.
   */
  private void positionTokenAtTile(ImageView tokenView, int tileId, int playerIndex) {
    int[] coords = tileIdToGridCoordinates(tileId);
    int row = coords[0];
    int col = coords[1];

    double offsetX = (playerIndex % 2) * 20;
    double offsetY = (playerIndex / 2) * 20;

    boardGridPane.getChildren().remove(tokenView);

    tokenView.setTranslateX(offsetX);
    tokenView.setTranslateY(offsetY);
    tokenView.setLayoutX(0);
    tokenView.setLayoutY(0);

    StackPane cell = getStackPaneAt(row, col);
    if (cell != null) {
      cell.getChildren().add(tokenView);
    } else {
      tokenView.setLayoutX(col * CELL_SIZE + offsetX);
      tokenView.setLayoutY(row * CELL_SIZE + offsetY);
      boardGridPane.getChildren().add(tokenView);
    }
  }

  /**
   * Gets the StackPane at specific row and column.
   * Helper method for @positionTokenAtTile.
   * @param row The row index.
   * @param col The column index.
   * @return The StackPane at that position, or null if not found.
   */
  private StackPane getStackPaneAt(int row, int col) {
    for (javafx.scene.Node node : boardGridPane.getChildren()) {
      if (node instanceof StackPane &&
          GridPane.getRowIndex(node) == row &&
          GridPane.getColumnIndex(node) == col) {
        return (StackPane) node;
      }
    }
    return null;
  }

  /**
   * Converts a tile ID to grid coordinates.
   * @param tileId The tile ID.
   * @return Array with coordinates.
   */
  private int[] tileIdToGridCoordinates(int tileId) {
    tileId = Math.max(1, Math.min(90, tileId));

    int adjustedId = tileId - 1;

    int row = 9 - (adjustedId / 10);
    int col;

    if ((9 - row) % 2 == 0) {
      col = adjustedId % 10;
    } else {
      col = 9 - (adjustedId % 10);
    }

    return new int[] {row, col};
  }

  /**
   * Ends the game and displays the winner.
   * @param winner The winning player.
   */
  private void endGame(Player winner) {
    statusLabel.setText("Game Over! " + winner.getName() + " is the winner!");
    rollButton.setDisable(true);

    Button playAgainButton = new Button("Play Again");
    playAgainButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px 20px;");
    playAgainButton.setOnAction(event -> {
      stage.close();
      new BoardGameApplication().start(new Stage());
    });

    mainLayout.getChildren().add(playAgainButton);
  }
}