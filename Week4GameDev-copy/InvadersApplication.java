import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class InvadersApplication extends JFrame implements Runnable, KeyListener {
    //
    public enum GameState {
        MENU,
        IN_PROGRESS,
        GAME_OVER
    }
    Boolean isGameInProgress;
    private GameState currentState;

    private int currentAlienWave = 1;
    private double alienSpeedIncrease = 1.2; // the multiplier of how fast the alien wave get
    private boolean playerAlive = true;
    private final int NUM_ALIENS = 24;
    private Spaceship playerShip;
    private Thread gameThread;
    private BufferStrategy strategy;
    private Alien[] aliens;
    private boolean movingRight = true;
    private final int DOWN_STEP = 20;
    private final int ALIEN_WIDTH = 60;
    private Image bulletImage;
    private ArrayList<PlayerBullet> bulletlist = new ArrayList<>();
    private Image menu_Image;
    private Image gameOver_Image;
    public int player_Score;

    public InvadersApplication() {
        // Setup the game window
        this.setTitle("Space Invaders");
        this.setSize(1200, 900);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addKeyListener(this);
        //menu image
        ImageIcon image1 = new ImageIcon("C:/Users/leoma/Desktop/Week4GameDev-copy/Menu_Logo.png");
        menu_Image= image1.getImage();

        //game over image
        ImageIcon image2 = new ImageIcon("C:/Users/leoma/Desktop/Week4GameDev-copy/Game_Over.jpg");
        gameOver_Image= image2.getImage();

        // Initialize player's ship
        playerShip = new Spaceship("player_ship.png", "player_ship.png");
        playerShip.setPosition(600, 800);

        // Initialize aliens
        int aliensPerRow = 6; //how many per row
        int verticalRowHeight = 60; // The vertical space between rows of aliens
        aliens = new Alien[NUM_ALIENS]; //intitalise

        //loop to get the square formation of the aliens
        for (int i = 0; i < NUM_ALIENS; i++) {
            int row = i / aliensPerRow; // Calculate the current row
            int col = i % aliensPerRow; // Calculate the current column

            aliens[i] = new Alien("alien_ship_1.png", "alien_ship_2.png");

            // Set position with 50 pixels as the initial offset, 100 pixels between aliens horizontally, and rowHeight pixels between rows
            aliens[i].setPosition(50 + col * 100, 50 + row * verticalRowHeight); // calculates where the aliens should be based on set values for spacing and the variables like row and column
        }

        // Make sure the JFrame is visible before creating the BufferStrategy
        this.setVisible(true);
        this.createBufferStrategy(2);
        this.strategy = getBufferStrategy();

        // Start the game loop in a new thread
        gameThread = new Thread(this);
        gameThread.start();
        currentState = GameState.MENU;
    }

    @Override
    public void run() {
        while (true) {
            try {
                gameUpdate();
                gameRender();
                Thread.sleep(20);//how often it refreshes
            } catch (InterruptedException e) {//error handing for thethreads
                System.out.println("Thread interrupted: " + e.getMessage());
            }
        }
    }

    private void gameUpdate() {
        if (currentState == GameState.IN_PROGRESS && playerAlive) {
            // Move the player's ship
            playerShip.move();

            // Move each alien
            for (Alien alien : aliens) {
                alien.move();
            }

            // Check if aliens reach the edge of the screen and update their direction
            boolean edgeReached = false;
            for (Alien alien : aliens) {
                if (movingRight && alien.getX() + ALIEN_WIDTH >= this.getWidth()) {
                    edgeReached = true;
                    break;
                } else if (!movingRight && alien.getX() <= 0) {
                    edgeReached = true;
                    break;
                }
            }

            if (edgeReached) {
                movingRight = !movingRight; // Change direction
                for (Alien alien : aliens) {
                    alien.moveDown(DOWN_STEP);
                }
            }

            // Update alien movement direction and speed based on the current wave
            for (Alien alien : aliens) {
                alien.setXSpeed(movingRight ? 5 * Math.pow(alienSpeedIncrease, currentAlienWave - 1) : -5 * Math.pow(alienSpeedIncrease, currentAlienWave - 1));
                alien.move();
            }

            // Check for collisions between the player's bullets and aliens
            for (int i = 0; i < bulletlist.size(); i++) {
                PlayerBullet bullet = bulletlist.get(i);
                bullet.move(); // Move the bullet

                for (int j = 0; j < aliens.length; j++) {
                    Alien alien = aliens[j];
                    if (alien.isAlive() && isColliding(bullet, alien)) {
                        alien.removeSprite(); // Alien is hit
                        bulletlist.remove(i); // Remove the bullet
                        i--; // Adjust the loop index after removal
                        player_Score += 10; // Increase player score
                        break; // Stop checking collisions for this bullet
                    }
                }
            }

            // Check for collisions between the player's ship and any alien
            for (Alien alien : aliens) {
                if (alien.isAlive() && isColliding(playerShip, alien)) {
                    playerAlive = false; // Player is hit
                    break; // No need to check other aliens
                }
            }

            // If the player is not alive, transition to the GAME_OVER state
            if (!playerAlive) {
                currentState = GameState.GAME_OVER;
            }

            // Check if all aliens are dead to start a new wave
            boolean allAliensDead = true;
            for (Alien alien : aliens) {
                if (alien.isAlive()) {
                    allAliensDead = false;
                    break;
                }
            }

            // If all aliens are dead, start a new wave
            if (allAliensDead) {
                startNewWave();
            }
        }
    }

    private void gameRender() {
        //generalsetup needed for every game state
        // Get the drawing graphics from the buffer strategy
        Graphics g = strategy.getDrawGraphics();

        // Clear the screen
        g.setColor(Color.black);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        if (currentState == GameState.MENU) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Press ENTER to Start", 500, 750);
            g.drawString("10 points per alien", 500, 800);

            g.drawImage(menu_Image, 320, 100, 600, 600, null);
        }
        else if (currentState == GameState.IN_PROGRESS) {
            // Draw the game objects
            playerShip.paint(g);
            for (Alien alien : aliens) {
                if(alien != null && alien.isAlive()){//checks if the alien exists and is alive (not shot)
                    alien.paint(g);//paints it if its alive only
                }
            }
            for (int i = 0; i < bulletlist.size(); i++) {
                PlayerBullet bullet = (PlayerBullet) bulletlist.get(i);
                bullet.paint(g);
            }
        }else if(currentState == GameState.GAME_OVER){ 
            g.drawImage(gameOver_Image, 320, 0, 600, 600, null);

        }

        g.setColor(Color.GREEN);

        // Set the font for the score display
        g.setFont(new Font("Arial", Font.BOLD, 20));//sets green bold text at bottom left of screen in the IN_PROGESS state.

        // Draw the score in the bottom left corner of the screen
        String scoreText = "Score: " + player_Score;
        g.drawString(scoreText, 10, this.getHeight() - 10);
        // Dispose of the graphics (double buffering)
        g.dispose();
        strategy.show();
        Toolkit.getDefaultToolkit().sync();
    }

    // KeyListener methods here
    @Override
    public void keyPressed(KeyEvent e) {
        // Add a case to start the game when a specific key is pressed, e.g., ENTER
        if (currentState == GameState.MENU && e.getKeyCode() == KeyEvent.VK_ENTER) {
            currentState = GameState.IN_PROGRESS;
        } else if (currentState == GameState.IN_PROGRESS) {
            // Existing game control code...
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                playerShip.setXSpeed(-9);
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                playerShip.setXSpeed(9);
            }

            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                shootBullet();
            }
        }else if(currentState == GameState.GAME_OVER && e.getKeyCode() == KeyEvent.VK_SPACE) {
            currentState = GameState.IN_PROGRESS;
            //code for transitioninig to the game over state when the player dies
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
            playerShip.setXSpeed(0);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    public void startNewWave() {
        currentAlienWave++;
        for (int i = 0; i < NUM_ALIENS; i++) {
            int row = i / (NUM_ALIENS / 6); // Assuming 6 aliens per row as before
            int col = i % (NUM_ALIENS / 6);
            aliens[i] = new Alien("alien_ship_1.png", "alien_ship_2.png");
            aliens[i].setPosition(50 + col * 100, 50 + row * 60); // Reuse your positioning logic
            aliens[i].setXSpeed((movingRight ? 5 : -5) * Math.pow(alienSpeedIncrease, currentAlienWave - 1)); // Increase speed each wave
        }
    }

    public void startNewGame() {
        playerAlive = true;
        player_Score = 0;
        currentAlienWave = 1; // Reset to first wave
        startNewWave(); // Start with the first wave of aliens
        currentState = GameState.IN_PROGRESS;
        // Reset any other necessary game state (e.g., player position, clear bullets)
    }
    
    public void shootBullet() {
        // add a new bullet to our list
        double bulletX = playerShip.getX();
        double bulletY = playerShip.getY(); // Start bullet ay playerships y coordinate
        PlayerBullet b = new PlayerBullet("bullet.png", "bullet.png");//needs 2 inputs as the sprite2D super class has 2
 
        b.setPosition(playerShip.x + 25, playerShip.y);
        bulletlist.add(b);
    }

    private boolean isColliding(Sprite2D bullet, Sprite2D alien) {
        //the edges of the bullet
        int x1 = (int) bullet.getX();
        int y1 = (int) bullet.getY();
        int w1 = bullet.getWidth();
        int h1 = bullet.getHeight();
        //the edges of the alien
        int x2 = (int) alien.getX();
        int y2 = (int) alien.getY();
        int w2 = alien.getWidth();
        int h2 = alien.getHeight();

        return ((x1 < x2 && x1 + w1 > x2) || (x2 < x1 && x2 + w2 > x1)) &&
        ((y1 < y2 && y1 + h1 > y2) || (y2 < y1 && y2 + h2 > y1));
    }

    // Main method, the Application is initialzed here.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new InvadersApplication();
                }
            });
    }
}
