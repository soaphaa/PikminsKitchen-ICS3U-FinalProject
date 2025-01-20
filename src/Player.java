public class Player {
    private int highscore;
    private int score; //temporary score
    private int pY;
    private int pX;
    private int lives;
    private GameEventListener l;

    public Player(GameEventListener listener){
        this(500, listener);
    }

    public Player(int x_, GameEventListener listener){
        l = listener;
        highscore = 0;
        score = 0;
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

    public void setScore(int s){
        score = s;
    }

    public int getScore(){
        return score;
    }

    public void incScore(){
        score+=10;
    }

    public void incScoreDouble(){
        score+=20;
    }

    public void decreaseScore(){
        score-=10;
    }

    public void decreaseLives(){
        if(lives < 1){
            l.onGameLose();
        }
        lives--;
    }

    public int getLives(){
        return lives;
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
