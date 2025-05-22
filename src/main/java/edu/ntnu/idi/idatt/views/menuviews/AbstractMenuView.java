package edu.ntnu.idi.idatt.views.menuviews;

import edu.ntnu.idi.idatt.MainApp;
import edu.ntnu.idi.idatt.views.CssUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * <h1>Abstract Menu View</h1>
 *
 * <p>An abstract base class that provides common functionality for menu views.
 * This class handles the basic setup of the view structure, including layout,
 * styling, and window configuration.</p>
 *
 * @author Markus Øyen Lund
 * @since v1.1.0
 */
public abstract class AbstractMenuView {
  protected final MainApp application;
  protected Scene scene;
  protected BorderPane root;
  protected VBox layout;

  /**
   * Constructor for the abstract menu view.
   *
   * @param application The main application instance.
   */
  protected AbstractMenuView(MainApp application) {
    this.application = application;
    setupBaseView();
  }

  /**
   * Sets up the basic view structure common to all menu views.
   */
  protected void setupBaseView() {
    root = new BorderPane();
    root.getStyleClass().add("root");
    root.setPadding(new Insets(10));

    layout = new VBox(30);
    layout.setAlignment(Pos.CENTER);
    layout.setPadding(new Insets(10));
    layout.getStyleClass().add("content-box");

    root.setCenter(layout);

    scene = new Scene(root, 800, 600);
    CssUtil.applyStyleSheet(scene);

    Stage stage = application.getPrimaryStage();
    stage.setMinWidth(600);
    stage.setMinHeight(600);
    stage.centerOnScreen();
  }

  /**
   * Returns the scene for this view.
   *
   * @return The scene.
   */
  public Scene getScene() {
    return scene;
  }
} 