package application;

import javafx.application.Application;
import javafx.stage.*;

/**
 * Main class that extends the Application class Of JavaFX
 */
public class Main extends Application {

  /*
   * (non-Javadoc) - Entry point for this application
   * 
   * @see javafx.application.Application#start(javafx.stage.Stage)
   */
  public void start(Stage primaryStage) {
    FoodData foodData = new FoodData(); // reference to the object which represents the backend of
                                        // this application

    new PrimaryGUI(foodData, primaryStage); // reference to the object which
                                           // represents the primary GUI of
                                          // this application
  }

  /**
   * The main method to launch the application
   *
   * @param args the arguments
   */
  public static void main(String[] args) {
    launch(args);
  }

}
