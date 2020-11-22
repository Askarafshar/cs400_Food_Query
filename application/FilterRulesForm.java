package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 * Represents the form for filtering FoodItems. Composed of many UI elements.
 */
public class FilterRulesForm {

  private static final String FILTER_QUERY_VALUE_REGEX = "^(\\d*\\.)?\\d+$";

  private FoodData foodData; // the food database

  private FoodListView foodListView; // the listview showing food items

  private Label availableFoodsLabel; // label showing how many foods are being shown

  private List<FoodItem> filteredFoodItems; // a list of food items that satify the filters

  private GridPane filterRulesGridPane; // gridpane to display everything

  private TextField foodItemSearchTextField; // text field to search by name

  private ComboBox<Nutrient> nutrientSelectionComboBox; // nutrient to filter by

  private ComboBox<String> comparatorSelectionComboBox; // >=, ==, or <=

  private TextField nutrientValueTextField; // value of the nutrient

  private Button createFilterButton; // Button to create a filter query

  private Button clearFiltersButton; // Button to clear all the filter queries

  private Button applyFiltersButton; // Button to apply the created filter queries

  private SimpleEntry<String, Boolean> nameRule; // A name value pair to store the information
                                                 // related to the active name query

  private Map<String, Boolean> nutrientRulesMap; // A map to store the information related to the
                                                 // active nutrient queries

  private ListView<String> filtersListView; // list view for displaying filters

  /**
   * Creates the needed UI elements for a FilterRulesForm.
   *
   * @param foodData            - the food database object
   * @param foodListView        - the listview of the food items
   * @param availableFoodsLabel - the label of available foods to update when filters are applied
   */
  public FilterRulesForm(FoodData foodData, FoodListView foodListView, Label availableFoodsLabel) {
    this.foodData = foodData;
    this.foodListView = foodListView;
    this.availableFoodsLabel = availableFoodsLabel;
    nutrientRulesMap = new HashMap<>();

    filterRulesGridPane = new GridPane();
    VBox filterRulesVBox = new VBox();
    VBox nameFilterVBox = new VBox();
    HBox nameFilterLabelHBox = new HBox();
    Label nameFilterLabel = new Label("Name Filter");
    nameFilterLabel.setStyle("-fx-underline: true; -fx-font-weight: bold;");
    nameFilterLabelHBox.getChildren().addAll(nameFilterLabel);

    HBox foodItemNameHBox = new HBox();
    Label foodItemNameLabel = new Label("Name: ");
    foodItemSearchTextField = new TextField();
    foodItemSearchTextField.setPromptText("Food item(s) to filter");
    foodItemNameHBox.getChildren().addAll(foodItemNameLabel, foodItemSearchTextField);
    foodItemNameHBox.setSpacing(1);

    nameFilterVBox.getChildren().addAll(nameFilterLabelHBox, foodItemNameHBox);
    nameFilterVBox.setSpacing(2);
    VBox nutrientFilterVBox = new VBox();
    HBox nutrientFilterLabelHBox = new HBox();
    Label nutrientFilterLabel = new Label("Nutrient Filter");
    nutrientFilterLabel.setStyle("-fx-underline: true; -fx-font-weight: bold;");
    nutrientFilterLabelHBox.getChildren().addAll(nutrientFilterLabel);


    HBox nutrientSelectionHBox = new HBox();
    Label nutrientSelectionLabel = new Label("Nutrient: ");
    nutrientSelectionComboBox = new ComboBox<>();
    nutrientSelectionComboBox
        .setItems(FXCollections.observableList(Arrays.asList(Nutrient.values())));
    nutrientSelectionComboBox.setMaxWidth(100);
    nutrientSelectionHBox.getChildren().addAll(nutrientSelectionLabel, nutrientSelectionComboBox);
    nutrientSelectionHBox.setSpacing(40);


    HBox comparatorSelectionHBox = new HBox();
    Label comparatorSelectionLabel = new Label("Comparator: ");
    comparatorSelectionLabel.setTextAlignment(TextAlignment.JUSTIFY);
    comparatorSelectionComboBox = new ComboBox<>();
    comparatorSelectionComboBox
        .setItems(FXCollections.observableList(Arrays.asList(">=", "<=", "==")));
    comparatorSelectionHBox.getChildren().addAll(comparatorSelectionLabel,
        comparatorSelectionComboBox);
    comparatorSelectionHBox.setSpacing(55);


    HBox valueHBox = new HBox();
    Label valueLabel = new Label("Value: ");
    nutrientValueTextField = new TextField();
    nutrientValueTextField.setMaxWidth(100);
    valueHBox.getChildren().addAll(valueLabel, nutrientValueTextField);
    valueHBox.setSpacing(54);

    nutrientFilterVBox.getChildren().addAll(nutrientFilterLabelHBox, nutrientSelectionHBox,
        comparatorSelectionHBox, valueHBox);
    nutrientFilterVBox.setSpacing(3);


    HBox buttonsHBox = new HBox();
    createFilterButton = new Button("Create Filter");
    createFilterButton.setOnAction(new CreateFilterButtonHandler());

    clearFiltersButton = new Button("Clear Filters");
    clearFiltersButton.setOnAction(new ClearFilterButtonHandler());

    applyFiltersButton = new Button("Apply Filters");
    applyFiltersButton.setOnAction(new ApplyFilterButtonHandler());


    buttonsHBox.getChildren().addAll(createFilterButton, applyFiltersButton, clearFiltersButton);
    buttonsHBox.setSpacing(10);
    buttonsHBox.setAlignment(Pos.CENTER);


    VBox activeAppliedFiltersVBox = new VBox();
    HBox activeAppliedFilterLabelHBox = new HBox();
    Label activeAppliedFiltersLabel = new Label("Active Filters");
    activeAppliedFiltersLabel.setStyle("-fx-font-weight: bold;");
    activeAppliedFilterLabelHBox.getChildren().add(activeAppliedFiltersLabel);
    activeAppliedFilterLabelHBox.setAlignment(Pos.CENTER);
    filtersListView = new ListView<>();
    activeAppliedFiltersVBox.getChildren().addAll(activeAppliedFilterLabelHBox, filtersListView);
    activeAppliedFiltersVBox.setSpacing(1);

    filterRulesVBox.getChildren().addAll(nameFilterVBox, nutrientFilterVBox, buttonsHBox,
        activeAppliedFiltersVBox);
    filterRulesVBox.setSpacing(5);


    filterRulesVBox.setStyle("-fx-padding: 10;");
    filterRulesGridPane.getChildren().addAll(filterRulesVBox);
  }

