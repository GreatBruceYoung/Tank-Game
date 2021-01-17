package Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;


/**
 * ̹����Ϸ����
 */
public class Game extends JPanel {
    private static Image array[] = new Image[23];
    private Image OffScreenImage;
    //���1̹�˵�hashcode
    static int P1_TAG;
    //���2
    static int P2_TAG;


    static {
        array[0] = new ImageIcon(Game.class.getResource("/img/walls.gif")).getImage();
        array[1] = new ImageIcon(Game.class.getResource("/img/steels.gif")).getImage();
        array[2] = new ImageIcon(Game.class.getResource("/img/enemy1D.gif")).getImage();
        array[3] = new ImageIcon(Game.class.getResource("/img/enemy1L.gif")).getImage();
        array[4] = new ImageIcon(Game.class.getResource("/img/enemy1R.gif")).getImage();
        array[5] = new ImageIcon(Game.class.getResource("/img/enemy1U.gif")).getImage();
        array[6] = new ImageIcon(Game.class.getResource("/img/enemy2D.gif")).getImage();
        array[7] = new ImageIcon(Game.class.getResource("/img/enemy2L.gif")).getImage();
        array[8] = new ImageIcon(Game.class.getResource("/img/enemy2R.gif")).getImage();
        array[9] = new ImageIcon(Game.class.getResource("/img/enemy2U.gif")).getImage();
        array[10] = new ImageIcon(Game.class.getResource("/img/enemy3D.gif")).getImage();
        array[11] = new ImageIcon(Game.class.getResource("/img/enemy3L.gif")).getImage();
        array[12] = new ImageIcon(Game.class.getResource("/img/enemy3R.gif")).getImage();
        array[13] = new ImageIcon(Game.class.getResource("/img/enemy3U.gif")).getImage();
        array[14] = new ImageIcon(Game.class.getResource("/img/p1tankD.gif")).getImage();
        array[15] = new ImageIcon(Game.class.getResource("/img/p1tankL.gif")).getImage();
        array[16] = new ImageIcon(Game.class.getResource("/img/p1tankR.gif")).getImage();
        array[17] = new ImageIcon(Game.class.getResource("/img/p1tankU.gif")).getImage();
        array[18] = new ImageIcon(Game.class.getResource("/img/p2tankD.gif")).getImage();
        array[19] = new ImageIcon(Game.class.getResource("/img/p2tankL.gif")).getImage();
        array[20] = new ImageIcon(Game.class.getResource("/img/p2tankR.gif")).getImage();
        array[21] = new ImageIcon(Game.class.getResource("/img/p2tankU.gif")).getImage();
        array[22] = new ImageIcon(Game.class.getResource("/img/tankmissile.gif")).getImage();
    }


    static Mode mode;

    public static boolean live = true;

    //̹�˵��ƶ�����
    private final static int screenWidth = 900;
    private final static int screenHeight = 600;


    //һ��ͼ��Ĵ�С
    static final int width = 40;
    static final int height = 40;
    //̹�˵�Ѫ���͵�ҩ��
    static final int HP = width;
    static final int MP = width;
    //̹�˵��ƶ�
    static final int UP = 3;
    static final int DOWN = 0;
    static final int LEFT = 1;
    static final int RIGHT = 2;
    //map��ͼ��ı�־
    final static int BLANK = -1;
    final static int WALLS = -2;
    final static int STEELS = -3;
    //ͼ��ı�־
    final static int WALL = 0;
    final static int STEEL = 1;
    final static int ENEMY_1 = 0;
    final static int ENEMY_2 = 4;
    final static int ENEMY_3 = 8;
    //��ҿ��Ƶ�̹�˵ı�ţ�ͼ�������еı�ţ�
    final static int PLAY_1 = 12;
    final static int PLAY_2 = 16;
    //  final static int MISSILE = 22;

    //��ͼ����������ӵ�����Ķ���
    //ע�⵱ʹ��Coord��x��y��ʱ����map[y][x]
    volatile static int[][] map;

    volatile static ConcurrentHashMap<Integer, Tank> tanks = new ConcurrentHashMap<>();

    volatile static ConcurrentHashMap<Integer, Wall> walls = new ConcurrentHashMap<>();

    volatile static ArrayList<Missile> missile = new ArrayList<>();


