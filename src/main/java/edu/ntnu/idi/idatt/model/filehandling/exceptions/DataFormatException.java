package edu.ntnu.idi.idatt.model.filehandling.exceptions;

public class DataFormatException extends FileHandlerException {

  public DataFormatException(String message) {
    super(message);
  }

  public DataFormatException(String filename, int lineNumber, String description) {
    super("Format error in file " + filename + " at line " + lineNumber + ": " + description);
  }
}
