package edu.ntnu.idi.idatt.GUI;

import edu.ntnu.idi.idatt.GameLogic.BoardGame;
import edu.ntnu.idi.idatt.GameLogic.Player;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Graphical User Interface for Setting up a board game.
 * Handles game selection, player configuration, and token selection.
 */
public class GUI extends Application {
  private Stage primaryStage;
  private BoardGame boardGame;
  private String selectedGame;
  private int playerCount;
  private String selectedToken = null;
  private String [] playerTokens;
  private String [] playerNames;

  /**
   * The entry point for the javFX application.
   * Initializes the board game and starts the game setup process.
   * @param primaryStage The primary stage for the application.
   */
  @Override
  public void start(Stage primaryStage) {

    this.primaryStage = primaryStage;

    boardGame = new BoardGame();
    boardGame.createBoard();
    boardGame.createDice();

    primaryStage.setTitle("Game Setup");
    showGameSelectionScreen();
  }

  /**
   * Displays the game selection screen.
   * Allows the user to choose from three games (WIP).
   */
  private void showGameSelectionScreen() {
    VBox layout = new VBox(20);
    layout.setAlignment(Pos.CENTER);

    Label titleLabel = new Label("Select a game to play");
    titleLabel.setStyle("-fx-font-size: 24px");

    String[] games = {"Ladder Game", "TBA", "TBA"};

    for (String game : games) {
      Button gameButton = new Button(game);
      gameButton.setOnAction(event -> {
        selectedGame = game;
        showPlayerCountScreen();
      });
      layout.getChildren().add(gameButton);
    }

    Scene scene = new Scene(layout, 300, 400);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  /**
   * Displays the player count selection screen.
   * Allows users to choose the number of players for the game.
   */
  private void showPlayerCountScreen() {
    VBox layout = new VBox(20);
    layout.setAlignment(Pos.CENTER);

    Label titleLabel = new Label("Select Number of Players (Max 4)");
    titleLabel.setStyle("-fx-font-size: 24px");

    for (int i = 1; i <= 4; i++) {
      Button playerCountButton = new Button(i + " Player +" + (i > 1 ? "s" : ""));
      final int count = i;
      playerCountButton.setOnAction(event -> {
        playerCount = count;
        playerTokens = new String[playerCount];
        playerNames = new String[playerCount];
        showPlayerNameScreen(0);
      });
      layout.getChildren().add(playerCountButton);
    }
  }

  /**
   * Displays the player name input screen.
   * Allows each player to enter their name.
   * @param currentPlayer The current player entering their name.
   */
  private void showPlayerNameScreen(int currentPlayer) {
    VBox layout = new VBox(20);
    layout.setAlignment(Pos.CENTER);

    Label titleLabel = new Label("Player " + (currentPlayer + 1) + ": Enter Name");
    titleLabel.setStyle("-fx-font-size: 18px;");

    TextField nameField = new TextField();
    nameField.setPromptText("Enter player name");

    Button continueButton = new Button("Continue");
    continueButton.setOnAction(e -> {
      String playerName = nameField.getText().trim();
      if (!playerName.isEmpty()) {
        playerNames[currentPlayer] = playerName;
        if (currentPlayer + 1 < playerCount) {
          showPlayerNameScreen(currentPlayer + 1);
        } else {
          showTokenSelectionScreen(0);
        }
      }
    });

    layout.getChildren().addAll(titleLabel, nameField, continueButton);

    Scene scene = new Scene(layout, 300, 400);
    primaryStage.setScene(scene);
  }

  /**
   * Displays the token selection screen for each player.
   * Allows players to choose their unique game token.
   * @param currentPlayer The current player selecting their token.
   */
  private void showTokenSelectionScreen(int currentPlayer) {
    GridPane gridPane = new GridPane();
    gridPane.setAlignment(Pos.CENTER);
    gridPane.setHgap(20);
    gridPane.setVgap(20);

    Label instructionLabel = new Label(playerNames[currentPlayer] + "Select a token");
    gridPane.add(instructionLabel, 0, 0, 2, 1);
    instructionLabel.setStyle("-fx-font-size: 18px;");

    String[] tokenPaths = {
        "edu/ntnu/idi/idatt/GUI/Images/BlueToken.png",
        "edu/ntnu/idi/idatt/GUI/Images/RedToken.png",
        "edu/ntnu/idi/idatt/GUI/Images/GreenToken.png",
        "edu/ntnu/idi/idatt/GUI/Images/PinkToken.png"
    };

    //Creates token selection buttons, loads token image and a button with the image.
    for (int i = 0; i < tokenPaths.length; i++) {
      Image tokenImage = new Image(getClass().getResourceAsStream(tokenPaths[i]));
      ImageView tokenImageView = new ImageView(tokenImage);

      tokenImageView.setFitHeight(100);
      tokenImageView.setFitWidth(100);
      tokenImageView.setPreserveRatio(true);

      Button tokenButton = new javafx.scene.control.Button();
      tokenButton.setGraphic(tokenImageView);
      tokenButton.setStyle("-fx-background-color: transparent;");

      //Checks if a token is already selected by another player.
      final int tokenIndex = i;
      tokenButton.setOnAction(event -> {
        boolean tokenSelected = false;
        for (String selectedToken : playerTokens) {
          if (tokenPaths[tokenIndex].equals(selectedToken)) {
            tokenSelected = true;
            break;
          }
        }
        //Moves to next player or calls the startGame method.
        if (!tokenSelected) {
          playerTokens[currentPlayer] = tokenPaths[tokenIndex];
          if (currentPlayer + 1 < playerCount) {
            showTokenSelectionScreen(currentPlayer + 1);
          } else {
            startGame();
          }
        }
      });

      gridPane.add(tokenButton, i % 2, i + 1);
    }
    Scene scene = new Scene(gridPane, 300, 300);
    primaryStage.setScene(scene);
  }

  private void startGame() {
    BoardGame game = new BoardGame();
    game.createBoard();
    game.createDice();

    for (int i = 0; i < playerCount; i++) {
      Player player = new Player(playerNames[i], boardGame);
      boardGame.addPlayer(player);
    }

    System.out.println("Game starting");

    for (int i = 0; i < playerCount; i++) {
      System.out.println("Player " + (i + 1) + ": " + playerNames[i] + " Tokens: " + playerTokens[i]);
    }

    //Placeholder for future gameView.
    new Thread(() -> {
      boardGame.play();

      Player winner = boardGame.getWinner();
      if (winner != null) {
        System.out.println("Winner is " + winner.getName());
      }
    }).start();


  }
  public static void main(String[] args) {
    launch(args);
    }
  }
