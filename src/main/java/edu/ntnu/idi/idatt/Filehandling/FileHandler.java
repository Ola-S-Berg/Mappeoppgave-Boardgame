package edu.ntnu.idi.idatt.Filehandling;
import java.io.IOException;
import java.util.List;

/**
 * Interface for file handling. Defines methods for reading and writing objects to files.
 */
public interface FileHandler <T> {
  void writeToFile(String filename, List<T> objects) throws IOException;
  List<T> readFromFile(String filename) throws IOException;
}
