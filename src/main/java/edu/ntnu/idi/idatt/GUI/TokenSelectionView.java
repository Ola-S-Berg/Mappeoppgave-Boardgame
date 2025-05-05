package edu.ntnu.idi.idatt.GUI;

import edu.ntnu.idi.idatt.BoardGameApplication;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * View class for token selection.
 * Allows each player to select a unique token.
 */
public class TokenSelectionView {
  private final BoardGameApplication application;
  private final String[] playerNames;
  private final String [] playerTokens;
  private int currentPlayerIndex;
  private Scene scene;
  private static final Logger LOGGER = Logger.getLogger(TokenSelectionView.class.getName());


  private final String[] TOKEN_PATHS = {
      "/Images/Tokens/BlueToken.png",
      "/Images/Tokens/LightBlueToken.png",
      "/Images/Tokens/RedToken.png",
      "/Images/Tokens/GreenToken.png",
      "/Images/Tokens/PinkToken.png"
  };

  /**
   * Defines the positions for each token.
   */
  private final int[][] TOKEN_POSITIONS = {
      {1, 0},
      {0, 1},
      {1, 1},
      {2, 1},
      {1, 2},
  };

  private Button[] tokenButtons;

  /**
   * Constructor that creates the token selection view.
   * @param application The main application instance.
   * @param selectedGame The game selected by the user.
   * @param playerNames Array of player names.
   */
  public TokenSelectionView(BoardGameApplication application, String selectedGame, String[] playerNames) {
    this.application = application;
    //Used later when more games are added
    this.playerNames = playerNames;
    this.playerTokens = new String[playerNames.length];
    this.currentPlayerIndex = 0;

    createView();
  }

  /**
   * Creates the token selection view components.
   */
  private void createView() {
    VBox mainLayout = new VBox(20);
    mainLayout.setAlignment(Pos.CENTER);

    Label titleLabel = new Label(playerNames[currentPlayerIndex] + ", select your token");
    titleLabel.setStyle("-fx-font-size: 20px;");
    mainLayout.getChildren().add(titleLabel);

    GridPane tokenGrid = new GridPane();
    tokenGrid.setAlignment(Pos.CENTER);
    tokenGrid.setHgap(20);
    tokenGrid.setVgap(20);

    tokenButtons = new Button[TOKEN_PATHS.length];

    for (int i = 0; i < TOKEN_PATHS.length; i++) {
      try {
        Image tokenImage = new Image(
            Objects.requireNonNull(getClass().getResourceAsStream(TOKEN_PATHS[i])));
        ImageView tokenImageView = new ImageView(tokenImage);
        tokenImageView.setFitHeight(80);
        tokenImageView.setFitWidth(80);
        tokenImageView.setPreserveRatio(true);

        Button tokenButton = new Button();
        tokenButton.setGraphic(tokenImageView);
        tokenButton.setStyle("-fx-background-color: transparent;");
        assert tokenButtons != null;
        tokenButtons[i] = tokenButton;

        final int tokenIndex = i;
        tokenButton.setOnAction(event -> selectToken(TOKEN_PATHS[tokenIndex]));

        tokenGrid.add(tokenButton, TOKEN_POSITIONS[i][0], TOKEN_POSITIONS[i][1]);

      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Error loading token image: " + TOKEN_PATHS[i] ,e);
      }
    }

    mainLayout.getChildren().add(tokenGrid);
    scene = new Scene(mainLayout, 800, 800);
  }

  /**
   * Handles token selection for the current player.
   * @param tokenPath The path of the selected token.
   */
  private void selectToken(String tokenPath) {
    playerTokens[currentPlayerIndex] = tokenPath;
    currentPlayerIndex++;

    if (currentPlayerIndex < playerNames.length) {
      VBox layout = (VBox) scene.getRoot();
      Label titleLabel = (Label) layout.getChildren().getFirst();
      titleLabel.setText(playerNames[currentPlayerIndex] + ", Select Your Token");

      for (int i = 0; i < TOKEN_PATHS.length; i++) {
        if (i < tokenButtons.length) {
          Button tokenButton = tokenButtons[i];

          boolean tokenAlreadySelected = false;
          for (int j = 0; j < currentPlayerIndex; j++) {
            if (j < playerTokens.length && TOKEN_PATHS[i].equals(playerTokens[j])) {
              tokenAlreadySelected = true;
              break;
            }
          }

          tokenButton.setDisable(tokenAlreadySelected);
        }
      }
    } else {
      application.startGame(playerNames, playerTokens);
    }
  }

  /**
   * Returns the scene for this view.
   * @return The scene.
   */
  public Scene getScene() {
    return scene;
  }
}
