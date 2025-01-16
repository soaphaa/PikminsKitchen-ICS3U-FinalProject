import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.Timer;
/////// Manages and updates the multiple FallingObject objects ///////

public class FallingObjectsGame extends JPanel implements KeyListener{
    private JPanel panel;
    private JLabel basket_;
    private int score; // highscore counter
    public static List<FallingObject> fallingObjects;
    private Timer timer, spawningTimer;

    public FallingObjectsGame(JPanel p, JLabel b){
        panel = p;
        basket_ = b;
        fallingObjects = Collections.synchronizedList(new ArrayList<>());

        FallingObject obj = new FallingObject(panel, "images/cookie.png");
        fallingObjects.add(obj);

        timer = new Timer(50, new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e){
                updateObjects();
            }
        });
        timer.start();

        spawningTimer = new Timer(1000, new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e){
                spawnFallingObject();
            }
        });
        spawningTimer.start();
    }

    public FallingObject getFallingObject(int index) {
        return fallingObjects.get(index);
    }

    public void spawnFallingObject() {

        if (fallingObjects.size()>2){
            fallingObjects.subList(0, fallingObjects.size()-2).clear();
        }
        FallingObject obj = new FallingObject(panel, "images/cookie.png");
        fallingObjects.add(obj);

    }

    private void updateObjects() {
        //System.out.println("number of objects: " + fallingObjects.size());
            for (int i = 0; i < fallingObjects.size(); i++) {
                FallingObject obj = fallingObjects.get(i);
                obj.update();

                // Remove object if it goes off the screen
                if (obj.isOutOfBounds(panel)) {
                    panel.remove(obj.imgLabel);
                }

                obj.setLocation(getX(), getY());
                System.out.println(obj.imgLabel.getBounds());
                System.out.println(basket_.getBounds());
                if (obj.imgLabel.getBounds().intersects(basket_.getBounds())) {
                    panel.remove(obj.imgLabel);
                    score++;
                }
            }
    }

    public int getScore(){
        return score;
    }

    public void keyPressed(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            timer.stop();
        }

    }

    @Override
    public void keyTyped(KeyEvent e){

    }

    @Override public void keyReleased(KeyEvent e){

    }
}