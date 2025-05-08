package edu.ntnu.idi.idatt.views;

import edu.ntnu.idi.idatt.actions.monopoly_game.PropertyTileAction;
import edu.ntnu.idi.idatt.controllers.MonopolyGameController;
import edu.ntnu.idi.idatt.model.BoardGame;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.BoardGameObserver;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * View for the "Monopoly Game" game.
 * Displays the game board, handles dice rolling, and shows player movement.
 * Communicates with the controller "MonopolyGameController" to handle game logic.
 */
public class MonopolyGameView implements BoardGameObserver {

  private final Stage stage;
  private final BoardGame boardGame;
  private GridPane boardGridPane;
  private Label statusLabel;
  private Label actionLabel;
  private Button rollButton;
  private ImageView diceView1;
  private ImageView diceView2;
  private final Map<Player, ImageView> playerTokenViews;
  private static final int gridSize = 11;
  private double tokenSize = 20;
  private double boardWidth;
  private double boardHeight;
  private final MonopolyGameController controller;
  private final Map<Player, Boolean> animationsInProgress = new HashMap<>();
  private final Map<Player, VBox> playerInfoCards = new HashMap<>();
  private static final Logger LOGGER = Logger.getLogger(MonopolyGameView.class.getName());

  /**
   * Constructor that initializes the game view with a controller.
   *
   * @param boardGame The game logic.
   * @param stage The JavaFX stage to display the game on.
   * @param controller The controller that handles game logic.
   */
  public MonopolyGameView(BoardGame boardGame, Stage stage, MonopolyGameController controller) {
    this.boardGame = boardGame;
    this.stage = stage;
    this.playerTokenViews = new HashMap<>();
    this.controller = controller;

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

        if (diceValue > 0) {
          statusLabel.setText(player.getName() + " rolled " + diceValue + " and moved from " + fromTileId + " to " + toTileId);
          updatePlayerMoney(player);
        } else if (fromTileId != toTileId) {
          statusLabel.setText(player.getName() + " moved from " + fromTileId + " to " + toTileId + " due to an action");
        }

        Boolean inProgress = animationsInProgress.get(player);
        if (inProgress != null && inProgress) {
          return;
        }

        if (fromTileId != toTileId) {
          animationsInProgress.put(player, true);
          animateTokenMovement(tokenView, fromTileId, toTileId, playerIndex, () ->
              animationsInProgress.put(player, false));
        }
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
    Platform.runLater(() -> {
        statusLabel.setText("It is " + player.getName() + "'s turn");
        updateCurrentPlayerHighlight();
    });
  }

  /**
   * Sets up the player information panel as a vertically oriented box containing details about each player.
   * The panel includes a title and dynamically generated cards for every player in the game. Each card
   * displays the specific player's information. The method also adds a scrollable container to ensure
   * all player information is accessible, even if it exceeds the visible space.
   *
   * @return A VBox instance containing the styled and scrollable player information panel.
   */
  private VBox setupPlayerInfoPanel() {
    VBox infoPanel = new VBox(10);
    infoPanel.setPadding(new Insets(10));
    infoPanel.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #cccccc; -fx-border-width: 1px");
    infoPanel.setPrefWidth(200);
    infoPanel.setMinWidth(180);

    Label titleLabel = new Label("Player Information");
    titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
    titleLabel.setPadding(new Insets(0, 0, 10, 0));

    infoPanel.getChildren().addAll(titleLabel);

    for (Player player : boardGame.getPlayers()) {
      VBox playerCard = createPlayerInfoCard(player);
      playerInfoCards.put(player, playerCard);
      infoPanel.getChildren().add(playerCard);
    }

    ScrollPane scrollPane = new ScrollPane(infoPanel);
    scrollPane.setFitToWidth(true);
    scrollPane.setStyle("-fx-background-color: transparent;");

    VBox container = new VBox(scrollPane);
    container.setPrefWidth(200);
    container.setMinWidth(180);

    return container;
  }

