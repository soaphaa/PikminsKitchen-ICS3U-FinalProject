import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.io.*;

import static javax.swing.BorderFactory.createLineBorder;

public class Game extends JFrame implements ActionListener,KeyListener, GameEventListener{

    // Settings
    final int tile = 16; // 16x16 pixel sprites
    final int tileScale = 3;
    final int mPanelWidth = 1000;
    final int mPanelHeight = 800;
    boolean isOn = true;
    private Stack<JPanel> panelStack; // Stack to track navigation history with settings method
    JPanel mainPanel;
    Image scaledTitle;

    // Message displayed
    public static JLabel message;
    String[] msgPaths; //array of message paths
    JLabel[] msgLabels;

    //catching items minigame
    FallingObjectsGame fog;

    // GUI JFrame
    JPanel p1, p2, p3, p4, p5, p6, p7, nextP, pauseP, gameOverP;
    static JLabel title, aON, aOFF, bg1, bg2, basket, fallingObject, hs, tutorialMsg;
    JButton start, quit, audio, back, home, recipe1, startRecipe, exitRecipe, next, tryAgain;
    ImageIcon titleIcon, audioOn, audioOff, recipeBg, pikminIcon, ingredientIcon, BgIcon;
    static URL titleURL, audioUrl, audio2Url, bg1Url, pikminUrl, ingredientUrl, miniGameBGUrl;
    JProgressBar pb, pb2;
    private JLayeredPane layeredPane; // Layered pane to manage overlays

    Player p;

    //child class objects
    ArrayList<Step> r1Steps = new ArrayList<>();
    ArrayList<Ingredient> r1Ingredients = new ArrayList<>();
    Mix mixGame;
    Bake bakeGame;
    Ingredient flour, milk, eggs, chocolateChips, sugar;

    //game stage
    int gameStage;

    public Game() {
        message = new JLabel();

        // Setting the base Frame
        new JFrame("Pikmin's Kitchen!");
        setSize(1000, 850);
        setLayout(null); // Use null layout
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Initialize layered pane
        layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 1000, 850);
        add(layeredPane);

