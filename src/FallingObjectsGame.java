import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.Timer;
/////// Manages and updates the multiple FallingObject objects ///////

public class FallingObjectsGame extends JPanel implements KeyListener{
    private JPanel panel;
    private List<FallingObject> fallingObjects;
    private Timer timer, spawningTimer;

    public FallingObjectsGame(JPanel p){
        this.panel = p;
        this.fallingObjects = new ArrayList<>();
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
        FallingObject obj = new FallingObject(panel, "images/cookie.png");
        fallingObjects.add(obj);

    }

    private void updateObjects() {
        System.out.println("test updateObjects");
        System.out.println(fallingObjects.size());
        for (int i = 0; i<fallingObjects.size(); i++) {
            System.out.println("loop " + i);
            FallingObject obj = fallingObjects.get(i);
            obj.update();

            // Remove object if it goes off the screen
            if (obj.isOutOfBounds(panel)) {
                panel.remove(obj);
            }
        }
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