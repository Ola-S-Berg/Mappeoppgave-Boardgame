package edu.ntnu.idi.idatt.GUI;

import edu.ntnu.idi.idatt.BoardGameApplication;
import edu.ntnu.idi.idatt.Filehandling.PlayerFileHandler;
import edu.ntnu.idi.idatt.GameLogic.BoardGame;
import edu.ntnu.idi.idatt.GameLogic.Player;
import edu.ntnu.idi.idatt.Filehandling.BoardGameFactory;
import edu.ntnu.idi.idatt.GameLogic.BoardGameObserver;
import java.io.IOException;
import java.util.Objects;
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
 * TODO:
 * Display die images when rolling.
 * Add tile information.
 * Make GUI look and flow better in general.
 * Remove unnecessary code.
 * Create GUI and code for saving and loading the board and players from files.
 * Fix a bug with wait action.
 */
public class LadderGameClassicView implements BoardGameObserver {

  private final Stage stage;
  private final BoardGame boardGame;
  private GridPane boardGridPane;
  private VBox mainLayout;
  private Label statusLabel;
  private Button rollButton;
  private final Map<Player, ImageView> playerTokenViews;
  private int currentPlayerIndex = 0;
  private final String gameVariation;

  private static final int CELL_SIZE = 60;
  private static final int GRID_SIZE = 10;
  double radius = CELL_SIZE * 0.65;
  double imageOffsetY = -60;


  /**
   * Constructor that initializes the game view.
   * @param boardGame The game logic.
   * @param stage The JavaFX stage to display the game on.
   */
  public LadderGameClassicView (BoardGame boardGame, Stage stage, String gameVariation) {
    this.boardGame = boardGame;
    this.stage = stage;
    this.playerTokenViews = new HashMap<>();
    this.gameVariation = gameVariation;

    boardGame.addObserver(this);

    setupGameView();
  }

  /**
   * Handles the movement of a player's token on the game board.
   *
   * @param player The player whose token is moving.
   * @param fromTileId The ID of the tile the player is moving from.
   * @param toTileId The ID of the tile the player is moving to.
   * @param diceValue The value rolled on the dice which determined the movement.
   */
  @Override
  public void onPlayerMove(Player player, int fromTileId, int toTileId, int diceValue) {
    Platform.runLater(() -> {
      ImageView tokenView = playerTokenViews.get(player);
      if (tokenView != null) {
        int playerIndex = boardGame.getPlayers().indexOf(player);

        animateTokenMovement(tokenView, fromTileId, toTileId, playerIndex, () ->
            statusLabel.setText(player.getName() + " moved from " + fromTileId +
            " to " + toTileId + " (dice roll: " + diceValue + ")"));
      }
    });
  }

  /**
   * Ends the game and displays the winner's information.
   *
   * @param player The player who has won the game.
   */
  @Override
  public void onGameWon(Player player) {
    endGame(player);
  }

  /**
   * Handles the action when a player must skip their turn. Updates the game status
   * to reflect the player's skipped turn and disables the roll button temporarily.
   *
   * @param player The player who is skipping their turn.
   */
  @Override
  public void onPlayerSkipTurn(Player player) {
    Platform.runLater(() -> {
      statusLabel.setText(player.getName() + " Must skip their turn");
      rollButton.setDisable(true);
      new Thread(() -> {
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        Platform.runLater(() -> rollButton.setDisable(false));
      }).start();
    });
  }

  /**
   * Updates the view when the current player changes.
   * Displays a status message indicating the current player's turn
   * and updates the index of the current player in the game.
   *
   * @param player The player who is currently taking their turn.
   */
  @Override
  public void onCurrentPlayerChanged(Player player) {
    Platform.runLater(() -> {
      statusLabel.setText("It is " + player.getName() + "'s turn");
      currentPlayerIndex = boardGame.getPlayers().indexOf(player);
    });
  }

  /**
   * Gets the appropriate board image path based on the game variation.
   * @return The path to the board image.
   */
  private String getBoardImagePath() {
    return switch (gameVariation) {
      case "Classic Ladder Game Advanced" -> "/images/Games/LadderGameAdvanced.png";
      case "Classic Ladder Game Extreme" -> "/images/Games/LadderGameExtreme.png";
      default -> "/images/Games/LadderGame.png";
    };
  }

  /**
   * Sets up the game view with board image, player tokens, and controls.
   */
  private void setupGameView() {
    mainLayout = new VBox(20);
    mainLayout.setPadding(new Insets(20));
    mainLayout.setAlignment(Pos.CENTER);

    statusLabel = new Label(
        "Game Started! " + boardGame.getPlayers().getFirst().getName() + "'s Turn To Roll");
    statusLabel.setStyle("-fx-font-size: 18px");

    StackPane boardPane = new StackPane();
    boardPane.setMinSize(600, 600);
    boardPane.setMaxSize(600, 600);

    String imagePath = getBoardImagePath();
    Image boardImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
    ImageView boardImageView = new ImageView(boardImage);
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

    Button saveButton = new Button("Save Game");
    saveButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px 20px");
    saveButton.setOnAction(event -> saveGame());

    rollButton = new Button("Roll Die");
    rollButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px 20px");
    rollButton.setOnAction(event -> rollDice());

    mainLayout.getChildren().addAll(statusLabel, boardPane, saveButton, rollButton);

    Scene scene = new Scene(mainLayout, 800, 800);
    stage.setScene(scene);
    stage.setTitle("Ladder Game");
    stage.show();

    for (Player player : boardGame.getPlayers()) {
      if (player.getCurrentTile() == null) {
        player.placeOnTile(boardGame.getBoard().getTile(1));
      }
    }

    for (int i = 0; i < boardGame.getPlayers().size(); i++) {
      Player player = boardGame.getPlayers().get(i);
      ImageView tokenView = playerTokenViews.get(player);
      positionTokenAtTile(tokenView, player.getCurrentTile().getTileId(), i);
    }
  }

