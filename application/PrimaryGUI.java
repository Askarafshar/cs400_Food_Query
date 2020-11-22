package application;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * The main GUI for the application.
 */
public class PrimaryGUI {

  private Stage primaryStage; // The primary stage object constructed by JavaFX platform

  private FoodData foodData; // the food database

  private FoodListView foodListView; // the listview showing food items

  private FoodListView mealListView; // the listview showing the current meal items

  private FilterRulesForm filterRulesForm; // A reference to the FilterRulesForm object which
                                           // contains the different filter fields

  private Button newFoodButton; // A button to create a new food item

  private Button addFoodToMealButton; // A button to add the selected food items to the meal

  private Button analyzeMealButton; // A button to provide a pie-chart representation of the
                                    // analysis of food items present in the meal

  private Button removeFromMealButton; // A button to remove the selected food items from the meal
                                       // list

  private MenuBarWrapper menuBar; // Menubar object contains options to load/save food items to a
                                  // file

  private Label availableFoodsLabel; // The Available Foods Label in the Primary GUI which maintains
                                     // a count of filtered vs total number of food items

  /**
   * Creates the main GUI for the application. Composed of three major regions. On the left-side is
   * the meal list view with buttons for adding and removing items. In the middle is the list view
   * of all available foods and a button for creating a new food. On the right are UI elements for
   * creating queries to search foods by name or nutrient amount.
   * 
   * @param foodData     - food database object
   * @param primaryStage - a JavaFX stage
   */

  public PrimaryGUI(FoodData foodData, Stage primaryStage) {

    // initialize all of the UI elements
    this.primaryStage = primaryStage;
    this.foodData = foodData;
    foodListView = new FoodListView();
    for (FoodItem foodItem : foodData.getAllFoodItems()) {
      foodListView.addFoodItemToFoodItemList(foodItem);
      foodListView.addFoodItemToFoodListView(foodItem.getName());
    }

    mealListView = new FoodListView();
    newFoodButton = new Button("Create New Food");
    newFoodButton.setOnAction(new CreateNewFoodItemHandler());

    addFoodToMealButton = new Button("Add Food to Meal");
    addFoodToMealButton.setOnAction(new MealListAddFoodEventHandler());

    analyzeMealButton = new Button("Analyze Meal");
    analyzeMealButton.setOnAction(new MealAnalysisHandler());

    removeFromMealButton = new Button("Remove Food");
    removeFromMealButton.setOnAction(new MealListRemoveFoodEventHandler());

    availableFoodsLabel = createLabelWithBoldFont("Available Foods");
    availableFoodsLabel.setText(availableFoodsLabel.getText()
        .concat(" (Count = " + Integer.toString(getFilteredFoodItems().size()) + "/"
            + Integer.toString(foodListView.getFoodItemList().size()) + ")"));
    filterRulesForm = new FilterRulesForm(foodData, foodListView, availableFoodsLabel);

    menuBar = new MenuBarWrapper(this);

    // build the UI frame

    BorderPane borderPane = new BorderPane();

    VBox centerVBox = new VBox();
    VBox leftVBox = new VBox();
    VBox rightVBox = new VBox();
    VBox topVBox = new VBox();
    VBox belowFoodListVBox = new VBox();
    HBox belowFoodListButtonsHBox = new HBox();
    HBox belowMealList = new HBox();

    // add UI elements to containters
    belowMealList.getChildren().addAll(analyzeMealButton, removeFromMealButton);
    belowMealList.setAlignment(Pos.CENTER);
    belowMealList.setSpacing(70);

    belowFoodListButtonsHBox.getChildren().addAll(addFoodToMealButton, newFoodButton);
    belowFoodListButtonsHBox.setAlignment(Pos.CENTER);
    belowFoodListButtonsHBox.setSpacing(60);

    belowFoodListVBox.getChildren().addAll(belowFoodListButtonsHBox);
    belowFoodListVBox.setSpacing(5);

    HBox availableFoodsHBox = new HBox();

    availableFoodsHBox.getChildren().addAll(availableFoodsLabel);
    availableFoodsHBox.setAlignment(Pos.CENTER);

    centerVBox.getChildren().addAll(availableFoodsHBox, foodListView.getView(), belowFoodListVBox);
    centerVBox.setSpacing(1);

    HBox myMealHBox = new HBox();
    Label myMealLabel = createLabelWithBoldFont("My Meal");
    myMealHBox.getChildren().addAll(myMealLabel);
    myMealHBox.setAlignment(Pos.CENTER);

    leftVBox.getChildren().addAll(myMealHBox, mealListView.getView(), belowMealList);
    leftVBox.setSpacing(1);

    topVBox.getChildren().addAll(menuBar.getNode());

    HBox filterRulesFormLabelHBox = new HBox();
    Label filterRulesFormLabel = createLabelWithBoldFont("Filter Foods");
    filterRulesFormLabel.setStyle(filterRulesFormLabel.getStyle().concat("-fx-underline: true;"));
    filterRulesFormLabelHBox.getChildren().add(filterRulesFormLabel);
    filterRulesFormLabelHBox.setAlignment(Pos.CENTER);

    rightVBox.getChildren().addAll(filterRulesFormLabelHBox,
        filterRulesForm.getFilterRulesGridPane());
    rightVBox.setSpacing(2.0);

    borderPane.setCenter(centerVBox);
    borderPane.setLeft(leftVBox);
    borderPane.setTop(topVBox);
    borderPane.setRight(rightVBox);

    // show the stage
    Scene primaryScene = new Scene(borderPane, 810, 470, Color.DARKGRAY);
    primaryStage.setScene(primaryScene);
    primaryStage.setTitle("Food Query and Meal Analysis");
    primaryStage.show();
  }