    /**
     * ��ʼ���з�AI̹��
     */
    private void init_ETank() {
        Coord coord = randomCoord();
        Tank tank = new Tank(coord, DOWN, ENEMY_1);
        tank.speed = 10;
        map[coord.y][coord.x] = tank.hashCode();
        tanks.put(tank.hashCode(), tank);
        coord = randomCoord();
        tank = new Tank(coord, DOWN, ENEMY_2);
        tank.speed = 10;
        map[coord.y][coord.x] = tank.hashCode();
        tanks.put(tank.hashCode(), tank);
        coord = randomCoord();
        tank = new Tank(coord, DOWN, ENEMY_3);
        tank.speed = 10;
        map[coord.y][coord.x] = tank.hashCode();
        tanks.put(tank.hashCode(), tank);
    }

    /**
     * ��ʼ����ҵ�̹��
     */
    private void init_Tank(Mode mode) {
        Coord coord = randomCoord();
        Tank p1 = new Tank(coord, DOWN, PLAY_1);
        p1.speed = 20;
        P1_TAG = p1.hashCode();
        map[coord.y][coord.x] = p1.hashCode();
        tanks.put(p1.hashCode(), p1);
        //˫��ģʽ
        if (mode == Mode.Double) {
            coord = randomCoord();
            Tank p2 = new Tank(coord, DOWN, PLAY_2);
            p2.speed = 10;
            P2_TAG = p2.hashCode();
            map[coord.y][coord.x] = p2.hashCode();
            tanks.put(p2.hashCode(), p2);
        } else {
            init_ETank();
        }
    }

    /**
     * ��ʼ����ͼ
     */
    private void init_map() {

        int x = screenWidth / Game.width;
        int y = screenHeight / Game.height - 1;

        map = new int[y][x];

        for (int i = 0; i < y; ++i) {
            for (int j = 0; j < x; ++j) {
                if (i == 0 || i == y - 1 || j == 0 || j == x - 1) {
                    map[i][j] = STEELS;
                    Wall wall = new Wall(new Coord(j, i), STEEL);
                    walls.put(wall.hashCode(), wall);
                } else {
                    map[i][j] = BLANK;
                }
            }
        }
        //���
        for (int i = 0; i < x * y / 2; ++i) {
            //Coord��y��Ӧ�������
            Coord c = randomCoord();
            map[c.y][c.x] = WALLS;
            Wall wall = new Wall(c, WALL);
            walls.put(wall.hashCode(), wall);
        }

    }


    /**
     * ��ӡ��ά��ͼ����
     */
    static void printMap() {
        System.out.println("------------------------------start----------------------------");
        for (int[] map : map) {
            for (int m : map) {
                System.out.print(m + " ");
            }
            System.out.println();
        }
        System.out.println("-------------------------------------end--------------------------------");
    }

    /**
     * ���̹�˵�����
     */
    private Coord randomCoord() {
        Random random = new Random(System.currentTimeMillis());
        int x, y;
        do {
            y = random.nextInt(map.length);
            x = random.nextInt(map[0].length);
        } while (map[y][x] != BLANK);
        return new Coord(x, y);
    }


    public Game(Mode mode) {
        setForeground(Color.WHITE);
        setBackground(Color.BLACK);
        setBounds(0, 0, screenWidth, screenHeight);
        setLayout(null);
        Game.mode = mode;
        init_map();
        init_Tank(mode);
        addKeyListener(new KeyBoardListener());
        live = true;
        new Thread(new MissileMove()).start();
        new Thread(new Draw()).start();
    }


