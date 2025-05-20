package edu.ntnu.idi.idatt.views.menuviews;

import edu.ntnu.idi.idatt.MainApp;
import edu.ntnu.idi.idatt.views.CssUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;

/**
 * <h1>Token Selection View</h1>
 *
 * <p>This view enables players to select unique game tokens that will represent them on the
 * game board. It presents a grid of available tokens and handles the selection process
 * for each player sequentially, ensuring that no two players can select the same token.</p>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Displays a visually engaging grid of token options</li>
 *   <li>Processes players sequentially, showing the current player's name</li>
 *   <li>Disables already selected tokens with visual representation to prevent duplicates </li>
 *   <li>Initiates game start when all selections are complete</li>
 *   <li>Includes navigation controls to return to previous steps</li>
 * </ul>
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
 */
public class TokenSelectionView extends AbstractMenuView {
  private final String selectedGame;
  private final String[] playerNames;
  private final String[] tokenPaths = {
      "/images/tokens/BlueToken.png",
      "/images/tokens/LightBlueToken.png",
      "/images/tokens/RedToken.png",
      "/images/tokens/GreenToken.png",
      "/images/tokens/PinkToken.png"
  };
  private final int[][] tokenPositions = {
      {1, 0},
      {0, 1},
      {1, 1},
      {2, 1},
      {1, 2},
  };
  private Button[] tokenButtons;
  private int currentPlayerIndex;
  private String[] selectedTokenPaths;
  private static final Logger LOGGER = Logger.getLogger(TokenSelectionView.class.getName());

  /**
   * Constructor that creates the token selection view.
   *
   * @param application The main application instance.
   * @param selectedGame The game selected by the user.
   * @param playerNames The names of the players.
   */
  public TokenSelectionView(MainApp application, String selectedGame, String[] playerNames) {
    super(application);
    this.selectedGame = selectedGame;
    this.playerNames = playerNames;
    this.currentPlayerIndex = 0;
    this.tokenButtons = new Button[tokenPaths.length];
    this.selectedTokenPaths = new String[playerNames.length];
    createView();
  }

  /**
   * Creates the token selection view components.
   */
  private void createView() {
    Label titleLabel = new Label(playerNames[currentPlayerIndex] + ", select your token");
    titleLabel.getStyleClass().add("heading-medium");

    GridPane tokenGrid = new GridPane();
    tokenGrid.setAlignment(Pos.CENTER);
    tokenGrid.setHgap(20);
    tokenGrid.setVgap(20);

    for (int i = 0; i < tokenPaths.length; i++) {
      try {
        Image tokenImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(tokenPaths[i])));
        ImageView tokenImageView = new ImageView(tokenImage);
        tokenImageView.setFitHeight(80);
        tokenImageView.setFitWidth(80);
        tokenImageView.setPreserveRatio(true);

        Button tokenButton = new Button();
        tokenButton.setGraphic(tokenImageView);
        tokenButton.getStyleClass().add("token-button");
        tokenButtons[i] = tokenButton;

        final int tokenIndex = i;
        tokenButton.setOnAction(event -> selectToken(tokenPaths[tokenIndex], tokenButton));

        tokenGrid.add(tokenButton, tokenPositions[i][0], tokenPositions[i][1]);

      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Error loading token image: " + tokenPaths[i], e);
      }
    }

    Button backButton = new Button("Back To Player Names");
    backButton.getStyleClass().add("button");
    backButton.getStyleClass().add("button-secondary");
    backButton.setOnAction(event -> application.showPlayerNameView(selectedGame, playerNames.length));

    layout.getChildren().addAll(titleLabel, tokenGrid, backButton);
  }

  /**
   * Handles token selection for the current player.
   *
   * @param tokenPath The path of the selected token.
   * @param tokenButton The button associated with the selected token.
   */
  private void selectToken(String tokenPath, Button tokenButton) {
    selectedTokenPaths[currentPlayerIndex] = tokenPath;
    currentPlayerIndex++;

    if (currentPlayerIndex < playerNames.length) {
      VBox layout = (VBox) ((BorderPane) scene.getRoot()).getCenter();
      Label titleLabel = (Label) layout.getChildren().getFirst();
      titleLabel.setText(playerNames[currentPlayerIndex] + ", Select Your Token");

      for (int i = 0; i < tokenPaths.length; i++) {
        if (i < tokenButtons.length) {
          Button button = tokenButtons[i];
          boolean tokenAlreadySelected = false;
          for (int j = 0; j < currentPlayerIndex; j++) {
            if (tokenPaths[i].equals(selectedTokenPaths[j])) {
              tokenAlreadySelected = true;
              break;
            }
          }

          if (tokenAlreadySelected) {
            button.setDisable(true);
            button.getStyleClass().add("token-button-disabled");
          }
        }
      }
    } else {
      application.startGame(playerNames, selectedTokenPaths);
    }
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