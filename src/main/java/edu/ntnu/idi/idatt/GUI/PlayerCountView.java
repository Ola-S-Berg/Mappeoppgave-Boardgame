package edu.ntnu.idi.idatt.GUI;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PlayerCountView {
  private BoardGameApplication application;
  private String selectedGame;
  private Scene scene;

  public PlayerCountView(BoardGameApplication application, String selectedGame) {
    this.application = application;
    this.selectedGame = selectedGame;
    createView();
  }

  private void createView() {
    VBox layout = new VBox(20);
    layout.setAlignment(Pos.CENTER);

    Label titleLabel = new Label("Select Number of Players (Max 4)");
    titleLabel.setStyle("-fx-font-size: 24px");
    layout.getChildren().add(titleLabel);

    for (int i = 1; i <= 4; i++) {
      Button playerCountButton = new Button(i + " Player" + (i > 1 ? "s" : ""));
      final int count = i;
      playerCountButton.setOnAction(event -> {
        application.showPlayerNameView(selectedGame, count);
      });
      layout.getChildren().add(playerCountButton);
    }
    scene = new Scene(layout, 800, 800);
  }

  public Scene getScene() {
    return scene;
  }
}