  /**
   * Creates a visual information card for a player, displaying their name, money,
   * and properties in a styled container. The card includes a collapsible section
   * for properties and dynamically updates its components based on the player's data.
   *
   * @param player The Player object representing the details to be displayed in the card.
   * @return A VBox instance styled and populated with the player's information elements.
   */
  private VBox createPlayerInfoCard(Player player) {
    VBox playerCard = new VBox(5);
    playerCard.setPadding(new Insets(8));
    playerCard.setStyle("-fx-background-color: white; -fx-border-color: #dddddd; " +
        "-fx-border-width: 1px; -fx-border-radius: 5px;");

    String playerColor = getPlayerColorStyle(player);

    Label nameLabel = new Label(player.getName());
    nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; " + playerColor);

    Label moneyLabel = new Label (String.valueOf(player.getMoney()));
    moneyLabel.setId("money-" + player.getName());
    moneyLabel.setStyle("-fx-font-size: 13px;");

    VBox propertiesBox = new VBox(3);
    propertiesBox.setId("properties-" + player.getName());

    updatePlayerProperties(propertiesBox, player);

    TitledPane propertiesPane = new TitledPane("Properties", propertiesBox);
    propertiesPane.setCollapsible(true);
    propertiesPane.setExpanded(false);
    propertiesPane.setStyle("-fx-font-size: 12px;");

    playerCard.getChildren().addAll(nameLabel, moneyLabel, propertiesPane);

    updatePlayerCardHighlight(playerCard, player);

    return playerCard;
  }

  /**
   * Determines the color style for a player's token based on their associated token image.
   * The method maps specific token image paths to predefined color styles.
   *
   * @param player The Player object whose token determines the color style.
   * @return A String representing the CSS color style for the player's token.
   */
  private String getPlayerColorStyle(Player player) {
    String token = player.getToken();

    if (token.contains("Red")) {
      return "-fx-text-fill: #DE5757;";
    } else if (token.contains("LightBlue")) {
      return "-fx-text-fill: #17C1E8;";
    } else if (token.contains("Blue")) {
      return "-fx-text-fill: #002AFF;";
    } else if (token.contains("Green")) {
      return "-fx-text-fill: #7ACCA3;";
    } else if (token.contains("Pink")) {
      return "-fx-text-fill: #FC00BD;";
    } else {
      return "-fx-text-fill: #333333;";
    }
  }

  /**
   * Updates the specified VBox to display the properties owned by a given player.
   * The properties are retrieved from the player's data and displayed as a list of labels.
   * If the player owns no properties, a message stating this is displayed instead.
   *
   * @param propertiesBox The VBox container to populate with the player's property information.
   * @param player The Player object whose properties are to be displayed in the VBox.
   */
  private void updatePlayerProperties(VBox propertiesBox, Player player) {
    propertiesBox.getChildren().clear();

    List<PropertyTileAction> properties = player.getOwnedProperties();

    if (properties.isEmpty()) {
      Label noPropertiesLabel = new Label("No properties owned");
      noPropertiesLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #999999; -fx-font-size: 11px;");
      propertiesBox.getChildren().add(noPropertiesLabel);
    } else {
      properties.sort((p1, p2) -> p1.getPropertyName().compareTo(p2.getPropertyName()));

      for (PropertyTileAction property : properties) {
        Label propertyLabel = new Label("• " + property.getPropertyName());
        propertyLabel.setStyle("-fx-font-size: 11px;");
        propertiesBox.getChildren().add(propertyLabel);
      }
    }
  }

  /**
   * Updates the displayed money information for a specified player in the game's player information panel.
   * This method locates the corresponding UI label for the player's money and updates its value to reflect
   * the player's current monetary amount.
   *
   * @param player The Player object whose money information needs to be updated in the user interface.
   */
  public void updatePlayerMoney(Player player) {
    Platform.runLater(() -> {
      VBox playerCard = playerInfoCards.get(player);
      if (playerCard != null) {
       for (javafx.scene.Node node : playerCard.getChildren()) {
         if (node instanceof Label && node.getId() != null && node.getId().startsWith("money-" + player.getName())) {
           ((Label) node).setText(player.getMoney() + "");
           break;
         }
       }
      }
    });
  }

