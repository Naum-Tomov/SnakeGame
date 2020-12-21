import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class SnakePanel extends JPanel implements ActionListener {

    static final int WIDTH = 1280;
    static final int HEIGHT = 720;
    static final int UNIT_SIZE = 20;
    int delay;
    final int[] x = new int[(WIDTH*HEIGHT/UNIT_SIZE)];
    final int[] y = new int[(HEIGHT*WIDTH/UNIT_SIZE)];
    int bodyParts;
    int applesEaten;
    int appleX;
    int appleY;
    char direction;
    volatile boolean running = false;
    Random random;
    Timer timer;
    Queue<Character> commandQueue;




    /**
     * Constructor for the Snake Panel class.
     */
    public SnakePanel() {
        commandQueue = new LinkedList<>();
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
        x[0] = 0;
        y[0] = 0;
        applesEaten = 0;
        bodyParts = 6;
        delay = 50;
        direction = 'R';
        running = true;
        newApple();
        timer = new Timer(delay,this);
        timer.start();
    }

    public void reset() {
        timer.stop();
        startGame();
    }


    /**
     * Sets the coordinates of the apple to random coordinates.
     * @ensures the apple will not spawn inside the body of the snake.
     * @ensures the apple will appear on the panel
     */
    public void newApple() {
        boolean inBody = true;
        while (inBody){
            appleX = random.nextInt(WIDTH/UNIT_SIZE)*UNIT_SIZE;
            appleY = random.nextInt(HEIGHT/UNIT_SIZE)*UNIT_SIZE;
            for (int f = 1; f<bodyParts; f++) {
                inBody = appleX == x[f] && appleY == y[f];
            }
        }
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
            g.setColor(Color.white);
            g.setFont( new Font("Helvetica",Font.BOLD, 20));
            FontMetrics metrics1 = getFontMetrics(g.getFont());
            g.drawString("Score: "+applesEaten, (WIDTH - metrics1.stringWidth("Score: "+applesEaten))/2, UNIT_SIZE - 3);
            // draw apple
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // draw snake head
            g.setColor(Color.green);
            g.fillOval(x[0], y[0], UNIT_SIZE, UNIT_SIZE);

            // draw snake body
            for (int i = bodyParts - 2; i > 0; i--) {
                if (i%2 == 0) {
                    g.setColor(new Color(173,255,47));
                }
                else {
                    g.setColor(new Color(34,139,34));

                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            // draw snake tail
            g.fillArc(x[bodyParts-1] + UNIT_SIZE/5, y[bodyParts-1]  + UNIT_SIZE/5, UNIT_SIZE, UNIT_SIZE, 60, 120);
        }
        else {
            gameOver(g);
        }
    }

    /**
     * Moves the snake in the current set direction.
     *   If the commandQueue is not empty, it consumes a command from it. This is done in order to avoid
     *   doing more than 1 switch in direction without changing a position on the board.
     * @requires direction == 'R' || direction == 'L' || direction == 'U' || direction == 'D'
     */
    public void move() {
        boolean set = false;
        while (commandQueue.size() > 0) {
             switch (commandQueue.remove()) {
                 case 'L':
                     if (direction != 'R' && direction != 'L') {
                         direction = 'L';
                         set = true;
                     }
                     break;
                 case 'R':
                     if (direction != 'L' && direction != 'R') {
                         direction = 'R';
                         set = true;
                     }
                     break;
                 case 'U':
                     if (direction != 'D' && direction != 'U') {
                         direction = 'U';
                         set = true;
                     }
                     break;
                 case 'D':
                     if (direction != 'U' && direction != 'D') {
                         direction = 'D';
                         set = true;
                     }
                     break;
             }
             if (set) {
                 break;
             }
        }
        // each body part goes to the next
        for (int bodyPart = bodyParts; bodyPart > 0; bodyPart--) {
            x[bodyPart] = x[bodyPart - 1];
            y[bodyPart] = y[bodyPart - 1];
        }
        // head moves in the direction
        switch(direction) {
            case 'L':
                x[0] -= UNIT_SIZE;
                break;
            case 'R':
                x[0] += UNIT_SIZE;
                break;
            case 'U':
                y[0] -= UNIT_SIZE;
                break;
            case 'D':
                y[0] += UNIT_SIZE;
                break;
        }

    }

    /**
     * Checks if the head position is inside a wall or the body. Ends the game if you collide with body.
     *   Loops around if you hit a wall.
     */
    public void checkCollisions() {
        // check for body collision
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
                break;
            }
        }

        // check for upper wall collision
        if (y[0] < 0) {
            y[0] = HEIGHT - UNIT_SIZE;
        }
        // check for lower wall collision
        else if (y[0] >= HEIGHT) {
            y[0] = 0;
        }
        // check for left wall collision
        else if (x[0] < 0) {
            x[0] = WIDTH - UNIT_SIZE;
        }
        // check for right wall collision
        else if (x[0] >= WIDTH) {
            x[0] = 0;
        }
        
        if (!running) {
            timer.stop();
        }
        
    }


    /**
     * Is displayed when the game is over.
     * @param g - the graphics used to draw the game over screen.
     */
    public void gameOver(Graphics g) {
        //Score
        g.setColor(Color.red);
        g.setFont( new Font("Helvetica",Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: "+applesEaten, (WIDTH - metrics1.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
        //Game Over text
        g.setFont( new Font("Helvetica",Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (WIDTH - metrics2.stringWidth("Game Over"))/2, HEIGHT/2);
        //Restart text
        g.setColor(Color.white);
        g.setFont( new Font("Helvetica",Font.BOLD, 30));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("Press R to restart", (WIDTH - metrics3.stringWidth("Press R to restart"))/2, HEIGHT/2 + 75);
    }

    /**
     * Checks if the head is in the same position as the apple. If so, increments the length of the snake, the score
     *   and spawns a new apple.
     */
    public void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            applesEaten++;
            bodyParts++;
            newApple();
        }
    }


    /**
     * Gradually increases the difficulty of the game, by decreasing the timer delay.
     */
    private void increaseDifficulty() {
        if (applesEaten > 10 && applesEaten < 20) {
            timer.setDelay(40);
        }
        else if (applesEaten >= 20 && applesEaten < 30) {
            timer.setDelay(35);
        }
        else if (applesEaten >= 30 && applesEaten < 50) {
            timer.setDelay(30);
        }
        else if (applesEaten >= 50) {
            timer.setDelay(25);
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
            checkCollisions();
            checkApple();
            increaseDifficulty();
        }
        repaint();

    }


    public class MyKeyAdapter extends KeyAdapter {
        /**
         * Based on user input, adds commands to the command queue.
         * @param e - the keypress that triggers the call to this method.
         */
        @Override
        public void keyPressed(KeyEvent e){
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    commandQueue.add('L');
                     break;
                case KeyEvent.VK_RIGHT:
                    commandQueue.add('R');
                      break;
                case KeyEvent.VK_UP:
                    commandQueue.add('U');
                    break;
                case KeyEvent.VK_DOWN:
                    commandQueue.add('D');
                    break;
                case KeyEvent.VK_R:
                    if (!running){
                        reset();
                    }
                    break;
            }
        }
    }
}
