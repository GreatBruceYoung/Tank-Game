package Game;

import java.awt.Rectangle;

class MyImage {
    int width = Game.width;
    int height = Game.height;
    //��ά��ͼ������
    Coord coord;
    //��Ļ�ϵ���������
    int x;
    int y;

    MyImage(int x, int y) {
        this.x = x;
        this.y = y;
    }

    MyImage(Coord coord) {
        x = coord.x * width;
        y = coord.y * height;
        this.coord = coord;
    }

    private Rectangle getRect() {
        return new Rectangle(x, y, width, height);
    }

    boolean isIntersects(MyImage other) {
        return other.getRect().intersects(getRect());
    }
}