  /**
   * Saves the current state of the game to a file.
   */
  private void saveGame() {
    try {
      String saveName = "SavedGame";
      BoardGameFactory.saveBoardGame(boardGame, saveName);
      new PlayerFileHandler().writeToFile("src/main/resources/Saves/" + saveName + "_players.csv", boardGame.getPlayers());
      statusLabel.setText("Game saved successfully!");
    } catch (IOException e) {
      statusLabel.setText("Failed to save game.");
      e.printStackTrace();
    }
  }

  /**
   * Sets up player tokens on the board.
   */
  private void setupPlayerTokens() {
    playerTokenViews.clear();

    for (Player player : boardGame.getPlayers()) {
      Image tokenImage = new Image(
          Objects.requireNonNull(getClass().getResourceAsStream(player.getToken())));
      ImageView tokenView = new ImageView(tokenImage);
      tokenView.setFitHeight(30);
      tokenView.setFitWidth(30);
      tokenView.setPreserveRatio(true);

      playerTokenViews.put(player, tokenView);

    }
  }

  /**
   * Manages the player's dice roll during the game, updates the game state,
   * and handles gameplay logic for movement, skipping turns, and checking victory conditions.
   */
  private void rollDice() {
    rollButton.setDisable(true);

    Player currentPlayer = boardGame.getPlayers().get(currentPlayerIndex);

    if (currentPlayer.willWaitTurn()) {
      boardGame.notifyPlayerSkipTurn(currentPlayer);
      currentPlayer.setWaitTurn(false);

      currentPlayerIndex = (currentPlayerIndex + 1) % boardGame.getPlayers().size();
      Player nextPlayer = boardGame.getPlayers().get(currentPlayerIndex);
      boardGame.notifyCurrentPlayerChanged(nextPlayer);

      new Thread(() -> {
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        Platform.runLater(() -> rollButton.setDisable(false));
      }).start();

      return;
    }

    new Thread(() -> {
      int diceValue = boardGame.getDice().roll();

      currentPlayer.move(diceValue);

      Platform.runLater(() -> {
        if (currentPlayer.getCurrentTile().getTileId() != 90) {
          currentPlayerIndex = (currentPlayerIndex + 1) % boardGame.getPlayers().size();
          Player nextPlayer = boardGame.getPlayers().get(currentPlayerIndex);
          boardGame.notifyCurrentPlayerChanged(nextPlayer);

          new Thread(() -> {
            try {
              Thread.sleep(1000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            Platform.runLater(() -> rollButton.setDisable(false));
          }).start();
        }
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
      if (onFinished != null) onFinished.run();
      return;
    }

    int[] fromCoords = tileIdToGridCoordinates(fromTileId);
    int[] toCoords = tileIdToGridCoordinates(toTileId);

    double startX = tokenView.getTranslateX();
    double startY = tokenView.getTranslateY();

    double deltaX = (toCoords[1] - fromCoords[1]) * CELL_SIZE;
    double deltaY = (toCoords[0] - fromCoords[0]) * CELL_SIZE;

    TranslateTransition transition = new TranslateTransition(Duration.millis(1000), tokenView);
    transition.setFromX(startX);
    transition.setFromY(startY);
    transition.setToX(deltaX);
    transition.setToY(deltaY);

    transition.setOnFinished(event -> {
      StackPane currentParent = (StackPane) tokenView.getParent();
      if (currentParent != null) {
        currentParent.getChildren().remove(tokenView);
      }
      tokenView.setTranslateX(0);
      tokenView.setTranslateY(0);
      positionTokenAtTile(tokenView, toTileId, playerIndex);
      if (onFinished != null) onFinished.run();
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

    float totalPlayers = boardGame.getPlayers().size();
    double angle = (playerIndex * (360 / totalPlayers)) * Math.PI / 180;

    double offsetX = radius * Math.cos(angle);
    double offsetY = radius * Math.sin(angle) + imageOffsetY;

    boardGridPane.getChildren().remove(tokenView);

    StackPane currentParent = (StackPane) tokenView.getParent();
    if (currentParent != null) {
      currentParent.getChildren().remove(tokenView);
    }

    StackPane cell = getStackPaneAt(row, col);
    if (cell != null) {
      StackPane.setAlignment(tokenView, Pos.CENTER);
      StackPane.setMargin(tokenView, new Insets(offsetY, 0, 0, offsetX));
      cell.getChildren().add(tokenView);
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