  /**
   * Getter for the foodData object.
   *
   * @return the food data
   */
  public FoodData getFoodData() {
    return foodData;
  }

  /**
   * Getter for the food list view.
   *
   * @return the food list view
   */
  public FoodListView getFoodListView() {
    return foodListView;
  }

  /**
   * Getter for the form of filter rules.
   *
   * @return the filter rules form
   */
  public FilterRulesForm getFilterRulesForm() {
    return filterRulesForm;
  }

  /**
   * Getter for the available foods label.
   *
   * @return the available foods label
   */
  public Label getAvailableFoodsLabel() {
    return availableFoodsLabel;
  }

  /**
   * Getter for the stage.
   *
   * @return the primary stage
   */
  public Stage getPrimaryStage() {
    return primaryStage;
  }

  /**
   * Creates a label with bold font.
   *
   * @param caption the caption
   * @return the label
   */
  private Label createLabelWithBoldFont(String caption) {
    Label label = new Label(caption);
    label.setStyle("-fx-font-weight: bold;");
    return label;
  }

  /**
   * Creates an alert dialog.
   *
   * @param alertType the alert type
   * @param message   the message
   * @return the alert
   */
  private Alert createAlertDialog(AlertType alertType, String message) {
    return new Alert(alertType, message);
  }

  /**
   * Gets the filtered food items.
   *
   * @return the filtered food items
   */
  private List<FoodItem> getFilteredFoodItems() {
    return foodListView.getFilteredFoodItemList() != null ? foodListView.getFilteredFoodItemList()
        : foodListView.getFoodItemList();
  }

  /**
   * A custom event handler class to handle removal of selected food items from the meal list
   */
  private class MealListRemoveFoodEventHandler implements EventHandler<ActionEvent> {

    /*
     * (non-Javadoc)
     * 
     * @see javafx.event.EventHandler#handle(javafx.event.Event)
     */
    @Override
    public void handle(ActionEvent event) {
      if (mealListView.getView().getItems() != null
          && !mealListView.getView().getItems().isEmpty()) {
        Alert alertDialog = createAlertDialog(AlertType.CONFIRMATION,
            "Are you sure you want to remove the selected food(s)");
        alertDialog.showAndWait();
        if (alertDialog.getResult().equals(ButtonType.OK)) {
          ObservableList<Node> mealItemsToBeRemoved =
              mealListView.getView().getSelectionModel().getSelectedItems();
          removeItemsFromMealFoodList(mealItemsToBeRemoved);
          mealListView.getView().getItems().removeAll(mealItemsToBeRemoved);
          mealListView.getView().setItems(mealListView.getView().getItems());
        }
      } else {
        createAlertDialog(AlertType.ERROR, "Meal list doesn't contain any food item").showAndWait()
            .filter(e -> e == ButtonType.OK);
      }
    }

    /**
     * Removes the items from meal food list.
     *
     * @param mealItemsToBeRemoved the meal items to be removed
     */
    private void removeItemsFromMealFoodList(ObservableList<Node> mealItemsToBeRemoved) {
      List<FoodItem> existingMealList = mealListView.getFoodItemList();
      List<FoodItem> foodItemsToBeRemovedFromMeal = new ArrayList<>();
      for (int i = 0; i < mealItemsToBeRemoved.size(); i++) {
        String mealItemToBeRemoved =
            ((Label) ((HBox) mealItemsToBeRemoved.get(i)).getChildren().get(0)).getText();
        for (int j = 0; j < existingMealList.size(); j++) {
          if (mealItemToBeRemoved.equalsIgnoreCase(existingMealList.get(j).getName())) {
            foodItemsToBeRemovedFromMeal.add(existingMealList.get(j));
          }
        }
      }
      mealListView.getFoodItemList().removeAll(foodItemsToBeRemovedFromMeal);
    }
  }

  /**
   * A custom event handler class to handle addition of selected food items to the meal list
   */
  private class MealListAddFoodEventHandler implements EventHandler<ActionEvent> {

    /*
     * (non-Javadoc)
     * 
     * @see javafx.event.EventHandler#handle(javafx.event.Event)
     */
    @Override
    public void handle(ActionEvent event) {
      if (!foodListView.getView().getSelectionModel().getSelectedItems().isEmpty()) {
        for (FoodItem foodItem : foodListView.getSelection()) {
          mealListView.addFoodItemToFoodItemList(foodItem);
          mealListView.addFoodItemToFoodListView(foodItem.getName());
        }
        foodListView.getView().getSelectionModel().clearSelection();
      } else {
        createAlertDialog(AlertType.ERROR, "No food items selected to be added to the meal list")
            .showAndWait().filter(e -> e == ButtonType.OK);
      }
    }
  }

  /**
   * A custom event handler class to handle creation of a new food item
   */
  private class CreateNewFoodItemHandler implements EventHandler<ActionEvent> {

    /*
     * (non-Javadoc)
     * 
     * @see javafx.event.EventHandler#handle(javafx.event.Event)
     */
    @Override
    public void handle(ActionEvent event) {
      new NewFoodItemWindow(primaryStage, foodListView, availableFoodsLabel, filterRulesForm,
          foodData);
    }

  }

  /**
   * A custom event handler class to handle showing of a pie-chart representation of the analysis of
   * food items present in the meal list
   */
  private class MealAnalysisHandler implements EventHandler<ActionEvent> {

    /*
     * (non-Javadoc)
     * 
     * @see javafx.event.EventHandler#handle(javafx.event.Event)
     */
    @Override
    public void handle(ActionEvent event) {
      new MealAnalysis(primaryStage, mealListView);
    }

  }
}
