import javax.swing.*;
import java.net.*;
import java.util.*;
import java.awt.*;

public class FallingObject extends JPanel {
    private int x,y,speed;
    private ImageIcon image;
    JLabel imgLabel;
    private URL imgUrl;

    public FallingObject(JPanel panel, String imgPath){
        System.out.println("new falling Object created");
        this.x = (int)(Math.random() * (panel.getWidth() - 80)); // Randomize X position
        this.y = 0; // Start at the top
        this.speed = 20; // fixed falling speed of 20

        // Load the image
        imgUrl = Game.class.getResource(imgPath);
        image = new ImageIcon(imgUrl);
        Image scaledImage = image.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        image = new ImageIcon(scaledImage);

        System.out.println(imgUrl);


        imgLabel = new JLabel(image);
        imgLabel.setSize(80, 80); // Set size of JLabel
        panel.add(imgLabel);
        imgLabel.setVisible(true);
    }

    public void update(){
        y += speed;
        System.out.println("x: " + x + "y:" + y);
        imgLabel.setVisible(true);
        imgLabel.setLocation(x,y);
    }

    // Check if the object has fallen out of bounds
    boolean isOutOfBounds(JPanel panel) {
        return y > panel.getHeight();
    }

    // Add the JLabel to the panel
    void add(JPanel panel) {
        panel.add(imgLabel);
    }

    // Remove the JLabel from the panel
    void remove(JPanel panel) {
        panel.remove(imgLabel);
    }
}
