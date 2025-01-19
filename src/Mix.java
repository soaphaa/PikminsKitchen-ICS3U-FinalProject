import java.awt.*;
import javax.swing.*;
import java.net.*;
import java.util.Random;

public class Mix extends Step {

    int pos; // KeyPressed determines the position
    char order[];

    private String[] imagePaths; // To store the different image paths
    private String imgPath; // Stores the chosen img
    private String[] isPressedPaths;
    private String[] notPressedPaths;
    JLabel[] isPressed;
    JLabel[] notPressed;

    // GUI
    JFrame frame;
    JPanel panel;
    URL imgUrl, bgUrl;
    ImageIcon image, bg, seq;
    JLabel sequenceDisplay;
    JLabel[] imgLabel;
    Player p_;

    public Mix(JFrame jframe, Player player) {
        super("Mixing", player);
        frame = jframe;
        p_ = player;
        bgUrl = Mix.class.getResource("images/miniGameBackground.png");
        bg = new ImageIcon(bgUrl);
        Image scaledimg = bg.getImage().getScaledInstance(700, 700, Image.SCALE_SMOOTH);

        panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw the image
                g.drawImage(scaledimg, 0, 0, getWidth(), getHeight(), this);
            }
        };

        panel.setSize(1000, 700);
        panel.setLocation(0, 50);
        panel.setFocusable(true);
        panel.setVisible(true);

        URL sequenceUrl = Mix.class.getResource("images/awsd (1).png");
        seq = new ImageIcon(sequenceUrl);
        Image scaledseq = seq.getImage().getScaledInstance(96, 18, Image.SCALE_SMOOTH);
        seq = new ImageIcon(scaledseq);
        sequenceDisplay = new JLabel(seq);
        panel.add(sequenceDisplay);

        pos = -1;
        order = new char[]{'a', 'w', 'd', 's'};

        imagePaths = new String[]{"images/1.png", "images/2.png", "images/3.png", "images/4.png", "images/5.png"};
        imgLabel = new JLabel[5];

        for (int i = 0; i < 5; i++) {
            imgUrl = Mix.class.getResource(imagePaths[i]);
            image = new ImageIcon(imgUrl);
            Image scaledImage = image.getImage().getScaledInstance(500, 500, Image.SCALE_SMOOTH);
            image = new ImageIcon(scaledImage);
            imgLabel[i] = new JLabel(image);
            imgLabel[i].setSize(500, 500);
            imgLabel[i].setLocation(250, 100);
        }

        displayImage(0); // Display the default image at index 0

        frame.add(panel); // Add the panel to the main JFrame in the Game class
        frame.revalidate();
        frame.repaint();
    }

    public void displayImage(int index) {
        panel.removeAll();
        imgLabel[index].setVisible(true);
        panel.add(imgLabel[index]);
        frame.revalidate();
        frame.repaint();
    }

    // Handle the key pressed
    public void handleKeyPress(char keyChar) {
        if (pos >= -1 && pos < 3) {
            pos++;
            if (order[pos] != keyChar) {
                // Wrong key pressed, reset position and show an error image (e.g., index 4)
                pos = -1;
                displayImage(0); // display the messed up image
                System.out.println("Wrong sequence, try again");
            } else {
                // Correct key pressed, show the corresponding image
                displayImage(pos+1); // Show image for the current position
                System.out.println("Correct key pressed");
            }
            if (pos == 3) {
                // Sequence completed successfully
                p_.incHighscoreDouble();
                System.out.println("Win! Current high score: " + p_.getHighscore());
                pos = -2; // Reset to a state where sequence won't continue
            }
        }
    }

}
