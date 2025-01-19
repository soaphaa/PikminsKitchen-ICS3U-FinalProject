import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.io.*;

import static javax.swing.BorderFactory.createLineBorder;

public class Game extends JFrame implements ActionListener,KeyListener{

    // Instance variables
    public static String message;

    // Settings
    final int tile = 16; // 16x16 pixel sprites
    final int tileScale = 3;
    final int tileSize = tile * tileScale; // makes sprites scaled to 48x48
    final int frameLength = 1000;
    final int frameWidth = 800;
    boolean isOn = true;

    //player
    Player p;

    //catching items minigame
    FallingObjectsGame fog;
    private boolean hasCollided = false;

    // GUI JFrame
    JFrame frame;
    JPanel p1, p2, p3, p4, p5, p6, p7;
    static JLabel title, aON, aOFF, bg1, bg2, basket, fallingObject, highscore;
    JButton start, quit, settings, hs, audio, back, recipe1, startRecipe, exitRecipe, next;
    ImageIcon titleIcon, audioOn, audioOff, recipeBg, pikminIcon, ingredientIcon, BgIcon;
    static URL titleURL, audioUrl, audio2Url, bg1Url, pikminUrl, ingredientUrl, miniGameBGUrl;
    JLayeredPane pane1, pane2;
    JProgressBar pb, pb2;

    //child class objects
    ArrayList<Step> r1Steps = new ArrayList<>();
    ArrayList<Ingredient> r1Ingredients = new ArrayList<>();
    Mix mixGame;
    Ingredient flour, milk, eggs, chocolateChips, sugar;

    //game stage
    int gameStage;


    public void setMessage(String m) {
        message = m;
    }

    public String getMessage() {
        return message;
    }

