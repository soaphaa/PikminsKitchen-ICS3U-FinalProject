public class Chop extends Step{

    public Chop(){
        super("Chopping");
    }

    @Override
    public void performStep(){
        isCompleted = true;
    }
}
