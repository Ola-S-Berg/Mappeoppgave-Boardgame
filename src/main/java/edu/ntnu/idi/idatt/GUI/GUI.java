package edu.ntnu.idi.idatt.GUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GUI extends Application {

  @Override
  public void start(Stage primaryStage) {

    Button playButton = new Button("Play");

    VBox layout = new VBox(10);
    layout.getChildren().add(playButton);

    Scene scene = new Scene(layout, 300, 200);

    primaryStage.setTitle("Ladder game");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
