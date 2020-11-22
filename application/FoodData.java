package application;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * This class represents the backend for managing all the operations associated with FoodItems.
 *
 * @author sapan (sapan@cs.wisc.edu)
 */
public class FoodData implements FoodDataADT<FoodItem> {

  // List of all the food items.
  private List<FoodItem> foodItemList;

  // Map of nutrients and their corresponding index
  private HashMap<String, BPTree<Double, FoodItem>> indexes;

  // The regex for matching a line in the food data file
  private static final String DATA_FORMAT_PATTERN =
      "[a-zA-Z0-9]*,[a-zA-Z0-9_]*,calories,(\\d*\\.)?\\d+,fat,(\\d*\\.)?\\d+,carbohydrate,(\\d*\\.)?\\d+,fiber,(\\d*\\.)?\\d+,protein,(\\d*\\.)?\\d+";


  /**
   * Public constructor.
   */
  public FoodData() {
    foodItemList = new ArrayList<>();
    indexes = new HashMap<>();
    indexes.put("calories", new BPTree<Double, FoodItem>(3));
    indexes.put("fat", new BPTree<Double, FoodItem>(3));
    indexes.put("carbohydrates", new BPTree<Double, FoodItem>(3));
    indexes.put("fiber", new BPTree<Double, FoodItem>(3));
    indexes.put("protein", new BPTree<Double, FoodItem>(3));
    loadFoodItems("foodItems.csv");
  }


  /*
   * (non-Javadoc) - load food items from an input file
   * 
   * @see skeleton.FoodDataADT#loadFoodItems(java.lang.String)
   */
  @Override
  public void loadFoodItems(String filePath) {
    try (Scanner scanner = new Scanner(new File(filePath));) {
      foodItemList = new ArrayList<>();
      indexes = new HashMap<>();
      indexes.put("calories", new BPTree<Double, FoodItem>(3));
      indexes.put("fat", new BPTree<Double, FoodItem>(3));
      indexes.put("carbohydrate", new BPTree<Double, FoodItem>(3));
      indexes.put("fiber", new BPTree<Double, FoodItem>(3));
      indexes.put("protein", new BPTree<Double, FoodItem>(3));

      FoodItem newFood;
      while (scanner.hasNext()) {
        String line = scanner.next();
        if (line.matches(DATA_FORMAT_PATTERN)) {
          String[] lineSplit = line.split(",");
          newFood = new FoodItem(lineSplit[0], lineSplit[1]);
          for (int i = 2; i < lineSplit.length; i += 2) {
            newFood.addNutrient(lineSplit[i], Double.valueOf(lineSplit[i + 1]));
          }
          foodItemList.add(newFood);

          indexes.get("calories").insert(newFood.getNutrientValue("calories"), newFood);
          indexes.get("fat").insert(newFood.getNutrientValue("fat"), newFood);
          indexes.get("carbohydrate").insert(newFood.getNutrientValue("carbohydrate"), newFood);
          indexes.get("fiber").insert(newFood.getNutrientValue("fiber"), newFood);
          indexes.get("protein").insert(newFood.getNutrientValue("protein"), newFood);

        }
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  /*
   * (non-Javadoc) - filter list of FoodItem according to given substring
   * 
   * @see skeleton.FoodDataADT#filterByName(java.lang.String)
   */
  @Override
  public List<FoodItem> filterByName(String substring) {
    List<FoodItem> nameFilteredFoodItemList = new ArrayList<>();
    for (FoodItem foodItem : foodItemList) {
      if (foodItem.getName().toLowerCase().contains(substring.toLowerCase())) {
        nameFilteredFoodItemList.add(foodItem);
      }
    }
    return nameFilteredFoodItemList;
  }

  /*
   * (non-Javadoc) - filter list of FoodItem according to given list of rules
   * 
   * @see skeleton.FoodDataADT#filterByNutrients(java.util.List)
   */
  @Override
  public List<FoodItem> filterByNutrients(List<String> rules) {
    List<Set<FoodItem>> filterSets = new ArrayList<>();

    // for each rule, build a set of all the items that match the rule
    for (String rule : rules) {
      String[] ruleSplit = rule.split(" ");
      filterSets.add(new HashSet<FoodItem>(indexes.get(ruleSplit[0].toLowerCase())
          .rangeSearch(Double.valueOf(ruleSplit[2]), ruleSplit[1])));
    }

    Set<FoodItem> intersectionSet = filterSets.get(0);

    for (int i = 1; i < filterSets.size(); i++) {
      intersectionSet.retainAll(filterSets.get(i));
    }

    return new ArrayList<>(intersectionSet);
  }

  /*
   * (non-Javadoc) - adds a food item to the foodItemList
   * 
   * @see skeleton.FoodDataADT#addFoodItem(skeleton.FoodItem)
   */
  @Override
  public void addFoodItem(FoodItem foodItem) {
    foodItemList.add(foodItem);
    indexes.get("calories").insert(foodItem.getNutrientValue("calories"), foodItem);
    indexes.get("fat").insert(foodItem.getNutrientValue("fat"), foodItem);
    indexes.get("carbohydrate").insert(foodItem.getNutrientValue("carbohydrate"), foodItem);
    indexes.get("fiber").insert(foodItem.getNutrientValue("fiber"), foodItem);
    indexes.get("protein").insert(foodItem.getNutrientValue("protein"), foodItem);
  }

  /*
   * (non-Javadoc) - returns all the food items present in foodItemList
   * 
   * @see skeleton.FoodDataADT#getAllFoodItems()
   */
  @Override
  public List<FoodItem> getAllFoodItems() {
    return new ArrayList<>(foodItemList);
  }

  /*
   * (non-Javadoc) - saved food items to an input file
   * 
   * @see application.FoodDataADT#saveFoodItems(java.lang.String)
   */
  @Override
  public void saveFoodItems(String fileName) {
    foodItemList.sort((FoodItem f1, FoodItem f2) -> f1.getName().compareTo(f2.getName()));
    try (PrintWriter pw = new PrintWriter(new File(fileName));) {
      StringBuilder sb = new StringBuilder();
      for (FoodItem foodItem : foodItemList) {
        sb.append(foodItem.getID()).append(",").append(foodItem.getName()).append(",")
            .append("calories").append(",").append((int) foodItem.getNutrientValue("calories"))
            .append(",").append("fat").append(",").append((int) foodItem.getNutrientValue("fat"))
            .append(",").append("carbohydrate").append(",")
            .append((int) foodItem.getNutrientValue("carbohydrate")).append(",").append("fiber")
            .append(",").append((int) foodItem.getNutrientValue("fiber")).append(",")
            .append("protein").append(",").append((int) foodItem.getNutrientValue("protein"))
            .append("\n");
      }
      pw.write(sb.toString());
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}
