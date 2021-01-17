package Game;

/**
 * Ѱ·�㷨����Ľṹ��
 * �����x��y�Ƿֱ�ָ�ڶ�ά�����е��к���
 * ��˵�ʹ���������������ֵ�����޸ĵ�ʱ��
 * Ӧ��ʹ��map[y][x]=xxx
 */
class Coord {
    int x;
    int y;
    Coord per;
    boolean isMove;

    Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Coord) {
            Coord c = (Coord) obj;
            return x == c.x && y == c.y;
        }
        return false;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
