package application;

import java.security.SecureRandom;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Represents the window that presents the user with a list of required fields to fill in to create
 * a new food item
 */
public class NewFoodItemWindow {

  private static final String POSITIVE_NUMBER_REGEX = "^(\\d*\\.)?\\d+$"; // Regex to match positive
                                                                          // numbers

  // Regex to match alphanumeric values and underscores for food names
  private static final String FOOD_NAME_REGEX = "^[A-Za-z0-9_]*$";
                                                                 
  // A string comprising of lower case alphabets and unique numeric digits used to generate a
  // unique alphanumeric ID
  private static final String UNIQUE_ID_CHARS = "0123456789abcdefghijklmnopqrstuvwxyz";

  private Stage newFoodItemWindowStage; // A reference to a Stage object to represent the
                                        // NewFoodItemWindow

  private FoodListView foodListView; // A reference to the FoodListView object - the listview
                                     // showing food items

  private Label availableFoodsLabel; // The Available Foods Label in the Primary GUI which maintains
                                     // a count of filtered vs total number of food items

  private FilterRulesForm filterRulesForm; // A reference to the FilterRulesForm object which
                                           // contains the different filter fields

  private FoodData foodData; // the food database

  private TextField foodId; // A textfield for the id of new food item to be created

  private TextField foodName; // A textfield to enter the name of new food item to be created

  private TextField foodCalories; // A textfield to enter the calories of new food item to be
                                  // created

  private TextField foodFat; // A textfield to enter the fat of new food item to be created

  private TextField foodCarbohydrates; // A textfield to enter the carbohydrates of new food item to
                                       // be created

  private TextField foodFiber; // A textfield to enter the fiber of new food item to be created

  private TextField foodProteins; // A textfield to enter the proteins of new food item to be
                                  // created

  static SecureRandom rnd = new SecureRandom(); // A SecureRandom object used in creation of a
                                                // unique ID

