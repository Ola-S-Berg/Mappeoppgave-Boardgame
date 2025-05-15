package edu.ntnu.idi.idatt.model.exceptions.filehandling;

public class PlayerFileWriteException extends FileHandlerException {

  public PlayerFileWriteException(String filename, Exception cause) {
    super("Error writing player data to file: " + filename, cause);
  }

  public PlayerFileWriteException(String filename, String playerName, String description) {
    super("Error writing player '" + playerName + "' data to file "
        + filename + ": " + description);
  }
}
