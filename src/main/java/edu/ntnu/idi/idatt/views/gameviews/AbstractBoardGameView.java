package edu.ntnu.idi.idatt.views.gameviews;

import edu.ntnu.idi.idatt.controllers.BoardGameController;
import edu.ntnu.idi.idatt.model.gamelogic.BoardGame;
import edu.ntnu.idi.idatt.model.gamelogic.BoardGameObserver;
import edu.ntnu.idi.idatt.model.gamelogic.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;


/**
 * <h1>Abstract Board Game View</h1>
 *
 * <p>An abstract class that provides a foundation for creating board game user interfaces
 * in JavaFX. This class implements the BoardGameObserver interface to handle game state updates
 * and renders them visually to the user.</p>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Dynamic game board creation and rendering based on superclass specifications</li>
 *   <li>Player token visualization and animation between board positions</li>
 *   <li>Dice rolling and display mechanics with visual feedback</li>
 *   <li>Real-time UI updates based on game state changes</li>
 *   <li>Window resize handling with responsive layout adjustments</li>
 *   <li>Game state messaging system to inform players of events</li>
 * </ul>
 *
 * <h2>Design Pattern</h2>
 * <p>This class uses the Template Method design pattern, defining the skeleton of the game UI
 * while allowing subclasses to override specific methods to provide game-specific behavior
 * without changing the overall structure.</p>
 *
 * <h2>Observer implementation</h2>
 * <p>As a BoardGameObserver, this class receives notifications about:</p>
 * <ul>
 *   <li>Player movements and position changes</li>
 *   <li>Turn transitions between players</li>
 *   <li>Game victory conditions</li>
 *   <li>Special events like bankruptcy or turn skipping</li>
 * </ul>
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
 */
public abstract class AbstractBoardGameView implements BoardGameObserver {

  protected final Stage stage;
  protected final BoardGame boardGame;
  protected final BoardGameController controller;
  protected GridPane boardGridPane;
  protected Label statusLabel;
  protected Label actionLabel;
  protected Button rollButton;
  protected ImageView diceView1;
  protected ImageView diceView2;
  protected final Map<Player, ImageView> playerTokenViews;
  protected final Map<Player, Boolean> animationsInProgress = new HashMap<>();
  protected double tokenSize = 30;
  protected double boardWidth;
  protected double boardHeight;
  protected static final Logger LOGGER = Logger.getLogger(AbstractBoardGameView.class.getName());

  /**
   * Constructor for the abstract board game view.
   *
   * @param boardGame The game model.
   * @param stage The JavaFX stage to display the game on.
   * @param controller The controller that handles game logic.
   */
  public AbstractBoardGameView(BoardGame boardGame, Stage stage, BoardGameController controller) {
    this.boardGame = boardGame;
    this.stage = stage;
    this.controller = controller;
    this.playerTokenViews = new HashMap<>();

    boardGame.addObserver(this);
  }

  /**
   * Gets the board image path for the specific game variation.
   *
   * @return The path to the board image resource.
   */
  protected abstract String getBoardImagePath();

  /**
   * Gets the number of rows in the grid.
   *
   * @return The number of rows.
   */
  protected abstract int getGridRows();

  /**
   * Gets the number of columns in the grid.
   *
   * @return The number of columns.
   */
  protected abstract int getGridCols();

  /**
   * Sets up the game UI, creating all necessary parts.
   * This method should be called after initialization.
   */
  protected void setupGameView() {
    BorderPane root = new BorderPane();
    root.setPadding(new Insets(10));

    setupTopSection(root);
    setupBoardPane(root);
    setupControlsPane(root);
    setupPlayerTokens();

    Scene scene = new Scene(root, 800, 700);
    stage.setScene(scene);
    stage.setTitle(getGameTitle());
    stage.setMinWidth(700);
    stage.setMinHeight(700);
    stage.centerOnScreen();
    stage.show();

    root.widthProperty().addListener((observable, oldValue, newValue) -> {
      boardWidth = newValue.doubleValue();
      updateCellSize();
    });
    root.heightProperty().addListener((observable, oldValue, newValue) -> {
      boardHeight = newValue.doubleValue();
      updateCellSize();
    });

    Platform.runLater(this::updateGridPaneSize);
  }

  /**
   * Gets the title for the game window.
   *
   * @return The game title.
   */
  protected abstract String getGameTitle();

