import java.awt.*;
import javax.swing.*;
import java.net.URL;

public class Bake {
    private JPanel panel, mPanel;
    private Player p_;

    private String[] imgPath;
    private JLabel[] imgLabel;

    private JProgressBar pb;
    private Image pbImg;
    private ImageIcon pbIcon, image;
    private JLabel pbLabel;

    private Timer timer; // Progress bar timer
    int currentValue;    // Current value of progress bar

    public Bake(JPanel mainPanel, Player player) {
        this.mPanel = mainPanel;
        this.p_ = player;

        // Paths to cookie state images
        imgPath = new String[]{
                "images/bake/underCooked.png",
                "images/bake/good.png",
                "images/bake/perfect.png",
                "images/bake/burnt.png",
                "images/bake/fire.png",
                "images/bake/baking.png"
        };
        imgLabel = new JLabel[imgPath.length];

        for (int i = 0; i < imgLabel.length; i++) {
            URL imgUrl = Mix.class.getResource(imgPath[i]);
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

        // Create and configure the progress bar (used for progress value)
        pb = new JProgressBar(0, 100);
        pb.setValue(0); // Start with 0% progress
        pb.setStringPainted(false); // Hide default text
        pb.setFocusable(false);
        pb.setBorder(BorderFactory.createEmptyBorder());
        pb.setOpaque(false); // Transparent background
        pb.setForeground(Color.GREEN); // Set progress color
        pb.setBounds(125, 700, 750, 25); // Match the size and position of the background image

        // Use JLayeredPane for stacking components
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 1000, 800);

        // Add components to the layered pane
        layeredPane.add(pbLabel, JLayeredPane.DEFAULT_LAYER); // Background image on the bottom layer
        layeredPane.add(pb, JLayeredPane.PALETTE_LAYER);     // Progress bar on top layer


        displayImage(5); //default image

        // Add the layered pane to the panel
        panel.add(layeredPane);

        startBaking();

        // Add the panel to the main panel
        mPanel.add(panel);
        mPanel.revalidate();
        mPanel.repaint();
    }

    public void displayImage(int index) {
        imgLabel[5].setVisible(false); //set the default image of the oven as false
        imgLabel[index].setVisible(true); //replace with the cookie state
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
                displayImage(4);
            }
        });
        timer.start();
    }

    public void stopTimer() {
        timer.stop();
        panel.repaint();
        System.out.println("Timer stopped");
        cookieState();
    }

    public void cookieState() {
        if (currentValue > 0) {
            if (currentValue <= 55) {
                displayImage(0);//undercooked
            }
            else if(currentValue>=65 && currentValue <=70){
                displayImage(2);//perfect
            }
            else if(currentValue >=81){
                displayImage(3);//burnt
            }

            else{
                displayImage(1);//good
            }
            System.out.println(currentValue);
        }
    }
}
