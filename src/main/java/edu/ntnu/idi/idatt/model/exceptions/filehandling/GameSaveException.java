package edu.ntnu.idi.idatt.model.exceptions.filehandling;

public class GameSaveException extends FileHandlerException {

   public GameSaveException(String message, Throwable cause) {
      super(message, cause);
   }

   public GameSaveException(String saveName, String gameType, String description) {
    super("Failed to save game '" + saveName + "' of type '" + gameType + "': " + description);
   }
}
