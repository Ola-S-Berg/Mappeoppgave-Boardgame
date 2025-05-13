package edu.ntnu.idi.idatt.model.filehandling;

import java.io.IOException;
import java.util.List;

/**
 * <h1>File Handler Interface</h1>
 *
 * <p>A generic interface that defines standard operations for writing objects to- and reading
 * objects from files. This interface serves as a contract for implementing various file handling
 * strategies across the application.</p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Writing collections of objects to file storage</li>
 *   <li>Reading collections of objects from file storage</li>
 *   <li>Handling potential I/O exceptions during file operations</li>
 * </ul
 *
 * <h2>Type parameter</h2>>
 * <p>This interface uses a type parameter {@code <T>} to allow implementations to specify
 * the type of objects they can handle, providing type safety across the application.</p>
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
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
