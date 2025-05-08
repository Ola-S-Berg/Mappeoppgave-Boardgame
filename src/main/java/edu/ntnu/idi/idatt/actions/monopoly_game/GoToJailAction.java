package edu.ntnu.idi.idatt.actions.monopoly_game;

import edu.ntnu.idi.idatt.actions.TileAction;
import edu.ntnu.idi.idatt.model.BoardGame;
import edu.ntnu.idi.idatt.model.Player;
import edu.ntnu.idi.idatt.model.Tile;

/**
 * Class representing action when landing on a "Go To Jail" tile in Monopoly.
 */
public class GoToJailAction implements TileAction {
  private final int jailTileId;

  /**
   * Constructor for GoToJailAction.
   *
   * @param jailTileId The ID of the jail tile to send the player to.
   */
  public GoToJailAction(int jailTileId) {
    this.jailTileId = jailTileId;
  }

  /**
   * Performs the action of sending the player to jail.
   *
   * @param player The player that lands on the tile with this action.
   */
  @Override
  public void perform(Player player) {
    System.out.println(player.getName() + " is being sent to jail!");

    Tile jailTile = player.getGame().getBoard().getTile(jailTileId);
    if (jailTile == null) {
      throw new IllegalStateException("Jail tile does not exist: " + jailTileId);
    }

    player.setProperty("inJail", "true");
    player.setProperty("jailTurnCount", "0");

    BoardGame game = player.getGame();
    for (Player otherPlayer : game.getPlayers()) {
      if (!otherPlayer.equals(player)) {
        player.setProperty("waitFor_" + otherPlayer.getName(), "true");
      }
    }

    player.setWaitTurn(true);

    player.placeOnTile(jailTile);

    System.out.println(player.getName() + " went to jail and must wait 3 turns or pay bail/roll doubles to get out");
  }
}
