package edu.ntnu.idi.idatt.views;

import edu.ntnu.idi.idatt.BoardGameApplication;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * View for collecting player names.
 * Collects names for each player individually.
 */
public class PlayerNameView {
  private final BoardGameApplication application;
  private final String selectedGame;
  private final int playerCount;
  private final String[] playerNames;
  private int currentPlayerIndex;
  private Scene scene;

  /**
   * Constructor that creates the player name view.
   * @param application The main application instance.
   * @param selectedGame The game selected by the user.
   * @param playerCount The number of players.
   */
  public PlayerNameView(BoardGameApplication application, String selectedGame, int playerCount) {
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
    root.setPadding(new Insets(10));

    VBox layout = new VBox(30);
    layout.setAlignment(Pos.CENTER);
    layout.setPadding(new Insets(10));

    Label titleLabel = new Label("Player " + (currentPlayerIndex + 1) + ": Enter Name");
    titleLabel.setStyle("-fx-font-size: 18px;");

    TextField nameField = new TextField();
    nameField.setPromptText("Enter Player Name");
    nameField.setAlignment(Pos.CENTER);
    nameField.setMinWidth(50);
    nameField.setMaxWidth(200);
    nameField.prefWidthProperty().bind(root.widthProperty().multiply(0.6));

    Button continueButton = new Button("Continue");
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

    layout.getChildren().addAll(titleLabel, nameField, continueButton);

    scene = new Scene(layout, 800, 600);

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
   * @return The scene-
   */
  public Scene getScene() {
    return scene;
  }
}
