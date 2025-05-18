package edu.ntnu.idi.idatt.model.filehandling.exceptions;

public class GameLoadException extends FileHandlerException{

  public GameLoadException(String saveName, String gameType, String description) {
    super("Failed to load game '" + saveName + "' of type '" + gameType + "': " + description);
  }
}
