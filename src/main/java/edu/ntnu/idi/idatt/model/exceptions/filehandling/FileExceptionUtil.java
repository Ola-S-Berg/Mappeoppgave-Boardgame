package edu.ntnu.idi.idatt.model.exceptions.filehandling;

import java.io.IOException;

/**
 * <h1>File Exception Utility</h1>
 *
 * <p>A utility class for creating and wrapping file-related exceptions in a consistent manner.
 * This class centralizes exception creation logic to ensure consistent error handling and
 * messaging throughout the application.</p>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Standardized exception wrapping for I/O operations</li>
 *   <li>Consistent exception creation for various file handling scenarios</li>
 *   <li>Support for different file types (board files, player files)</li>
 *   <li>Utility methods for game saving and loading exceptions</li>
 * </ul>
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
 */
public class FileExceptionUtil {

  /**
   * Wraps an IOException that occurred during a file read operation into the appropriate
   * FileHandlerException subtype.
   *
   * @param filename The name of the file being read.
   * @param e The IOException that occurred.
   * @return An appropriate FileHandlerException subtype.
   */
  public static FileHandlerException wrapReadException(String filename, IOException e) {
    if (e.getMessage() != null && e.getMessage().contains("No such file")) {
      return new FileNotFoundException(filename);
    } else {
      return new FileReadException(filename, e);
    }
  }

  /**
   * Wraps an IOException that occurred during a file write operation into a FileWriteException.
   *
   * @param filename The name of the file being written.
   * @param e The IOException that occurred.
   * @return A FileWriteException with an appropriate message and cause.
   */
  public static FileHandlerException wrapWriteException(String filename, IOException e) {
    return new FileWriteException(filename, e);
  }

  /**
   * Creates a GameSaveException for errors that occur during game saving.
   *
   * @param saveName The name of the save file.
   * @param e The IOException that occurred.
   * @return A GameSaveException with an appropriate message and cause.
   */
  public static GameSaveException createSaveException(String saveName, IOException e) {
    return new GameSaveException("Failed to save game: " + saveName, e);
  }

  /**
   * Creates a GameSaveException with game type information.
   *
   * @param saveName The name of the save file.
   * @param gameType The type of game being saved.
   * @param description A description of the error.
   * @return A GameSaveException with detailed information.
   */
  public static GameSaveException
        createSaveException(String saveName, String gameType, String description) {
    return new GameSaveException(saveName, gameType, description);
  }

  /**
   * Creates a GameLoadException with game type information.
   *
   * @param saveName The name of the save file.
   * @param gameType The type of game being loaded.
   * @param description A description of the error.
   * @return A GameLoadException with detailed information.
   */
  public static GameLoadException
        createLoadException(String saveName, String gameType, String description) {
    return new GameLoadException(saveName, gameType, description);
  }

  /**
   * Creates a BoardFileException for errors related to board file handling.
   *
   * @param message The error message.
   * @return A BoardFileException with the specified message.
   */
  public static BoardFileException createBoardFileException(String message) {
    return new BoardFileException(message);
  }

  /**
   * Creates a BoardFileException for errors related to board file handling with a specific variant.
   *
   * @param variantName The name of the board game variant.
   * @param description A description of the error.
   * @return A BoardFileException with variant-specific information.
   */
  public static BoardFileException
        createBoardFileException(String variantName, String description) {
    return new BoardFileException(variantName, description);
  }

  /**
   * Creates a DataFormatException for errors related to data formatting.
   *
   * @param filename The name of the file with the format error.
   * @param lineNumber The line number where the error occurred.
   * @param description A description of the format error.
   * @return A DataFormatException with detailed information.
   */
  public static DataFormatException
        createDataFormatException(String filename, int lineNumber, String description) {
    return new DataFormatException(filename, lineNumber, description);
  }

  /**
   * Creates a PlayerFileException for errors related to player file handling.
   *
   * @param playerName The name of the player.
   * @param description A description of the error.
   * @return A PlayerFileException with player-specific information.
   */
  public static PlayerFileException
        createPlayerFileException(String playerName, String description) {
    return new PlayerFileException(playerName, description);
  }

  /**
   * Creates a PlayerDataFormatException for errors related to player data formatting.
   *
   * @param filename The name of the file with the format error.
   * @param lineNumber The line number where the error occurred.
   * @param description A description of the format error.
   * @return A PlayerDataFormatException with detailed information.
   */
  public static PlayerDataFormatException createPlayerDataFormatException(
      String filename, int lineNumber, String description) {
    return new PlayerDataFormatException(filename, lineNumber, description);
  }

  /**
   * Creates a PlayerDataFormatException for errors related to player data formatting.
   *
   * @param filename The name of the file with the format error.
   * @param playerName The name of the player.
   * @param description A description of the format error.
   * @return A PlayerDataFormatException with player-specific information.
   */
  public static PlayerDataFormatException createPlayerDataFormatException(
      String filename, String playerName, String description) {
    return new PlayerDataFormatException(filename, playerName, description);
  }

  /**
   * Creates a PlayerFileReadException for errors that occur during player file reading.
   *
   * @param message The error message.
   * @return A PlayerFileReadException with the specified message.
   */
  public static PlayerFileReadException createPlayerFileReadException(String message) {
    return new PlayerFileReadException(message);
  }

  /**
   * Creates a PlayerFileReadException for errors that occur during player file reading.
   *
   * @param filename The name of the file being read.
   * @param e The exception that occurred.
   * @return A PlayerFileReadException with an appropriate message and cause.
   */
  public static PlayerFileReadException
        createPlayerFileReadException(String filename, Exception e) {
    return new PlayerFileReadException(filename, e);
  }

  /**
   * Creates a PlayerFileWriteException for errors that occur during player file writing.
   *
   * @param filename The name of the file being written.
   * @param e The exception that occurred.
   * @return A PlayerFileWriteException with an appropriate message and cause.
   */
  public static PlayerFileWriteException
        createPlayerFileWriteException(String filename, Exception e) {
    return new PlayerFileWriteException(filename, e);
  }

  /**
   * Creates a PlayerFileWriteException for errors that occur during player file writing.
   *
   * @param filename The name of the file being written.
   * @param playerName The name of the player.
   * @param description A description of the error.
   * @return A PlayerFileWriteException with player-specific information.
   */
  public static PlayerFileWriteException createPlayerFileWriteException(
      String filename, String playerName, String description) {
    return new PlayerFileWriteException(filename, playerName, description);
  }
}