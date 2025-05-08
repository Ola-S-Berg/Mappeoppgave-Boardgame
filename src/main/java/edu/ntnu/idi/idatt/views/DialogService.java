package edu.ntnu.idi.idatt.views;

import edu.ntnu.idi.idatt.BoardGameApplication;
import edu.ntnu.idi.idatt.actions.monopoly_game.PropertyTileAction;
import edu.ntnu.idi.idatt.model.Player;
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

  public static void showPropertyPurchaseDialog(Stage ownerStage, PropertyTileAction property,
                                                Runnable onPurchase, Runnable onDecline) {
    if (ownerStage == null) {
      if (onDecline != null) {
        onDecline.run();
      }
      return;
    }

    Stage dialogStage = new Stage();
    dialogStage.initModality(Modality.APPLICATION_MODAL);
    dialogStage.initOwner(ownerStage);
    dialogStage.setTitle("Purchase Property");

    VBox dialogVBox = new VBox(15);
    dialogVBox.setPadding(new Insets(20));
    dialogVBox.setAlignment(Pos.CENTER);

    Label propertyNameLabel = new Label(property.getPropertyName());
    propertyNameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

    Label costLabel = new Label("Cost: " + property.getCost());
    costLabel.setStyle("-fx-font-size: 14px;");

    Label promptLabel = new Label("Would you like to purchase this property?");
    promptLabel.setStyle("-fx-font-size: 14px; -fx-padding: 8px;");

    Button declineButton = new Button("Decline");
    declineButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px;");
    declineButton.setOnAction(event -> {
      dialogStage.close();
      onDecline.run();
    });

    Button purchaseButton = new Button("Purchase");
    purchaseButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-color: #4CAF50; -fx-text-fill: white;");

    purchaseButton.setOnAction(event -> {
      dialogStage.close();
      onPurchase.run();
    });
    HBox buttonBox = new HBox(20);
    buttonBox.setAlignment(Pos.CENTER);
    buttonBox.getChildren().addAll(declineButton, purchaseButton);

    dialogVBox.getChildren().addAll(propertyNameLabel, costLabel, promptLabel, buttonBox);

    Scene dialogScene = new Scene(dialogVBox, 400, 200);
    dialogStage.setScene(dialogScene);
    dialogStage.showAndWait();
  }

  public static void showJailOptionsDialog (Player player, Stage ownerStage, Runnable onPayBail, Runnable onTryRollDoubles) {
    int JAIL_BAIL = 5000;

    if (ownerStage == null) {
      if (onTryRollDoubles != null) {
        onTryRollDoubles.run();
      }
      return;
    }

    Stage dialogStage = new Stage();
    dialogStage.initModality(Modality.APPLICATION_MODAL);
    dialogStage.initOwner(ownerStage);
    dialogStage.setTitle("Jail Options");

    VBox dialogVBox = new VBox(15);
    dialogVBox.setPadding(new Insets(20));
    dialogVBox.setAlignment(Pos.CENTER);

    Label titleLabel = new Label(player.getName() + " is in jail.");
    titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

    String jailTurnCount = player.getProperty("jailTurnCount");
    int turnsInJail = jailTurnCount == null ? 1 : Integer.parseInt(jailTurnCount);
    Label turnsLabel = new Label("Turn " + turnsInJail + " of 3 in jail");
    turnsLabel.setStyle("-fx-font-size: 14px;");

    Label promptLabel = new Label("Would you like to pay bail or roll doubles?");
    promptLabel.setStyle("-fx-font-size: 14px; -fx-padding: 8px;");

    Button payButton = new Button("Pay $" + JAIL_BAIL + " Bail");
    payButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
    payButton.setOnAction(event -> {
      dialogStage.close();
      if (onPayBail != null) {
        onPayBail.run();
      }
    });

    Button rollButton = new Button("Try to Roll Doubles");
    rollButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
    rollButton.setOnAction(event -> {
      dialogStage.close();
      if (onTryRollDoubles != null) {
        onTryRollDoubles.run();
      }
    });

    if (player.getMoney() < JAIL_BAIL) {
      payButton.setDisable(true);
      payButton.setText("Not enough money to pay bail.");
    }

    HBox buttonBox = new HBox(20);
    buttonBox.setAlignment(Pos.CENTER);
    buttonBox.getChildren().addAll(rollButton, payButton);

    dialogVBox.getChildren().addAll(titleLabel, turnsLabel, promptLabel, buttonBox);

    Scene dialogScene = new Scene(dialogVBox, 400, 200);
    dialogStage.setScene(dialogScene);
    dialogStage.showAndWait();
  }
}
