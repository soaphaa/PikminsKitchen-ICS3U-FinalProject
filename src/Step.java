public abstract class Step {
    String stepName;
    boolean isCompleted;

    public Step(String name){
        this.stepName = name;
        this.isCompleted = false;
    }
}
