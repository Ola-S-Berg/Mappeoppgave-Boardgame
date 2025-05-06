package edu.ntnu.idi.idatt.views;

import edu.ntnu.idi.idatt.BoardGameApplication;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Service class for creating and displaying dialogs across the application.
 */
public class DialogService {

  /**
   * Shows a confirmation dialog for quitting to the menu.
   *
   * @param ownerStage The stage that owns this dialog.
   * @param onConfirm Runnable to execute if user confirms quitting.
   */
  public static void showQuitConfirmationDialog(Stage ownerStage, Runnable onConfirm) {
    Stage dialogStage = new Stage();
    dialogStage.initModality(Modality.APPLICATION_MODAL);
    dialogStage.initOwner(ownerStage);
    dialogStage.setTitle("Confirm Quit");

    VBox dialogVbox = new VBox(20);
    dialogVbox.setPadding(new Insets(20));
    dialogVbox.setAlignment(Pos.CENTER);

    Label confirmLabel = new Label("Are you sure you want to quit? \nUnsaved data will be lost.");
    confirmLabel.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px;");

    Button cancelButton = new Button("Cancel");
    cancelButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px;");
    cancelButton.setOnAction(event -> dialogStage.close());

    Button confirmButton = new Button("Quit");
    confirmButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-color: #CC0000; -fx-text-fill: white;");
    confirmButton.setOnAction(event -> {
      dialogStage.close();
      ownerStage.close();
      new BoardGameApplication().start(new Stage());
    });

    HBox buttonBox = new HBox(20);
    buttonBox.setAlignment(Pos.CENTER);
    buttonBox.getChildren().addAll(cancelButton, confirmButton);

    dialogVbox.getChildren().addAll(confirmLabel, buttonBox);

    Scene dialogScene = new Scene(dialogVbox, 350, 150);
    dialogStage.setScene(dialogScene);
    dialogStage.show();
  }
}
