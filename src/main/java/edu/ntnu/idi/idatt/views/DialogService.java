package edu.ntnu.idi.idatt.views;

import edu.ntnu.idi.idatt.MainApp;
import edu.ntnu.idi.idatt.model.actions.monopolygame.PropertyTileAction;
import edu.ntnu.idi.idatt.model.gamelogic.Player;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * <h1>Dialog Service</h1>
 *
 * <p>A utility service class that provides a centralized system for creating and displaying
 * modal dialogs throughout the application. This service ensures consistent dialog styling,
 * behavior, and error handling while supporting various game-specific interaction scenarios.</p>
 *
 * <h2>Dialog Types</h2>
 * <ul>
 *   <li>Confirmation dialogs - For verifying user intent before significant actions</li>
 *   <li>Property purchase actions - For buying properties in Monopoly Gameplay</li>
 *   <li>Jail option dialogs - For handling player choices when in jail</li>
 *   <li>Tax payment dialogs - For selecting between tax payment options</li>
 * </ul>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Consistent styling across all dialogs through CSS application</li>
 *   <li>Animated transitions for a smoother user experience</li>
 *   <li>Centralized dialog creation to reduce code duplication</li>
 *   <li>Callback-based design pattern for handling user choices</li>
 *   <li>Proper model behavior to prevent interaction with the main window</li>
 *   <li>Error handling and logging</li>
 * </ul>
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
 */
public class DialogService {
  private static final Logger LOGGER = Logger.getLogger(DialogService.class.getName());


  /**
   * Shows a confirmation dialog for quitting to the menu.
   *
   * @param ownerStage The stage that owns this dialog.
   * @param onConfirm Runnable to execute if user confirms quitting.
   */
  public static void showQuitConfirmationDialog(Stage ownerStage, Runnable onConfirm) {
    if (ownerStage == null) {
      return;
    }

    Stage dialogStage = new Stage();
    dialogStage.initModality(Modality.APPLICATION_MODAL);
    dialogStage.initOwner(ownerStage);
    dialogStage.setTitle("Confirm Quit");

    handleDialogCloseRequest(dialogStage, null);

    VBox dialogVbox = new VBox(20);
    dialogVbox.getStyleClass().add("dialog-pane");
    dialogVbox.setPadding(new Insets(20));
    dialogVbox.setAlignment(Pos.CENTER);

    Label confirmLabel = new Label("Are you sure you want to quit?");
    confirmLabel.getStyleClass().add("dialog-header");

    Label subLabel = new Label("Unsaved data will be lost.");
    subLabel.getStyleClass().add("dialog-message");

    Button cancelButton = new Button("Cancel");
    cancelButton.getStyleClass().addAll("button", "button-secondary");
    cancelButton.setOnAction(event -> animateDialogAndClose(dialogStage));

    Button confirmButton = new Button("Quit");
    confirmButton.getStyleClass().addAll("button", "button-danger");
    confirmButton.setOnAction(event -> {
      animateDialogAndClose(dialogStage);
      if (onConfirm != null) {
        onConfirm.run();
      } else {
        ownerStage.close();
        new MainApp().start(new Stage());
      }
    });

    HBox buttonBox = new HBox(20);
    buttonBox.setAlignment(Pos.CENTER);
    buttonBox.getChildren().addAll(cancelButton, confirmButton);

    dialogVbox.getChildren().addAll(confirmLabel, subLabel, buttonBox);

    Scene dialogScene = new Scene(dialogVbox, 350, 150);

    try {
      String cssPath = Objects.requireNonNull(
          MainApp.class.getResource("/styles.css")).toExternalForm();
      dialogScene.getStylesheets().add(cssPath);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error loading CSS: " + e);
    }

    dialogStage.setScene(dialogScene);

    try {
      dialogStage.show();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error showing dialog: " + e);
    }
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

    VBox dialogVbox = new VBox(15);
    dialogVbox.getStyleClass().add("dialog-pane");
    dialogVbox.setPadding(new Insets(20));
    dialogVbox.setAlignment(Pos.CENTER);

    Label propertyNameLabel = new Label(property.getPropertyName());
    propertyNameLabel.getStyleClass().add("dialog-header");

    Label costLabel = new Label("Cost: " + property.getCost());
    costLabel.getStyleClass().add("dialog-message");

    Label promptLabel = new Label("Would you like to purchase this property?");
    promptLabel.getStyleClass().add("dialog-message");

    Button purchaseButton = new Button("Purchase");
    purchaseButton.getStyleClass().addAll("button", "button-primary");
    purchaseButton.setOnAction(event -> {
      animateDialogAndClose(dialogStage);
      if (onPurchase != null) {
        onPurchase.run();
      }
    });

    Button declineButton = new Button("Decline");
    declineButton.getStyleClass().addAll("button", "button-secondary");
    declineButton.setOnAction(event -> {
      animateDialogAndClose(dialogStage);
      if (onDecline != null) {
        onDecline.run();
      }
    });

    dialogContentAssembler(dialogStage, dialogVbox, propertyNameLabel, costLabel, promptLabel,
        purchaseButton, declineButton);

    try {
      dialogStage.show();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error showing property dialog: " + e);
      if (onDecline != null) {
        onDecline.run();
      }
    }
  }

