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

    handleDialogCloseRequest(dialogStage, null);

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

  /**
   * Shows a dialog for purchasing a property.
   *
   * @param ownerStage The stage that owns this dialog.
   * @param property The property to purchase.
   * @param onPurchase Runnable to execute if the user chooses to purchase the property.
   * @param onDecline Runnable to execute if the user chooses to decline the purchase.
   */
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

    handleDialogCloseRequest(dialogStage, onDecline);

    VBox dialogVBox = new VBox(15);
    dialogVBox.setPadding(new Insets(20));
    dialogVBox.setAlignment(Pos.CENTER);

    Label propertyNameLabel = new Label(property.getPropertyName());
    propertyNameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

    Label costLabel = new Label("Cost: " + property.getCost());
    costLabel.setStyle("-fx-font-size: 14px;");

    Label promptLabel = new Label("Would you like to purchase this property?");
    promptLabel.setStyle("-fx-font-size: 14px; -fx-padding: 8px;");

    Button purchaseButton = new Button("Purchase");
    purchaseButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
    purchaseButton.setOnAction(event -> {
      dialogStage.close();
      onPurchase.run();
    });

    Button declineButton = new Button("Decline");
    declineButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-color: #CC0000; -fx-text-fill: white;");
    declineButton.setOnAction(event -> {
      dialogStage.close();
      onDecline.run();
    });

    handleDialogCloseRequest(dialogStage, onDecline);

    HBox buttonBox = new HBox(20);
    buttonBox.setAlignment(Pos.CENTER);
    buttonBox.getChildren().addAll(purchaseButton, declineButton);

    dialogVBox.getChildren().addAll(propertyNameLabel, costLabel, promptLabel, buttonBox);

    Scene dialogScene = new Scene(dialogVBox, 400, 200);
    dialogStage.setScene(dialogScene);
    dialogStage.showAndWait();
  }

  /**
   * Shows a dialog for jail options.
   *
   * @param player The player that is in jail.
   * @param ownerStage The stage that owns this dialog.
   * @param onPayBail Runnable to execute if the user chooses to pay bail.
   * @param onTryRollDoubles Runnable to execute if the user chooses to roll for doubles.
   */
  public static void showJailOptionsDialog (Player player, Stage ownerStage,
                                            Runnable onPayBail, Runnable onTryRollDoubles) {
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

    handleDialogCloseRequest(dialogStage, onTryRollDoubles);

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

    dialogStage.setOnCloseRequest(event -> {
      event.consume();
      dialogStage.close();
      if (onTryRollDoubles != null) {
        onTryRollDoubles.run();
      }
    });

    HBox buttonBox = new HBox(20);
    buttonBox.setAlignment(Pos.CENTER);
    buttonBox.getChildren().addAll(rollButton, payButton);

    dialogVBox.getChildren().addAll(titleLabel, turnsLabel, promptLabel, buttonBox);

    Scene dialogScene = new Scene(dialogVBox, 400, 200);
    dialogStage.setScene(dialogScene);
    dialogStage.showAndWait();
  }

  /**
   * Shows a dialog for tax payment options.
   *
   * @param ownerStage The stage that owns this dialog.
   * @param percentageTax The percentage tax rate.
   * @param fixedTax The fixed tax amount.
   * @param player The player who landed on the tax tile.
   * @param onPercentage Runnable to execute if the user chooses to pay percentage tax.
   * @param onFixed Runnable to execute if the user chooses to pay fixed tax.
   */
  public static void showTaxPaymentDialog(Stage ownerStage, int percentageTax, int fixedTax, Player player, Runnable onPercentage, Runnable onFixed) {
    if (ownerStage == null) {
      if (onFixed != null) {
        onFixed.run();
      }
      return;
    }

    Stage dialogStage = new Stage();
    dialogStage.initModality(Modality.APPLICATION_MODAL);
    dialogStage.initOwner(ownerStage);
    dialogStage.setTitle("Tax Payment");

    handleDialogCloseRequest(dialogStage, onFixed);

    VBox dialogVBox = new VBox(15);
    dialogVBox.setPadding(new Insets(20));
    dialogVBox.setAlignment(Pos.CENTER);

    Label titleLabel = new Label(player.getName() + " landed on a tax tile");
    titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

    int percentageAmount = (int) (player.getMoney() * (percentageTax / 100.0));

    Label optionsLabel = new Label("Choose your payment option:");
    optionsLabel.setStyle("-fx-font-size: 14px;");

    Label percentageLabel = new Label("Pay " + percentageTax + "% of your money: $" + percentageAmount);
    percentageLabel.setStyle("-fx-font-size: 14px;");

    Label fixedLabel = new Label("Pay fixed tax: $" + fixedTax);
    fixedLabel.setStyle("-fx-font-size: 14px;");

    Button percentageButton = new Button("Pay " + percentageTax + "% ($" + percentageAmount + ")");
    percentageButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px;");
    percentageButton.setOnAction(event -> {
      dialogStage.close();
      if (onPercentage != null) {
        onPercentage.run();
      }
    });

    Button fixedButton = new Button("Pay fixed tax ($" + fixedTax + ")");
    fixedButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px;");
    fixedButton.setOnAction(event -> {
      dialogStage.close();
      if (onFixed != null) {
        onFixed.run();
      }
    });

    if (player.getMoney() < fixedTax) {
      fixedButton.setDisable(true);
      fixedButton.setText("Not enough money to pay fixed tax.");
    }

    HBox buttonBox = new HBox(20);
    buttonBox.setAlignment(Pos.CENTER);
    buttonBox.getChildren().addAll(percentageButton, fixedButton);

    dialogVBox.getChildren().addAll(titleLabel, optionsLabel, percentageLabel, fixedLabel, buttonBox);

    Scene dialogScene = new Scene(dialogVBox, 400, 200);
    dialogStage.setScene(dialogScene);
    dialogStage.showAndWait();
  }

  /**
   * Helper method to handle the dialog window close requests.
   *
   * @param dialogStage The dialog stage to handle.
   * @param defaultAction The default action to execute if the user closes the dialog.
   */
  private static void handleDialogCloseRequest(Stage dialogStage, Runnable defaultAction) {
    dialogStage.setOnCloseRequest(event -> {
      if (defaultAction != null) {
        defaultAction.run();
      }
    });
  }
}
