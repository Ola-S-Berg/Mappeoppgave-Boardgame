package edu.ntnu.idi.idatt.views;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Scene;

/**
 * <h1>CSS Utility Class</h1>
 *
 * <p>A utility class that provides centralized functionality for CSS styling operations
 * throughout the application. This class ensures the consistent application of stylesheets
 * to JavaFX scenes and handles potential exceptions during the process.</p>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Centralizes CSS application logic in a single utility class</li>
 *   <li>Maintains consistent styling across the application</li>
 *   <li>Uses logging to track styling errors when they occur.</li>
 * </ul>
 *
 * @author Ola Syrstad Berg
 * @since v1.1.0
 */
public class CssUtil {
  private static final Logger LOGGER = Logger.getLogger(CssUtil.class.getName());
  private static final String CSS_PATH = "/styles.css";

  /**
   * Applies the application's stylesheet to the given scene.
   *
   * @param scene The JavaFX scene to apply CSS to.
   */
  public static void applyStyleSheet(Scene scene) {
    try {
      String cssPath = Objects.requireNonNull(CssUtil.class.getResource(CSS_PATH)).toExternalForm();
      scene.getStylesheets().add(cssPath);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to apply CSS stylesheets" + e.getMessage(), e);
    }
  }
}
