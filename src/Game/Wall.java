package Game;

/**
 * ��Ϸ�ĸ��ַ���ĳ���
 */
class Wall extends MyImage{

    //ʶ��Ϊ���ַ����id
	int id;

	Wall(Coord coord,int id){
		super(coord);
		this.id = id;
	}
}