    /**
     * ͼ���ػ��߳�
     */
    class Draw implements Runnable {
        public void run() {
            while (live) {
                repaint();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * �ӵ��ƶ����߳�
     */
    class MissileMove implements Runnable {
        public void run() {
            while (live) {
                synchronized ("missile") {
                    for (int i = missile.size() - 1; i >= 0; --i) {
                        if (missile.get(i).Move() && live) {
                            missile.remove(i);
                        }
                    }
                }
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * ����̹�˺�Ѫ������
     */
    private void paintTank(Graphics2D g2, Tank tank) {
        //Ѫ���������ĸ߶�
        int h = 5;
        g2.drawImage(array[2 + tank._direction + tank.id], tank.x, tank.y, width, height, null);
        g2.setColor(Color.RED);
        g2.draw3DRect(tank.x, tank.y + 1, HP, h, true);
        g2.fill3DRect(tank.x, tank.y + 1, tank.hp, h, true);
        g2.setColor(Color.BLUE);
        g2.draw3DRect(tank.x, tank.y + 1 + h, MP, h, true);
        g2.fill3DRect(tank.x, tank.y + 1 + h, tank.mp, h, true);
    }

    /**
     * �ػ溯��
     */
    synchronized public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        //�滭ǽ��
        for (Wall wall : walls.values()) {
            g2.drawImage(array[wall.id], wall.x, wall.y, width, height, null);
        }

        //����̹��
        for (Tank tank : tanks.values()) {
            paintTank(g2, tank);
        }

        //�ӵ��滭
        for (Missile m : missile) {
            g2.drawImage(array[22], m.x, m.y, m.width, m.height, null);
        }


    }

    //�����ͼ
    synchronized public void update(Graphics g) {
        super.update(g);
        if (OffScreenImage == null)
            OffScreenImage = this.createImage(screenWidth, screenHeight);
        Graphics goffscrenn = OffScreenImage.getGraphics();    //����һ���ڴ滭����ɫΪǰ��ͼƬ��ɫ
        Color c = goffscrenn.getColor();    //�����ȱ���ǰ����ɫ
        goffscrenn.setColor(Color.BLACK);    //�����ڴ滭����ɫΪ��ɫ
        goffscrenn.fillRect(0, 0, screenWidth, screenHeight);    //����ͼƬ����СΪ��Ϸ��С
        goffscrenn.setColor(c);    //��ԭ��ɫ
        g.drawImage(OffScreenImage, 0, 0, null);    //�ڽ��滭�������ͼƬ
        paint(goffscrenn);    //���ڴ滭�ʵ��ø�paint
    }

    /**
     * ��������
     */
    private class KeyBoardListener extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            int key = e.getKeyCode();
            //�������ֲ�ͬ�İ���
            //ASDWGΪP2�İ���
            //��������+SPACEΪP1����
            if (key < 65) {
                if (key != KeyEvent.VK_SPACE && tanks.get(P1_TAG) != null) {
                    tanks.get(P1_TAG).key = key;
                    tanks.get(P1_TAG).move = true;
                }
                if (key == KeyEvent.VK_ESCAPE) {
                    ShutDown();
                }
            } else {
                if (key != KeyEvent.VK_G && tanks.get(P2_TAG) != null) {
                    switch (key) {
                        case KeyEvent.VK_W:
                            key = KeyEvent.VK_UP;
                            break;
                        case KeyEvent.VK_A:
                            key = KeyEvent.VK_LEFT;
                            break;
                        case KeyEvent.VK_S:
                            key = KeyEvent.VK_DOWN;
                            break;
                        case KeyEvent.VK_D:
                            key = KeyEvent.VK_RIGHT;
                            break;
                    }
                    tanks.get(P2_TAG).key = key;
                    tanks.get(P2_TAG).move = true;
                }
            }
        }

        public void keyReleased(KeyEvent e) {
            super.keyReleased(e);
            int key = e.getKeyCode();
            if (key < 65) {
                if (tanks.get(P1_TAG) != null) {
                    if (key != KeyEvent.VK_SPACE && key == tanks.get(P1_TAG).key) {
                        tanks.get(P1_TAG).move = false;
                    } else {
                        tanks.get(P1_TAG).GetKey(key);
                    }
                }
            } else {
                switch (key) {
                    case KeyEvent.VK_W:
                        key = KeyEvent.VK_UP;
                        break;
                    case KeyEvent.VK_A:
                        key = KeyEvent.VK_LEFT;
                        break;
                    case KeyEvent.VK_S:
                        key = KeyEvent.VK_DOWN;
                        break;
                    case KeyEvent.VK_D:
                        key = KeyEvent.VK_RIGHT;
                        break;
                    case KeyEvent.VK_G:
                        key = KeyEvent.VK_SPACE;
                        break;
                }
                if (null != tanks.get(P2_TAG)) {
                    if (key != KeyEvent.VK_SPACE && key == tanks.get(P2_TAG).key) {
                        tanks.get(P2_TAG).move = false;
                    } else {
                        tanks.get(P2_TAG).GetKey(key);
                    }
                }
            }
        }
    }

    static synchronized void ShutDown() {
        Game.live = false;
        //ֹͣAI
        for (Tank tank : Game.tanks.values()) {
            tank.flag = false;
            tank.executorService.shutdown();
        }
        Game.tanks.clear();
        Game.walls.clear();
        Game.missile.clear();
    }


}
