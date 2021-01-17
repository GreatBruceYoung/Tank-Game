package Game;

import java.awt.event.KeyEvent;
import java.util.*;
import java.util.concurrent.*;

public class Tank extends MyImage implements Runnable {


    ExecutorService executorService = Executors.newCachedThreadPool();

    //�߳�˯�ߵ�ʱ��
    //MP�Ļָ�ʱ��
    final static int MP_TIME = 1000;
    //AI�ƶ���һ����ʱ��
    final static int MOVE_TIME = 50;
    //�Ƿ�������߳���ֹ
    boolean flag = true;

    //��һ��λ�Ƶ�����
    private volatile Coord next;
    private Future<Stack<Coord>> stackFuture;

    private boolean direction[] = {false, false, false, false};
    int _direction;

    int id;

    //����̹�˵��ٶ�
    int speed;
    //̹�˵�Ѫ��
    int hp = Game.HP;
    //̹�˵�MP
    int mp = Game.MP;
    //��ǰλ�Ƶİ���
    int key;
    //�Ƿ�����ƶ������ڴ�������������Ӧ
    boolean move = false;

    //-------------------���߳��߳����õ����ڲ���------------------------start
    //AI̹���ƶ����߳�

    //�������ֻҪ�ƶ�һ������¼���·�������һ��Ҫ���ĸ��ӵ�����Ĳ���
    //����һ�ֽ��˼·��ÿ�ƶ�������������¼��㣬�����ڿ��ܵ�����ʼ���귢���ı䣬��˲�����

    //����ʹ�õ�һ�ŷ������ܻᵼ��һ���������̹�˲����ƶ���ʱ�����޷����������
    //���ڵ�һ������������һ���Լ����¸����̹�˵������������������̹��
    //��˻���ֵ��л���̹�˺���������̹�˻��࿨ס�޷��ƶ������
    //�����������ͳ���ظ��ƶ�ͬһ������Ĵ��������������������һ��Ĳ���
    //�����Ǵ������¼���·���ģ�������Ч�����Ǻܺ�
    //ʵ�����ڻ��·�ߵ��㷨�Ͻ��й��Ż�������Ч�����ã������ϻ��ǻ��������̹�˿�ס�����

