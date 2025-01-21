import java.awt.*;
import javax.swing.*;
import java.net.URL;

public class Bake {
    private JPanel panel, mPanel;
    private Player p_;
    private GameEventListener listener;

    private String[] imgPath;
    private JLabel[] imgLabel;

    private JProgressBar pb;
    private Image pbImg;
    private ImageIcon pbIcon, image;
    private JLabel pbLabel;

    private Timer timer; // Progress bar timer
    private int currentValue; // Current progress value

    public Bake(JPanel mainPanel, Player player, GameEventListener l) {
        this.mPanel = mainPanel;
        this.p_ = player;
        this.listener = l;

        // Paths to cookie state images
        imgPath = new String[]{
                "images/bake/underCooked.png", // 0
                "images/bake/good.png",        // 1
                "images/bake/perfect.png",     // 2
                "images/bake/burnt.png",       // 3
                "images/bake/fire.png",        // 4
                "images/bake/baking.png"       // 5 (default image)
        };
        imgLabel = new JLabel[imgPath.length];

        for (int i = 0; i < imgLabel.length; i++) {
            URL imgUrl = Bake.class.getResource(imgPath[i]);
            image = new ImageIcon(imgUrl);
            Image scaledImage = image.getImage().getScaledInstance(500, 500, Image.SCALE_SMOOTH);
            image = new ImageIcon(scaledImage);
            imgLabel[i] = new JLabel(image);
            imgLabel[i].setSize(500, 500);
            imgLabel[i].setLocation(250, 150);
        }

        // Load progress bar background image
        URL pbUrl = Bake.class.getResource("images/bake/progressBar.png");
        if (pbUrl != null) {
            pbIcon = new ImageIcon(pbUrl);
            pbImg = pbIcon.getImage().getScaledInstance(750, 70, Image.SCALE_SMOOTH);
            pbIcon = new ImageIcon(pbImg);
        }
        pbLabel = new JLabel(pbIcon);
        pbLabel.setBounds(125, 675, 750, 25);

        panel = new JPanel(null);

        // Configure the main panel
        panel.setBounds(0, 0, 1000, 800);
        panel.setBackground(Color.decode("#789adb"));

        // Create and configure the progress bar
        pb = new JProgressBar(0, 100);
        pb.setValue(0); // Start with 0% progress
        pb.setStringPainted(false); // Hide default text
        pb.setFocusable(false);
        pb.setBorder(BorderFactory.createEmptyBorder());
        pb.setOpaque(false); // Transparent background
        pb.setForeground(Color.GREEN); // Set progress color
        pb.setBounds(125, 700, 750, 25);

        // Use JLayeredPane for stacking components
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 1000, 800);

        // Add components to the layered pane
        layeredPane.add(pbLabel, JLayeredPane.DEFAULT_LAYER); // Background image on the bottom layer
        layeredPane.add(pb, JLayeredPane.PALETTE_LAYER);     // Progress bar on top layer

        displayImage(5); // Default image (baking)

        // Add the layered pane to the panel
        panel.add(layeredPane);

        startBaking();

        // Add the panel to the main panel
        mPanel.add(panel);
        mPanel.revalidate();
        mPanel.repaint();
    }

    public void displayImage(int index) {
        for (JLabel label : imgLabel) {
            label.setVisible(false); // Hide all images
        }
        imgLabel[index].setVisible(true); // Show the selected image
        panel.add(imgLabel[index]);
        mPanel.revalidate();
        mPanel.repaint();
    }

    // Simulate the baking process
    public void startBaking() {
        timer = new Timer(75, e -> {
            currentValue = pb.getValue();
            if (currentValue < pb.getMaximum()) { // Keep timer running
                pb.setValue(currentValue + 1);   // Increment progress
                panel.repaint();                // Repaint the panel
            } else {
                ((Timer) e.getSource()).stop(); // Stop when progress bar is full
                displayImage(4); // Show "fire" image (baking failed)
                p_.decreaseLives(); // Decrease a life
                if (p_.getLives() == 0) {
                    listener.onGameLose();
                }
            }
        });
        timer.start();
    }

    public void stopTimer() {
        timer.stop();
        panel.repaint();
        cookieState();
    }

    public void cookieState() {
        if (currentValue > 0) {
            if (currentValue <= 55) {
                displayImage(0); // Undercooked
                bakeScore(0);
            } else if (currentValue >= 65 && currentValue <= 70) {
                displayImage(2); // Perfect
                bakeScore(2);
            } else if (currentValue >= 81) {
                displayImage(3); // Burnt
                bakeScore(3);
            } else {
                displayImage(1); // Good
                bakeScore(1);
            }
        }
    }

    public void bakeScore(int imgIndex) {
        switch (imgIndex) {
            case 0: // Undercooked
                p_.decreaseScore();
                break;
            case 1: // Good
                p_.incScore(50);
                break;
            case 2: // Perfect
                p_.incScore(100);
                break;
            case 3: // Burnt
                p_.decreaseScore();
                break;
            case 4: // Failed completely
                p_.setScore(0);
                break;
        }

        // Update and save the high score if applicable
        p_.updateHighScore();

        // Check if lives are exhausted
        if (p_.getLives() == 0) {
            listener.onGameLose();
        } else {
            listener.onGameWin();
        }
    }

    public void start() {
        pb.setValue(0); // Reset progress bar
        timer.start();  // Start the baking process
    }
}