  /**
   * Sets up the top section of the UI containing status labels.
   *
   * @param root The root border pane.
   */
  protected void setupTopSection(BorderPane root) {
    VBox topSection = new VBox(10);
    topSection.setAlignment(Pos.CENTER);
    topSection.setPadding(new Insets(0, 0, 10, 0));

    Player startingPlayer = boardGame.getCurrentPlayer() != null
                          ? boardGame.getCurrentPlayer() :
                            boardGame.getPlayers().getFirst();

    statusLabel = new Label("Game Started! " + startingPlayer.getName() + "'s Turn To Roll");
    statusLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");

    actionLabel = new Label("");
    actionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #CC0000;");
    actionLabel.setVisible(false);

    topSection.getChildren().addAll(statusLabel, actionLabel);
    root.setTop(topSection);
  }

  /**
   * Sets up the board pane containing the game board and grid.
   *
   * @param root The root border pane.
   */
  protected void setupBoardPane(BorderPane root) {
    StackPane boardPane = new StackPane();
    boardPane.setPadding(new Insets(5));
    boardPane.setStyle("-fx-background-color: white;"
        + " -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
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
    boardImageView.imageProperty().addListener((obs,
        oldImg, newImg) -> updateGridPaneSize());
    boardImageView.fitWidthProperty().addListener((obs,
        oldVal, newVal) -> updateGridPaneSize());
    boardImageView.fitHeightProperty().addListener((obs,
        oldVal, newVal) -> updateGridPaneSize());

    int rows = getGridRows();
    int cols = getGridCols();

    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        StackPane cell = new StackPane();
        cell.prefWidthProperty().bind(boardGridPane.widthProperty().divide(cols));
        cell.prefHeightProperty().bind(boardGridPane.heightProperty().divide(rows));
        boardGridPane.add(cell, col, row);
      }
    }

