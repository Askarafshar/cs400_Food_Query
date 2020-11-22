package application;

/**
 * The Enum Nutrient.
 */
public enum Nutrient {

  FATS("Fat"), PROTEINS("Protein"), CARBOHYDRATES("Carbohydrate"), FIBER("Fiber"), CALORIES(
      "Calories");

  /** The nutrient type. */
  private String nutrientType;

  /**
   * Instantiates a new nutrient.
   *
   * @param nutrient the nutrient
   */
  Nutrient(String nutrient) {
    this.nutrientType = nutrient;
  }

  /**
   * Gets the nutrient type
   *
   * @return the nutrient
   */
  public String getNutrient() {
    return nutrientType;
  }

}
