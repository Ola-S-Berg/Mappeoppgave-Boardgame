package edu.ntnu.idi.idatt.model.filehandling.exceptions;

public class BoardFileException extends FileHandlerException {

  public BoardFileException(String message) {
    super(message);
  }

  public BoardFileException(String variantName, String description) {
    super("Error with board file for game variant '" + variantName + "': " + description);
  }
}
