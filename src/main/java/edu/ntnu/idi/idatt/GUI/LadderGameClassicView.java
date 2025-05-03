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
import javafx.geometry.Bounds;
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
 * Fix bug with GridPane where tokens get misaligned the further up they get.
 * Fix the statusLabel to more accurately display the status of the game.
 */
public class LadderGameClassicView implements BoardGameObserver {

  private final Stage stage;
  private final BoardGame boardGame;
  private GridPane boardGridPane;
  private Label statusLabel;
  private Button rollButton;
  private ImageView diceView1;
  private ImageView diceView2;
  private final Map<Player, ImageView> playerTokenViews;
  private int currentPlayerIndex = 0;
  private final String gameVariation;
  private static final int GRID_SIZE = 10;
  private double tokenSize = 30;
  private double boardWidth;
  private double boardHeight;


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

        positionTokenAtTile(tokenView, toTileId, playerIndex);

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
    BorderPane root = new BorderPane();
    root.setPadding(new Insets(10));

    statusLabel = new Label(
        "Game Started! " + boardGame.getPlayers().getFirst().getName() + "'s Turn To Roll");
    statusLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");

    VBox topSection = new VBox(10);
    topSection.setAlignment(Pos.CENTER);
    topSection.setPadding(new Insets(0, 0, 10, 0));
    topSection.getChildren().addAll(statusLabel);
    root.setTop(topSection);

    StackPane boardPane = new StackPane();
    boardPane.setPadding(new Insets(5));
    boardPane.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
    boardPane.prefWidthProperty().bind(root.widthProperty().multiply(0.85));
    boardPane.prefHeightProperty().bind(root.heightProperty().multiply(0.85));
    boardPane.setMinSize(400, 400);

    String imagePath = getBoardImagePath();
    Image boardImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
    ImageView boardImageView = new ImageView(boardImage);
    boardImageView.fitWidthProperty().bind(boardPane.widthProperty());
    boardImageView.fitHeightProperty().bind(boardPane.heightProperty());
    boardImageView.setPreserveRatio(true);

    boardGridPane = new GridPane();
    boardImageView.imageProperty().addListener((obs, oldImg, newImg) -> updateGridPaneSize());
    boardImageView.fitWidthProperty().addListener((obs, oldVal, newVal) -> updateGridPaneSize());
    boardImageView.fitHeightProperty().addListener((obs, oldVal, newVal) -> updateGridPaneSize());

    for (int row = 0; row < GRID_SIZE; row++) {
      for (int col = 0; col < GRID_SIZE; col++) {
        StackPane cell = new StackPane();
        cell.prefWidthProperty().bind(boardGridPane.widthProperty().divide(GRID_SIZE));
        cell.prefHeightProperty().bind(boardGridPane.heightProperty().divide(GRID_SIZE));
        boardGridPane.add(cell, col, row);
      }
    }

    boardPane.getChildren().addAll(boardImageView, boardGridPane);
    root.setCenter(boardPane);

    setupPlayerTokens();

    Platform.runLater(this::updateGridPaneSize);

    HBox controlSection = new HBox(20);
    controlSection.setAlignment(Pos.CENTER);
    controlSection.setSpacing(20);
    controlSection.setPadding(new Insets(15, 0, 5, 0));

    Button saveButton = new Button("Save Game");
    saveButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
    saveButton.setOnAction(event -> saveGame());

    VBox diceControlContainer = new VBox(10);
    diceControlContainer.setAlignment(Pos.CENTER);

    HBox diceBox = new HBox(15);
    diceBox.setAlignment(Pos.CENTER);

    diceView1 = new ImageView();
    diceView2 = new ImageView();
    diceView1.setFitWidth(50);
    diceView1.setFitHeight(50);
    diceView2.setFitWidth(50);
    diceView2.setFitHeight(50);

    updateDieImages(1, 1);

    diceBox.getChildren().addAll(diceView1, diceView2);

    HBox rollDiceBox = new HBox (10);
    rollDiceBox.setAlignment(Pos.CENTER);

    Label rollDiceLabel = new Label("Roll Dice:");
    rollDiceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");

    rollButton = new Button();
    rollButton.setOnAction(event -> rollDice());

    Image rollDieImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/die/RollDie.png")));
    ImageView rollDieImageView = new ImageView(rollDieImage);
    rollDieImageView.setFitWidth(60);
    rollDieImageView.setFitHeight(60);
    rollDieImageView.setPreserveRatio(true);

