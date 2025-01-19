import java.awt.*;
import javax.swing.*;
import java.net.*;

public class Mix extends Step {

    int pos; //keyPressed determines the position
    int[] sequence; //movement sequence
    boolean correctPos;

    private String[] imagePaths; //to store the different image paths
    private String imgPath; //stores the chosen img

    //GUI
    Frame frame;
    JPanel panel;
    URL imgUrl, bgUrl;
    ImageIcon image, bg;
    JLabel[] imgLabel;

    public Mix(JFrame jframe) {
        super("Mixing");
        frame = jframe;

        bgUrl = Mix.class.getResource("images/miniGameBackground.png");
        bg = new ImageIcon(bgUrl);
        Image scaledimg = bg.getImage().getScaledInstance(700, 700, Image.SCALE_SMOOTH);

        JPanel panel = new JPanel() {
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

        correctPos = false;

        sequence = new int[4];

        imagePaths = new String[]{"images/whisk.png", "images/a.png", "images/w.png", "images/s.png", "images/d.png"};
        imgLabel = new JLabel[5];

        for(int i = 0; i<5; i++) {
            System.out.println(i);
            imgUrl = Mix.class.getResource(imagePaths[i]);
            image = new ImageIcon(imgUrl);
            System.out.println(i + "one");
            Image scaledImage = image.getImage().getScaledInstance(500, 500, Image.SCALE_SMOOTH);
            image = new ImageIcon(scaledImage);
            System.out.println(i + "two");
            imgLabel[i] = new JLabel(image);
            imgLabel[i].setSize(500,500);
            imgLabel[i].setLocation(250, 100);
            System.out.println(i + "three");
        }

        displayImage(0); //display the default image at index 0
    }

    public void displayImage(int index){
        imgLabel[index].setVisible(true);
        panel.add(imgLabel[index]);
        frame.add(panel); //add the panel to the main JFrame in the Game class
    }

    public int getPos(){
        return pos;
    }

    public void setPos(int p){
        pos = pos;
    }

    public void setSequence(){
        for(int i = 1; i<=4; i++){
            sequence[i] = i;
        }
    }

    public void updatePos(){
       imgPath = imagePaths[pos];
    }

    //handle the key  pressed
    public void handleKeyPress(char keyChar) {
        int keyIndex = -1;
        switch (keyChar) {
            case 'a':
                keyIndex = 1;
                break;
            case 'w':
                keyIndex = 2;
                break;
            case 's':
                keyIndex = 3;
                break;
            case 'd':
                keyIndex = 4;
                break;
        }

        if(keyIndex == sequence[pos]) {
            pos++;
            if (pos >= sequence.length) {
                pos = 0;
                setSequence(); // Generate a new sequence
            }
            updatePos();
        }
    }
}
