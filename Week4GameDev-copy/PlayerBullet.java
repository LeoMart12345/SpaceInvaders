public class PlayerBullet extends Sprite2D {
    // Constructor
    public PlayerBullet(String imagePath1, String imagePath2) {
        super(imagePath1 ,imagePath2 );
        
    }

    // Move function that is specific for the Spaceship
    public void move() {
        // Spaceship movement logic here
        y -= 10;
    }
}