    rollButton.setGraphic(rollDieImageView);
    rollButton.setStyle("-fx-background-color: transparent; -fx-padding: 5px;");

    rollDiceBox.getChildren().addAll(rollDiceLabel, rollButton);

    diceControlContainer.getChildren().addAll(diceBox, rollDiceBox);

    HBox bottomContainer = new HBox();
    bottomContainer.setAlignment(Pos.CENTER);
    bottomContainer.setSpacing(20);
    bottomContainer.setPadding(new Insets(10));

    Region spacerLeft = new Region();
    HBox.setHgrow(spacerLeft, Priority.ALWAYS);

    Region spacerRight = new Region();
    HBox.setHgrow(spacerRight, Priority.ALWAYS);

    HBox saveButtonContainer = new HBox(saveButton);
    saveButtonContainer.setAlignment(Pos.BOTTOM_LEFT);
    saveButtonContainer.setPadding(new Insets(0, 0, 10, 10));
    saveButtonContainer.prefWidthProperty().bind(bottomContainer.widthProperty().multiply(0.1));

    diceControlContainer.prefWidthProperty().bind(bottomContainer.widthProperty().multiply(0.9));

    bottomContainer.getChildren().addAll(saveButtonContainer, diceControlContainer);
    root.setBottom(bottomContainer);

    Scene scene = new Scene(root, 800, 800);
    stage.setScene(scene);
    stage.setTitle("Ladder Game");
    stage.setMinWidth(600);
    stage.setMinHeight(600);
    stage.show();

    for (Player player : boardGame.getPlayers()) {
      if (player.getCurrentTile() == null) {
        player.placeOnTile(boardGame.getBoard().getTile(1));
      }
    }

    root.widthProperty().addListener((observable, oldValue, newValue) -> {
      boardWidth = newValue.doubleValue();
      updateCellSize();
    });
    root.heightProperty().addListener((observable, oldValue, newValue) -> {
      boardHeight = newValue.doubleValue();
      updateCellSize();
    });

