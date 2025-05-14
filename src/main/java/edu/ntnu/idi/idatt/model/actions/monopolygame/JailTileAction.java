package edu.ntnu.idi.idatt.model.actions.monopolygame;

import edu.ntnu.idi.idatt.controllers.MonopolyGameController;
import edu.ntnu.idi.idatt.model.actions.TileAction;
import edu.ntnu.idi.idatt.model.gamelogic.BoardGame;
import edu.ntnu.idi.idatt.model.gamelogic.Player;
import edu.ntnu.idi.idatt.model.gamelogic.Tile;
import edu.ntnu.idi.idatt.views.DialogService;
import javafx.application.Platform;
import javafx.stage.Stage;


/**
 * <h1>Jail Tile Action</h1>
 *
 * <p>Represents the action and behavior of the jail tile in Monopoly. This class handles two cases:
 * players who are imprisoned and must find a way out, and players who are "just visiting" jail.</p>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Jail turn tracking and automatic release after maximum turns</li>
 *   <li>Interactive options for players to escape jail (pay bail or roll doubles)</li>
 *   <li>UI integration for presenting jail-related decisions</li>
 *   <li>Proper game flow management during imprisonment</li>
 * </ul>
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
 */
public class JailTileAction implements TileAction {
  private static final int JAIL_BAIL = 5000;
  private static final int MAX_JAIL_TURNS = 3;

  private Stage ownerStage;
  private MonopolyGameController controller;

  /**
   * Sets the owner stage for displaying dialogs.
   *
   * @param stage The stage that owns this action's dialogs.
   */
  public void setOwnerStage(Stage stage) {
    this.ownerStage = stage;
  }

  /**
   * Sets the game controller for this action.
   *
   * @param controller The controller for this action.
   */
  public void setController(MonopolyGameController controller) {
    this.controller = controller;
  }

  /**
   * Performs the action of the jail tile.
   * Player in this tile must either roll doubles or pay to get out.
   *
   * @param player The player that lands on the tile with this action.
   */
  @Override
  public void perform(Player player) {

    String inJail = player.getProperty("inJail");
    System.out.println("Player " + player.getName() + " jail status: " + inJail);

    if (inJail != null && inJail.equals("true")) {
      System.out.println(player.getName() + " is in jail");

      String jailTurnCount = player.getProperty("jailTurnCount");
      int jailTurnCountInt = 1;
      if (jailTurnCount != null) {
        jailTurnCountInt = Integer.parseInt(jailTurnCount) + 1;
      }
      player.setProperty("jailTurnCount", String.valueOf(jailTurnCountInt));
      System.out.println(player.getName() + " has been in jail for " + jailTurnCountInt + " turns");

      if (jailTurnCountInt >= MAX_JAIL_TURNS) {
        System.out.println(player.getName() + " has spent "
            + MAX_JAIL_TURNS + " turns in jail and is released");
        player.releaseFromJail();
        player.setProperty("jailTurnCount", "0");
        Platform.runLater(() -> {
          controller.getCurrentPlayer().setWaitTurn(false);
          controller.enableRollButton(false);
        });

        if (controller != null) {
          Platform.runLater(() -> controller.advanceToNextPlayer());
        }
        return;
      }

      if (controller != null && ownerStage != null) {
        System.out.println("Showing jail options dialog for " + player.getName());
        Platform.runLater(() -> DialogService.showJailOptionsDialog(player, ownerStage,
            () -> handlePayBail(player),
            () -> handleRollDoubles(player)));
      } else {
        System.out.println("ERROR: Cannot show jail options dialog. Controller or stage is null.");
        if (controller != null) {
          Platform.runLater(() -> controller.advanceToNextPlayer());
        }
      }
    } else {
      System.out.println(player.getName() + " is just visiting the jail");
    }
  }

  /**
   * Handles the action of paying the bail to get out of jail.
   *
   * @param player The player performing the action.
   */
  public void handlePayBail(Player player) {
    if (player.payMoney(JAIL_BAIL)) {
      System.out.println(player.getName() + " paid $" + JAIL_BAIL + " for getting out of jail");
      player.releaseFromJail();
      player.setProperty("jailTurnCount", "0");

      if (controller != null) {
        controller.updatePlayerMoney(player);
      }

      Platform.runLater(() -> {
        controller.getCurrentPlayer().setWaitTurn(false);
        controller.enableRollButton(false);
      });

      BoardGame game = player.getGame();
      if (game != null) {
        game.notifyPlayerMove(player,
            player.getCurrentTile().getTileId(), player.getCurrentTile().getTileId(), 0);
      }
    } else {
      System.out.println(player.getName() + " cannot afford " + JAIL_BAIL
          + " and must roll doubles to get out");
    }
    if (controller != null) {
      Platform.runLater(() -> controller.advanceToNextPlayer());
    }
  }

  /**
   * Handles the action of attempting to roll doubles to get out of jail.
   *
   * @param player The player performing the action.
   */
  public void handleRollDoubles(Player player) {
    BoardGame game = player.getGame();
    int[] diceValues = game.rollDice();

    System.out.println(player.getName() + " rolled " + diceValues[0] + " and " + diceValues[1]);

    if (diceValues[0] == diceValues[1]) {
      System.out.println(player.getName() + " rolled doubles and gets out of jail");
      player.releaseFromJail();
      player.setProperty("jailTurnCount", "0");

      int fromTileId = player.getCurrentTile().getTileId();

      Tile destinationTile = player.getCurrentTile();
      int steps = diceValues[0] + diceValues[1];
      for (int i = 0; i < steps; i++) {
        if (destinationTile.getNextTile() != null) {
          destinationTile = destinationTile.getNextTile();
        }
      }

      player.placeOnTile(destinationTile);
      int toTileId = destinationTile.getTileId();

      game.notifyPlayerMove(player, fromTileId, toTileId, steps);

      if (destinationTile.getAction() != null) {
        destinationTile.getAction().perform(player);
      } else {
        System.out.println(player.getName() + " failed to roll doubles and stays in jail");
      }
    }
    if (controller != null) {
      Platform.runLater(() -> controller.advanceToNextPlayer());
    }
  }
}