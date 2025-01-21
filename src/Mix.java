import java.awt.*;
import javax.swing.*;
import java.net.*;

public class Mix extends Step {

    int pos; // KeyPressed determines the position
    char[] order;

    private String[] imagePaths; // To store the different image paths
    private String[] sequencePaths; // Stores the different img paths of the sequence

    // GUI
    private GameEventListener listener;
    JPanel mPanel, panel;
    URL imgUrl, bgUrl;
    ImageIcon image, bg, seq;
    JLabel[] imgLabel, sequenceLabel;
    Player p_;

    public Mix(JPanel mainPanel, Player player, GameEventListener listener) {
        super("Mixing", player);
        this.listener = listener;
        mPanel = mainPanel;
        p_ = player;
        bgUrl = Mix.class.getResource("images/miniGameBackground.png");
        bg = new ImageIcon(bgUrl);
        Image scaledimg = bg.getImage().getScaledInstance(1000, 800, Image.SCALE_SMOOTH);

        panel = new JPanel(null)
        {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw the image
                g.drawImage(scaledimg, 0, 0, getWidth(), getHeight(), this);
            }
        };

        panel.setSize(1000, 800);
        panel.setLocation(0, 0);
        panel.setBackground(Color.decode("#feece0"));
        panel.setFocusable(true);
        panel.setVisible(true);

        sequencePaths = new String[]{"images/aPressed.png", "images/wPressed.png", "images/dPressed.png", "images/sPressed.png" };
        sequenceLabel = new JLabel[4];

        for(int i = 0; i<4; i++) {
            URL sequenceUrl = Mix.class.getResource(sequencePaths[i]);
            seq = new ImageIcon(sequenceUrl);
            Image scaledseq = seq.getImage().getScaledInstance(745, 160, Image.SCALE_SMOOTH);
            seq = new ImageIcon(scaledseq);
            sequenceLabel[i] = new JLabel(seq);
            sequenceLabel[i].setBounds(120, 30, 750, 165);
            sequenceLabel[i].setVisible(false);
        }

        pos = -1;
        order = new char[]{'a', 'w', 'd', 's'};

        imagePaths = new String[]{"images/2.png", "images/3.png", "images/4.png", "images/5.png", "images/messedUp.png", "images/1.png"};
        imgLabel = new JLabel[6];

        for (int i = 0; i < 6; i++) {
            imgUrl = Mix.class.getResource(imagePaths[i]);
            image = new ImageIcon(imgUrl);
            Image scaledImage = image.getImage().getScaledInstance(500, 500, Image.SCALE_SMOOTH);
            image = new ImageIcon(scaledImage);
            imgLabel[i] = new JLabel(image);
            imgLabel[i].setSize(500, 500);
            imgLabel[i].setLocation(240, 200);
        }

        displayImage(5); // Display the default image at index 5
        displaySequence(0); //Display default instruction (click a)

        mPanel.add(panel); // Add the panel to the main JFrame in the Game class
        mPanel.revalidate();
        mPanel.repaint();
    }

    public void displayImage(int index) {
        panel.removeAll();
        imgLabel[index].setVisible(true);
        panel.add(imgLabel[index]);
        mPanel.revalidate();
        mPanel.repaint();
    }

    public void displaySequence(int index){
        if(sequenceLabel!=null){
            if(index > 0) {
                sequenceLabel[index - 1].setVisible(false);
            }
        }

        assert sequenceLabel != null;
        sequenceLabel[index].setVisible(true);
        panel.add(sequenceLabel[index]);
        mPanel.revalidate();
        mPanel.repaint();
    }

    // Handle the key pressed
    public void handleKeyPress(char keyChar) {
        if (pos >= -1 && pos < 3) {
            pos++;
            if (order[pos] != keyChar) {
                // Wrong key pressed, reset position and show an error image (e.g., index 4)
                pos = -1;
                displayImage(4); // display the messed up image
                displaySequence(0);
                p_.decreaseLives();
//                if(p_.getLives()<1){
//                    if (listener != null) {
//                        listener.onGameLose(); // Notify the game to show the gameover panel
//                    }
//                    return;
//                }
//                else{
//                    p_.decreaseLives();
//                }
            } else {
                // Correct key pressed, show the corresponding image
                displayImage(pos); // Show image for the current position
                if(pos<3) {
                    displaySequence(pos + 1);
                }
                System.out.println("Correct key pressed");
            }
            if (pos == 3) {
                // Sequence completed successfully
                p_.incScoreDouble();
                System.out.println("Win! Current score: " + p_.getScore());
                pos = -2; // Reset to a state where sequence won't continue

                if (listener != null) {
                    listener.onGameWin(); // Notify the game to show the next panel
                }
                return;
            }
        }
    }

}
