package edu.ntnu.idi.idatt.GUI;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class TokenSelectionView {
  private BoardGameApplication application;
  private String selectedGame;
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

  public TokenSelectionView(BoardGameApplication application, String selectedGame, String[] playerNames) {
    this.application = application;
    this.selectedGame = selectedGame;
    this.playerNames = playerNames;
    this.playerTokens = new String[playerNames.length];
    this.currentPlayerIndex = 0;

    createView();
  }

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
    scene = new Scene(mainLayout, 400, 400);
  }

  private void selectToken(String tokenPath) {
    playerTokens[currentPlayerIndex] = tokenPath;
    currentPlayerIndex++;

    if (currentPlayerIndex < playerNames.length) {
      VBox layout = (VBox) scene.getRoot();
      Label titleLabel = (Label) layout.getChildren().get(0);
      titleLabel.setText(playerNames[currentPlayerIndex] + ", Select Your Token");

      for (int i = 0; i < TOKEN_PATHS.length; i++) {
        // Make sure we have this button
        if (i < tokenButtons.length) {
          Button tokenButton = tokenButtons[i];

          boolean tokenAlreadySelected = false;
          // Only check players who have already selected (indices less than currentPlayerIndex)
          for (int j = 0; j < currentPlayerIndex; j++) {
            // Make sure we're within bounds of playerTokens array
            if (j < playerTokens.length && TOKEN_PATHS[i].equals(playerTokens[j])) {
              tokenAlreadySelected = true;
              break;
            }
          }

          tokenButton.setDisable(tokenAlreadySelected);
        }
      }
    } else {
      // All players have selected tokens, start the game
      application.startGame(playerNames, playerTokens);
    }
  }

  public Scene getScene() {
    return scene;
  }
}