  /**
   * Updates the properties section of the specified player's information card in the UI.
   * This method locates the player's card, identifies the "Properties" section
   * within the card, and updates it with the player's latest property details.
   *
   * @param player The Player object whose properties are to be updated in the UI.
   */
  public void updatePlayerProperties(Player player) {
    Platform.runLater(() -> {
      VBox playerCard = playerInfoCards.get(player);
      if (playerCard != null) {
        for (javafx.scene.Node node : playerCard.getChildren()) {
          if (node instanceof TitledPane && "Properties".equals(((TitledPane) node).getText())) {
            VBox propertiesBox = (VBox) ((TitledPane) node).getContent();
            updatePlayerProperties(propertiesBox, player);
            break;
          }
        }
      }
    });
  }

  /**
   * Updates the visual highlight of a player's card in the game interface based on whether they are the current player.
   *
   * @param playerCard The visual element representing the player's card
   * @param player The player associated with the card being updated
   */
  private void updatePlayerCardHighlight(VBox playerCard, Player player) {
    if (player.equals(boardGame.getCurrentPlayer())) {
      playerCard.setStyle("-fx-background-color: #e3f2fd; -fx-border-color: #2196f3; " +
          "-fx-border-width: 2px; -fx-border-radius: 5px;");
    } else {
      playerCard.setStyle("-fx-background-color: white; -fx-border-color: #dddddd; " +
          "-fx-border-width: 1px; -fx-border-radius: 5px;");
    }
  }

  /**
   * Updates the visual highlight for the current player's information card in the UI.
   */
  public void updateCurrentPlayerHighlight() {
    Platform.runLater(() -> {
      for (Map.Entry<Player, VBox> entry : playerInfoCards.entrySet()) {
        updatePlayerCardHighlight(entry.getValue(), entry.getKey());
      }
    });
  }

  /**
   * Handles property change events for a player.
   *
   * @param player The player whose property is being changed.
   * @param propertyName The name of the property that has changed.
   */
  public void onPropertyChange(Player player, String propertyName) {
    Platform.runLater(() -> {
      updatePlayerMoney(player);
      updatePlayerProperties(player);
    });
  }

  /**
   * Handles the event of a money change for the specified player.
   *
   * @param player The player whose money has changed.
   */
  public void onMoneyChange(Player player) {
    updatePlayerMoney(player);
  }

