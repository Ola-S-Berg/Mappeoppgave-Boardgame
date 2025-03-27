package edu.ntnu.idi.idatt.GUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GUI extends Application {

  @Override
  public void start(Stage primaryStage) {

    Button playButton = new Button("Play");

    Image image = new Image("file:src/main/java/edu/ntnu/idi/idatt/GUI/Images/LadderGameClassic.png");
    ImageView imageView = new ImageView(image);

    imageView.setFitHeight(1200);
    imageView.setFitWidth(600);
    imageView.setPreserveRatio(true);

    VBox layout = new VBox(10);
    layout.getChildren().addAll(playButton, imageView);

    Scene scene = new Scene(layout, 1800, 1000);

    primaryStage.setTitle("Ladder game");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
