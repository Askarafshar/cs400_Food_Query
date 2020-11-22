package application;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Represents the view of a FoodItem for putting into a listview.
 */
public class FoodItemView {

  private HBox foodItemHBox = null;
  private Label nameLabel = null;

  /**
   * Creates and returns the FoodItemView node to be used in the FoodListView in the PrimaryGUI.
   */
  public Node getNode() {
    foodItemHBox = new HBox();
    nameLabel = new Label();
    Label caloriesLabel = new Label();
    Label fatLabel = new Label();
    Label carbLabel = new Label();
    Label fiberLabel = new Label();
    Label proteinLabel = new Label();

    foodItemHBox.getChildren().addAll(nameLabel, caloriesLabel, fatLabel, carbLabel, fiberLabel,
        proteinLabel);
    foodItemHBox.setSpacing(5.0);
    return foodItemHBox;
  }

  public HBox getFoodItemHBox() {
    return foodItemHBox;
  }

  public Label getNameLabel() {
    return nameLabel;
  }

}