  /**
   * Get the GridPane that contains all of the UI Elements.
   *
   * @return the filter rules grid pane
   */
  public GridPane getFilterRulesGridPane() {
    return filterRulesGridPane;
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
   * Clear all filter query fields.
   */
  public void clearAllFilterQueryFields() {
    foodItemSearchTextField.setText(null);
    nutrientSelectionComboBox.setValue(null);
    comparatorSelectionComboBox.setValue(null);
    nutrientValueTextField.setText(null);
  }

  /**
   * Clear active filters list view.
   */
  public void clearActiveFiltersListView() {
    filtersListView.getItems().clear();
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
   * A custom event handler class to handle creation of a new filter query
   */
  private class CreateFilterButtonHandler implements EventHandler<ActionEvent> {

    /*
     * (non-Javadoc)
     * 
     * @see javafx.event.EventHandler#handle(javafx.event.Event)
     */
    @Override
    public void handle(ActionEvent event) {
      if (nameRule != null && isStringNotEmpty(foodItemSearchTextField.getText())) {
        createAlertDialog(AlertType.ERROR, "Only one name filter is allowed at a time.")
            .showAndWait().filter(e -> e == ButtonType.OK);
        foodItemSearchTextField.setText(null);
        return;
      }

      Boolean validNutrientFilter = checkForValidNutrientFilter();
      Boolean validNameFilter = checkForValidNameFilter();

      if (!validNameFilter && !validNutrientFilter) {
        createAlertDialog(AlertType.ERROR, "Create at least one filter!").showAndWait()
            .filter(e -> e == ButtonType.OK);
      }
      clearAllFilterQueryFields();
    }

    /**
     * Check for validity of the created nutrient filter.
     *
     * @return true, if valid
     */
    private boolean checkForValidNutrientFilter() {
      if (nutrientSelectionComboBox.getValue() != null
          || comparatorSelectionComboBox.getValue() != null
          || isStringNotEmpty(nutrientValueTextField.getText())) {
        if (nutrientSelectionComboBox.getValue() != null
            && comparatorSelectionComboBox.getValue() != null
            && isStringNotEmpty(nutrientValueTextField.getText())) {
          addNutrientRule();
          return true;
        } else {
          createAlertDialog(AlertType.ERROR, "Please select all required filter criteria")
              .showAndWait().filter(e -> e == ButtonType.OK);

        }
      }
      return false;
    }

    /**
     * Check for validity of created name filter.
     *
     * @return true, if valid
     */
    private boolean checkForValidNameFilter() {
      if (isStringNotEmpty(foodItemSearchTextField.getText())) {
        addNameRule();
        return true;
      }
      return false;
    }

    /**
     * Adds the created name rule.
     */
    private void addNameRule() {
      nameRule = new SimpleEntry<>(foodItemSearchTextField.getText(), false);
      filtersListView.getItems().add("Food items containing: " + foodItemSearchTextField.getText());
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

    /**
     * Adds the created nutrient rule.
     */
    private void addNutrientRule() {
      if (nutrientValueTextField.getText().matches(FILTER_QUERY_VALUE_REGEX)) {
        // build the nutrient filter string
        String nutrientFilterString = nutrientSelectionComboBox.getValue().getNutrient();
        nutrientFilterString += " " + comparatorSelectionComboBox.getValue();
        nutrientFilterString += " " + nutrientValueTextField.getText();

        nutrientRulesMap.put(nutrientFilterString, false);
        filtersListView.getItems().add(nutrientFilterString);

      } else {
        createAlertDialog(AlertType.ERROR, "Please enter valid numeric input").showAndWait()
            .filter(e -> e == ButtonType.OK);
      }
    }
  }

  /**
   * A custom event handler class to handle clearing of all the filter query fields - involves
   * populating foodListView with all the available food items
   */
  private class ClearFilterButtonHandler implements EventHandler<ActionEvent> {

    /*
     * (non-Javadoc)
     * 
     * @see javafx.event.EventHandler#handle(javafx.event.Event)
     */
    @Override
    public void handle(ActionEvent event) {
      clearAllFilterQueryFields();
      clearActiveFiltersListView();
      nutrientRulesMap.clear();
      nameRule = null;
      foodListView.setFilteredFoodItemList(null);
      foodListView.setFiltered(false);
      foodListView.getView().getItems().clear();
      for (int i = 0; i < foodListView.getFoodItemList().size(); i++) {
        foodListView.addFoodItemToFoodListView(foodListView.getFoodItemList().get(i).getName());
      }
      availableFoodsLabel.setText(
          "Available Foods".concat(" (Count = " + Integer.toString(getFilteredFoodItems().size())
              + "/" + Integer.toString(foodListView.getFoodItemList().size()) + ")"));
    }
  }

  /**
   * A custom event handler class to handle application of the created filter queries to the food
   * list view
   */
  private class ApplyFilterButtonHandler implements EventHandler<ActionEvent> {

    /*
     * (non-Javadoc)
     * 
     * @see javafx.event.EventHandler#handle(javafx.event.Event)
     */
    @Override
    public void handle(ActionEvent event) {
      if (nameRule == null && nutrientRulesMap.isEmpty()) {
        createAlertDialog(AlertType.ERROR, "You must create a filter first!").showAndWait()
            .filter(e -> e == ButtonType.OK);
        return;
      }
      if (nameRule != null) {
        nameRule.setValue(true);
      }
      for (Map.Entry<String, Boolean> rule : nutrientRulesMap.entrySet()) {
        rule.setValue(true);
      }
      applyAllFilters();
      for (int i = 0; i < filtersListView.getItems().size(); i++) {
        String current = filtersListView.getItems().get(i);
        if (!current.endsWith("(Applied)")) {
          filtersListView.getItems().set(i, current + " (Applied)");
        }
      }
    }
  }

  /**
   * Apply all created filters.
   */
  public void applyAllFilters() {
    filteredFoodItems = new ArrayList<>();
    filteredFoodItems = applyNameFilter();
    filteredFoodItems.retainAll(applyNutrientFilters());
    foodListView.setFilteredFoodItemList(filteredFoodItems);
    foodListView.setFiltered(true);
    availableFoodsLabel.setText(
        "Available Foods".concat(" (Count = " + Integer.toString(getFilteredFoodItems().size())
            + "/" + Integer.toString(foodListView.getFoodItemList().size()) + ")"));
  }

  /**
   * Apply name filter.
   *
   * @return the list
   */
  private List<FoodItem> applyNameFilter() {
    if (nameRule != null && nameRule.getValue()) {
      return foodData.filterByName(nameRule.getKey());
    } else {
      return foodData.getAllFoodItems();
    }
  }

  /**
   * Apply nutrient filters.
   *
   * @return the list
   */
  private List<FoodItem> applyNutrientFilters() {
    if (!nutrientRulesMap.isEmpty() && nutrientRulesMap.containsValue(true)) {
      List<String> ruleList = new ArrayList<>();
      for (Map.Entry<String, Boolean> rule : nutrientRulesMap.entrySet()) {
        if (rule.getValue()) {
          ruleList.add(rule.getKey());
        }
      }
      return foodData.filterByNutrients(ruleList);
    } else {
      return foodData.getAllFoodItems();
    }
  }
}
