import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.Timer;
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
    JLabel title, aON, aOFF, bg1, basket, fallingObject, highscore;
    JButton start, quit, settings, hs, audio, back, recipe1, startRecipe, exitRecipe, next;
    ImageIcon titleIcon, audioOn, audioOff, recipeBg, pikminIcon, ingredientIcon;
    static URL titleURL, audioUrl, audio2Url, bg1Url, pikminUrl, ingredientUrl;
    JLayeredPane pane1, pane2;
    JProgressBar pb;

    //child class objects
    ArrayList<Step> r1Steps = new ArrayList<>();
    ArrayList<Ingredient> r1Ingredients = new ArrayList<>();
    Step chop,stir,cut,bake;
    Ingredient flour, milk, eggs, chocolateChips, sugar;


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
        p7.setBackground(Color.GREEN);

        fog = new FallingObjectsGame(p7);
        //fog.spawnFallingObject();

        pb = new JProgressBar(0, 100); //score progress bar
        pb.setValue(0);
        pb.setStringPainted(false);  // Don't show percentage on bar
        int currentValue = pb.getValue();
        pb.setBounds(25,10,950,10);
        p7.add(pb);

        // Initialize basket (pikmin icon)
        pikminUrl = Game.class.getResource("images/pikminFront.png"); // dimensions: 35x55
        pikminIcon = new ImageIcon(pikminUrl);
        Image pikminFrontV2 = pikminIcon.getImage().getScaledInstance(175, 276, Image.SCALE_SMOOTH);
        pikminIcon = new ImageIcon(pikminFrontV2);

        basket = new JLabel(pikminIcon);
        basket.setSize(175,276);
        basket.setLocation(p.getpX(), p.getpY());

        // Timer for falling object animation
//        timer = new Timer(50, new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                fallingObject.setVisible(true);
//                //pb.setValue(p.getHighscore()*10);
//
//                fallingObject.setVisible(true); //return it to visible at the top
//
//
//                if(!hasCollided) {
//                    iY += 15; //speed the object falls
//                    if(fallingObject.getBounds().intersects(basket.getBounds())){
//                        p.incHighscore();
//                        pb.setValue(p.getHighscore());  // Increase by 10
//                        hasCollided = true; // Mark as collided
//                        System.out.println("falling val after collision: " + iY + " basket val " + p.getpY());
//                        iY = 0;
//                    }
//
//                    if (iY > p7.getHeight()) {  // Check if it goes off the screen
//                        iY = 0;  // Reset to the top if it falls off the screen
//                    }
//
//                    fallingObject.setLocation(x, iY); // Update the falling object's position
//                    System.out.println("falling val whenFalling: " + iY + " basket val " + p.getpY());
//                    fallingObject.setLocation(x,iY);
//                    basket.setLocation(p.getpX(), p.getpY());
//                    hasCollided = false;
//
//                    if(pb.getValue() >=100){
//                        fallingObject.setVisible(false);
//                        System.out.println("You did it! 5/5, JButton Next");
//                    }
//                }
//            }
//        });


        System.out.println(p.getHighscore());
        // Initial random X position for the falling object


        // Add components to the panel
        p7.add(basket);

         //Ensure frame gets focus to listen to key events
        frame.addKeyListener(this);  // Add key listener to the frame
        frame.setFocusable(true);
        frame.requestFocusInWindow();  // Request focus on the frame

        // Refresh the panel and frame layout
        frame.getContentPane().removeAll();
        frame.add(p7);
        frame.revalidate();
        frame.repaint();
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

            r1Steps.add(cut = new Chop());
            r1Steps.add(stir = new Stir());
            r1Steps.add(bake = new Bake());

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
        if (e.getKeyCode() == KeyEvent.VK_LEFT){
            if(p7.isVisible()) {
                pikminUrl = Game.class.getResource("images/pikminLeft.png"); // dimensions: 35x55
                p.moveL();
                basket.setLocation(p.getpX(),p.getpY());
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            if(p7.isVisible()){

                pikminUrl = Game.class.getResource("images/pikminRight.png"); // dimensions: 35x55
                p.moveR();
                basket.setLocation(p.getpX(),p.getpY());
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
