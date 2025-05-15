package edu.ntnu.idi.idatt.model.exceptions.filehandling;

/**
 * <h1>File Handler Exception</h1>
 *
 * <p>Base exception class for all file-handling-related exceptions in the application.
 * This class extends RuntimeException to provide unchecked exceptions for file operations
 * that may fail due to reasons such as "file not found" or data format problems.</p>
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
 */
public class FileHandlerException extends RuntimeException {

  /**
   * Constructs a new file handler exception with the specified message.
   *
   * @param message The message.
   */
  public FileHandlerException(String message) {
    super(message);
  }

  /**
   * Constructs a new file handler exception with the specified message and cause.
   *
   * @param message The message.
   * @param cause The cause of the exception.
   */
  public FileHandlerException(String message, Throwable cause) {
    super(message, cause);
  }
}
