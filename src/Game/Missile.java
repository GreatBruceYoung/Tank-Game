package Game;

import javax.swing.*;

class Missile extends MyImage {
    private int direction;
    private final static int speed = 10;
    private final static int damage = 10;
    //�ӵ��ĳ���
    final static int m_w = 10;
    final static int m_h = 10;
    private int id;

    Missile(int x, int y, int direction, int _id) {
        super(x, y);
        this.height = 10;
        this.width = 10;
        this.direction = direction;
        this.id = _id;
    }

    /**
     * @return �ӵ��Ƿ���������
     */
    private boolean isMeet() {

        for (Wall wall : Game.walls.values()) {
            if (wall.isIntersects(this)) {
                if (wall.id == Game.WALL) {
                    Game.map[wall.coord.y][wall.coord.x] = Game.BLANK;
                    Game.walls.remove(wall.hashCode());
                }
                return true;
            }
        }

        for (Tank tank : Game.tanks.values()) {
            if (tank.isIntersects(this)) {
                //�ӵ����˺�
                if (id != tank.id) {
                    tank.hp -= damage/10;
                }
                if (tank.hp <= 0) {
                    //�����̹�˲�������ҿ��ƵĻ��Ͳ�������һ���Ĵ���
                    if (tank.id >= Game.PLAY_1) {
                        tank.flag = false;
                        if (Game.mode == Mode.Single) {
                            if (tank.equals(Game.tanks.get(tank.hashCode()))) {
                                Game.ShutDown();
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, String.valueOf(tank.id == Game.PLAY_1 ? "p2 win!!" : "p2 win!!"));
                            Game.ShutDown();
                        }
                    }
                    Game.map[tank.coord.y][tank.coord.x] = Game.BLANK;
                    tank.flag = false;
                    Game.tanks.remove(tank.hashCode());
                }
                return true;
            }
        }
        return false;
    }

    /**
     * @return �Ƿ���ײ������
     */
    boolean Move() {
        switch (direction) {
            case Game.UP:
                y -= speed;
                break;
            case Game.DOWN:
                y += speed;
                break;
            case Game.LEFT:
                x -= speed;
                break;
            case Game.RIGHT:
                x += speed;
                break;
        }
        return isMeet();
    }
}
