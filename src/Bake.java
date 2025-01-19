import javax.swing.*;

public class Bake extends Step{
    JFrame frame;
    Player p_;

    public Bake(JFrame jframe, Player player){
        super("Baking", player);
        p_ = player;
    }
}
