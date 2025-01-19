public class Player {
    private int highscore;
    private int pY;
    private int pX;
    private int lives;

    public Player(){
        this(500);
    }

    public Player(int x_){
        highscore = 0;
        pX = x_;
        pY = 500;
        lives = 3;
    }

    public int getHighscore(){
        return highscore;
    }

    public void setHighscore(int hs){
        highscore = hs;
    }

    public void incHighscore(){
        highscore+=10;
    }

    public void incHighscoreDouble(){
        highscore+=20;
    }

    public void decreaseHighscore(){
        highscore-=10;
    }

    public void decreaseLives(){
        if(lives >0){
            System.out.println("GAME OVER!");
            //use GAME OVER panel in GAME
        }
        lives--;
    }

    public void resetLives(){
        lives = 3;
    }

    public int getpX(){
        return pX;
    }

    public int getpY(){
        return pY;
    }

    public void moveL(){
        if (pX>=0){
            pX-=10;
        }
    }

    public void moveR(){
        if(pX<=800){
            pX+=10;
        }
    }


}
