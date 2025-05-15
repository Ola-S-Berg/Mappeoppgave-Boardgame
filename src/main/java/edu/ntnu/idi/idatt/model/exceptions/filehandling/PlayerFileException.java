package edu.ntnu.idi.idatt.model.exceptions.filehandling;

public class PlayerFileException extends FileHandlerException {

  public PlayerFileException(String playerName, String description) {
    super("Error with player file for player '" + playerName + "': " + description);
  }
}
