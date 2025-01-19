import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.Timer;
/////// Manages and updates the multiple FallingObject objects ///////

public class FallingObjectsGame {
    private JFrame frame;
    private JPanel panel, goToNext;
    private JLabel basket_;
    private Player p_;
    public static List<FallingObject> fallingObjects;
    private Timer timer, spawningTimer;
    private JProgressBar pb;
    private boolean escPressed; //for keylistener

    private String[] imagePaths; //an array to store the image icon objects used for the falling objects
    private String imgPath;

    private Runnable GameEnd;

    public FallingObjectsGame(JPanel p, JLabel b, Player person, JFrame mainGame){
        panel = p;
        //goToNext = n; //the panel that will be displayed once this minigame is complete, returning back to the Game class
        basket_ = b;
        escPressed = false;
        person = new Player();
        frame = mainGame;
        imgPath = "images/milk.png"; //default

        imagePaths = new String[]{"images/egg.png", "images/flour.png", "images/sugar.png", "images/milk.png"};

        pb = new JProgressBar(0, 100); //score progress bar

        pb.setValue(0);
        pb.setStringPainted(false);  // Don't show percentage on bar
        pb.setBounds(25,10,950,10);
        panel.add(pb);

        p_ = person;
        fallingObjects = Collections.synchronizedList(new ArrayList<>());

        System.out.println(imgPath);

        FallingObject obj = new FallingObject(panel, imgPath); //default, first object
        fallingObjects.add(obj);
        panel.setFocusable(true);



        timer = new Timer(35, new ActionListener(){

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

        int rng = (int)(Math.random() * imagePaths.length); // Randomize the image
        imgPath = imagePaths[rng]; //set the image to display the one randomly chosen
        System.out.println("Numer" + rng);
        System.out.println(imagePaths.length);

        FallingObject obj = new FallingObject(panel, imgPath);
        fallingObjects.add(obj);
        //System.out.println("Amount of objects: " + fallingObjects.size());

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
                i--; // Adjust index after removal
                continue;
            }

            // Handle object intersection with basket
            if (obj.imgLabel.getBounds().intersects(basket_.getBounds())) {
                panel.remove(obj.imgLabel);
                fallingObjects.remove(i);
                i--; // Adjust index after removal

                // Update the score
                p_.incHighscore();
                pb.setValue(p_.getHighscore());

                // End game if score reaches 100
                if (p_.getHighscore() >= 100) {
                    System.out.println("You win yay " + p_.getHighscore());
                    timer.stop();
                    spawningTimer.stop();
                    panel.setVisible(false);
                    frame.revalidate();
                    frame.repaint();
                }
            }
        }

        // Revalidate and repaint the panel
        panel.revalidate();
        panel.repaint();
    }

    public void pause(){
        timer.stop();
        spawningTimer.stop();
    }

//    public void keyPressed(KeyEvent e){
//        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
//            escPressed = true;
//            System.out.println("Pressed in minigame");
//            timer.stop();
//            spawningTimer.stop();
//        }
//
//    }
//
//    @Override
//    public void keyTyped(KeyEvent e){
//
//    }
//
//    @Override public void keyReleased(KeyEvent e){
//
//    }
}