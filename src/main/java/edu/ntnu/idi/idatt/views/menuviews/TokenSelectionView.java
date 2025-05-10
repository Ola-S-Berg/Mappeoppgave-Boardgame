package edu.ntnu.idi.idatt.views.menuviews;

import edu.ntnu.idi.idatt.MainApp;
import edu.ntnu.idi.idatt.views.CssUtil;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
public class TokenSelectionView {
  private final MainApp application;
  private final String[] playerNames;
  private final String [] playerTokens;
  private final String selectedGame;
  private int currentPlayerIndex;
  private Scene scene;
  private static final Logger LOGGER = Logger.getLogger(TokenSelectionView.class.getName());


  private final String[] tokenPaths = {
      "/Images/tokens/BlueToken.png",
      "/Images/tokens/LightBlueToken.png",
      "/Images/tokens/RedToken.png",
      "/Images/tokens/GreenToken.png",
      "/Images/tokens/PinkToken.png"
  };

  /**
   * Defines the positions for each token.
   */
  private final int[][] tokenPositions = {
      {1, 0},
      {0, 1},
      {1, 1},
      {2, 1},
      {1, 2},
  };

  private Button[] tokenButtons;

  /**
   * Constructor that creates the token selection view.
   *
   * @param application The main application instance.
   * @param selectedGame The game selected by the user.
   * @param playerNames Array of player names.
   */
  public TokenSelectionView(MainApp application, String selectedGame, String[] playerNames) {
    this.application = application;
    this.selectedGame = selectedGame;
    this.playerNames = playerNames;
    this.playerTokens = new String[playerNames.length];
    this.currentPlayerIndex = 0;

    createView();
  }

  /**
   * Creates the token selection view components.
   */
  private void createView() {
    BorderPane root = new BorderPane();
    root.getStyleClass().add("root");
    root.setPadding(new Insets(10));

    VBox mainLayout = new VBox(30);
    mainLayout.setAlignment(Pos.CENTER);
    mainLayout.setPadding(new Insets(10));
    mainLayout.getStyleClass().add("content-box");

    Label titleLabel = new Label(playerNames[currentPlayerIndex] + ", select your token");
    titleLabel.getStyleClass().add("heading-medium");
    mainLayout.getChildren().add(titleLabel);

    GridPane tokenGrid = new GridPane();
    tokenGrid.setAlignment(Pos.CENTER);
    tokenGrid.setHgap(20);
    tokenGrid.setVgap(20);

    tokenButtons = new Button[tokenPaths.length];

    for (int i = 0; i < tokenPaths.length; i++) {
      try {
        Image tokenImage = new Image(
            Objects.requireNonNull(getClass().getResourceAsStream(tokenPaths[i])));
        ImageView tokenImageView = new ImageView(tokenImage);
        tokenImageView.setFitHeight(80);
        tokenImageView.setFitWidth(80);
        tokenImageView.setPreserveRatio(true);

        Button tokenButton = new Button();
        tokenButton.setGraphic(tokenImageView);
        tokenButton.getStyleClass().add("token-button");
        assert tokenButtons != null;
        tokenButtons[i] = tokenButton;

        final int tokenIndex = i;
        tokenButton.setOnAction(event -> selectToken(tokenPaths[tokenIndex]));

        tokenGrid.add(tokenButton, tokenPositions[i][0], tokenPositions[i][1]);

      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Error loading token image: " + tokenPaths[i], e);
      }
    }

    mainLayout.getChildren().add(tokenGrid);

    Button backButton = new Button("Back To Player Name Selection");
    backButton.getStyleClass().add("button");
    backButton.getStyleClass().add("button-secondary");
    backButton.setOnAction(event ->
        application.showPlayerNameView(selectedGame, playerNames.length));

    mainLayout.getChildren().add(backButton);

    root.setCenter(mainLayout);
    scene = new Scene(root, 800, 600);
    CssUtil.applyStyleSheet(scene);

    Stage stage = application.getPrimaryStage();
    stage.setMinWidth(600);
    stage.setMinHeight(600);
    stage.centerOnScreen();
  }

  /**
   * Handles token selection for the current player.
   *
   * @param tokenPath The path of the selected token.
   */
  private void selectToken(String tokenPath) {
    playerTokens[currentPlayerIndex] = tokenPath;
    currentPlayerIndex++;

    if (currentPlayerIndex < playerNames.length) {
      VBox layout = (VBox) ((BorderPane) scene.getRoot()).getCenter();
      Label titleLabel = (Label) layout.getChildren().getFirst();
      titleLabel.setText(playerNames[currentPlayerIndex] + ", Select Your Token");

      for (int i = 0; i < tokenPaths.length; i++) {
        if (i < tokenButtons.length) {
          Button tokenButton = tokenButtons[i];

          boolean tokenAlreadySelected = false;
          for (int j = 0; j < currentPlayerIndex; j++) {
            if (j < playerTokens.length && tokenPaths[i].equals(playerTokens[j])) {
              tokenAlreadySelected = true;
              break;
            }
          }

          if (tokenAlreadySelected) {
            tokenButton.setDisable(true);
            tokenButton.getStyleClass().add("token-button-disabled");
          }
        }
      }
    } else {
      application.startGame(playerNames, playerTokens);
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