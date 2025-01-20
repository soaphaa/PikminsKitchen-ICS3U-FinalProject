import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileIO {
    private final String filePath;

    public FileIO(String filePath) {
        this.filePath = filePath;
    }

    // Method to save scores
    public void saveScore(Player p_) {
        try (FileWriter fw = new FileWriter(filePath, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(p_.getScore());
        } catch (IOException e) {
            System.out.println("Error saving score: " + e.getMessage());
        }
    }

    // Method to load scores
    public List<String> loadScores() {
        List<String> scores = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                scores.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error loading scores: " + e.getMessage());
        }
        return scores;
    }

    public void setHighscore(Player p_) {
        String filePath = "highscore.txt";
        int highestScore = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Assuming scores are saved in the format: "PlayerName: Score"
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    try {
                        int score = Integer.parseInt(parts[1].trim());
                        if (score > highestScore) {
                            highestScore = score;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid score format: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading high scores: " + e.getMessage());
        }

        // Set the high score in the player object
        p_.setHighscore(highestScore); // Assuming Player class has setHighscore method
    }

}
