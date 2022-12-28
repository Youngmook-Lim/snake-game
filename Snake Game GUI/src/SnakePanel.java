import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class SnakePanel extends JPanel implements ActionListener {
    static final int GRID_WIDTH = 400;
    static final int GRID_HEIGHT = 400;

    static final int UNIT_SIZE = 20;
    static final int TOTAL_UNITS = (GRID_HEIGHT / UNIT_SIZE) * (GRID_WIDTH / UNIT_SIZE);
    static final int TICK = 350;
    static int[] dx = {0, 0, -1, 1};
    static int[] dy = {-1, 1, 0, 0};
    Timer timer;
    Random random = new Random();
    int direction, prevDir;
    int appleX, appleY;
    int[] x, y;
    int[][] snakeMap;
    int length;
    boolean isPlaying;
    boolean justEaten;
    int applesEaten;
    int curTick;

    // 생성자
    SnakePanel() {
        this.setPreferredSize(new Dimension(GRID_WIDTH, GRID_HEIGHT));
        this.setFocusable(true);
        this.setBackground(Color.WHITE);
        this.addKeyListener(new myKeyAdapter());
        this.init();
        newGame();
    }

    ////////////////////////////////////////////////////////////////////

    // 초기화
    public void init() {
        length = 1;
        applesEaten = 0;
        curTick = TICK;
        snakeMap = new int[GRID_HEIGHT / UNIT_SIZE][GRID_WIDTH / UNIT_SIZE];
        this.setPlayer();
        this.setApple();
        this.setDirection();
    }

    public void setPlayer() {
        int snakeX = random.nextInt(GRID_WIDTH / UNIT_SIZE);
        int snakeY = random.nextInt(GRID_HEIGHT / UNIT_SIZE);
        x = new int[TOTAL_UNITS];
        y = new int[TOTAL_UNITS];
        x[0] = snakeX * UNIT_SIZE;
        y[0] = snakeY * UNIT_SIZE;
        snakeMap[snakeY][snakeX] = 1;
    }

    public void setApple() {
        do {
            appleX = random.nextInt(GRID_WIDTH / UNIT_SIZE) * UNIT_SIZE;
            appleY = random.nextInt(GRID_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        } while (snakeMap[appleY / UNIT_SIZE][appleX / UNIT_SIZE] == 1);
    }

    public void setDirection() {
        int tmp = (int) Math.floor(Math.random() * 4);
        switch (tmp) {
            case 0:
                direction = 0;
                break;
            case 1:
                direction = 1;
                break;
            case 2:
                direction = 2;
                break;
            case 3:
                direction = 3;
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////

    // 새 게임
    public void newGame() {
        isPlaying = true;
        timer = new Timer(curTick, this);
        timer.start();
    }

    ////////////////////////////////////////////////////////////////////

    // Timer 변경에 따른 호출
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isPlaying) return;

//        for (int[] x : snakeMap) {
//            System.out.println(Arrays.toString(x));
//        }
//        System.out.println("---------------");
        System.out.println(curTick);
        move();
        growIfEaten();
        if (checkCollision()) {
            gameOver();
        } else {
            repaint();
        }
    }

    ////////////////////////////////////////////////////////////////////

    // 메인 로직
    public void move() {
        int nx = x[0] + dx[direction] * UNIT_SIZE;
        int ny = y[0] + dy[direction] * UNIT_SIZE;
        try {
            if (!justEaten) {
                snakeMap[y[length - 1] / UNIT_SIZE][x[length - 1] / UNIT_SIZE]--;
            }
            snakeMap[ny / UNIT_SIZE][nx / UNIT_SIZE]++;
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("Out of bounds");
        }

        for (int i = length - 1; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        x[0] = nx;
        y[0] = ny;

        prevDir = direction;
        justEaten = false;
    }

    public void growIfEaten() {
        if (x[0] != appleX || y[0] != appleY) return;

        this.setApple();
        length++;
        applesEaten++;
        justEaten = true;
        if (curTick > 50) {
            curTick -= 12.5;
            timer.stop();
            timer = new Timer(curTick, this);
            timer.start();
        }
    }

    public boolean checkCollision() {
//        for (int i = 1; i < length; i++) {
//            if (x[0] == x[i] && y[0] == y[i]) {
//                return true;
//            }
//        }
        if (x[0] < 0 || x[0] >= GRID_WIDTH || y[0] < 0 || y[0] >= GRID_HEIGHT) {
            return true;
        }
        return snakeMap[y[0] / UNIT_SIZE][x[0] / UNIT_SIZE] > 1;
    }

    public void gameOver() {
        isPlaying = false;
        timer.stop();
        String[] options = new String[]{"종료", "다시하기"};
        JLabel label = new JLabel("게임 오버입니다.");
        label.setHorizontalAlignment(JLabel.CENTER);
        int res = JOptionPane.showOptionDialog(this, label, "게임 오버!", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
        if (res == 1) {
            this.init();
            newGame();
        } else {
            SwingUtilities.getWindowAncestor(this).dispose();
            System.exit(0);
        }
    }

    ////////////////////////////////////////////////////////////////////

    // 게임판 그리기
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Draw lines
        g.setColor(new Color(173, 181, 189));
        for (int i = 1; i < GRID_HEIGHT / UNIT_SIZE; i++) {
            g.drawLine(0, UNIT_SIZE * i, GRID_WIDTH, UNIT_SIZE * i);
        }
        for (int i = 1; i < GRID_WIDTH / UNIT_SIZE; i++) {
            g.drawLine(UNIT_SIZE * i, 0, UNIT_SIZE * i, GRID_HEIGHT);
        }

        if (isPlaying) {
            // Draw apple
            g.setColor(Color.red);
            g.fillRect(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // Draw snake
            for (int i = 0; i < (justEaten ? length - 1 : length); i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                } else {
//                    g.setColor(new Color(0, 255, 0, 128));
                    g.setColor(Color.YELLOW);
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            // Draw score
            g.setColor(Color.BLACK);
            g.setFont(new Font("Dialog", Font.BOLD, 30));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("점수 : " + applesEaten,
                    (GRID_WIDTH - metrics.stringWidth("점수 : " + applesEaten)) / 2, g.getFont().getSize());


        }

    }

    ////////////////////////////////////////////////////////////////////

    // 키보드 입력 (방향전환)
    public class myKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            setDirection(e.getKeyCode());
        }
    }

    public void setDirection(int code) {
        switch (code) {
            case KeyEvent.VK_UP:
                if (prevDir == 1) break;
                direction = 0;
                break;
            case KeyEvent.VK_DOWN:
                if (prevDir == 0) break;
                direction = 1;
                break;
            case KeyEvent.VK_LEFT:
                if (prevDir == 3) break;
                direction = 2;
                break;
            case KeyEvent.VK_RIGHT:
                if (prevDir == 2) break;
                direction = 3;
                break;
        }
    }
}