    public Game() {
        message = " ";

        // Setting the base Frame
        frame = new JFrame("Pikmin's Kitchen!");
        frame.setSize(frameLength, frameWidth);
        frame.setLayout(null); // Use null layout
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        p = new Player(500);

        gameStage = 0;

        this.titlescreen();

        frame.addKeyListener(this);  // Add key listener to the frame
        //highscores File I/O
        File highscoreFile = new File ("highscore.txt");
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

    //flashscreen
    public void titlescreen(){
        p1 = new JPanel();
        p1.setLayout(null);
        p1.setSize(frameLength,frameWidth);
        p1.setBackground(Color.decode("#d9e1f1"));
        titleURL = Game.class.getResource("images/pikminsTitlescreen.png");
        if (titleURL != null) {
            titleIcon = new ImageIcon(titleURL);
            Image titlev2 = titleIcon.getImage().getScaledInstance(1000,800, Image.SCALE_SMOOTH);
            titleIcon = new ImageIcon(titlev2);
        }
        title = new JLabel(titleIcon);
        title.setBounds(0,0,frameLength,frameWidth);
        p1.add(title);

        start = new JButton();
        start.setBounds(86,618,232,86);
        start.setVisible(true);
        start.setOpaque(false);
        start.setContentAreaFilled(false);
        start.setBorderPainted(false);

        settings = new JButton();
        settings.setBounds(357,618,307,86);

        settings.setVisible(true);
        settings.setOpaque(false);
        settings.setContentAreaFilled(false);
        settings.setBorderPainted(false);

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
        settings.addActionListener(this);
        hs.addActionListener(this);
        audio.addActionListener(this);

        p1.add(start);
        p1.add(settings);
        p1.add(quit);
        p1.add(hs);
        p1.add(audio);


        frame.getContentPane().removeAll(); // Remove existing content
        frame.add(p1);
        // Revalidate and repaint to ensure changes are displayed
        frame.revalidate(); // Refresh layout
        frame.repaint(); // Redraw frame
        frame.requestFocusInWindow(); // Re-assert focus to ensure KeyListener works.

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

        // Revalidate and repaint to ensure changes are displayed
        frame.getContentPane().removeAll(); // Remove existing content
        frame.add(p2); //add the new panel
        frame.revalidate(); // Refresh layout
        frame.repaint(); // Redraw frame
        frame.requestFocusInWindow(); // Re-assert focus to ensure KeyListener works.
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

        frame.getContentPane().removeAll(); // Remove existing content
        frame.add(p3); //add the new panel
        frame.revalidate(); // Refresh layout
        frame.repaint(); // Redraw frame
        frame.requestFocusInWindow(); // Re-assert focus to ensure KeyListener works.
    }

    public void highscorePg(){
        p5 = new JPanel(null);
        p5.setBounds(150,100,500,700);
        p5.setBackground(Color.GREEN);
        highscore = new JLabel();

        frame.add(p5);
    }

    public void settings(){
        p6 = new JPanel(null);

    }

    public void catching(){
        p7 = new JPanel(null);
        p7.setBounds(0, 0, 1000, 800);

        gameStage = 1;

        //screen background
        miniGameBGUrl = Game.class.getResource("images/miniGamebackground.png");
        BgIcon = new ImageIcon(miniGameBGUrl);
        Image bgv2 = BgIcon.getImage().getScaledInstance(1000,800, Image.SCALE_SMOOTH);
        BgIcon = new ImageIcon(bgv2);
        bg2 = new JLabel(BgIcon);
        bg2.setBounds(0,0,1000,800);

        // Initialize basket
        pikminUrl = Game.class.getResource("images/pikminFront.png"); // dimensions: 35x55
        pikminIcon = new ImageIcon(pikminUrl);
        Image pikminFrontV2 = pikminIcon.getImage().getScaledInstance(175, 276, Image.SCALE_SMOOTH);
        pikminIcon = new ImageIcon(pikminFrontV2);

        basket = new JLabel(pikminIcon);
        basket.setSize(175,276);
        basket.setLocation(p.getpX(), p.getpY());

        fog = new FallingObjectsGame(p7, basket, p, frame);

        System.out.println("out of the object");
        System.out.println(p.getHighscore());

        // Add components to the panel
        p7.add(basket);
        //p7.add(bg2);

//        //Ensure frame gets focus to listen to key events
//        frame.setFocusable(true);
//        frame.requestFocusInWindow();  // Request focus on the frame

        // Refresh the panel and frame layout
        frame.getContentPane().removeAll();
        frame.add(p7);
        frame.revalidate();
        frame.repaint();
        frame.requestFocusInWindow(); // Re-assert focus to ensure KeyListener works.

    }

    public void mix(){
        gameStage = 2;

        mixGame = new Mix(frame);
        r1Steps.add(mixGame);

        frame.getContentPane().removeAll();
        frame.revalidate();
        frame.repaint();
        frame.requestFocusInWindow(); // Re-assert focus to ensure KeyListener works.
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == start) {
            recipeList();
        }
        if (e.getSource() == quit) {
            if (JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Pikmin", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(null, "Closing game..");
                System.exit(0);
            }
        }
        if (e.getSource() == settings) {

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

//            r1Steps.add(cut = new Chop());
//            r1Steps.add(mix = new Mix(frame));
//            r1Steps.add(bake = new Bake());

            r1Ingredients.add(flour = new Ingredient("flour"));
            r1Ingredients.add(sugar = new Ingredient("sugar"));
            r1Ingredients.add(milk = new Ingredient("milk"));
            r1Ingredients.add(eggs = new Ingredient("eggs"));
            r1Ingredients.add(chocolateChips = new Ingredient("chocolate"));

            catching();

        }
        if(e.getSource() == next){

        }
    }

    public void keyPressed(KeyEvent e){

        switch(gameStage) {
            case 1:
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    if (p7.isVisible()) {
                        pikminUrl = Game.class.getResource("images/pikminLeft.png"); // dimensions: 35x55
                        basket.setIcon(pikminIcon);
                        p.moveL();
                        basket.setLocation(p.getpX(), p.getpY());
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    if (p7.isVisible()) {

                        pikminUrl = Game.class.getResource("images/pikminRight.png"); // dimensions: 35x55
                        basket.setIcon(pikminIcon);
                        p.moveR();
                        basket.setLocation(p.getpX(), p.getpY());
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
                    mixGame.displayImage(1);
                    System.out.println("a pressed");
                }
                if (e.getKeyCode() == KeyEvent.VK_W) {
                    mixGame.handleKeyPress('w');
                    mixGame.displayImage(2);
                    System.out.println("w pressed");
                }
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    mixGame.handleKeyPress('s');
                    mixGame.displayImage(3);
                    System.out.println("s pressed");
                }
                if (e.getKeyCode() == KeyEvent.VK_D) {
                    mixGame.handleKeyPress('d');
                    mixGame.displayImage(4);
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
