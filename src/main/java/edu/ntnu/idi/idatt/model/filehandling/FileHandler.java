package edu.ntnu.idi.idatt.model.filehandling;

import edu.ntnu.idi.idatt.model.exceptions.filehandling.FileHandlerException;
import edu.ntnu.idi.idatt.model.exceptions.filehandling.FileReadException;
import edu.ntnu.idi.idatt.model.exceptions.filehandling.FileWriteException;
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
 *
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
   * @throws FileWriteException If an error occurs during writing to the file.
   * @throws FileHandlerException If a general file handling error occurs.
   */
  void writeToFile(String filename, List<T> objects) throws FileWriteException, FileHandlerException;

  /**
   * Reads a list of objects from the specified file.
   *
   * @param filename The name of the file to read from.
   * @return A list of objects read from the file.
   * @throws FileReadException If an error occurs during reading from the file.
   * @throws FileHandlerException If a general file handling error occurs.
   */
  List<T> readFromFile(String filename) throws FileReadException, FileHandlerException;
}