        // Initialize mainPanel
        mainPanel = new JPanel(null);
        mainPanel.setBounds(0, 0, 1000, 800);
        mainPanel.setBackground(Color.decode("#d9e1f1"));

        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);

        msgPaths = new String[]{"images/catchingMessage.png", "images/msgMix.png", "images/msgBake.png"};
        msgLabels = new JLabel[msgPaths.length];
        URL msgUrl;
        ImageIcon msgImg;

        for (int i = 0; i < 3; i++) {
            msgUrl = Mix.class.getResource(msgPaths[i]);
            msgImg = new ImageIcon(msgUrl);
            Image scaledImage = msgImg.getImage().getScaledInstance(1000, 800, Image.SCALE_SMOOTH);
            msgImg = new ImageIcon(scaledImage);
            msgLabels[i] = new JLabel(msgImg);
            msgLabels[i].setSize(1000, 800);
        }

        gameStage = 0;

        panelStack = new Stack<>();

        //player
        p = new Player(this, 3, mainPanel);

        this.titlescreen(); //start the game at the titlescreen flashscreen

        addKeyListener(this);  // Add key listener to the frame

        mainPanel.addKeyListener(this); //add key listener once
    }

    public void setMsg(int ind1, JPanel p) {
        // Display the message
        tutorialMsg = msgLabels[ind1];
        tutorialMsg.setLocation(0, 0);
        tutorialMsg.setVisible(true);

        // Add the tutorial message to a higher layer
        layeredPane.add(tutorialMsg, JLayeredPane.MODAL_LAYER);

        // Set focus on the layeredPane to capture key events
        layeredPane.setFocusable(true);
        layeredPane.requestFocusInWindow();

        // Add a KeyListener to the layeredPane
        layeredPane.addKeyListener(this);
    }

    private void startCurrentGame(int ind) {
        // Start the specific game
        switch (ind) {
            case 0: // Catching game
                System.out.println("Starting Catching Game...");
                fog.start();
                break;
            case 1: // Mix game
                System.out.println("Starting Mix Game...");
                break;
            case 2: // Bake game
                System.out.println("Starting Bake Game...");
                bakeGame.start();
                break;
        }
    }

    private void switchPanel(JPanel newPanel) {
        mainPanel.removeAll(); // Remove existing components from the main panel
        mainPanel.add(newPanel); // Add the new panel
        mainPanel.revalidate(); // Refresh layout
        mainPanel.repaint(); // Redraw frame
        mainPanel.setFocusable(true);
        mainPanel.requestFocusInWindow(); // Re-assert focus to ensure KeyListener works.
    }


    public void next() {
        if (nextP != null) {
            // If the overlay exists, remove it before recreating
            layeredPane.remove(nextP);
        }
        // Create the overlay panel
        URL nextUrl = Game.class.getResource("images/nextScreen.png");
        ImageIcon nextIcon = new ImageIcon(nextUrl);
        Image resizedNextIcon = nextIcon.getImage().getScaledInstance(362, 241, Image.SCALE_SMOOTH);

        nextP = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw the background image
                g.drawImage(resizedNextIcon, 0, 0, 362, 241, this);
            }
        };

        nextP.setBounds(600, 196, 362, 241);

        // Add the "Next" button
        next = new JButton();
        next.setBounds(76, 256, 219, 56);
        next.setVisible(true);
        next.setOpaque(false);
        next.setContentAreaFilled(false);
        next.setBorderPainted(false);
        // Set the action command corresponding to the gameStage
        switch (gameStage) {
            case 1 -> next.setActionCommand("NEXT_STAGE_1");
            case 2 -> next.setActionCommand("NEXT_STAGE_2");
            case 3 -> next.setActionCommand("NEXT_STAGE_3");
        }
        next.addActionListener(this); // Add action listener to this button

        // Add the "Try Again" button
        tryAgain = new JButton();
        tryAgain.setBounds(76, 322, 219, 56);
        tryAgain.setVisible(true);
        tryAgain.setOpaque(false);
        tryAgain.setContentAreaFilled(false);
        tryAgain.setBorderPainted(false);
        tryAgain.setActionCommand("RETRY_STAGE_" + gameStage);

        tryAgain.addActionListener(e -> {
            if (!panelStack.isEmpty()) {
                JPanel previousPanel = panelStack.pop();
                switchPanel(previousPanel);
            }
            // Remove overlay
            layeredPane.remove(nextP);
            layeredPane.repaint();
        });

        // Display the current score
        JLabel scoreLabel = new JLabel("Score: " + p.getScore());
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 25));
        scoreLabel.setForeground(Color.decode("#224b9d"));
        scoreLabel.setBounds(100, 140, 200, 50); // Adjust size and position as needed
        nextP.add(scoreLabel);

        // Add buttons and score label to the panel
        nextP.add(next);
        nextP.add(tryAgain);

        // Add overlay to the layeredPane at a higher layer
        layeredPane.add(nextP, JLayeredPane.MODAL_LAYER);
        layeredPane.repaint();
        layeredPane.revalidate();
    }


    public void gameOver(){
        if (gameOverP != null) {
            // If the overlay exists, remove it before recreating
            layeredPane.remove(gameOverP);
            if(nextP !=null){
                layeredPane.remove(gameOverP);
            }

        }

        URL overURL = Game.class.getResource("images/GameOver.png");
        ImageIcon overIcon = new ImageIcon(overURL);
        Image resizedGameOverIcon = overIcon.getImage().getScaledInstance(362, 241, Image.SCALE_SMOOTH);

        gameOverP = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw the background image
                g.drawImage(resizedGameOverIcon, 0, 0, 357, 241, this);
            }
        };

        gameOverP.setBounds(319, 196, 357, 241);

        home = new JButton();
        home.setBounds(71, 149, 219, 60);
        home.setVisible(true);
        home.setOpaque(false);
        home.setContentAreaFilled(false);
        home.setBorderPainted(false);
        home.addActionListener(this);

        gameOverP.add(home);

        //gameOverP.add(tryAgain);

        // Add overlay to the layeredPane at a higher layer
        layeredPane.add(gameOverP, JLayeredPane.MODAL_LAYER);
        layeredPane.repaint();
        layeredPane.revalidate();
    }

    //flashscreen
    public void titlescreen(){

        titleURL = Game.class.getResource("images/pikminsTitlescreen.png");
        if (titleURL != null) {
            titleIcon = new ImageIcon(titleURL);
            scaledTitle = titleIcon.getImage().getScaledInstance(1000,800, Image.SCALE_SMOOTH);
        }

        p1 = new JPanel(null)
        {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw the image
                g.drawImage(scaledTitle, 0, 0, getWidth(), getHeight(), this);
            }
        };
        p1.setLayout(null);
        p.setLivesVisible(false); // Hide lives display
        p1.setSize(mPanelWidth,mPanelHeight);

        start = new JButton();
        start.setBounds(86,618,232,86);
        start.setVisible(true);
        start.setOpaque(false);
        start.setContentAreaFilled(false);
        start.setBorderPainted(false);

        quit = new JButton();
        quit.setBounds(718,618,186,86);
        
        // Add high score label
        hs = new JLabel("High Score: " + p.getHighscore());
        System.out.println(p.getHighscore());
        hs.setFont(new Font("Arial", Font.BOLD, 35));
        hs.setForeground(Color.decode("#224b9d"));
        hs.setBounds(375,615,300,100);
        hs.setVisible(true);

        audio = new JButton();

        audioUrl = Game.class.getResource("images/audioOn.png");
        if (audioUrl != null) {
            audioOn = new ImageIcon(audioUrl);
            Image audioOnv2 = audioOn.getImage().getScaledInstance(60,60, Image.SCALE_SMOOTH);
            audioOn = new ImageIcon(audioOnv2);
        }

        audio2Url = Game.class.getResource("images/audioOff.png");
        audioOff = new ImageIcon(audio2Url);
        Image audioOffv2 = audioOff.getImage().getScaledInstance(60,60,Image.SCALE_SMOOTH);
        audioOff = new ImageIcon(audioOffv2);

        audio.setIcon(audioOn);
        audio.setBounds(915,2,65,65);
        audio.setVisible(true);
        audio.setBackground(Color.decode("#d4e7fe"));
        audio.setBorderPainted(false);
        audio.setFocusable(false);

        quit.setVisible(true);
        quit.setOpaque(false);
        quit.setContentAreaFilled(false);
        quit.setBorderPainted(false);

        start.addActionListener(this);
        quit.addActionListener(this);
        audio.addActionListener(this);


        p1.add(start);
        p1.add(quit);
        p1.add(hs);
        p1.add(audio);

        switchPanel(p1);
    }

    public void recipeList(){
        p2 = new JPanel(null);
        p2.setBounds(150,100,700,500);
        p2.setBorder(createLineBorder(Color.WHITE, 15, true));
        p2.setBackground(Color.decode("#d9e1f1"));

        recipe1 = new JButton("recipe");
        recipe1.setBounds(50,50,175,175);
        recipe1.addActionListener(this);

        p2.add(recipe1);

        switchPanel(p2);
    }


    //recipe screen
    public void recipe1(){

        Border borderWhite = BorderFactory.createLineBorder(Color.WHITE, 15, true);
        p3 = new JPanel(null);
        p3.setBounds(150,100,700,500);
        p3.setBorder(borderWhite);

        //creating the background
        bg1Url = Game.class.getResource("images/recipe1.png");
        recipeBg = new ImageIcon(bg1Url);
        Image recipeBgv2 = recipeBg.getImage().getScaledInstance(700,500,Image.SCALE_SMOOTH);
        recipeBg = new ImageIcon(recipeBgv2);
        bg1 = new JLabel(recipeBg);
        bg1.setBounds(0,0,700,500);
        bg1.setBorder(borderWhite);

        p3.add(bg1);

        startRecipe = new JButton();
        startRecipe.setBounds(516,379,139,72);
        startRecipe.setVisible(true);
        startRecipe.setOpaque(false);
        startRecipe.setContentAreaFilled(false);
        startRecipe.setBorderPainted(false);

        exitRecipe = new JButton();
        exitRecipe.setBounds(60,379,150,72);
        exitRecipe.setVisible(true);
        exitRecipe.setOpaque(false);
        exitRecipe.setContentAreaFilled(false);
        exitRecipe.setBorderPainted(false);

        startRecipe.addActionListener(this);
        exitRecipe.addActionListener(this);

        p3.add(startRecipe);
        p3.add(exitRecipe);

        switchPanel(p3);
    }

    public void catching() {
        gameStage = 1;
        p.resetLives(); // Reset lives for the minigame
        p.setLivesVisible(true); // Make lives visible

        p7 = new JPanel(null);
        p7.setBackground(Color.decode("#789adb"));
        p7.setBounds(0, 0, 1000, 800);

        // Set up the basket
        pikminUrl = Game.class.getResource("images/pikminFront.png");
        pikminIcon = new ImageIcon(pikminUrl);
        Image pikminFrontV2 = pikminIcon.getImage().getScaledInstance(175, 276, Image.SCALE_SMOOTH);
        pikminIcon = new ImageIcon(pikminFrontV2);

        basket = new JLabel(pikminIcon);
        basket.setSize(175, 276);
        basket.setLocation(p.getpX(), p.getpY());

        // Initialize FallingObjectsGame
        fog = new FallingObjectsGame(p7, basket, p, mainPanel, this);

        setMsg(0, p7); // Show tutorial message

        p7.add(basket);
        layeredPane.add(p7, JLayeredPane.DEFAULT_LAYER);
        switchPanel(p7);
    }


    public void mix(){
        gameStage = 2;
        p.resetLives();

        mainPanel.removeAll();

        mixGame = new Mix(mainPanel, p, this);
        r1Steps.add(mixGame);

        setMsg(1, mainPanel);

        mainPanel.revalidate();
        mainPanel.repaint();
        mainPanel.requestFocusInWindow(); // Re-assert focus to ensure KeyListener works
    }

    public void bake() {
        gameStage = 3;
        p.resetLives();

        mainPanel.removeAll();

        bakeGame = new Bake(mainPanel, p, this);

        setMsg(2, mainPanel); // Show tutorial message and wait for 'Q'

        mainPanel.revalidate();
        mainPanel.repaint();
        mainPanel.requestFocusInWindow();
    }

    @Override
    public void onGameWin() {
        p.updateHighScore(); // Save the high score
        next();
    }

    @Override
    public void onGameLose() {
        System.out.println("Game lost. Showing Game Over screen...");
        p.setLivesVisible(false); // Hide lives display
        gameOver(); // Show the Game Over screen
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == home) {
            titlescreen();
            layeredPane.remove(gameOverP);// Remove overlay
            layeredPane.repaint();
        }

        if (e.getSource() == start) {
            recipe1();
        }

        if (e.getSource() == quit) {
            if (JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Pikmin", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }

        if(e.getSource() == audio){
            if(isOn == true) {
                audio.setIcon(audioOff);
                isOn = false;
            }
            else{
                audio.setIcon(audioOn);
                isOn = true;
            }
        }

        if(e.getSource() == startRecipe){
            //creating the recipe object details
            Recipe cookies = new Recipe("cookies", r1Steps, r1Ingredients);

            r1Ingredients.add(flour = new Ingredient("flour"));
            r1Ingredients.add(sugar = new Ingredient("sugar"));
            r1Ingredients.add(milk = new Ingredient("milk"));
            r1Ingredients.add(eggs = new Ingredient("eggs"));
            r1Ingredients.add(chocolateChips = new Ingredient("chocolate"));

            catching();

        }
        else if(e.getSource() == exitRecipe){
            titlescreen();
        }

        if(e.getSource() == back){
            titlescreen();
        }

        String command = e.getActionCommand();

        if ("NEXT_STAGE_1".equals(command)) {
            mix(); // Call mix() method
            layeredPane.remove(nextP);// Remove overlay
            layeredPane.repaint();
        }
        else if("NEXT_STAGE_2".equals(command)){
            bake();
            layeredPane.remove(nextP);// Remove overlay
            layeredPane.repaint();
        }
        else if("NEXT_STAGE_3".equals(command)){
            titlescreen();
            layeredPane.remove(nextP);// Remove overlay
            layeredPane.repaint();
        }

        if ("RETRY_STAGE_1".equals(command)) {
            // Restart the catching game
            catching();
            layeredPane.remove(nextP);
            layeredPane.repaint();
        } else if ("RETRY_STAGE_2".equals(command)) {
            // Restart the mix game
            mix();
            layeredPane.remove(nextP);
            layeredPane.repaint();
        } else if ("RETRY_STAGE_3".equals(command)) {
            // Restart the bake game
            bake();
            layeredPane.remove(nextP);
            layeredPane.repaint();
        }
    }

    public void keyPressed(KeyEvent e){
            if (e.getKeyCode() == KeyEvent.VK_Q) {
                layeredPane.removeKeyListener(this);
                tutorialMsg.setVisible(false); // Hide the message
                startCurrentGame(gameStage-1); // Start the game
            }

        switch(gameStage) {
            case 1:
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        pikminUrl = Game.class.getResource("images/pikminLeft.png"); // dimensions: 35x55
                        basket.setIcon(pikminIcon);
                        p.moveL();
                        basket.setLocation(p.getpX(), p.getpY());

                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        pikminUrl = Game.class.getResource("images/pikminRight.png"); // dimensions: 35x55
                        basket.setIcon(pikminIcon);
                        p.moveR();
                        basket.setLocation(p.getpX(), p.getpY());
                }

            case 2:
                if (e.getKeyCode() == KeyEvent.VK_A) {
                    System.out.println("a pressed");
                    mixGame.handleKeyPress('a');
                }
                if (e.getKeyCode() == KeyEvent.VK_W) {
                    System.out.println("w pressed");
                    mixGame.handleKeyPress('w');
                }
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    System.out.println("s pressed");
                    mixGame.handleKeyPress('s');
                }
                if (e.getKeyCode() == KeyEvent.VK_D) {
                    System.out.println("d pressed");
                    mixGame.handleKeyPress('d');
                }
            case 3:
                if(e.getKeyCode() == KeyEvent.VK_SPACE){
                    bakeGame.stopTimer();
                    System.out.println("space pressed");
                }

        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
