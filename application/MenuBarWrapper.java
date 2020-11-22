package application;

import java.io.File;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Abstracts the menu bar for the GUI. Has operations for loading and saving a csv file of food
 * items.
 */
public class MenuBarWrapper {
  private FoodData foodData;
  private Stage primaryStage;
  private FoodListView foodListView;
  private FilterRulesForm filterRulesForm;
  private Label availableFoodsLabel;

  /**
   * Constructor for the MenuBarWrapper. Initializes the needed food data and JavaFX objects to
   * facilitate loading and saving.
   * 
   * @param primaryGUI - the gui object this menubar will be part of
   */
  public MenuBarWrapper(PrimaryGUI primaryGUI) {
    this.foodData = primaryGUI.getFoodData();
    this.primaryStage = primaryGUI.getPrimaryStage();
    this.foodListView = primaryGUI.getFoodListView();
    this.filterRulesForm = primaryGUI.getFilterRulesForm();
    this.availableFoodsLabel = primaryGUI.getAvailableFoodsLabel();
  }

  /**
   * Creates the menu bar node and returns it.
   */
  public Node getNode() {
    MenuBar menuBar = new MenuBar();
    Menu menu = new Menu("Menu");
    MenuItem loadFileMenuItem = new MenuItem("Load File");
    loadFileMenuItem.setOnAction(new LoadFileMenuItemActionHandler());
    ImageView loadMenuImage = createMenuItemImage("file:open-file.png");
    loadFileMenuItem.setGraphic(loadMenuImage);
    MenuItem saveFileMenuItem = new MenuItem("Save File");
    saveFileMenuItem.setOnAction(new SaveFileMenuItemActionHandler());
    ImageView saveMenuImage = createMenuItemImage("file:save-file.jpg");
    saveFileMenuItem.setGraphic(saveMenuImage);
    menu.getItems().addAll(loadFileMenuItem, saveFileMenuItem);
    menuBar.getMenus().add(menu);
    return menuBar;
  }

  private Alert createAlertDialog(AlertType alertType, String message) {
    return new Alert(alertType, message);
  }

  private ImageView createMenuItemImage(String imageFileName) {
    ImageView menuItemImage = new ImageView(imageFileName);
    menuItemImage.setFitHeight(30);
    menuItemImage.setFitWidth(30);
    menuItemImage.setPreserveRatio(true);
    return menuItemImage;
  }

  /**
   * Event handler for loading a csv file. Prompts the user for a csv file and attempts to parse it
   * into the FoodItems list.
   */
  private class LoadFileMenuItemActionHandler implements EventHandler<ActionEvent> {

    @Override
    public void handle(ActionEvent event) {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Load From");
      fileChooser.setInitialDirectory(new File(new File(".").getAbsolutePath()));
      fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV", "*.csv"));
      File selectedFile = fileChooser.showOpenDialog(primaryStage);
      if (selectedFile != null) {
        filterRulesForm.clearActiveFiltersListView();
        filterRulesForm.clearAllFilterQueryFields();
        foodData.loadFoodItems(selectedFile.getPath());
        foodListView.setFilteredFoodItemList(null);
        foodListView.getFoodItemList().clear();
        foodListView.getView().getItems().clear();
        for (FoodItem foodItem : foodData.getAllFoodItems()) {
          foodListView.addFoodItemToFoodItemList(foodItem);
          foodListView.addFoodItemToFoodListView(foodItem.getName());
        }
        int numberofFoodItems = foodListView.getFoodItemList().size();
        availableFoodsLabel
            .setText("Available Foods".concat(" (Count = " + Integer.toString(numberofFoodItems)
                + "/" + Integer.toString(numberofFoodItems) + ")"));
        createAlertDialog(AlertType.INFORMATION,
            "Food items from " + selectedFile.getName() + " loaded successfully").show();
      }
    }
  }

  /**
   * Event handler for saving the current foodlist as a csv.
   */
  private class SaveFileMenuItemActionHandler implements EventHandler<ActionEvent> {

    @Override
    public void handle(ActionEvent event) {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Save To");
      fileChooser.setInitialDirectory(new File(new File(".").getAbsolutePath()));
      fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV", "*.csv"));
      File selectedFile = fileChooser.showSaveDialog(primaryStage);
      if (selectedFile != null) {
        foodData.saveFoodItems(selectedFile.getAbsolutePath());
        createAlertDialog(AlertType.INFORMATION,
            "Available food items saved to " + selectedFile.getName() + " successfully").show();
      }
    }
  }

}
