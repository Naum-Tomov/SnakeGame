import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class SnakePanel extends JPanel implements ActionListener {

    static final int WIDTH = 1280;
    static final int HEIGHT = 720;
    static final int UNIT_SIZE = 20;
    int delay = 50;
    final int x[] = new int[(WIDTH/UNIT_SIZE) + 1];
    final int y[] = new int[(HEIGHT/UNIT_SIZE) + 1];
    int bodyParts = 6;
    int applesEaten = 0;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Random random;
    Timer timer;

    /**
     * Constructor for the Snake Panel class.
     */
    public SnakePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    /**
     * Starts the game.
     */
    public void startGame() {
        running = true;
        newApple();
        timer = new Timer(delay,this);
        timer.start();
    }

    /**
     * Sets the coordinates of the apple to random coordinates.
     * @ensures the apple will appear on the panel
     */
    public void newApple() {
        appleX = random.nextInt((int)(WIDTH/UNIT_SIZE))*UNIT_SIZE;
        appleY = random.nextInt((int)(HEIGHT/UNIT_SIZE))*UNIT_SIZE;
    }

    /**
     * Paints component.
     * @param g - the graphics used for drawing;
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    /**
     * Creates the GUI for the game.
     * @param g - the graphics used for drawing;
     */
    public void draw(Graphics g) {
        if (running) {
            // score
            g.setColor(Color.red);
            g.setFont( new Font("Helvetica",Font.BOLD, 20));
            FontMetrics metrics1 = getFontMetrics(g.getFont());
            g.drawString("Score: "+applesEaten, (WIDTH - metrics1.stringWidth("Score: "+applesEaten))/2, UNIT_SIZE);
            // draw apple
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // draw snake head
            g.setColor(new Color(0, 90, 150));
            g.fillRect(x[0], y[0], UNIT_SIZE, UNIT_SIZE);

            // draw snake body
            g.setColor(new Color(50, 255, 185));
            for (int i = bodyParts - 1; i > 0; i--) {
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            // draw snake tail
            g.fillArc(x[bodyParts], y[bodyParts], UNIT_SIZE, UNIT_SIZE, 120, 120);
        }
        else {
            gameOver(g);
        }
    }

    /**
     * Moves the snake in the current set direction
     * @requires direction == 'R' || direction == 'L' || direction == 'U' || direction == 'D'
     */
    public void move() {
        // each body part goes to the next
        for (int bodyPart = bodyParts; bodyPart > 0; bodyPart--) {
            x[bodyPart] = x[bodyPart - 1];
            y[bodyPart] = y[bodyPart - 1];
        }
        switch(direction) {
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;

        }
    }

    public void checkCollisions() {
        // check for body collision
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }

        // check for upper wall collision
        if (y[0] < 0) {
            running = false;
        }
        // check for lower wall collision
        if (y[0] >= HEIGHT) {
            running = false;
        }
        // check for right wall collision
        if (x[0] < 0) {
            running = false;
        }
        // check for left wall collision
        if (x[0] >= WIDTH) {
            running = false;
        }
        
        if (!running) {
            System.out.println("ded");
            timer.stop();
        }
        
    }

    public void gameOver(Graphics g) {
        //Score
        g.setColor(Color.red);
        g.setFont( new Font("Helvetica",Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: "+applesEaten, (WIDTH - metrics1.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
        //Game Over text
        g.setColor(Color.red);
        g.setFont( new Font("Helvetica",Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (WIDTH - metrics2.stringWidth("Game Over"))/2, HEIGHT/2);
    }

    public void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            applesEaten++;
            bodyParts++;
            newApple();
        }
    }


    /**
     * Invoked when an action occurs.
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if(running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();

    }


    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e){
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                     break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                      break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }

}
