import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

//parent class that has the implementation for the general functions that MOST sprite2D's need
public class Sprite2D {
    // Member variables
    protected double x, y; // Sprite position
    protected double xSpeed; // Sprite horizontal speed
    protected Image myImage1, myImage2; // Image for the sprite
    protected int framesDrawn; // Frame counter for the 2 image animation
     private boolean isAlive = true;
    // Constructor
    public Sprite2D(String imagePath1, String imagePath2) {
        framesDrawn = 0; // Initialize the frame counter
        ImageIcon icon1 = new ImageIcon(imagePath1);
        myImage1 = icon1.getImage(); // Load the first image
        ImageIcon icon2 = new ImageIcon(imagePath2);
        myImage2 = icon2.getImage(); // Load the second image
    }

    public double getX() {
        return x;
    }

    // Getter for the y coordinate
    public double getY() {
        return y;
    }

    // Set the sprite's position
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    
    // Set the sprite's horizontal speed
    public void setXSpeed(double dx) {
        xSpeed = dx;
    }
    
    public int getWidth() {
        // Assuming myImage1 is always set and is the primary image used for the sprite
        if (myImage1 != null) {
            return myImage1.getWidth(null); // ImageObserver is null, we're not dealing with asynchronous image loading
        }
        return 0; // Return 0 or some default value if the image is not set
    }

    public int getHeight() {
        if (myImage1 != null) {
            return myImage1.getHeight(null); // 
        }
        return 0; // Return 0 or a default value if the image is not set
    }
    //checks if isalive mainly for aliens class
    public boolean isAlive() {
        return isAlive;
    }
    //dont delete the spite just dont paint is. this keeps track 
    public void removeSprite() {
        isAlive = false;
    }
    
    // Paint the sprite
    public void paint(Graphics g) {
        framesDrawn++;
        if ( framesDrawn%20 < 10)
            g.drawImage(myImage1, (int)x, (int)y, null);
        else
            g.drawImage(myImage2, (int)x, (int)y, null);
    }

}
