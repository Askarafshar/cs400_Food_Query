package application;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;

/**
 * Abstracts a list of food items. Supports deletion & selection of items.
 */
public class FoodListView {
  private ListView<Node> foodItemListView;
  private List<FoodItem> foodItemList;
  private List<FoodItem> filteredFoodItemList;
  private boolean isFiltered;

  /**
   * Initialize the FoodListView by making an empty listview and arraylist. Allow
   * selection of multiple items.
   */
  public FoodListView() {
    foodItemListView = new ListView<>();
    foodItemList = new ArrayList<>();

    foodItemListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

  }

  /**
   * Gets the list of food items that is currently selected in the ListView.
   * 
   * @return - the list of food items that is currently selected
   */

  public List<FoodItem> getSelection() {
    ObservableList<Node> selectedNodes = foodItemListView.getSelectionModel().getSelectedItems();
    List<String> selectedFoodNames = new ArrayList<>();
    List<FoodItem> selectedFoods = new ArrayList<>();

    // get the names of all the selected foods
    for (Node node : selectedNodes) {
      HBox asHbox = (HBox) node;
      Label nameLabel = (Label) asHbox.getChildren().get(0);
      selectedFoodNames.add(nameLabel.getText());
    }

    // go through the food list and add the ones that are selected
    for (FoodItem foodItem : foodItemList) {
      if (selectedFoodNames.contains(foodItem.getName())) {
        selectedFoods.add(foodItem);
      }
    }
    return selectedFoods;

  }

  /**
   * Adds a new food item to the array list of food items.
   * 
   * @param foodItem - new item to add
   */
  public void addFoodItemToFoodItemList(FoodItem foodItem) {
    foodItemList.add(foodItem);
  }

  /**
   * Creates a foodItemView object based on the given foodItemName and adds it
   * to the listView.
   * 
   * @param foodItemName - name of a food
   */
  public void addFoodItemToFoodListView(String foodItemName) {
    FoodItemView fiv = new FoodItemView();
    HBox foodHBox = (HBox) fiv.getNode();

    Label nameLabel = (Label) foodHBox.getChildren().get(0);
    nameLabel.setText(foodItemName);

    foodItemListView.getItems().add(foodHBox);
    foodItemListView.getItems().sort(new Comparator<Node>() {
      @Override
      public int compare(Node n1, Node n2) {
        HBox hbox1 = (HBox) n1;
        HBox hbox2 = (HBox) n2;

        Label lbl1 = (Label) hbox1.getChildren().get(0);
        Label lbl2 = (Label) hbox2.getChildren().get(0);

        return lbl1.getText().toLowerCase().compareTo(lbl2.getText().toLowerCase());
      }
    });
  }

  /**
   * Clears the list view.
   */
  public void reset() {
    foodItemListView.getItems().clear();
  }

  /**
   * Getter for the list view
   * @return - food list view
   */
  public ListView<Node> getView() {
    return foodItemListView;
  }

  /**
   * Getter for the list of food items.
   * @return - list of food items
   */
  public List<FoodItem> getFoodItemList() {
    return foodItemList;
  }

  /**
   * Is the listview currently filtered?
   * @return true if the listview is filtered, otherwise false
   */
  public boolean isFiltered() {
    return isFiltered;
  }

  /**
   * Set the list view to filtered. If true is passed, update the list view
   * to the contents of the filtered food list. If false is passed,
   * reset the food list view and add the original items back to the view.
   * @param isFiltered
   */
  public void setFiltered(boolean isFiltered) {
    this.isFiltered = isFiltered;
    if (this.isFiltered){
      this.reset();
      for (FoodItem fi: filteredFoodItemList){
        this.addFoodItemToFoodListView(fi.getName());
      }
    }
    else {
      this.reset();
      for (FoodItem fi: foodItemList){
        this.addFoodItemToFoodListView(fi.getName());
      }
    }
  }

  /**
   * Getter for the filtered food list
   * @return - the filtred food list
   */
  public List<FoodItem> getFilteredFoodItemList() {
    return filteredFoodItemList;
  }

  /**
   * Setter for the filtered food list.
   * @param filteredFoodItemList - sets the filtered list to this new list.
   */
  public void setFilteredFoodItemList(List<FoodItem> filteredFoodItemList) {
      this.filteredFoodItemList = filteredFoodItemList;
  }

  /**
   * Setter for the food list
   * @param foodItemList - change the food list to this
   */
  public void setFoodItemList(List<FoodItem> foodItemList) {
    this.foodItemList = foodItemList;
  }

  /**
   * Adds the given food item to the filtered list
   * @param foodItem
   */
  public void addFoodItemToFilteredFoodItemsList(FoodItem foodItem) {
    if (isFiltered) {
      filteredFoodItemList.add(foodItem);
    }
  }

}
