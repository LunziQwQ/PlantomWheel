package sample;

import java.util.List;

/**
 * ***********************************************
 * Created by Lunzi on 10/11/17.
 * Just presonal practice.
 * Not allowed to copy without permission.
 * ***********************************************
 */
class Chess {
	Coord coord;
	private int health;
	char status;       //b黑, w白, e为空, ?为空但不可用
	Group group = null;     //Chess所在的Group引用
	
	Chess(int x, int y, char status) {
		this.coord = new Coord(x, y);
		this.status = status;
	}
	
	Chess(Coord coord, char status) {
		this.coord = coord;
		this.status = status;
	}
	
	//更新本棋子的信息
	private void update() {
		if (status == 'b' || status == 'w') {
			updateGroup();
			updateHealth();
			if (this.health == 0 && (this.status == 'b' || this.status == 'w')) {
				//Todo:提子自身
			}
		} else {
			health = -1;
			group = null;
		}
		
	}
	
	//更新本棋子的Group信息
	private void updateGroup() {
		for (Chess x : ChessBoard.getChesses(coord.getNear4Coord(true))) {
			if (x.status == status) {
				if (group == null) {
					if (x.group == null) {
						Group tempGroup = new Group(status);
						tempGroup.addChess(this);
						tempGroup.addChess(x);
						ChessBoard.groups.add(tempGroup);
					} else {
						x.group.addChess(this);
					}
				} else {
					if (x.group == null) {
						group.addChess(x);
					} else {
						group.merge(x.group);
					}
				}
			} else {
				if(x.group!=null) x.group.update();
			}
		}
	}
	
	/**
	 * 更新本棋子的气
	 */
	private void updateHealth() {
		health = 0;
		if (group == null)
			ChessBoard.getChesses(coord.getNear4Coord(true)).stream().filter(x -> (x.status != 'b' && x.status != 'w')).forEach(x -> health++);
		else health = group.health;
	}
	
	void setChess(char status) {
		//若收到Illegal，尝试判定是否为存在己方棋子
		if (status == '?') {
			long nearFriendCount = ChessBoard.getChesses(coord.getNear4Coord(true))
					.stream().filter(x -> x.status == (Main.isBlackPlayer ? 'b' : 'w')).count();
			if (nearFriendCount > 0) {
				setChess(Main.isBlackPlayer ? 'w' : 'b');
				return;
			}
		}
		this.status = status;
		update();
		ChessBoard.getChesses(coord.getNear4Coord(true)).forEach(Chess::update);
	}
	
	void capture(){
		setChess('e');
	}
	
	//判断该Chess是否可以落子
	boolean canSet(boolean isBlack) {
		if (this.status == 'b' || this.status == 'w') return false;
		//检查周边棋子是否有空
		List<Chess> near = ChessBoard.getChesses(coord.getNear4Coord(true));
		for (Chess x : near) {
			if (x.status == 'e') {
				return true;
			}
		}
		
		for (Chess x : near) {
			//检查周边是否有友方棋子且落子后是否有气
			if (x.status == (isBlack ? 'b' : 'w')) {
				if (x.group != null) {
					if (x.group.health >= 2)
						return true;
				} else if (x.health >= 2)
					return true;
				
				//检查周边是否有对方棋子且落子后可提子
			} else if (x.status == (isBlack ? 'w' : 'b')) {
				if (x.group != null) {
					if (x.group.health == 1)
						return true;
				} else if (x.health == 1)
					return true;
			}
		}
		return false;
	}
}
