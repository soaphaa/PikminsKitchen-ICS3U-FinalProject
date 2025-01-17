import javax.swing.*;
import java.net.*;
import java.util.*;
import java.awt.*;

public class FallingObject extends JPanel {
    private int x,y,speed;
    private ImageIcon image;
    public JLabel imgLabel;
    private URL imgUrl;

    public FallingObject(JPanel panel){
        System.out.println("new falling Object created");
        this.x = (int)(Math.random() * (panel.getWidth() - 80)); // Randomize X position
        this.y = 0; // Start at the top
        this.speed = 20; // fixed falling speed of 20
    }

    public void update(){
        y += speed;
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

    void setup(){
        imgLabel.setVisible(true);
    }

    void setPath(String imgPath){
        // Load the image
        imgUrl = Game.class.getResource(imgPath);
        image = new ImageIcon(imgUrl);
        Image scaledImage = image.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        image = new ImageIcon(scaledImage);
        imgLabel = new JLabel(image);
    }

    void setImg(JPanel panel){
        imgLabel.setSize(80, 80); // Set size of JLabel
        panel.add(imgLabel);
        imgLabel.setVisible(false);
    }

    URL getPath(){
        return imgUrl;
    }

    public int getY(){
        return y;
    }

    public int getX(){
        return x;
    }
}
