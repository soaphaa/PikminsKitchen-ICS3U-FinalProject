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
    final int tileSize = tile * tileScale; // makes sprites scaled to 48x48
    final int mPanelWidth = 1000;
    final int mPanelHeight = 800;
    boolean isOn = true;
    private Stack<JPanel> panelStack; // Stack to track navigation history with settings method
    JPanel mainPanel;

    //player
    Player p;

    // Message displayed
    public static JLabel message;
    String[] msgPaths; //array of message paths
    JLabel[] msgLabels;

    //catching items minigame
    FallingObjectsGame fog;
    private boolean hasCollided = false;

    // GUI JFrame
    JPanel p1, p2, p3, p4, p5, p6, p7, nextP, pauseP, gameOverP;
    static JLabel title, aON, aOFF, bg1, bg2, basket, fallingObject, highscore;
    JButton start, quit, pause, hs, audio, back, recipe1, startRecipe, exitRecipe, next, tryAgain;
    ImageIcon titleIcon, audioOn, audioOff, recipeBg, pikminIcon, ingredientIcon, BgIcon;
    static URL titleURL, audioUrl, audio2Url, bg1Url, pikminUrl, ingredientUrl, miniGameBGUrl;
    JProgressBar pb, pb2;
    private JLayeredPane layeredPane; // Layered pane to manage overlays

    //child class objects
    ArrayList<Step> r1Steps = new ArrayList<>();
    ArrayList<Ingredient> r1Ingredients = new ArrayList<>();
    Mix mixGame;
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

        p = new Player(500);

        // Initialize layered pane
        layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 1000, 850);
        add(layeredPane);

        // Initialize mainPanel
        mainPanel = new JPanel(null);
        mainPanel.setBounds(0, 0, 1000, 800);
        mainPanel.setBackground(Color.LIGHT_GRAY);
        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);

        msgPaths = new String[]{"images/catchingMessage.png", "images/catchingMessage.png", "images/catchingMessage.png"};
        msgLabels = new JLabel[msgPaths.length];
        URL msgUrl;
        ImageIcon msgImg;

        for (int i = 0; i < 3; i++) {
            msgUrl = Mix.class.getResource(msgPaths[i]);
            msgImg = new ImageIcon(msgUrl);
            Image scaledImage = msgImg.getImage().getScaledInstance(1000, 200, Image.SCALE_SMOOTH);
            msgImg = new ImageIcon(scaledImage);
            msgLabels[i] = new JLabel(msgImg);
            msgLabels[i].setSize(1000, 200);
        }

        //pause button
        pause = new JButton();
        URL pauseUrl = Game.class.getResource("images/pause.png");
        ImageIcon pauseIcon = new ImageIcon(pauseUrl);
        Image scaledPause = pauseIcon.getImage().getScaledInstance(60,60,Image.SCALE_SMOOTH);
        pauseIcon = new ImageIcon(scaledPause);
        pause.setIcon(pauseIcon);
        pause.setBounds(2,2,65,65);
        pause.setVisible(true);
        pause.setBackground(Color.decode("#d4e7fe"));
        pause.setBorderPainted(false);
        pause.setFocusable(false);
        layeredPane.add(pause, JLayeredPane.PALETTE_LAYER); // Add to a higher layer

        gameStage = 0;

        panelStack = new Stack<>();

        this.mix();

        addKeyListener(this);  // Add key listener to the frame
        //highscores File I/O
        File highscoreFile = new File ("highscore.txt");
    }

    public void setMsg(int index, JPanel p) {
        p.add(msgLabels[index]);
    }