  /**
   * Shows a dialog for jail options.
   *
   * @param player The player that is in jail.
   * @param ownerStage The stage that owns this dialog.
   * @param onPayBail Runnable to execute if the user chooses to pay bail.
   * @param onTryRollDoubles Runnable to execute if the user chooses to roll for doubles.
   */
  public static void showJailOptionsDialog(Player player, Stage ownerStage,
      Runnable onPayBail, Runnable onTryRollDoubles) {

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

    VBox dialogVbox = new VBox(15);
    dialogVbox.getStyleClass().add("dialog-pane");
    dialogVbox.setPadding(new Insets(20));
    dialogVbox.setAlignment(Pos.CENTER);

    Label titleLabel = new Label(player.getName() + " is in jail.");
    titleLabel.getStyleClass().add("dialog-header");

    String jailTurnCount = player.getProperty("jailTurnCount");
    int turnsInJail = jailTurnCount == null ? 1 : Integer.parseInt(jailTurnCount);
    Label turnsLabel = new Label("Turn " + turnsInJail + " of 3 in jail");
    turnsLabel.getStyleClass().add("dialog-message");

    Label promptLabel = new Label("Would you like to pay bail or roll doubles?");
    promptLabel.getStyleClass().add("dialog-message");

    int jailBail = 5000;

    Button payButton = new Button("Pay $" + jailBail + " Bail");
    payButton.getStyleClass().addAll("button", "button-primary");
    payButton.setOnAction(event -> {
      animateDialogAndClose(dialogStage);
      if (onPayBail != null) {
        onPayBail.run();
      }
    });

    Button rollButton = new Button("Try to Roll Doubles");
    rollButton.getStyleClass().addAll("button", "button-secondary");
    rollButton.setOnAction(event -> {
      animateDialogAndClose(dialogStage);
      if (onTryRollDoubles != null) {
        onTryRollDoubles.run();
      }
    });

    if (player.getMoney() < jailBail) {
      payButton.setDisable(true);
      payButton.getStyleClass().add("button-disabled");
      payButton.setText("Not enough money to pay bail.");
    }

    dialogContentAssembler(dialogStage, dialogVbox, titleLabel, turnsLabel, promptLabel, rollButton,
        payButton);

    try {
      dialogStage.show();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error showing jail dialog: " + e);
      if (onTryRollDoubles != null) {
        onTryRollDoubles.run();
      }
    }
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
  public static void showTaxPaymentDialog(Stage ownerStage, int percentageTax, int fixedTax,
      Player player, Runnable onPercentage, Runnable onFixed) {
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

    VBox dialogVbox = new VBox(15);
    dialogVbox.getStyleClass().add("dialog-pane");
    dialogVbox.setPadding(new Insets(20));
    dialogVbox.setAlignment(Pos.CENTER);

    Label titleLabel = new Label(player.getName() + " landed on a tax tile");
    titleLabel.getStyleClass().add("dialog-header");

    int percentageAmount = (int) (player.getMoney() * (percentageTax / 100.0));

    Label optionsLabel = new Label("Choose your payment option:");
    optionsLabel.getStyleClass().add("dialog-message");

    Label percentageLabel = new Label("Pay " + percentageTax
        + "% of your money: $" + percentageAmount);

    percentageLabel.getStyleClass().add("dialog-message");

    Label fixedLabel = new Label("Pay fixed tax: $" + fixedTax);
    fixedLabel.getStyleClass().add("dialog-message");

    Button percentageButton = new Button("Pay " + percentageTax
        + "% ($" + percentageAmount + ")");

    percentageButton.getStyleClass().addAll("button", "button-primary");
    percentageButton.setOnAction(event -> {
      animateDialogAndClose(dialogStage);
      if (onPercentage != null) {
        onPercentage.run();
      }
    });

    Button fixedButton = new Button("Pay fixed tax ($" + fixedTax + ")");
    fixedButton.getStyleClass().addAll("button", "button-secondary");
    fixedButton.setOnAction(event -> {
      animateDialogAndClose(dialogStage);
      if (onFixed != null) {
        onFixed.run();
      }
    });

    if (player.getMoney() < fixedTax) {
      fixedButton.setDisable(true);
      fixedButton.getStyleClass().add("button-disabled");
      fixedButton.setText("Not enough money to pay fixed tax.");
    }

    HBox buttonBox = new HBox(20);
    buttonBox.setAlignment(Pos.CENTER);
    buttonBox.getChildren().addAll(percentageButton, fixedButton);

    dialogVbox.getChildren().addAll(titleLabel, optionsLabel, percentageLabel,
        fixedLabel, buttonBox);

    setupDialogStage(dialogStage, dialogVbox);

    try {
      dialogStage.show();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error showing tax dialog: " + e);
      if (onFixed != null) {
        onFixed.run();
      }
    }
  }

  /**
   * Shows a dialog displaying game information.
   *
   * @param ownerStage The stage that owns this dialog.
   * @param title The title of the dialog.
   * @param gameInfo The information about the game to display.
   */
  public static void showGameInfoDialog(Stage ownerStage, String title, String gameInfo) {
    if (ownerStage == null) {
      return;
    }

    Stage dialogStage = new Stage();
    dialogStage.initModality(Modality.APPLICATION_MODAL);
    dialogStage.initOwner(ownerStage);
    dialogStage.setTitle(title);

    handleDialogCloseRequest(dialogStage, null);

    VBox dialogVbox = new VBox(15);
    dialogVbox.getStyleClass().add("dialog-pane");
    dialogVbox.setPadding(new Insets(20));
    dialogVbox.setAlignment(Pos.CENTER);

    Label titleLabel = new Label(title);
    titleLabel.getStyleClass().add("dialog-header");

    Label infoLabel = new Label(gameInfo);
    infoLabel.getStyleClass().add("dialog-message");
    infoLabel.setWrapText(true);

    Button closeButton = new Button("Close");
    closeButton.getStyleClass().addAll("button", "button-secondary");
    closeButton.setOnAction(event -> animateDialogAndClose(dialogStage));

    dialogVbox.getChildren().addAll(titleLabel, infoLabel, closeButton);

    Scene dialogScene = new Scene(dialogVbox, 600, 600);

    try {
      String cssPath = Objects.requireNonNull(
          MainApp.class.getResource("/styles.css")).toExternalForm();
      dialogScene.getStylesheets().add(cssPath);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error loading CSS: " + e);
    }

    dialogStage.setScene(dialogScene);

    try {
      dialogStage.show();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error showing dialog: " + e);
    }
  }

  /**
   * Helper method to handle the dialog window close requests.
   *
   * @param dialogStage The dialog stage to handle.
   * @param defaultAction The default action to execute if the user closes the dialog.
   */
  private static void handleDialogCloseRequest(Stage dialogStage, Runnable defaultAction) {
    dialogStage.setOnCloseRequest(event -> {
      event.consume();
      animateDialogAndClose(dialogStage);
      if (defaultAction != null) {
        defaultAction.run();
      }
    });
  }

  /**
   * Animates a dialog closing with a fade out effect.
   *
   * @param dialogStage The dialog stage to animate and close.
   */
  private static void animateDialogAndClose(Stage dialogStage) {
    try {
      FadeTransition fadeOut = new FadeTransition(Duration.millis(150),
                                    dialogStage.getScene().getRoot());
      fadeOut.setFromValue(1);
      fadeOut.setToValue(0);
      fadeOut.setOnFinished(event -> dialogStage.close());
      fadeOut.play();
    } catch (Exception e) {
      System.err.println("Error during fade out animation: " + e.getMessage());
      dialogStage.close();
    }
  }


  /**
   * Assembles the dialog box layout by adding labels and buttons to a dialog VBox and configuring
   * the button layout in an HBox.
   *
   * @param dialogStage The stage that will display the dialog.
   * @param dialogVbox The main VBox container for the dialog content.
   * @param propertyNameLabel The title or header label for the dialog.
   * @param costLabel The label displaying cost or additional information.
   * @param promptLabel The label with the user prompt or question.
   * @param confirmButton The primary action button.
   * @param declineButton The secondary action button.
   */
  private static void dialogContentAssembler(Stage dialogStage, VBox dialogVbox,
      Label propertyNameLabel, Label costLabel, Label promptLabel, Button confirmButton,
      Button declineButton) {
    HBox buttonBox = new HBox(20);
    buttonBox.setAlignment(Pos.CENTER);
    buttonBox.getChildren().addAll(confirmButton, declineButton);

    dialogVbox.getChildren().addAll(propertyNameLabel, costLabel, promptLabel, buttonBox);

    setupDialogStage(dialogStage, dialogVbox);
  }

  /**
   * Configures the dialog scene with proper dimensions and CSS styling.
   *
   * @param dialogStage The stage that will display the dialog.
   * @param dialogVbox The main VBOX container holding all dialog content.
   */
  private static void setupDialogStage(Stage dialogStage, VBox dialogVbox) {
    Scene dialogScene = new Scene(dialogVbox, 400, 200);

    try {
      String cssPath = Objects.requireNonNull(
          MainApp.class.getResource("/styles.css")).toExternalForm();

      dialogScene.getStylesheets().add(cssPath);
    } catch (Exception e) {
      System.err.println("Error loading CSS: " + e.getMessage());
    }

    dialogStage.setScene(dialogScene);
  }
}