    Platform.runLater(this::updatePlayerPositions);
  }

  /**
   * Updates the gridPane size to match the actual displayed size of the board image.
   */
  private void updateGridPaneSize() {
    Platform.runLater(() -> {
      ImageView boardImageView = null;

      for (javafx.scene.Node node : ((StackPane) boardGridPane.getParent()).getChildren()) {
        if (node instanceof ImageView) {
          boardImageView = (ImageView) node;
          break;
        }
      }

      if (boardImageView != null) {
        Bounds imageBounds = boardImageView.getBoundsInParent();
        double imageWidth = imageBounds.getWidth();
        double imageHeight = imageBounds.getHeight();

        boardGridPane.setPrefSize(imageWidth, imageHeight);
        boardGridPane.setMaxSize(imageWidth, imageHeight);
        boardGridPane.setMinSize(imageWidth, imageHeight);

        boardGridPane.setLayoutX(imageBounds.getMinX());
        boardGridPane.setLayoutY(imageBounds.getMinY());

        boardWidth = imageWidth;
        boardHeight = imageHeight;

        updateCellSize();
        updatePlayerPositions();
      }
    });
  }

  /**
   * Updates cell sizes dynamically when resizing the window.
   */
  private void updateCellSize() {
    double newCellSize = Math.min(boardWidth, boardHeight) / GRID_SIZE;
    tokenSize = newCellSize * 0.4;
    updatePlayerPositions();
  }

  /**
   * Updates player positions when resizing the view.
   */
  private void updatePlayerPositions() {
    for (int i = 0; i < boardGame.getPlayers().size(); i++) {
      Player player = boardGame.getPlayers().get(i);
      ImageView tokenView = playerTokenViews.get(player);
      if (player.getCurrentTile() != null && tokenView != null) {
        positionTokenAtTile(tokenView, player.getCurrentTile().getTileId(), i);
      }
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
   * Helper method for rollDice.
   * Updates the die images when displaying a rolled dice.
   * @param dice1 The image of the first dice rolled.
   * @param dice2 The image of the second dice rolled.
   */
  private void updateDieImages(int dice1, int dice2) {
    String path1 = "/images/die/Dice" + dice1 + ".png";
    String path2 = "/images/die/Dice" + dice2 + ".png";

    Image diceImage1 = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path1)));
    Image diceImage2 = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path2)));

    diceView1.setImage(diceImage1);
    diceView2.setImage(diceImage2);
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
      int dice1 = boardGame.getDice().roll();
      int dice2 = boardGame.getDice().roll();
      int diceValue = dice1 + dice2;

      currentPlayer.move(diceValue);

      final int finalDice1 = dice1;
      final int finalDice2 = dice2;

      Platform.runLater(() -> {
        updateDieImages(finalDice1, finalDice2);
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

    StackPane fromCell = getStackPaneAt(fromCoords[0], fromCoords[1]);
    StackPane toCell = getStackPaneAt(toCoords[0], toCoords[1]);

    if (fromCell == null || toCell == null) {
      System.out.println("ERROR: Could not find cells for animation from " + fromTileId + " to " + toTileId);
      if (onFinished != null) onFinished.run();
      return;
    }

    Bounds fromBounds = fromCell.localToScene(fromCell.getBoundsInLocal());
    Bounds toBounds = toCell.localToScene(toCell.getBoundsInLocal());

    Image tokenImage = tokenView.getImage();
    ImageView animatedToken = new ImageView(tokenImage);
    animatedToken.setFitHeight(tokenSize);
    animatedToken.setFitWidth(tokenSize);
    animatedToken.setPreserveRatio(true);

    StackPane overlayPane = new StackPane();
    overlayPane.setMouseTransparent(true);

    Scene scene = boardGridPane.getScene();
    StackPane sceneRoot = new StackPane();
    sceneRoot.getChildren().add(overlayPane);

    double offsetAngle = (playerIndex * (360.0 / boardGame.getPlayers().size())) * Math.PI / 180;
    double offsetRadius = Math.min(fromBounds.getWidth(), fromBounds.getHeight()) * 0.25;
    double offsetX = offsetRadius * Math.cos(offsetAngle);
    double offsetY = offsetRadius * Math.sin(offsetAngle);

    double startX = fromBounds.getMinX() + (fromBounds.getWidth() / 2) + offsetX - (tokenSize / 2);
    double startY = fromBounds.getMinY() + (fromBounds.getHeight() / 2) + offsetY - (tokenSize / 2);

    double endX = toBounds.getMinX() + (toBounds.getWidth() / 2) + offsetX - (tokenSize / 2);
    double endY = toBounds.getMinY() + (toBounds.getHeight() / 2) + offsetY - (tokenSize / 2);

    animatedToken.setLayoutX(startX);
    animatedToken.setLayoutY(startY);

    Pane rootPane = (Pane) scene.getRoot();
    rootPane.getChildren().add(animatedToken);

    StackPane currentParent = (StackPane) tokenView.getParent();
    if (currentParent != null) {
      currentParent.getChildren().remove(tokenView);
    }

    TranslateTransition transition = new TranslateTransition(Duration.millis(800), animatedToken);
    transition.setFromX(0);
    transition.setFromY(0);
    transition.setToX(endX - startX);
    transition.setToY(endY - startY);

    transition.setOnFinished(event -> {
      rootPane.getChildren().remove(animatedToken);

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

    Bounds gridBounds = boardGridPane.getBoundsInParent();
    double cellWidth = gridBounds.getWidth() / GRID_SIZE;
    double cellHeight = gridBounds.getHeight() / GRID_SIZE;

    tokenView.setFitHeight(tokenSize);
    tokenView.setFitWidth(tokenSize);

    double offsetAngle = (playerIndex * (360.0 / boardGame.getPlayers().size())) * Math.PI / 180;
    double offsetRadius = Math.min(cellWidth, cellHeight) * 0.25;
    double offsetX = offsetRadius * Math.cos(offsetAngle);
    double offsetY = offsetRadius * Math.sin(offsetAngle);

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
   * Gets the StackPane at a specific row and column.
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

    BorderPane root = (BorderPane) stage.getScene().getRoot();

    VBox topSection = (VBox) root.getTop();
    if (topSection != null) {
      topSection.getChildren().add(playAgainButton);
    } else {
      topSection = new VBox(10);
      topSection.setAlignment(Pos.CENTER);
      topSection.getChildren().addAll(statusLabel, playAgainButton);
      root.setTop(topSection);
    }
  }
}