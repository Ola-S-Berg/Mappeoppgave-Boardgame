package edu.ntnu.idi.idatt.model.exceptions.filehandling;

public class FileNotFoundException extends FileHandlerException {

  private final String filename;

  public FileNotFoundException(String filename) {
    super("File not found:" + filename);
    this.filename = filename;
  }

  public String getFilename() {
    return filename;
  }
}