    //ʵ���ϻ����ֹ�̹��һֱ���ط����˶�������������Ǵ���ͨ���޸�Ѱ·�㷨�в��Բ�ͬ�ķ����ʱ���������ͬ������Ч�����ˣ�����
    class ETankMove implements Runnable {
        public void run() {
            int d = _direction;
            int count = 0;
            while (flag) {
                if (stackFuture.isDone()) {
                    try {
                        //ʹ��ջ��Ź�ȱ����㷨�õ����ƶ���·��
                        Stack<Coord> result = stackFuture.get();
                        //�����һ��·��
                        if (null != result && result.size() != 0 && null == next) {
                            next = result.pop();
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                ETankMove();
                //Ϊ�˷�ֹ����̹��Ϊ�˾���ͬһ��ǰ��ķ������ס
                // ������������ͬһ���ƶ�����ͣ�͹��þ����������ƶ�һ��ķ���
                if (d == _direction) {
                    if (++count > 80) {
                        int n;
                        switch (d) {
                            case Game.UP:
                                n = KeyEvent.VK_DOWN;
                                break;
                            case Game.DOWN:
                                n = KeyEvent.VK_UP;
                                break;
                            case Game.LEFT:
                                n = KeyEvent.VK_RIGHT;
                                break;
                            case Game.RIGHT:
                                n = KeyEvent.VK_LEFT;
                                break;
                            default:
                                n = KeyEvent.VK_SHIFT;
                        }
                        try {

//                            next = GetPath().pop();
                            for (int j = 0; j < 5; ++j) {
                                GetKey(n);
                                Thread.sleep(MOVE_TIME);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        count = 0;
                    }
                } else {
                    d = _direction;
                    count = 0;
                }
                try {
                    Thread.sleep(MOVE_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //·����õ��߳�
    class TaskWithPath implements Callable<Stack<Coord>> {

        /**
         * ����ľ�����̣�һ�����񴫸�ExecutorService��submit������
         * ��÷����Զ���һ���߳���ִ��
         */
        public Stack<Coord> call() {
            //�÷��ؽ������Future��get�����õ�
//            Random random = new Random();
//            Thread.sleep(random.nextInt(50));
            return GetPath();
        }
    }

    //���������Ľ���
    class MyTankMove implements Runnable {
        public void run() {
            while (flag) {
                while (move) {
                    GetKey(key);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //----------------�߳��ڲ���-----------------end

    //���ݼ���õ���·�������ƶ�
    private void ETankMove() {
        //����λ�Ʒ����ж�ֱ�Ӽ���,���ܱȽϳ��󡣡���
        //�����̰߳�ȫ���⣬����������ܴ�λһλ���ϣ����ֻ�ܲ���>=
        if (null != next && !next.equals(coord)) {
            if (Math.abs(coord.x - next.x) > 1 || Math.abs(coord.y - next.y) > 1) {
                System.out.println(Thread.currentThread().getName() + ":" + coord.toString() + "->" + next.toString());
            }
            GetKey(GetDirection(coord, next));
//            if (Game.map[next.y][next.x] == Game.WALLS) {
//                GetKey(arr[4]);
//            }
        }
    }


    /**
     * ʹ�ù�ȱ����㷨��ʹ�ö��д洢�����Ľڵ�
     *
     * @return �ƶ���·��
     */
    private Stack<Coord> GetPath() {
        Coord target = Game.tanks.get(Game.P1_TAG).coord;
        Queue<Coord> d_q = new LinkedBlockingQueue<>();
        ArrayList<Coord> IsMove = new ArrayList<>();
        IsMove.add(coord);
        d_q.offer(coord);
        Coord last = null;
        boolean flag;
        while (!d_q.isEmpty()) {
            Coord t = d_q.poll();
            int tx = t.x;
            int ty = t.y;
            int i;
            //�������еķ���
//            Random r = new Random(System.currentTimeMillis());
            for (i = 0; i < 4; ++i) {
                switch (i /*+ (r.nextInt(2)) % 4*/) {
                    case Game.UP:
                        ty -= 1;
                        break;
                    case Game.LEFT:
                        tx -= 1;
                        break;
                    case Game.RIGHT:
                        tx += 1;
                        break;
                    case Game.DOWN:
                        ty += 1;
                        break;
                }
                //�жϸõ��Ƿ����
                flag = true;
                Coord z = new Coord(tx, ty);
                //����Ƿ�ΪĿ���յ�
                if (z.equals(target)) {
                    z.per = t;
                    last = z;
                    break;
                }
                //���������Ƿ��Ѿ�������
                for (Coord c : IsMove) {
                    if (c.equals(z)) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    //ͨ�����飬�ж��Ƿ���һ�������
                    flag = (Game.map[ty][tx] == Game.BLANK || Game.map[ty][tx] == Game.WALLS);
                    if (flag) {
                        Coord temp = new Coord(z.x, z.y);
                        switch (i) {
                            case Game.UP:
                                temp.y -= 1;
                                break;
                            case Game.LEFT:
                                temp.x -= 1;
                                break;
                            case Game.RIGHT:
                                temp.x += 1;
                                break;
                            case Game.DOWN:
                                temp.y += 1;
                                break;
                        }
                        flag = (Game.map[temp.y][temp.x] == Game.BLANK || Game.map[temp.y][temp.x] == Game.WALLS || Game.map[temp.y][temp.x] == Game.P1_TAG || Game.map[temp.y][temp.x] == this.hashCode());
                    }
                }
                //�õ������
                if (flag) {
                    //�����������Ѿ������Ķ�����
                    d_q.offer(z);
                    z.per = t;
                    last = z;
                }
                IsMove.add(z);
                //����ѡ�������
                tx = t.x;
                ty = t.y;
            }
            //���û���ĸ����򶼱������������˵���Ѿ��ҵ����յ�
            if (i != 4) {
                break;
            }
        }
        Stack<Coord> coords = new Stack<>();
        while (null != last && last.per != null) {
            coords.push(last);
            last = last.per;
        }
        return coords;
    }

    Tank(Coord coord, int direction, int id) {
        super(coord);
        this.direction[direction] = true;
        this._direction = direction;
        this.id = id;
        if (id < Game.PLAY_1) {
            stackFuture = executorService.submit(new TaskWithPath());
            executorService.execute(new ETankMove());
            System.out.println(executorService.toString());
        } else {
            executorService.execute(new MyTankMove());
        }
        executorService.execute(new TankMpRecover());
    }


    int GetDirection(Coord coord, Coord next) {
        int n;
        if (coord.x - next.x <= -1) {
            n = KeyEvent.VK_RIGHT;
        } else if (coord.x - next.x >= 1) {
            n = KeyEvent.VK_LEFT;
        } else {
            if (coord.y - next.y <= -1) {
                n = KeyEvent.VK_DOWN;
            } else {
                n = KeyEvent.VK_UP;
            }
        }
        return n;
    }

    /**
     * @return �Ƿ�����ƶ�
     */
    private boolean isMovable() {
        //����ϰ���
        for (Wall wall : Game.walls.values()) {
            if (wall.isIntersects(this)) {
                if (id < Game.PLAY_1 && wall.id == Game.WALL)
                    GetKey(16);
                return true;
            }
        }
        //���̹��
        for (Tank tank : Game.tanks.values()) {
            if (tank.isIntersects(this) && !this.equals(tank)) {
                //�������Ҿ͹���
                if (id < Game.PLAY_1 && tank.id >= Game.PLAY_1) {
                    GetKey(16);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * ����̹���ƶ�
     *
     * @param n �ƶ�����ֵ
     */
    void GetKey(int n) {
        int t_x = x;
        int t_y = y;
        //�жϰ���
        switch (n) {
            //�ж��ƶ������϶����ȼ����Ѿ��ƶ�Ȼ���ж��ƶ�֮���Ƿ�ᷢ���ص�
            //������귢����һ����ĸı䣬�Ǿ͸���map
            case KeyEvent.VK_UP: {
                y -= speed;
                if (!direction[Game.UP] || isMovable()) {
                    y = t_y;
                    if (!direction[Game.UP]) {
                        direction[Game.UP] = true;
                        direction[_direction] = false;
                        _direction = Game.UP;
                    } else {
                        return;
                    }
                }
                break;
            }
            case KeyEvent.VK_DOWN: {
                y += speed;
                if (!direction[Game.DOWN] || isMovable()) {
                    y = t_y;
                    if (!direction[Game.DOWN]) {
                        direction[Game.DOWN] = true;
                        direction[_direction] = false;
                        _direction = Game.DOWN;
                    } else {
                        return;
                    }
                }
                break;
            }
            case KeyEvent.VK_LEFT: {
                x -= speed;
                if (!direction[Game.LEFT] || isMovable()) {
                    x = t_x;
                    if (!direction[Game.LEFT]) {
                        direction[Game.LEFT] = true;
                        direction[_direction] = false;
                        _direction = Game.LEFT;
                    } else {
                        return;
                    }
                }
                break;
            }
            case KeyEvent.VK_RIGHT: {
                x += speed;
                if (!direction[Game.RIGHT] || isMovable()) {
                    x = t_x;
                    if (!direction[Game.RIGHT]) {
                        direction[Game.RIGHT] = true;
                        direction[_direction] = false;
                        _direction = Game.RIGHT;
                    } else {
                        return;
                    }
                }
                break;
            }
            case KeyEvent.VK_SPACE: {
                if (mp > 0) {
                    synchronized ("KEY") {
                        mp -= 10;
                    }
                    if (_direction == Game.UP)
                        Game.missile.add(new Missile(x + Game.width / 2, y - Missile.m_h, _direction, id));
                    if (_direction == Game.DOWN)
                        Game.missile.add(new Missile(x + Game.width / 2, y + Game.height + Missile.m_h, _direction, id));
                    if (_direction == Game.LEFT)
                        Game.missile.add(new Missile(x - Missile.m_w, y + Game.height / 2, _direction, id));
                    if (_direction == Game.RIGHT)
                        Game.missile.add(new Missile(x + Missile.m_w + Game.width, y + Game.height / 2, _direction, id));
                    return;
                }
                break;
            }
        }
        //������귢����һ����ı仯���͸��¶�ά����
        if (t_y != y | t_x != x) {
            t_y = y / Game.height;
            t_x = x / Game.width;
            if (((t_y != coord.y) || (t_x != coord.x)) && (x % Game.width == 0 && y % Game.height == 0)) {
                Game.map[t_y][t_x] = Game.map[coord.y][coord.x];
                Game.map[coord.y][coord.x] = Game.BLANK;
                coord.x = t_x;
                coord.y = t_y;
//                if (id == Game.PLAY_1)  Game.printMap();
                if (id <= Game.PLAY_1) {
                    stackFuture = executorService.submit(new TaskWithPath());
                    next = null;
                }

            }
        }
    }

    /**
     * �����Ļָ�
     */
    class TankMpRecover implements Runnable {
        public void run() {
            while (flag) {
                synchronized ("MP") {
                    if (mp < Game.MP)
                        mp += 10;
                }
                try {
                    Thread.sleep(MP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * ÿ��һ�����ʱ���Զ������ӵ�
     */
    public void run() {
        Random r = new Random();
        while (flag) {
            try {
                Thread.sleep(r.nextInt(5000));
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            GetKey(16);
        }
    }
}
