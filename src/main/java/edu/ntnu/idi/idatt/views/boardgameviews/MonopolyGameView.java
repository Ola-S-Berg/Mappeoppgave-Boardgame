package edu.ntnu.idi.idatt.views.boardgameviews;

import edu.ntnu.idi.idatt.actions.monopoly_game.PropertyTileAction;
import edu.ntnu.idi.idatt.controllers.MonopolyGameController;
import edu.ntnu.idi.idatt.model.BoardGame;
import edu.ntnu.idi.idatt.model.Player;
import java.util.Comparator;
import java.util.List;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

/**
 * View for the "Monopoly Game" game.
 * Displays the game board and player information, handles dice rolling, and shows player movement.
 * Communicates with the controller "MonopolyGameController" to handle game logic.
 * Extends AbstractGameView to inherit common game view functionality.
 */
public class MonopolyGameView extends AbstractBoardGameView {

  private final Map<Player, VBox> playerInfoCards = new HashMap<>();
  private boolean gameOverDisplayed = false;
  private static final int GRID_SIZE = 11;


  /**
   * Constructor that initializes the game view with a controller.
   *
   * @param boardGame The game logic.
   * @param stage The JavaFX stage to display the game on.
   * @param controller The controller that handles game logic.
   */
  public MonopolyGameView(BoardGame boardGame, Stage stage, MonopolyGameController controller) {
    super(boardGame, stage, controller);
    setupGameView();
  }

  /**
   * Gets the board image path for the Monopoly game.
   * @return The image path.
   */
  @Override
  protected String getBoardImagePath() {
    return "/images/Games/MonopolyGame.png";
  }

  /**
   * Gets the grid rows for the Monopoly game.
   *
   * @return The grid rows.
   */
  @Override
  protected int getGridRows() {
    return GRID_SIZE;
  }

  /**
   * Gets the grid columns for the Monopoly game.
   *
   * @return The grid columns.
   */
  @Override
  protected int getGridCols() {
    return GRID_SIZE;
  }

  /**
   * Gets the game title for the Monopoly game.
   *
   * @return The game title.
   */
  @Override
  protected String getGameTitle() {
    return "Monopoly Game";
  }

  /**
   * Gets the win message for the Monopoly game.
   *
   * @return The win message.
   */
  @Override
  protected String getWinMessage() {
    return "has won the game!";
  }

  /**
   * Gets the root border pane for the Monopoly game.
   *
   * @param root The root border pane.
   */
  @Override
  protected void setupBoardPane(BorderPane root) {
    super.setupBoardPane(root);

    VBox playerInfoPanel = setupPlayerInfoPanel();
    root.setRight(playerInfoPanel);
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
    super.onPlayerMove(player, fromTileId, toTileId, diceValue);
    if (diceValue > 0) {
      updatePlayerMoney(player);
    }
  }

  /**
   * Shows the action messages for landing on a tile with an action.
   *
   * @param player The player performing the action.
   * @param actionType The type of action being performed.
   */
  @Override
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
   * Prepares the view for the next turn and updates player highlights.
   */
  @Override
  public void prepareForNextTurn() {
    super.prepareForNextTurn();
    updateCurrentPlayerHighlight();
  }

  /**
   * Updates the view when the current player changes. Also updates player highlights.
   *
   * @param player The player who is currently taking their turn.
   */
  @Override
  public void onCurrentPlayerChanged(Player player) {
    super.onCurrentPlayerChanged(player);
    Platform.runLater(this::updateCurrentPlayerHighlight);
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

    Label moneyLabel = new Label(String.valueOf(player.getMoney()));
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
   * Determines the color style for a property based on their associated type.
   *
   * @param property The property object whose type determines the color style.
   * @return A String representing the CSS color style for the property.
   */
  private String getPropertyColorType(PropertyTileAction property) {
    String colorType = property.getPropertyType();

    if (colorType.contains("blue")) {
      return "-fx-text-fill: #379EFF;";
    } else if (colorType.contains("pink")) {
      return "-fx-text-fill: #FF38D4;";
    } else if (colorType.contains("green")) {
      return "-fx-text-fill: #39C739";
    } else if (colorType.contains("gray")) {
      return "-fx-text-fill: #ACACAC";
    } else if (colorType.contains("red")) {
      return "-fx-text-fill: #F01919";
    } else if (colorType.contains("yellow")) {
      return "-fx-text-fill: #FEFE10";
    } else if (colorType.contains("purple")) {
      return "-fx-text-fill: #5E294E";
    } else if (colorType.contains("orange")) {
      return "-fx-text-fill: #FF7B07";
    } else {
      return "-fx-text-fill: #000000";
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
      properties.sort(Comparator.comparing(PropertyTileAction::getPropertyName));

      for (PropertyTileAction property : properties) {
        Label propertyLabel = new Label("• " + property.getPropertyName());
        propertyLabel.setStyle("-fx-font-size: 11px;" + getPropertyColorType(property));
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
            ((Label) node).setText(String.valueOf(player.getMoney()));
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
    for (Map.Entry<Player, VBox> entry : playerInfoCards.entrySet()) {
      updatePlayerCardHighlight(entry.getValue(), entry.getKey());
    }
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
   * Updates the UI to reflect that a player has gone bankrupt.
   *
   * @param player The player who went bankrupt.
   */
  public void onPlayerBankrupt(Player player) {
    Platform.runLater(() -> {
      VBox playerCard = playerInfoCards.get(player);
      if (playerCard != null) {
        playerCard.setStyle("-fx-background-color: #ffebee; -fx-border-color: #ef5350;" +
            "-fx-border-width: 1px; -fx-border-radius: 5px; -fx-opacity: 0.7");
        Label bankruptLabel = new Label("Bankrupt");
        bankruptLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #d32f2f;");

        boolean hasLabel = false;
        for (javafx.scene.Node node : playerCard.getChildren()) {
          if (node instanceof Label && "Bankrupt".equals(((Label) node).getText())) {
            hasLabel = true;
            break;
          }
        }

        if (!hasLabel) {
          playerCard.getChildren().add(1, bankruptLabel);
        }

        actionLabel.setText(player.getName() + " has gone bankrupt");
        actionLabel.setVisible(true);

        if (controller instanceof MonopolyGameController) {
          ((MonopolyGameController) controller).handlePlayerBankrupt(player);
        }
      }
    });
  }

  /**
   * Displays a message indicating that a player has won the game.
   * Shows a dialog with the winner's information and options to play again or return to the menu.
   *
   * @param winner The player who has won the game.
   */
  public void showGameWonMessage(Player winner) {
    Platform.runLater(() -> {
      if (gameOverDisplayed) {
        return;
      }
      gameOverDisplayed = true;

      actionLabel.setText(winner.getName() + " " + getWinMessage());
      actionLabel.setVisible(true);
      rollButton.setDisable(true);

      VBox winnerCard = playerInfoCards.get(winner);
      if (winnerCard != null) {
        winnerCard.setStyle("-fx-background-color: #e8f5e9; -fx-border-color: #4caf50; " +
            "-fx-border-width: 2px; -fx-border-radius: 5px;");
      }

      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Game Over");
      alert.setHeaderText(winner.getName() + " has won the game!");
      alert.setContentText("Congratulations!" + winner.getName() + " has won the game!");
    });
  }
}