  /**
   * Method to create a unique alphanumeric ID-like string of specified input length
   *
   * @param len the length of string to be created
   * @return the string
   */
  static String randomString(int length) {
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++)
      sb.append(UNIQUE_ID_CHARS.charAt(rnd.nextInt(UNIQUE_ID_CHARS.length())));
    return sb.toString();
  }

  /**
   * Instantiates a new new food item window.
   *
   * @param primaryStage        the primary stage
   * @param foodListView        the food list view
   * @param availableFoodsLabel the available foods label
   * @param filterRulesForm     the filter rules form
   * @param foodData            the food data
   */
  public NewFoodItemWindow(Stage primaryStage, FoodListView foodListView, Label availableFoodsLabel,
      FilterRulesForm filterRulesForm, FoodData foodData) {
    this.foodListView = foodListView;
    this.availableFoodsLabel = availableFoodsLabel;
    this.filterRulesForm = filterRulesForm;
    this.foodData = foodData;

    newFoodItemWindowStage = new Stage();
    newFoodItemWindowStage.initOwner(primaryStage);
    newFoodItemWindowStage.initModality(Modality.WINDOW_MODAL);
    newFoodItemWindowStage.setTitle("Create New Food Item");

    newFoodItemWindowStage.setX(primaryStage.getX());
    newFoodItemWindowStage.setY(primaryStage.getY());

    GridPane newFoodItemProperties = new GridPane();
    VBox newFoodItemPropertiesVBox = new VBox();

    HBox foodIdHBox = new HBox();
    Label foodIdLabel = createLabelWithBoldFont("ID: ");
    foodId = new TextField();
    foodId.setMaxWidth(100);
    foodId.setText(randomString(24));
    foodId.setEditable(false);
    foodId.setDisable(true);
    foodIdHBox.getChildren().addAll(foodIdLabel, foodId);
    foodIdHBox.setSpacing(100);

    HBox foodNameHBox = new HBox();
    Label foodNameLabel = createLabelWithBoldFont("Name: ");
    foodName = new TextField();
    foodName.setMaxWidth(100);
    foodNameHBox.getChildren().addAll(foodNameLabel, foodName);
    foodNameHBox.setSpacing(80);

    HBox foodCaloriesHBox = new HBox();
    Label foodCaloriesLabel = createLabelWithBoldFont("Calories: ");
    foodCalories = new TextField();
    foodCalories.setMaxWidth(100);
    foodCaloriesHBox.getChildren().addAll(foodCaloriesLabel, foodCalories);
    foodCaloriesHBox.setSpacing(70);

    HBox foodFatHBox = new HBox();
    Label foodFatLabel = createLabelWithBoldFont("Fat: ");
    foodFat = new TextField();
    foodFat.setMaxWidth(100);
    foodFatHBox.getChildren().addAll(foodFatLabel, foodFat);
    foodFatHBox.setSpacing(100);

    HBox foodCarbsHBox = new HBox();
    Label foodCarbsLabel = createLabelWithBoldFont("Carbohydrates: ");
    foodCarbohydrates = new TextField();
    foodCarbohydrates.setMaxWidth(100);
    foodCarbsHBox.getChildren().addAll(foodCarbsLabel, foodCarbohydrates);
    foodCarbsHBox.setSpacing(35);

    HBox foodFiberHBox = new HBox();
    Label foodFiberLabel = createLabelWithBoldFont("Fiber: ");
    foodFiber = new TextField();
    foodFiber.setMaxWidth(100);
    foodFiberHBox.getChildren().addAll(foodFiberLabel, foodFiber);
    foodFiberHBox.setSpacing(90);

    HBox foodProteinHBox = new HBox();
    Label foodProteinLabel = createLabelWithBoldFont("Protein: ");
    foodProteins = new TextField();
    foodProteins.setMaxWidth(100);
    foodProteinHBox.getChildren().addAll(foodProteinLabel, foodProteins);
    foodProteinHBox.setSpacing(75);

    HBox createNewFoodHBox = new HBox();
    Button createNewFoodButton = new Button("Create");
    createNewFoodButton.setOnAction(new CreateNewFoodHandler());
    createNewFoodHBox.getChildren().add(createNewFoodButton);
    createNewFoodHBox.setAlignment(Pos.CENTER);

    newFoodItemPropertiesVBox.getChildren().addAll(foodIdHBox, foodNameHBox, foodCaloriesHBox,
        foodFatHBox, foodFiberHBox, foodCarbsHBox, foodProteinHBox, createNewFoodHBox);
    newFoodItemPropertiesVBox.setSpacing(10);
    newFoodItemPropertiesVBox.setStyle("-fx-padding: 10;");

    newFoodItemProperties.getChildren().add(newFoodItemPropertiesVBox);

    Scene createFoodItemScene = new Scene(newFoodItemProperties, 300, 300, Color.DARKGRAY);
    newFoodItemWindowStage.setScene(createFoodItemScene);
    newFoodItemWindowStage.show();
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
   * A custom event handler class to handle creation of a new food item
   */
  private class CreateNewFoodHandler implements EventHandler<ActionEvent> {

    /*
     * (non-Javadoc)
     * 
     * @see javafx.event.EventHandler#handle(javafx.event.Event)
     */
    @Override
    public void handle(ActionEvent event) {

      if (!checkIfRequiredFieldsAreNotEmpty()){
        createAlertDialog(AlertType.ERROR, 
        "All fields are required. Please fill them out and try again.")
        .showAndWait().filter(e -> e == ButtonType.OK);
        return;
      }

      if (!validateEnteredDataInAllRequiredFields()){
        createAlertDialog(AlertType.ERROR, 
        "Food names may only contain letters, numbers, and underscores(_).\n"+
        "Nutrient values may only contain numbers.")
        .showAndWait().filter(e -> e == ButtonType.OK);
        return;
      }

      FoodItem newFoodItem = createNewFoodItem();
        foodData.addFoodItem(newFoodItem);
        foodListView.addFoodItemToFoodItemList(newFoodItem);
        if (!foodListView.isFiltered()) {
          foodListView.addFoodItemToFoodListView(newFoodItem.getName());
        } else {
          filterRulesForm.applyAllFilters();
        }
        availableFoodsLabel.setText(
            "Available Foods".concat(" (Count = " + Integer.toString(getFilteredFoodItems().size())
                + "/" + Integer.toString(foodListView.getFoodItemList().size()) + ")"));
        NewFoodItemWindow.this.newFoodItemWindowStage.close();
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
     * Creates a new food item.
     *
     * @return the food item
     */
    private FoodItem createNewFoodItem() {
      FoodItem newFoodItem = new FoodItem(foodId.getText(), foodName.getText());
      if (newFoodItem.getNutrients().isEmpty()) {
        newFoodItem.getNutrients().put("calories", Double.valueOf(foodCalories.getText()));
        newFoodItem.getNutrients().put("fat", Double.valueOf(foodFat.getText()));
        newFoodItem.getNutrients().put("carbohydrate", Double.valueOf(foodCarbohydrates.getText()));
        newFoodItem.getNutrients().put("fiber", Double.valueOf(foodFiber.getText()));
        newFoodItem.getNutrients().put("protein", Double.valueOf(foodProteins.getText()));
      }
      return newFoodItem;
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
     * Validate entered data in all required fields.
     *
     * @return true, if successful
     */
    private boolean validateEnteredDataInAllRequiredFields() {
      return checkIfEnteredDataMatchesPositiveNumberRegex(foodCalories.getText())
          && checkIfEnteredDataMatchesPositiveNumberRegex(foodFat.getText())
          && checkIfEnteredDataMatchesPositiveNumberRegex(foodFiber.getText())
          && checkIfEnteredDataMatchesPositiveNumberRegex(foodCarbohydrates.getText())
          && checkIfEnteredDataMatchesPositiveNumberRegex(foodProteins.getText())
          && matchesNameRegex(foodName.getText());
    }

    /**
     * Check if required fields are not empty.
     *
     * @return true, if successful
     */
    private boolean checkIfRequiredFieldsAreNotEmpty() {
      return isStringNotEmpty(foodName.getText()) && isStringNotEmpty(foodCalories.getText())
          && isStringNotEmpty(foodFat.getText()) && isStringNotEmpty(foodFiber.getText())
          && isStringNotEmpty(foodCarbohydrates.getText())
          && isStringNotEmpty(foodProteins.getText());
    }

    /**
     * Check if entered data matches positive number regex.
     *
     * @param input the input
     * @return true, if successful
     */
    private boolean checkIfEnteredDataMatchesPositiveNumberRegex(String input) {
      return input.matches(POSITIVE_NUMBER_REGEX);
    }

    /**
     * Checks if string is not empty.
     *
     * @param string the string
     * @return true, if is string not empty
     */
    private boolean isStringNotEmpty(String string) {
      return string != null && !string.isEmpty();
    }
  }

  /**
   * Check if a string matches the food name regex.
   * 
   * @param string, string to test
   * @return true, if matches the regex
   */
  private boolean matchesNameRegex(String string) {
    return string.matches(FOOD_NAME_REGEX);
  }
}
