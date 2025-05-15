package edu.ntnu.idi.idatt.model.exceptions.filehandling;

public class PlayerFileReadException extends FileHandlerException {

  public PlayerFileReadException(String message) {
      super(message);
   }

   public PlayerFileReadException(String filename, Exception cause) {
    super("Error reading player data from file: " + filename, cause);
   }
}
