package application;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Represents the window for analyzing the contents of a meal. Has a table
 * listing all the food items in the meal and their nutritional information.
 * Also has a pie chart that shows the caloric breakdown and a table listing
 * the totals for each nutrient.
 * 
 * @author: Harrison Clark
 */
public class MealAnalysis {

  private PieChart calorieBreakdown;
  private FoodListView mealListView;
  private Stage mealAnalysisWindow;
  
  private final double CALORIES_PER_FAT = 9.0;
  private final double CALORIES_PER_CARB = 4.0;
  private final double CALORIES_PER_PROTEIN = 4.0;

  private double totalCalories = 0.0;
  private double totalFat = 0.0;
  private double totalCarbs = 0.0;
  private double totalFiber = 0.0;
  private double totalProteins = 0.0;

  private TableView<FoodItem> nutrientTable;

  /**
   * Creates a new scene on the given stage. Using the given FoodListView, 
   * the contructor builds a TableView of all the nutrients, builds a pie chart
   * of the caloric breakdown of fat, carbs, and protein, and builds a table
   * with the totals for each nutrient in the meal.
   * 
   * @param primaryStage - the stage to attach this window to
   * @param mealListView - a foodListView of the meal
   */

  public MealAnalysis(Stage primaryStage, FoodListView mealListView) {
    this.mealListView = mealListView;
    mealAnalysisWindow = new Stage();
    mealAnalysisWindow.initOwner(primaryStage);
    mealAnalysisWindow.initModality(Modality.WINDOW_MODAL);
    mealAnalysisWindow.setTitle("Meal Analysis");
    VBox mainLayoutVBox = new VBox();
    HBox lowerHBox = new HBox();
    VBox pieChartVBox = new VBox();
    VBox totalsVBox = new VBox();
    VBox totalLabelVBox = new VBox();


    boolean canNewWindowOpen = setValueOfPieChartData();

    if (canNewWindowOpen) {
      calorieBreakdown = new PieChart();

      PieChart.Data fats = new PieChart.Data("Fat", totalFat * CALORIES_PER_FAT);
      PieChart.Data carbohydrates = new PieChart.Data("Carbohydrates", totalCarbs * CALORIES_PER_CARB);
      PieChart.Data proteins = new PieChart.Data("Protein", totalProteins * CALORIES_PER_PROTEIN);

      calorieBreakdown.getData().add(fats);
      calorieBreakdown.getData().add(carbohydrates);
      calorieBreakdown.getData().add(proteins);

      calorieBreakdown.setLegendVisible(false);
      buildNutrientTable();
      
      Label calorieBreakdownLabel = new Label("Calorie Breakdown");
      Label totalNutrientsLabel = new Label("Total Nutrients");

      Label totalCaloriesLabel = new Label("Total Calories: " + totalCalories);
      Label totalFatLabel = new Label("Total Fat: " + totalFat);
      Label totalCarbsLabel = new Label("Total Carbohydrates: " + totalCarbs);
      Label totalFiberLabel = new Label("Total Fiber: " + totalFiber);
      Label totalProteinLabel = new Label("Total Protein: " + totalProteins);

      
      totalsVBox.getChildren().addAll(totalCaloriesLabel, totalFatLabel, 
      totalCarbsLabel, totalFiberLabel, totalProteinLabel);
      totalsVBox.setAlignment(Pos.CENTER_LEFT);
      totalLabelVBox.getChildren().addAll(totalNutrientsLabel, totalsVBox);
      totalLabelVBox.setAlignment(Pos.TOP_CENTER);

      mainLayoutVBox.getChildren().add(nutrientTable);
      pieChartVBox.getChildren().addAll(calorieBreakdownLabel, calorieBreakdown);
      pieChartVBox.setAlignment(Pos.CENTER);
      lowerHBox.getChildren().addAll(pieChartVBox, totalLabelVBox);
      mainLayoutVBox.getChildren().add(lowerHBox);
      

      
      mainLayoutVBox.setSpacing(10);
      Scene pieScene = new Scene(mainLayoutVBox, 800, 600);
      pieScene.getStylesheets().add("mealAnalysis.css");
      mealAnalysisWindow.setScene(pieScene);
      mealAnalysisWindow.show();
    }
  }

