package edu.ntnu.idi.idatt.views.menuviews;

import edu.ntnu.idi.idatt.MainApp;
import edu.ntnu.idi.idatt.views.CssUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * <h1>Player Name View</h1>
 *
 * <p>This view handles the collection of player names in a sequential manner, displaying an
 * input field for each player one at a time. Builds upon the player count selected in the
 * previous view and prepares the data for token selection.</p>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Collects names for each player individually through a step-by-step process</li>
 *   <li>Validates inputs to ensure names are not empty</li>
 *   <li>Provides visual feedback for the current player being prompted</li>
 *   <li>Maintains state across multiple player entries</li>
 *   <li>Includes navigation controls to return to previous steps</li>
 * </ul>
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
 */
public class PlayerNameView {
  private final MainApp application;
  private final String selectedGame;
  private final int playerCount;
  private final String[] playerNames;
  private int currentPlayerIndex;
  private Scene scene;

  /**
   * Constructor that creates the player name view.
   *
   * @param application The main application instance.
   * @param selectedGame The game selected by the user.
   * @param playerCount The number of players.
   */
  public PlayerNameView(MainApp application, String selectedGame, int playerCount) {
    this.application = application;
    this.selectedGame = selectedGame;
    this.playerCount = playerCount;
    this.playerNames = new String[playerCount];
    this.currentPlayerIndex = 0;

    createView();
  }

  /**
   * Creates the player name view components.
   */
  private void createView() {
    BorderPane root = new BorderPane();
    root.getStyleClass().add("root");
    root.setPadding(new Insets(10));

    VBox layout = new VBox(30);
    layout.setAlignment(Pos.CENTER);
    layout.setPadding(new Insets(10));
    layout.getStyleClass().add("content-box");

    Label titleLabel = new Label("Player " + (currentPlayerIndex + 1) + ": Enter Name");
    titleLabel.getStyleClass().add("heading-medium");

    TextField nameField = new TextField();
    nameField.setPromptText("Enter Player Name");
    nameField.setAlignment(Pos.CENTER);
    nameField.getStyleClass().add("text-field");
    nameField.setMinWidth(50);
    nameField.setMaxWidth(200);
    nameField.prefWidthProperty().bind(root.widthProperty().multiply(0.6));

    HBox buttonLayout = new HBox(20);
    buttonLayout.setAlignment(Pos.CENTER);

    Button continueButton = new Button("Continue");
    continueButton.getStyleClass().add("button");
    continueButton.getStyleClass().add("button-primary");

    continueButton.setDisable(true);

    nameField.textProperty().addListener((observable, oldValue, newValue) ->
        continueButton.setDisable(newValue.trim().isEmpty()));

    continueButton.setOnAction(e -> {
      String playerName = nameField.getText().trim();
      if (!playerName.isEmpty()) {
        playerNames[currentPlayerIndex] = playerName;
        currentPlayerIndex++;

        if (currentPlayerIndex < playerCount) {
          updateView(titleLabel, nameField);
        } else {
          application.showTokenSelectionView(selectedGame, playerNames);
        }
      }
    });

    Button backButton = new Button("Back To Player Count Selection");
    backButton.getStyleClass().add("button");
    backButton.getStyleClass().add("button-secondary");
    backButton.setOnAction(event -> application.showPlayerCountView(selectedGame));

    buttonLayout.getChildren().addAll(backButton, continueButton);

    layout.getChildren().addAll(titleLabel, nameField, buttonLayout);

    root.setCenter(layout);
    scene = new Scene(root, 800, 600);
    CssUtil.applyStyleSheet(scene);

    Stage stage = application.getPrimaryStage();
    stage.setMinWidth(600);
    stage.setMinHeight(600);
    stage.centerOnScreen();
  }

  /**
   * Updates the view for the next player.
   *
   * @param titleLabel The title label to update.
   * @param nameField The name field to clear.
   */
  private void updateView(Label titleLabel, TextField nameField) {
    titleLabel.setText("Player " + (currentPlayerIndex + 1) + ": Enter Name");
    nameField.clear();
  }

  /**
   * Returns the scene for this view.
   *
   * @return The scene for this view.
   */
  public Scene getScene() {
    return scene;
  }
}