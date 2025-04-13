package edu.ntnu.idi.idatt.GUI;

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
  private BoardGameApplication application;
  private String selectedGame; //Used later when more games are added
  private String[] playerNames;
  private String [] playerTokens;
  private int currentPlayerIndex;
  private Scene scene;

  private final String[] TOKEN_PATHS = {
      "/Images/Tokens/BlueToken.png",
      "/Images/Tokens/RedToken.png",
      "/Images/Tokens/GreenToken.png",
      "/Images/Tokens/PinkToken.png"
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
        Image tokenImage = new Image(getClass().getResourceAsStream(TOKEN_PATHS[i]));
        ImageView tokenImageView = new ImageView(tokenImage);
        tokenImageView.setFitHeight(80);
        tokenImageView.setFitWidth(80);
        tokenImageView.setPreserveRatio(true);

        Button tokenButton = new Button();
        tokenButton.setGraphic(tokenImageView);
        tokenButton.setStyle("-fx-background-color: transparent;");
        tokenButtons[i] = tokenButton;

        final int tokenIndex = i;
        tokenButton.setOnAction(event -> {
          selectToken(TOKEN_PATHS[tokenIndex]);
        });

        tokenGrid.add(tokenButton, i % 2, i / 2);

      } catch (Exception e) {
        System.err.println("Error loading token image: " + TOKEN_PATHS[i]);
        e.printStackTrace();
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
      //Updates view for the current player.
      VBox layout = (VBox) scene.getRoot();
      Label titleLabel = (Label) layout.getChildren().get(0);
      titleLabel.setText(playerNames[currentPlayerIndex] + ", Select Your Token");

      //Iterates between selected tokens and disables previously selected tokens.
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

  public Scene getScene() {
    return scene;
  }
}
