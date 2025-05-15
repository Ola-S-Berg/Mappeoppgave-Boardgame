package edu.ntnu.idi.idatt.model.exceptions.filehandling;

public class PlayerDataFormatException extends DataFormatException {

   public PlayerDataFormatException(String filename, int lineNumber, String description) {
    super(filename, lineNumber, description);
   }

   public PlayerDataFormatException(String filename, String playerName, String description) {
    super("Format error in player file " + filename + " for player '"
        + playerName + "': " + description);
   }
}
