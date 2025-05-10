package edu.ntnu.idi.idatt.views;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Scene;

/**
 * Utility class for CSS styling operations.
 * Provides methods to consistently apply CSS stylesheets to scenes.
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
