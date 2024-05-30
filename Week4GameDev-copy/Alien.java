import java.util.Random;

public class Alien extends Sprite2D {
    // Constructor using super() for the image
    public Alien(String imagePath, String imagePath2) {
        super(imagePath, imagePath2);
    }

    // Move the alien
    public void move() {
        x += xSpeed; // Use the xSpeed set by setXSpeed
    }

    // not used this week
    public void checkCollision() {
        // 
    }
    //function that is called with an int of downstep inputted
    public void moveDown(int downStep) {
        y += downStep;
    }
}