//    //method to read file
//    private static String readFile(String filePath) {
//        FileBuilder content = new FileBuilder();
//        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                content.append(line).append("\n");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "Error reading file.";
//        }
//        return content.toString();
//    }

    private void switchPanel(JPanel newPanel) {
        mainPanel.removeAll(); // Remove existing components from the main panel
        mainPanel.add(newPanel); // Add the new panel
        mainPanel.revalidate(); // Refresh layout
        mainPanel.repaint(); // Redraw frame
        mainPanel.setFocusable(true);
        mainPanel.requestFocusInWindow(); // Re-assert focus to ensure KeyListener works.
        mainPanel.addKeyListener(this);
    }


    public void next() {
        if (nextP != null) {
            // If the overlay exists, remove it before recreating
            layeredPane.remove(nextP);
        }
        // Create the overlay panel
        URL nextUrl = Game.class.getResource("images/nextScreen.png");
        ImageIcon nextIcon = new ImageIcon(nextUrl);
        Image resizedNextIcon = nextIcon.getImage().getScaledInstance(362, 408, Image.SCALE_SMOOTH);

        nextP = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw the background image
                g.drawImage(resizedNextIcon, 0, 0, 362, 408, this);
            }
        };

        nextP.setBounds(319, 196, 362, 408);

        // Add the "Next" button
        next = new JButton();
        next.setBounds(76, 256, 219, 56);
        next.setVisible(true);
        next.setOpaque(false);
        next.setContentAreaFilled(false);
        next.setBorderPainted(false);
        // Set the action command corresponding to the gameStage
        switch(gameStage){
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

        tryAgain.addActionListener(e -> {
            if (!panelStack.isEmpty()) {
                JPanel previousPanel = panelStack.pop();
                switchPanel(previousPanel);
            }
            // Remove overlay
            layeredPane.remove(nextP);
            layeredPane.repaint();
        });

        // Add buttons to the panel
        nextP.add(next);
        nextP.add(tryAgain);

        // Add overlay to the layeredPane at a higher layer
        layeredPane.add(nextP, JLayeredPane.MODAL_LAYER);
        layeredPane.repaint();
        layeredPane.revalidate();
    }

    public void pausePanel() {
        // Remove the existing pause overlay if present
        if (nextP != null) {
            layeredPane.remove(nextP);
        }

        // Create the pause panel
        nextP = new JPanel(null);
        nextP.setBounds(319, 196, 362, 408); // Centered
        nextP.setBackground(new Color(0, 0, 0, 150)); // Semi-transparent black

        // Resume Button
        JButton resumeButton = new JButton("Resume");
        resumeButton.setBounds(76, 100, 219, 56);
        resumeButton.addActionListener(e -> {
            layeredPane.remove(nextP);
            layeredPane.repaint();
            fog.resume(); // Resume the FallingObjectsGame
        });

        // Restart Button
        JButton restartButton = new JButton("Restart");
        restartButton.setBounds(76, 180, 219, 56);
        restartButton.addActionListener(e -> {
            layeredPane.remove(nextP);
            layeredPane.repaint();
            catching(); // Restart the current game
        });

        // Audio Button
        JButton audioButton = new JButton("Audio: " + (isOn ? "On" : "Off"));
        audioButton.setBounds(76, 260, 219, 56);
        audioButton.addActionListener(e -> {
            isOn = !isOn; // Toggle audio
            audioButton.setText("Audio: " + (isOn ? "On" : "Off"));
        });

        // Exit Button
        JButton exitButton = new JButton("Exit");
        exitButton.setBounds(76, 340, 219, 56);
        exitButton.addActionListener(e -> {
            System.exit(0); // Exit the application
        });

        // Add buttons to the pause panel
        nextP.add(resumeButton);
        nextP.add(restartButton);
        nextP.add(audioButton);
        nextP.add(exitButton);

        // Add the pause panel to the layered pane
        layeredPane.add(nextP, JLayeredPane.MODAL_LAYER);
        layeredPane.repaint();
        layeredPane.revalidate();
    }


    public void gameOver(){

    }


    private void overlayPanel() {
        JPanel overlay = new JPanel(null);
        overlay.setBackground(Color.decode("#d9f1e1"));
        overlay.setBounds(0, 0, 1000, 800);

        JLabel overlayLabel = new JLabel("Overlay Panel");
        overlayLabel.setBounds(400, 300, 200, 50);
        overlay.add(overlayLabel);

        JButton backButton = new JButton("Back");
        backButton.setBounds(400, 400, 200, 50);
        backButton.addActionListener(e -> {
            if (!panelStack.isEmpty()) {
                JPanel previousPanel = panelStack.pop();
                switchPanel(previousPanel);
            }
        });

        overlay.add(backButton);

        // Push current panel to the stack before switching to overlay
        panelStack.push((JPanel) mainPanel.getComponent(0));
        switchPanel(overlay);
    }

    //flashscreen
    public void titlescreen(){
        p1 = new JPanel();
        p1.setLayout(null);
        p1.setSize(mPanelWidth,mPanelHeight);
        p1.setBackground(Color.decode("#d9e1f1"));
        titleURL = Game.class.getResource("images/pikminsTitlescreen.png");
        if (titleURL != null) {
            titleIcon = new ImageIcon(titleURL);
            Image titlev2 = titleIcon.getImage().getScaledInstance(1000,800, Image.SCALE_SMOOTH);
            titleIcon = new ImageIcon(titlev2);
        }
        title = new JLabel(titleIcon);
        title.setBounds(0,0,mPanelWidth,mPanelHeight);
        p1.add(title);

        start = new JButton();
        start.setBounds(86,618,232,86);
        start.setVisible(true);
        start.setOpaque(false);
        start.setContentAreaFilled(false);
        start.setBorderPainted(false);

        quit = new JButton();
        quit.setBounds(718,618,186,86);

        hs = new JButton(); //highscore
        hs.setBounds(609,10,271,71);
        hs.setVisible(true);
        hs.setOpaque(false);
        hs.setContentAreaFilled(false);
        hs.setBorderPainted(false);

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
        hs.addActionListener(this);
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

    public void highscorePg(){
        p5 = new JPanel(null);
        p5.setBounds(150,100,500,700);
        p5.setBackground(Color.GREEN);
        highscore = new JLabel();

        switchPanel(p5);
    }

    public void catching(){
        gameStage = 1;
        p.resetLives();

        //screen background
        miniGameBGUrl = Game.class.getResource("images/miniGamebackground.png");
        BgIcon = new ImageIcon(miniGameBGUrl);
        Image bgv2 = BgIcon.getImage().getScaledInstance(1000,800, Image.SCALE_SMOOTH);
//        BgIcon = new ImageIcon(bgv2);
//        bg2 = new JLabel(BgIcon);
//        bg2.setBounds(0,0,1000,800);

//        p7 = new JPanel(null) {
//            @Override
//            protected void paintComponent(Graphics g) {
//                super.paintComponent(g);
//                // Draw the image
//                g.drawImage(bgv2, 0, 0, 1000, 800, this);
//            }
//        };
        p7 = new JPanel(null);
        p7.setBackground(Color.decode("#789adb"));
        p7.setBounds(0, 0, 1000, 800);

        // Initialize message
        setMsg(0,p7);
        msgLabels[0].setLocation(0,50);

        // Initialize basket
        pikminUrl = Game.class.getResource("images/pikminFront.png"); // dimensions: 35x55
        pikminIcon = new ImageIcon(pikminUrl);
        Image pikminFrontV2 = pikminIcon.getImage().getScaledInstance(175, 276, Image.SCALE_SMOOTH);
        pikminIcon = new ImageIcon(pikminFrontV2);

        basket = new JLabel(pikminIcon);
        basket.setSize(175,276);
        basket.setLocation(p.getpX(), p.getpY());


        fog = new FallingObjectsGame(p7, basket, p, mainPanel, this);

//        if(p.getHighscore() >=100) {
//            System.out.println("You win!");
//            next();
//        }

        // Add components to the panel
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
        mainPanel.revalidate();
        mainPanel.repaint();
        mainPanel.addKeyListener(this);
        mainPanel.requestFocusInWindow(); // Re-assert focus to ensure KeyListener works
    }

    public void bake(){
        gameStage = 3;
        p.resetLives();

        mainPanel.removeAll();
        mainPanel.revalidate();
        mainPanel.repaint();
        mainPanel.requestFocusInWindow(); // Re-assert focus to ensure KeyListener works
    }

    @Override
    public void onGameWin() {
        // Handle the game win event
        System.out.println("You win! Transitioning to the next stage...");
        next();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == start) {
            recipeList();
        }

        if (e.getSource() == pause) {
            System.out.println("On pause panel");
        }

        if (e.getSource() == quit) {
            if (JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Pikmin", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(null, "Closing game..");
                System.exit(0);
            }
        }

        if (e.getSource() == hs){
            highscorePg();
        }

        if(e.getSource() == recipe1){
            recipe1();
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

        String command = e.getActionCommand();

        if ("NEXT_STAGE_1".equals(command)) {
            mix(); // Call mix() method
            layeredPane.remove(nextP);// Remove overlay
            layeredPane.repaint();
            System.out.println("Next clicked for mix");
        }
        else if("NEXT_STAGE_2".equals(command)){
            layeredPane.remove(nextP);// Remove overlay
            layeredPane.repaint();
            System.out.println("Going to gamestage 3");
        }
    }

    public void keyPressed(KeyEvent e){
System.out.println(gameStage);
        switch(gameStage) {
            case 1:
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        pikminUrl = Game.class.getResource("images/pikminLeft.png"); // dimensions: 35x55
                        basket.setIcon(pikminIcon);
                        p.moveL();
                        basket.setLocation(p.getpX(), p.getpY());
                        if(msgLabels[0]!=null){
                            msgLabels[0].setVisible(false);
                        }

                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        pikminUrl = Game.class.getResource("images/pikminRight.png"); // dimensions: 35x55
                        basket.setIcon(pikminIcon);
                        p.moveR();
                        basket.setLocation(p.getpX(), p.getpY());
                        if(msgLabels[0]!=null){
                            msgLabels[0].setVisible(false);
                        }
                }

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.out.println("ESC pressed");
                    if (fog != null) {
                        fog = null;
                        System.gc();
                    }
                    titlescreen();
                }
                break;
            case 2:
                if (e.getKeyCode() == KeyEvent.VK_A) {
                    mixGame.handleKeyPress('a');
                    System.out.println("a pressed");
                }
                else if (e.getKeyCode() == KeyEvent.VK_W) {
                    mixGame.handleKeyPress('w');
                    System.out.println("w pressed");
                }
                else if (e.getKeyCode() == KeyEvent.VK_S) {
                    mixGame.handleKeyPress('s');
                    System.out.println("s pressed");
                }
                else if (e.getKeyCode() == KeyEvent.VK_D) {
                    mixGame.handleKeyPress('d');
                    System.out.println("d pressed");
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