  /**
   * Initializes and sets up the game's main view, including the layout, UI components,
   * and event handlers. This method defines the structure of the game UI such as the
   * board, dice, player tokens, and control buttons.
   */
  private void setupGameView() {
    BorderPane root = new BorderPane();
    root.setPadding(new Insets(10));

    VBox topSection = new VBox(10);
    topSection.setAlignment(Pos.CENTER);
    topSection.setPadding(new Insets(0, 0, 10, 0));

    statusLabel = new Label("Game Started! " + boardGame.getPlayers().getFirst().getName() + "'s Turn To Roll");
    statusLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");

    actionLabel = new Label("");
    actionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #CC0000;");
    actionLabel.setVisible(false);

    topSection.getChildren().addAll(statusLabel, actionLabel);
    root.setTop(topSection);

    StackPane boardPane = new StackPane();
    boardPane.setPadding(new Insets(5));
    boardPane.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
    boardPane.prefWidthProperty().bind(root.widthProperty().multiply(0.75));
    boardPane.prefHeightProperty().bind(root.heightProperty().multiply(0.85));
    boardPane.setMinSize(400, 400);

    String imagePath = "/images/Games/MonopolyGame.png";
    Image boardImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
    ImageView boardImageView = new ImageView(boardImage);
    boardImageView.fitWidthProperty().bind(boardPane.widthProperty());
    boardImageView.fitHeightProperty().bind(boardPane.heightProperty());
    boardImageView.setPreserveRatio(true);

    boardGridPane = new GridPane();
    boardImageView.imageProperty().addListener((obs, oldImg, newImg) -> updateGridPaneSize());
    boardImageView.fitWidthProperty().addListener((obs, oldVal, newVal) -> updateGridPaneSize());
    boardImageView.fitHeightProperty().addListener((obs, oldVal, newVal) -> updateGridPaneSize());

    for (int row = 0; row < gridSize; row++) {
      for (int col = 0; col < gridSize; col++) {
        StackPane cell = new StackPane();
        cell.prefWidthProperty().bind(boardGridPane.widthProperty().divide(gridSize));
        cell.prefHeightProperty().bind(boardGridPane.heightProperty().divide(gridSize));
        boardGridPane.add(cell, col, row);
      }
    }

    boardPane.getChildren().addAll(boardImageView, boardGridPane);
    root.setCenter(boardPane);

    VBox playerInfoPanel = setupPlayerInfoPanel();
    root.setRight(playerInfoPanel);

    setupPlayerTokens();

    Button saveButton = new Button("Save Game");
    saveButton.setMinWidth(120);
    saveButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
    saveButton.setOnAction(event -> {
      if (controller.saveGame()) {
        statusLabel.setText("Game saved successfully!");
      } else {
        statusLabel.setText("Failed to save game.");
      }
    });

    Button quitButton = new Button("Quit to Menu");
    quitButton.setMinWidth(120);
    quitButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-color: #CC0000; -fx-text-fill: white;");
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

    Image rollDieImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/die/RollDie.png")));
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

    Scene scene = new Scene(root, 1000, 800);
    stage.setScene(scene);
    stage.setTitle("Monopoly Game");
    stage.setMinWidth(800);
    stage.setMinHeight(600);
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
    tokenSize = Math.min(boardWidth / gridSize, boardHeight / gridSize) * 0.4;
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
   * Updates the dice display with new dice values.
   *
   * @param dice1 The value of the first die.
   * @param dice2 The value of the second die.
   */
  public void updateDiceDisplay(int dice1, int dice2) {
    Platform.runLater(() -> updateDieImages(dice1, dice2));
  }

  /**
   * Displays an action message for a player.
   *
   * @param player The player performing the action.
   * @param actionType The type of action being performed.
   */
  public void showActionMessage(Player player, String actionType) {
    Platform.runLater(() -> {
      switch (actionType) {
        case "PropertyAction":
          actionLabel.setText(player.getName() + " landed on a property");
          break;
        case "ChanceAction":
          actionLabel.setText(player.getName() + " landed on chance");
          break;
        case "JailAction":
          actionLabel.setText(player.getName() + " is going to jail");
          break;
        case "TaxAction":
          actionLabel.setText(player.getName() + " must pay tax");
          break;
        default:
          actionLabel.setText(player.getName() + " landed on a tile action");
          break;
      }
      actionLabel.setVisible(true);
    });
  }

  /**
   * Enables or disables the roll button.
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
      updateCurrentPlayerHighlight();
    });
  }

  /**
   * Initializes and assigns visual tokens to all players in the game.
   * This method clears any previously stored player tokens before setup.
   * For each player in the game, it retrieves the corresponding token image,
   * resizes it according to the predefined token size, and maps it to the player.
   */
  private void setupPlayerTokens() {
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
   * Updates the images displayed for the two dice based on the given dice values.
   *
   * @param dice1 The value of the first die (expected range: 1-6).
   * @param dice2 The value of the second die (expected range: 1-6).
   */
  private void updateDieImages(int dice1, int dice2) {
    String path1 = "/images/die/Dice" + dice1 + ".png";
    String path2 = "/images/die/Dice" + dice2 + ".png";

    diceView1.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path1))));
    diceView2.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path2))));
  }


  /**
   * Animates the movement of a player's token from one tile to another on the game board.
   *
   * @param tokenView The ImageView representing the player's token to be animated.
   * @param fromTileId The ID of the tile the token is moving from.
   * @param toTileId The ID of the tile the token is moving to.
   * @param playerIndex The index of the player whose token is being animated.
   * @param onFinished A callback Runnable that is executed after the animation completes.
   */
  private void animateTokenMovement(ImageView tokenView, int fromTileId, int toTileId, int playerIndex, Runnable onFinished) {
    if (fromTileId == toTileId) {
      if (onFinished != null) onFinished.run();
      return;
    }

    int[] fromCoords = controller.convertTileIdToGridCoordinates(fromTileId);
    int[] toCoords = controller.convertTileIdToGridCoordinates(toTileId);

    StackPane fromCell = getStackPaneAt(fromCoords[0], fromCoords[1]);
    StackPane toCell = getStackPaneAt(toCoords[0], toCoords[1]);

    if (fromCell == null || toCell == null) {
      System.out.println("ERROR: Could not find cells for animation from " + fromTileId +
          " to " + toTileId + " (coords: [" + fromCoords[0] + "," + fromCoords[1] +
          "] to [" + toCoords[0] + "," + toCoords[1] + "])");

      positionTokenAtTile(tokenView, toTileId, playerIndex);
      if (onFinished != null) onFinished.run();
      return;
    }

    Platform.runLater(() -> {
      Bounds fromBounds = fromCell.localToScene(fromCell.getBoundsInLocal());
      Bounds toBounds = toCell.localToScene(toCell.getBoundsInLocal());

      if (fromBounds.getWidth() <= 0 || fromBounds.getHeight() <= 0 ||
          toBounds.getWidth() <= 0 || toBounds.getHeight() <= 0) {
        System.out.println("ERROR: Invalid bounds for animation");
        positionTokenAtTile(tokenView, toTileId, playerIndex);
        if (onFinished != null) onFinished.run();
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

      double startX = fromBounds.getMinX() + (fromBounds.getWidth() / 2) + offsetX - (tokenSize / 2);
      double startY = fromBounds.getMinY() + (fromBounds.getHeight() / 2) + offsetY - (tokenSize / 2);

      double endX = toBounds.getMinX() + (toBounds.getWidth() / 2) + offsetX - (tokenSize / 2);
      double endY = toBounds.getMinY() + (toBounds.getHeight() / 2) + offsetY - (tokenSize / 2);

      animatedToken.setLayoutX(startX);
      animatedToken.setLayoutY(startY);

      Pane rootPane = (Pane) stage.getScene().getRoot();
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
    });
  }

  /**
   * Positions a player's token on a specific tile on the game board.
   * This method now delegates the calculation of player positioning to the controller.
   *
   * @param tokenView The ImageView representing the player's token.
   * @param tileId The ID of the tile where the token should be placed.
   * @param playerIndex The index of the player whose token is being positioned.
   */
  private void positionTokenAtTile(ImageView tokenView, int tileId, int playerIndex) {
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
   * Retrieves the StackPane located at the specified row and column in the GridPane.
   * If no StackPane exists at the specified coordinates, the method returns null.
   *
   * @param row The row index of the desired StackPane.
   * @param col The column index of the desired StackPane.
   * @return The StackPane located at the given row and column, or null if no StackPane is found.
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
   * Ends the game and displays the winner's information. This method updates the game's user interface
   * to show the winning player's details, disables further input from the user, and provides an option
   * to restart the game.
   *
   * @param winner The player who has won the game.
   */
  private void endGame(Player winner) {
    statusLabel.setText("Game Over! " + winner.getName() + " is the winner!");
    actionLabel.setText(winner.getName() + " has won the game!");
    actionLabel.setVisible(true);
    rollButton.setDisable(true);

    Button playAgainButton = new Button("Play Again");
    playAgainButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px 20px;");
    playAgainButton.setOnAction(event -> controller.restartGame());

    BorderPane root = (BorderPane) stage.getScene().getRoot();
    VBox topSection = (VBox) root.getTop();
    topSection.getChildren().add(playAgainButton);
  }
}
