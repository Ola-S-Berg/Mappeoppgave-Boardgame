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
public class PlayerNameView extends AbstractMenuView {
  private final String selectedGame;
  private final int playerCount;
  private final String[] playerNames;
  private int currentPlayerIndex;
  private static final int MAX_NAME_LENGTH = 12;

  /**
   * Constructor that creates the player name view.
   *
   * @param application The main application instance.
   * @param selectedGame The game selected by the user.
   * @param playerCount The number of players.
   */
  public PlayerNameView(MainApp application, String selectedGame, int playerCount) {
    super(application);
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
    nameField.setPromptText("Enter Player Name (max 12 characters)");
    nameField.setAlignment(Pos.CENTER);
    nameField.getStyleClass().add("text-field");
    nameField.setMinWidth(50);
    nameField.setMaxWidth(200);
    nameField.prefWidthProperty().bind(root.widthProperty().multiply(0.6));

    
    nameField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.length() > MAX_NAME_LENGTH) {
        nameField.setText(oldValue);
      }
    });

    Label errorLabel = new Label();
    errorLabel.getStyleClass().add("error-label");
    errorLabel.setVisible(false);

    HBox buttonLayout = new HBox(20);
    buttonLayout.setAlignment(Pos.CENTER);

    Button continueButton = new Button("Continue");
    continueButton.getStyleClass().add("button");
    continueButton.getStyleClass().add("button-primary");
    continueButton.setDisable(true);

    nameField.textProperty().addListener((observable, oldValue, newValue) -> {
      String trimmedName = newValue.trim();
      boolean isEmpty = trimmedName.isEmpty();
      boolean isDuplicate = isDuplicateName(trimmedName);
      
      continueButton.setDisable(isEmpty || isDuplicate);
      
      if (isDuplicate) {
        errorLabel.setText("This name is already taken!");
        errorLabel.setVisible(true);
      } else {
        errorLabel.setVisible(false);
      }
    });

    continueButton.setOnAction(e -> {
      String playerName = nameField.getText().trim();
      if (!playerName.isEmpty() && !isDuplicateName(playerName)) {
        playerNames[currentPlayerIndex] = playerName;
        currentPlayerIndex++;

        if (currentPlayerIndex < playerCount) {
          updateView(titleLabel, nameField, errorLabel);
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

    layout.getChildren().addAll(titleLabel, nameField, errorLabel, buttonLayout);

    root.setCenter(layout);
    scene = new Scene(root, 800, 600);
    CssUtil.applyStyleSheet(scene);

    Stage stage = application.getPrimaryStage();
    stage.setMinWidth(600);
    stage.setMinHeight(600);
    stage.centerOnScreen();
  }

  /**
   * Checks if a name is already taken by another player.
   *
   * @param name The name to check.
   * @return true if the name is already taken, false otherwise.
   */
  private boolean isDuplicateName(String name) {
    for (int i = 0; i < currentPlayerIndex; i++) {
      if (name.equalsIgnoreCase(playerNames[i])) {
        return true;
      }
    }
    return false;
  }

  /**
   * Updates the view for the next player.
   *
   * @param titleLabel The title label to update.
   * @param nameField The name field to clear.
   * @param errorLabel The error label to reset.
   */
  private void updateView(Label titleLabel, TextField nameField, Label errorLabel) {
    titleLabel.setText("Player " + (currentPlayerIndex + 1) + ": Enter Name");
    nameField.clear();
    errorLabel.setVisible(false);
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