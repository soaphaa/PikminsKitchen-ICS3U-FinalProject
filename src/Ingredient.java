public class Ingredient{
    String name;
    int amount;
    int height; //minigame
    public final int LENGTH = 80;
    public final int WIDTH = 80;

    public Ingredient(String n){
        this.name = n;
        amount = 0;
        height = 0;
    }

    public int getAmount() {
        return amount;
    }

    public void incAmount(){
            this.amount++;
    }

    public void setHeight(int h){
        height = h;
    }

    public int getHeight(){
        return height;
    }

    public int getWIDTH(){
        return WIDTH;
    }

    public int getLENGTH(){
        return LENGTH;
    }
}