    boardPane.getChildren().addAll(boardImageView, boardGridPane);
    root.setCenter(boardPane);
  }

  /**
   * Sets up the controls pane containing buttons and dice.
   *
   * @param root The root border pane.
   */
  protected void setupControlsPane(BorderPane root) {
    Button saveButton = new Button("Save Game");
    saveButton.setMinWidth(120);
    saveButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px;"
        + " -fx-background-color: #4CAF50; -fx-text-fill: white;");
    saveButton.setOnAction(event -> {
      if (controller.saveGame()) {
        statusLabel.setText("Game saved successfully!");
      } else {
        statusLabel.setText("Failed to save game.");
      }
    });

    Button quitButton = new Button("Quit to Menu");
    quitButton.setMinWidth(120);
    quitButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px;"
        + " -fx-background-color: #CC0000; -fx-text-fill: white;");
    quitButton.setOnAction(event -> controller.quitToMenu());

    diceView1 = new ImageView();
    diceView2 = new ImageView();
    diceView1.setFitWidth(50);
    diceView1.setFitHeight(50);
    diceView2.setFitWidth(50);
    diceView2.setFitHeight(50);

    updateDieImages(1, 1);

    HBox diceBox = new HBox(15);
    diceBox.setAlignment(Pos.CENTER);
    diceBox.getChildren().addAll(diceView1, diceView2);

    Label rollDiceLabel = new Label("Roll Dice:");
    rollDiceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");

    rollButton = new Button();
    rollButton.setOnAction(event -> controller.rollDice());

    Image rollDieImage = new Image(Objects.requireNonNull(
        getClass().getResourceAsStream("/images/die/RollDie.png")));
    ImageView rollDieImageView = new ImageView(rollDieImage);
    rollDieImageView.setFitWidth(60);
    rollDieImageView.setFitHeight(60);
    rollDieImageView.setPreserveRatio(true);

    rollButton.setGraphic(rollDieImageView);
    rollButton.setStyle("-fx-background-color: transparent; -fx-padding: 5px;");

    HBox diceControlLayout = new HBox(15);
    diceControlLayout.setAlignment(Pos.CENTER);
    diceControlLayout.getChildren().addAll(rollDiceLabel, rollButton);

    VBox diceControlContainer = new VBox(10);
    diceControlContainer.setAlignment(Pos.CENTER);
    diceControlContainer.getChildren().addAll(diceBox, diceControlLayout);

    HBox buttonBox = new HBox(15);
    buttonBox.setAlignment(Pos.CENTER_LEFT);
    buttonBox.getChildren().addAll(saveButton, quitButton);

    BorderPane bottomLayout = new BorderPane();
    bottomLayout.setLeft(buttonBox);
    bottomLayout.setCenter(diceControlContainer);
    bottomLayout.setPadding(new Insets(10));
    root.setBottom(bottomLayout);
  }

  /**
   * Initializes and assigns visual tokens to all players in the game.
   */
  protected void setupPlayerTokens() {
    playerTokenViews.clear();

    for (Player player : boardGame.getPlayers()) {
      Image tokenImage = new Image(
          Objects.requireNonNull(getClass().getResourceAsStream(player.getToken())));
      ImageView tokenView = new ImageView(tokenImage);
      tokenView.setFitHeight(tokenSize);
      tokenView.setFitWidth(tokenSize);
      tokenView.setPreserveRatio(true);

      playerTokenViews.put(player, tokenView);
    }
  }

  /**
   * Updates the gridPane size to match the actual displayed size of the board image.
   */
  protected void updateGridPaneSize() {
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
  protected void updateCellSize() {
    tokenSize = Math.min(boardWidth / getGridCols(), boardHeight / getGridRows()) * 0.4;
    updatePlayerPositions();
  }

  /**
   * Updates player positions when resizing the view.
   */
  protected void updatePlayerPositions() {
    for (int i = 0; i < boardGame.getPlayers().size(); i++) {
      Player player = boardGame.getPlayers().get(i);
      ImageView tokenView = playerTokenViews.get(player);
      if (player.getCurrentTile() != null && tokenView != null) {
        positionTokenAtTile(tokenView, player.getCurrentTile().getTileId(), i);
      }
    }
  }

  /**
   * Positions a player's token on a specific tile on the game board.
   *
   * @param tokenView The ImageView representing the player's token.
   * @param tileId The ID of the tile where the token should be placed.
   * @param playerIndex The index of the player whose token is being positioned.
   */
  protected void positionTokenAtTile(ImageView tokenView, int tileId, int playerIndex) {
    int[] coords = controller.convertTileIdToGridCoordinates(tileId);
    int row = coords[0];
    int col = coords[1];

    StackPane cell = getStackPaneAt(row, col);
    if (cell != null) {
      StackPane currentParent = (StackPane) tokenView.getParent();
      if (currentParent != null) {
        currentParent.getChildren().remove(tokenView);
      }

      tokenView.setFitHeight(tokenSize);
      tokenView.setFitWidth(tokenSize);

      double[] offsetPosition = controller.calculateTokenOffset(playerIndex,
          boardGame.getPlayers().size(),
          Math.min(cell.getWidth(), cell.getHeight()) * 0.25);
      double offsetX = offsetPosition[0];
      double offsetY = offsetPosition[1];

      StackPane.setAlignment(tokenView, Pos.CENTER);
      StackPane.setMargin(tokenView, new Insets(offsetY, 0, 0, offsetX));
      cell.getChildren().add(tokenView);
    }
  }

  /**
   * Animates the movement of a player token from one tile to another.
   *
   * @param tokenView The token to animate.
   * @param fromTileId The starting tile ID.
   * @param toTileId The destination tile ID.
   * @param playerIndex The player's index in the player list.
   * @param onFinished A callback to run when the animation completes.
   */
  protected void animateTokenMovement(ImageView tokenView, int fromTileId,
      int toTileId, int playerIndex, Runnable onFinished) {
    if (fromTileId == toTileId) {
      if (onFinished != null) {
        onFinished.run();
      }
      return;
    }

    int[] fromCoords = controller.convertTileIdToGridCoordinates(fromTileId);
    int[] toCoords = controller.convertTileIdToGridCoordinates(toTileId);

    StackPane fromCell = getStackPaneAt(fromCoords[0], fromCoords[1]);
    StackPane toCell = getStackPaneAt(toCoords[0], toCoords[1]);

    if (fromCell == null || toCell == null) {
      System.out.println("ERROR: Could not find cells for animation from " + fromTileId
          + " to " + toTileId + " (coords: [" + fromCoords[0] + "," + fromCoords[1]
          + "] to [" + toCoords[0] + "," + toCoords[1] + "])");

      positionTokenAtTile(tokenView, toTileId, playerIndex);
      if (onFinished != null) {
        onFinished.run();
      }
      return;
    }

    Platform.runLater(() -> {
      Bounds fromBounds = fromCell.localToScene(fromCell.getBoundsInLocal());
      Bounds toBounds = toCell.localToScene(toCell.getBoundsInLocal());

      if (fromBounds.getWidth() <= 0 || fromBounds.getHeight() <= 0
          || toBounds.getWidth() <= 0 || toBounds.getHeight() <= 0) {
        System.out.println("ERROR: Invalid bounds for animation");
        positionTokenAtTile(tokenView, toTileId, playerIndex);
        if (onFinished != null) {
          onFinished.run();
        }
        return;
      }

      Image tokenImage = tokenView.getImage();
      ImageView animatedToken = new ImageView(tokenImage);
      animatedToken.setFitHeight(tokenSize);
      animatedToken.setFitWidth(tokenSize);
      animatedToken.setPreserveRatio(true);

      double[] offsetPosition = controller.calculateTokenOffset(playerIndex,
          boardGame.getPlayers().size(),
          Math.min(fromBounds.getWidth(), fromBounds.getHeight()) * 0.25);
      double offsetX = offsetPosition[0];
      double offsetY = offsetPosition[1];

      double startX = fromBounds.getMinX() + (fromBounds.getWidth() / 2)
          + offsetX - (tokenSize / 2);
      double startY = fromBounds.getMinY() + (fromBounds.getHeight() / 2)
          + offsetY - (tokenSize / 2);

      animatedToken.setLayoutX(startX);
      animatedToken.setLayoutY(startY);

      Pane rootPane = (Pane) stage.getScene().getRoot();
      rootPane.getChildren().add(animatedToken);

      StackPane currentParent = (StackPane) tokenView.getParent();
      if (currentParent != null) {
        currentParent.getChildren().remove(tokenView);
      }

      double endX = toBounds.getMinX() + (toBounds.getWidth() / 2) + offsetX - (tokenSize / 2);
      double endY = toBounds.getMinY() + (toBounds.getHeight() / 2) + offsetY - (tokenSize / 2);

      TranslateTransition transition = new TranslateTransition(Duration.millis(800), animatedToken);
      transition.setFromX(0);
      transition.setFromY(0);
      transition.setToX(endX - startX);
      transition.setToY(endY - startY);

      transition.setOnFinished(event -> {
        rootPane.getChildren().remove(animatedToken);
        positionTokenAtTile(tokenView, toTileId, playerIndex);
        if (onFinished != null) {
          onFinished.run();
        }
      });

      transition.play();
    });
  }

  /**
   * Retrieves the StackPane located at the specified row and column in the GridPane.
   *
   * @param row The row index.
   * @param col The column index.
   * @return The StackPane at the given coordinates, or null if not found.
   */
  protected StackPane getStackPaneAt(int row, int col) {
    for (javafx.scene.Node node : boardGridPane.getChildren()) {
      if (node instanceof StackPane
          && GridPane.getRowIndex(node) == row
          && GridPane.getColumnIndex(node) == col) {
        return (StackPane) node;
      }
    }
    return null;
  }

  /**
   * Updates the images displayed for the two dice based on the given dice values.
   *
   * @param dice1 The value of the first die.
   * @param dice2 The value of the second die.
   */
  protected void updateDieImages(int dice1, int dice2) {
    String path1 = "/images/die/Dice" + dice1 + ".png";
    try {
      diceView1.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path1))));
    } catch (NullPointerException e) {
      LOGGER.log(Level.WARNING, "Could not find image for first die: " + path1, e);
    }

    String path2 = "/images/die/Dice" + dice2 + ".png";
    try {
      diceView2.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path2))));
    } catch (NullPointerException e) {
      LOGGER.log(Level.WARNING, "Could not find image for second die: " + path2, e);
    }
  }

  /**
   * Ends the game and displays the winner's information.
   *
   * @param winner The player who has won the game.
   */
  protected void endGame(Player winner) {
    statusLabel.setText("Game Over! " + winner.getName() + " is the winner!");
    actionLabel.setText(winner.getName() + " " + getWinMessage());
    actionLabel.setVisible(true);
    rollButton.setDisable(true);

    Button playAgainButton = new Button("Play Again");
    playAgainButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px 20px;");
    playAgainButton.setOnAction(event -> controller.restartGame());

    BorderPane root = (BorderPane) stage.getScene().getRoot();
    VBox topSection = (VBox) root.getTop();
    topSection.getChildren().add(playAgainButton);
  }

  /**
   * Gets the game-specific win message.
   *
   * @return The win message.
   */
  protected abstract String getWinMessage();

  /**
   * Updates the dice display with new dice values.
   *
   * @param dice1 The value of the first die.
   * @param dice2 The value of the second die.
   */
  public void updateDiceDisplay(int dice1, int dice2) {
    Platform.runLater(() -> updateDieImages(dice1, dice2));
  }

  /**
   * Disables or enables the roll button.
   *
   * @param disable True to disable the button, false to enable it.
   */
  public void disableRollButton(boolean disable) {
    Platform.runLater(() -> {
      rollButton.setDisable(disable);
      if (disable) {
        rollButton.setOpacity(0.5);
      } else {
        rollButton.setOpacity(1.0);
      }
    });
  }

  /**
   * Prepares the view for the next player's turn.
   */
  public void prepareForNextTurn() {
    Platform.runLater(() -> {
      rollButton.setDisable(false);
      rollButton.setOpacity(1.0);
      actionLabel.setVisible(false);
    });
  }

  // BoardGameObserver implementation

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

        updateStatusLabelForMove(player, fromTileId, toTileId, diceValue);

        if (fromTileId != toTileId) {
          animationsInProgress.put(player, true);
          animateTokenMovement(tokenView, fromTileId, toTileId, playerIndex, () -> {
            animationsInProgress.put(player, false);
            performPostMoveUpdates(player);
          });
        } else {
          performPostMoveUpdates(player);
        }
      }
    });
  }

  /**
   * Updates the status label with the appropriate message for player movement.
   *
   * @param player The player who is moving.
   * @param fromTileId The ID of the tile the player is moving from.
   * @param toTileId The ID of the tile the player is moving to.
   * @param diceValue The value rolled on the dice which determined the movement.
   */
  protected void updateStatusLabelForMove(Player player, int fromTileId,
                                          int  toTileId, int diceValue) {
    if (diceValue > 0) {
      statusLabel.setText(player.getName() + " rolled " + diceValue + " and moved from "
          + fromTileId + " to " + toTileId);
    } else if (fromTileId != toTileId) {
      statusLabel.setText(player.getName() + " moved from " + fromTileId + " to "
          + toTileId + " due to an action");
    }
  }

  /**
   * Performs game-specific updates after a player's move is completed.
   * To Be overwritten by subclasses for game-specific behavior.
   *
   * @param player The player who just moved.
   */
  protected void performPostMoveUpdates(Player player) {}

  /**
   * Ends the game and displays the winner's information.
   *
   * @param player The player who has won the game.
   */
  @Override
  public void onGameWon(Player player) {
    Platform.runLater(() -> endGame(player));
  }

  /**
   * Handles the action when a player must skip their turn.
   *
   * @param player The player who is skipping their turn.
   */
  @Override
  public void onPlayerSkipTurn(Player player) {
    Platform.runLater(() -> {
      actionLabel.setText(player.getName() + " must skip their turn");
      actionLabel.setVisible(true);
      rollButton.setDisable(true);
      new Thread(() -> {
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          LOGGER.log(Level.SEVERE, "Failed to sleep thread", e);
        }
        Platform.runLater(() -> {
          rollButton.setDisable(false);
          actionLabel.setVisible(false);
        });
      }).start();
    });
  }

  /**
   * Updates the view when the current player changes.
   *
   * @param player The player who is currently taking their turn.
   */
  @Override
  public void onCurrentPlayerChanged(Player player) {
    Platform.runLater(() -> statusLabel.setText("It is " + player.getName() + "'s turn"));
  }

  /**
   * Called when a player goes bankrupt in the game.
   * Delegates to the concrete view implementation.
   *
   * @param player The player who went bankrupt.
   */
  @Override
  public void onPlayerBankrupt(Player player) {
    if (this instanceof MonopolyGameView) {
      this.onPlayerBankrupt(player);
    }
  }

  /**
   * Displays a game-specific action message.
   *
   * @param player The player performing the action.
   * @param actionType The type of action being performed.
   */
  public abstract void showActionMessage(Player player, String actionType);
}