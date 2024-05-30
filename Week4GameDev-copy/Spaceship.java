public class Spaceship extends Sprite2D {
    // Constructor
    public Spaceship(String imagePath1, String imagePath2) {
        super(imagePath1 ,imagePath2 );
    }

    // Move function that is specific for the Spaceship
    public void move() {
        // Spaceship movement logic here
        x += xSpeed;
        if (x < 0) x = 0; //so the spaceship stops when it reaches the left border 
        if (x > 1130) x = 1130; // same for right border as 1130 is an approx
    }
    
    public void TestMethod() {
        int width = getWidth(); // This will call the getWidth method from Sprite2D
        System.out.println("The width of the spaceship is: " + width);
    }

}