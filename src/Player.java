import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;

public class Player {
    private static final String HIGHSCORE_FILE = "highscore.txt";
    private int highscore;
    private int score; // Temporary score
    private int pY;
    private int pX;
    private int lives;
    private GameEventListener l;

    private static final int MAX_LIVES = 3;

    // GUI
    private String[] imgPaths;
    private JLabel[] label; // Array of JLabels for displaying lives
    private JPanel mainPanel; // Reference to the mainPanel

    public Player(GameEventListener listener, int iLives, JPanel mainPanel) {
        l = listener;
        highscore = 0;
        score = 0;
        pX = 500;
        pY = 500;
        lives = iLives;
        this.mainPanel = mainPanel;

        imgPaths = new String[]{
                "images/lives/fullLives.png",
                "images/lives/2Lives.png",
                "images/lives/1Lives.png",
                "images/lives/0Lives.png"
        };

        label = new JLabel[imgPaths.length];

        // Initialize life display JLabels
        for (int i = 0; i < imgPaths.length; i++) {
            URL imgUrl = Player.class.getResource(imgPaths[i]);
            ImageIcon img = new ImageIcon(imgUrl);
            Image scaledImg = img.getImage().getScaledInstance(176, 50, Image.SCALE_SMOOTH);
            img = new ImageIcon(scaledImg);
            label[i] = new JLabel(img);
            label[i].setBounds(10, 10, 176, 50); // Adjust position and size
            label[i].setVisible(false); // Initially hidden
            mainPanel.add(label[i]);
        }
        updateLives(); // Display the correct number of lives
    }

    // Update the lives display
    public void updateLives() {
        for (int i = 0; i < label.length; i++) {
            label[i].setVisible(i == MAX_LIVES - lives); // Show only the relevant image
        }
    }

    // Get current lives
    public int getLives() {
        return lives;
    }

    public void decreaseLives() {
        if (lives > 0) {
            lives--;
            updateLives();
            if (lives == 0 && l != null) {
                l.onGameLose(); // Notify game listener only once when lives reach 0
            }
        }
    }


    // Reset lives to the maximum
    public void resetLives() {
        lives = MAX_LIVES;
        updateLives();
    }

    // Load high score from the file
    private int loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGHSCORE_FILE))) {
            return Integer.parseInt(reader.readLine().trim());
        } catch (IOException | NumberFormatException e) {
            // Return 0 if file does not exist or is invalid
            return 0;
        }
    }

    // Save high score to the file
    private void saveHighScore(int newHighScore) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGHSCORE_FILE))) {
            writer.write(String.valueOf(newHighScore));
        } catch (IOException e) {
            System.err.println("Error saving high score: " + e.getMessage());
        }
    }

    // Update high score if the current score is higher
    public void updateHighScore() {
        if (score > highscore) {
            highscore = score;
            saveHighScore(highscore); // Save the new high score to the file
        }
    }

    public int getHighscore() {
        return loadHighScore();
    }

    public void setHighscore(int hs) {
        highscore = hs;
    }

    public void setScore(int s) {
        score = s;
    }

    public int getScore() {
        return score;
    }

    public void incScore() {
        score += 10;
    }

    public void incScore(int s){
        score+=s;
    }

    public void incScoreDouble() {
        score += 20;
    }

    public void decreaseScore() {
        score -= 10;
    }

    public int getpX() {
        return pX;
    }

    public int getpY() {
        return pY;
    }

    public void moveL() {
        if (pX >= 0) {
            pX -= 10;
        }
    }

    public void moveR() {
        if (pX <= 800) {
            pX += 10;
        }
    }

    // Additional helper to ensure the label is visible
    public void setLivesVisible(boolean visible) {
        for (JLabel lifeLabel : label) {
            lifeLabel.setVisible(false); // Hide all first
        }
        if (visible) {
            updateLives(); // Show the correct label if visible is true
        }
    }
}

