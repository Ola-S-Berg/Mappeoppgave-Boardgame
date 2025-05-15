package edu.ntnu.idi.idatt.model.exceptions.filehandling;

public class FileReadException extends FileHandlerException {

  public FileReadException(String message) {
    super(message);
  }

  public FileReadException(String filename, Exception cause) {
    super("Error reading from file: " + filename, cause);
  }
}
