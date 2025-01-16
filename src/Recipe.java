import java.util.ArrayList;

public class Recipe {
    String name;
    //CURRYYYY

    ArrayList<Step> steps;
    ArrayList<Ingredient> ingredients;


    public Recipe(String name, ArrayList<Step> steps, ArrayList<Ingredient> ingredients) {
        this.name = name;
        this.steps = new ArrayList<Step>();
        this.ingredients = new ArrayList<Ingredient>();
    }

    public void addStep(Step step) {
        steps.add(step);
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public void addIngredient(Ingredient ingredient){
        ingredients.add(ingredient);
    }

    public ArrayList<Ingredient> getIngredients(){
        return ingredients;
    }


}
