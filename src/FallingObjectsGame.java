import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.Timer;
import javax.swing.border.Border;
/////// Manages and updates the multiple FallingObject objects ///////

public class FallingObjectsGame {
    private GameEventListener listener;
    private JPanel mPanel, panel;
    private JLabel basket_, message;
    private Player p_;
    public static List<FallingObject> fallingObjects;
    private Timer timer, spawningTimer;
    private JProgressBar pb;
    private boolean escPressed; //for keylistener

    private String[] imagePaths; //an array to store the image icon objects used for the falling objects
    private String imgPath;

    private Runnable GameEnd;

    public FallingObjectsGame(JPanel p, JLabel b, Player person, JPanel mainPanel, GameEventListener listener){
        panel = p;
        this.listener  = listener;
        //goToNext = n; //the panel that will be displayed once this minigame is complete, returning back to the Game class
        basket_ = b;
        escPressed = false;
        p_ = person;
        imgPath = "images/milk.png"; //default

        imagePaths = new String[]{"images/egg.png", "images/flour.png", "images/sugar.png", "images/milk.png"};

        pb = new JProgressBar(0, 100); //score progress bar

        pb.setValue(0);
        pb.setStringPainted(false);  // Don't show percentage on bar
        pb.setBackground(Color.WHITE);
        pb.setForeground(Color.GREEN);
        pb.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3)); // White border
        pb.setBounds(25,10,950,25);
        panel.add(pb);

        p_ = person;
        fallingObjects = Collections.synchronizedList(new ArrayList<>());

        FallingObject obj = new FallingObject(panel, imgPath); //default, first object
        fallingObjects.add(obj);
        panel.setFocusable(true);



        timer = new Timer(75, new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e){
                updateObjects();
            }
        });
//        timer.start();

        spawningTimer = new Timer(1000, new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e){
                spawnFallingObject();
            }
        });
//        spawningTimer.start();

    }

    public FallingObject getFallingObject(int index) {
        return fallingObjects.get(index);
    }

    public void spawnFallingObject() {

        if (fallingObjects.size()>2){
            fallingObjects.subList(0, fallingObjects.size()-2).clear();
        }

        int rng = (int)(Math.random() * imagePaths.length); // Randomize the image
        imgPath = imagePaths[rng]; //set the image to display the one randomly chosen

        FallingObject obj = new FallingObject(panel, imgPath);
        fallingObjects.add(obj);

        // Revalidate and repaint the panel
        panel.revalidate();
        panel.repaint();
    }

    private void updateObjects() {
        for (int i = 0; i < fallingObjects.size(); i++) {
            FallingObject obj = fallingObjects.get(i);
            obj.update();

            // Remove object if it goes off the screen
            if (obj.isOutOfBounds(panel)) {
                panel.remove(obj.imgLabel);
                fallingObjects.remove(i);
                i--;

                p_.decreaseLives(); // Decrease a life
                if (p_.getLives() == 0) {
                    System.out.println("Lives exhausted. Triggering game over...");
                    timer.stop();
                    spawningTimer.stop();
                    if (listener != null) {
                        listener.onGameLose(); // Notify the Game class
                    }
                    return;
                }
                continue;
            }

            // Handle object intersection with basket
            if (obj.imgLabel.getBounds().intersects(basket_.getBounds())) {
                panel.remove(obj.imgLabel);
                fallingObjects.remove(i);
                i--;
                p_.incScore();
                pb.setValue(p_.getScore());

                // End game if score reaches 100
                if (p_.getScore() >= 100) {
                    System.out.println("Player wins");
                    timer.stop();
                    spawningTimer.stop();
                    if (listener != null) {
                        listener.onGameWin();
                    }
                    return;
                }
            }
        }

        panel.revalidate();
        panel.repaint();
    }




    public void start() {
        timer.start();
        spawningTimer.start();
    }
}