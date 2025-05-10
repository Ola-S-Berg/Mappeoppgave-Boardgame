package edu.ntnu.idi.idatt.model.filehandling;

import java.io.IOException;
import java.util.List;

/**
 * Interface for file handling. Defines methods for reading and writing objects to files.
 */
public interface FileHandler<T> {

  /**
   * Writes a list of objects to the specified file.
   *
   * @param filename The name of the file to write to.
   * @param objects The list of objects to be written to the file.
   * @throws IOException If an I/O error occurs during writing.
   */
  void writeToFile(String filename, List<T> objects) throws IOException;

  /**
   * Reads a list of objects from the specified file.
   *
   * @param filename The name of the file to read from.
   * @return A list of objects read from the file.
   * @throws IOException If an I/O error occurs during reading.
   */
  List<T> readFromFile(String filename) throws IOException;
}
