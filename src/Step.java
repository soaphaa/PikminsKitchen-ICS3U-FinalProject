public abstract class Step {
    String stepName;
    boolean isCompleted;
    Player pers;

    public Step(String name, Player person){
        this.stepName = name;
        this.pers = person;
        this.isCompleted = false;
    }
}
