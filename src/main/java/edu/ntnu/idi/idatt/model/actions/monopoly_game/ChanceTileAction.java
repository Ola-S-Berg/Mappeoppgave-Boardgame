package edu.ntnu.idi.idatt.model.actions.monopoly_game;

import edu.ntnu.idi.idatt.model.actions.TileAction;
import edu.ntnu.idi.idatt.controllers.MonopolyGameController;
import edu.ntnu.idi.idatt.model.gamelogic.Player;
import edu.ntnu.idi.idatt.model.gamelogic.Tile;
import java.util.Random;

/**
 * Class representing action when landing on a chance tile in Monopoly.
 */
public class ChanceTileAction implements TileAction {
  private static final Random random = new Random();
  private MonopolyGameController controller;
  private static final String[] CHANCE_ACTIONS = {
      "Move forward 3 spaces",
      "Collect 5000 from the bank",
      "Pay 3000 to the bank",
      "Advance to the nearest landmark",
      "Pay each player 1000",
      "Collect 1000 from each player",
  };

  public void setController(MonopolyGameController controller) {
    this.controller = controller;
  }

  /**
   * Performs a random chance action when a player lands on a chance tile.
   *
   * @param player The player that lands on the tile with this action.
   */
  @Override
  public void perform(Player player) {
    int actionIndex = random.nextInt(CHANCE_ACTIONS.length);
    String action = CHANCE_ACTIONS[actionIndex];

    System.out.println(player.getName() + " draws a chance card: " + action);

    if (controller != null) {
      controller.updateActionLabel(player.getName() + " draws a chance card: " + action);
    }

    executeChanceAction(player, actionIndex);
  }

  /**
   * Executes the specific chance action based on the action index.
   *
   * @param player The player performing the action.
   * @param actionIndex The index of the action to perform.
   */
  private void executeChanceAction(Player player, int actionIndex) {
    String actionMessage;
    int playerCount = 0;
    switch (actionIndex) {
      case 0: // Move forward 3 spaces
        Tile destinationTile = player.getCurrentTile();
        for (int i = 0; i < 3; i++) {
          if (destinationTile.getNextTile() != null) {
            destinationTile = destinationTile.getNextTile();
          }
        }

        actionMessage = player.getName() + " moves forward 3 spaces to tile " + Tile.getTileName(destinationTile);
        System.out.println(actionMessage);

        if (controller != null) {
          controller.updateActionLabel(actionMessage);
        }

        player.placeOnTile(destinationTile);
        if (destinationTile.getAction() != null && !(destinationTile.getAction() instanceof ChanceTileAction)) {
          if (destinationTile.getAction() instanceof PropertyTileAction) {
            ((PropertyTileAction) destinationTile.getAction()).setController(this.controller);
          }
          destinationTile.getAction().perform(player);
        }
        break;

      case 1: // Collect 5000 from the bank.
        player.addMoney(5000);
        actionMessage= player.getName() + " collects 5000 from the bank";
        System.out.println(actionMessage);

        if (controller != null) {
          controller.updateActionLabel(actionMessage);
          controller.updatePlayerMoney(player);
        }
        break;

      case 2: // Pay 3000 to the bank.
        player.payMoney(3000);
        actionMessage = player.getName() + " pays 3000 to the bank";
        System.out.println(actionMessage);

        if (controller != null) {
          controller.updateActionLabel(actionMessage);
          controller.updatePlayerMoney(player);
        }
        break;

      case 3: // Advance to the nearest landmark.
        int currentPosition = player.getCurrentTile().getTileId();
        Tile nearestLandmarkTile6 = player.getGame().getBoard().getTile(6);
        Tile nearestLandmarkTile16 = player.getGame().getBoard().getTile(16);
        Tile nearestLandmarkTile26 = player.getGame().getBoard().getTile(26);
        Tile nearestLandmarkTile36 = player.getGame().getBoard().getTile(36);

        Tile landmarkTile = null;

        if (currentPosition == 3 || currentPosition == 8) {
          landmarkTile = nearestLandmarkTile6;
        } else if (currentPosition == 13 || currentPosition == 18) {
          landmarkTile = nearestLandmarkTile16;
        } else if (currentPosition == 23 || currentPosition == 29) {
          landmarkTile = nearestLandmarkTile26;
        } else if (currentPosition == 34 || currentPosition == 39) {
          landmarkTile = nearestLandmarkTile36;
        }

        if (landmarkTile != null && landmarkTile.getAction() instanceof PropertyTileAction) {
          String propertyName = ((PropertyTileAction)landmarkTile.getAction()).getPropertyName();
          actionMessage = player.getName() + " advances to the nearest landmark: " + propertyName;
          System.out.println(actionMessage);

          if (controller != null) {
            controller.updateActionLabel(actionMessage);
          }

          player.placeOnTile(landmarkTile);

          if (landmarkTile.getAction() != null && landmarkTile.getAction() instanceof PropertyTileAction propertyAction) {
            propertyAction.setController(this.controller);
            propertyAction.perform(player);
          }
        } else {
          actionMessage = "No landmark found";
          System.out.println(actionMessage);

          if (controller != null) {
            controller.updateActionLabel(actionMessage);
          }
        }
        break;
      case 4: // Pay each player 1000.
        for (Player otherPlayer : player.getGame().getPlayers()) {
          if (otherPlayer != player) {
            player.payPlayer(otherPlayer, 1000);
            controller.updatePlayerMoney(otherPlayer);
            playerCount++;
          }
        }
        actionMessage = player.getName() + " pays 1000 to each player (total: " + (playerCount * 1000) + ")";
        System.out.println(actionMessage);

        if (controller != null) {
          controller.updateActionLabel(actionMessage);
          controller.updatePlayerMoney(player);
        }
        break;
      case 5: // Collect 1000 from each player.
        for (Player otherPlayer : player.getGame().getPlayers()) {
          if (otherPlayer != player) {
            otherPlayer.payPlayer(player, 1000);
            controller.updatePlayerMoney(otherPlayer);
            playerCount++;
          }
        }
        actionMessage = player.getName() + " collects 1000 from each player (total: " + (playerCount * 1000) + ")";
        System.out.println(actionMessage);

        if (controller != null) {
          controller.updateActionLabel(actionMessage);
          controller.updatePlayerMoney(player);
        }
        break;
      default:
        actionMessage = "Unknown chance action";
        System.out.println(actionMessage);

        if (controller != null) {
          controller.updateActionLabel(actionMessage);
        }
        break;
    }
  }
}