package edu.ntnu.idi.idatt.model.exceptions.filehandling;

public class FileWriteException extends FileHandlerException {

  public FileWriteException (String filename, Exception cause) {
    super("Error writing to file: " + filename, cause);
  }
}
