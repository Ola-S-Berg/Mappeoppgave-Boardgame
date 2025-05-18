package edu.ntnu.idi.idatt.model.actions.monopolygame;

import edu.ntnu.idi.idatt.model.actions.TileAction;
import edu.ntnu.idi.idatt.model.gamelogic.BoardGame;
import edu.ntnu.idi.idatt.model.gamelogic.Player;
import edu.ntnu.idi.idatt.model.gamelogic.Tile;

/**
 * <h1>Go To Jail Action</h1>
 *
 * <p>Implements the action when a player lands on the "Go To Jail" tile in Monopoly.
 * This class handles the mechanics of relocating a player to the jail tile and applying
 * the Jail tile effect, see {@link JailTileAction}.</p>
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
 */
public class GoToJailAction implements TileAction {
  private final int jailTileId;

  /**
   * Constructs a new GoToJailAction tile that sends players to jail.
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

    System.out.println(player.getName()
        + " went to jail and must wait 3 turns or pay bail/roll doubles to get out");
  }

  /**
   * Gets the jail tile ID associated with this action.
   *
   * @return The jail tile ID.
   */
  public int getJailTileId() {
    return jailTileId;
  }
}