  private boolean setValueOfPieChartData() {
    boolean canMealAnalysisWindowOpen = true;
    if (mealListView.getView().getItems() != null && !mealListView.getView().getItems().isEmpty()) {
      //go through the meal list and sum the nutrients
      for (FoodItem mealFoodItem : mealListView.getFoodItemList()) {
        totalCalories += mealFoodItem.getNutrientValue("calories");
        totalCarbs += mealFoodItem.getNutrientValue("carbohydrate");
        totalFat += mealFoodItem.getNutrientValue("fat");
        totalFiber += mealFoodItem.getNutrientValue("fiber");
        totalProteins += mealFoodItem.getNutrientValue("protein");
      }
    } 
    
    else {
      canMealAnalysisWindowOpen = false;
      createAlertDialog(AlertType.ERROR, "Meal list doesn't contain any food items").showAndWait()
          .filter(e -> e == ButtonType.OK);
    }
    return canMealAnalysisWindowOpen;
  }

  private void buildNutrientTable(){
    nutrientTable = new TableView<FoodItem>();
    
    ObservableList<FoodItem> obsFoodItems = FXCollections.observableArrayList();
    obsFoodItems.addAll(mealListView.getFoodItemList());
    nutrientTable.setItems(obsFoodItems);

    TableColumn<FoodItem, String> foodNameColumn = new TableColumn<FoodItem, String>("Food");
    foodNameColumn.setCellValueFactory(new PropertyValueFactory("name"));

    TableColumn<FoodItem, Double> caloriesColumn = new TableColumn<FoodItem, Double>("Calories");
    caloriesColumn.setCellValueFactory(new Callback<CellDataFeatures<FoodItem, Double>, ObservableValue<Double>>() {
      public ObservableValue<Double> call(CellDataFeatures<FoodItem, Double> p){
        ObservableValue<Double> obs = new SimpleDoubleProperty(p.getValue().getNutrientValue("calories")).asObject();
        return obs;
      }
    });
    caloriesColumn.setPrefWidth(caloriesColumn.getText().length()*20);

    TableColumn<FoodItem, Double> fatColumn = new TableColumn<FoodItem, Double>("Fat");
    fatColumn.setCellValueFactory(new Callback<CellDataFeatures<FoodItem, Double>, ObservableValue<Double>>() {
      public ObservableValue<Double> call(CellDataFeatures<FoodItem, Double> p){
        ObservableValue<Double> obs = new SimpleDoubleProperty(p.getValue().getNutrientValue("fat")).asObject();
        return obs;
      }
    });
    TableColumn<FoodItem, Double> carbohydrateCoulumn = new TableColumn<FoodItem, Double>("Carbohydrates");
    carbohydrateCoulumn.setCellValueFactory(new Callback<CellDataFeatures<FoodItem, Double>, ObservableValue<Double>>() {
      public ObservableValue<Double> call(CellDataFeatures<FoodItem, Double> p){
        ObservableValue<Double> obs = new SimpleDoubleProperty(p.getValue().getNutrientValue("carbohydrate")).asObject();
        return obs;
      }
    });
    TableColumn<FoodItem, Double> fiberColumn = new TableColumn<FoodItem, Double>("Fiber");
    fiberColumn.setCellValueFactory(new Callback<CellDataFeatures<FoodItem, Double>, ObservableValue<Double>>() {
      public ObservableValue<Double> call(CellDataFeatures<FoodItem, Double> p){
        ObservableValue<Double> obs = new SimpleDoubleProperty(p.getValue().getNutrientValue("fiber")).asObject();
        return obs;
      }
    });
    TableColumn<FoodItem, Double> proteinColumn = new TableColumn<FoodItem, Double>("Protein");
    proteinColumn.setCellValueFactory(new Callback<CellDataFeatures<FoodItem, Double>, ObservableValue<Double>>() {
      public ObservableValue<Double> call(CellDataFeatures<FoodItem, Double> p){
        ObservableValue<Double> obs = new SimpleDoubleProperty(p.getValue().getNutrientValue("protein")).asObject();
        return obs;
      }
    });

    nutrientTable.getColumns().addAll(foodNameColumn, caloriesColumn, fatColumn, carbohydrateCoulumn, fiberColumn, proteinColumn);

    nutrientTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    
  }

  public PieChart getPieChart() {
    return calorieBreakdown;
  }

  private Alert createAlertDialog(AlertType alertType, String message) {
    return new Alert(alertType, message);
  }
}


