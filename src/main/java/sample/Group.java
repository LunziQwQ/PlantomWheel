package sample;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;

/**
 * ***********************************************
 * Created by Lunzi on 10/11/17.
 * Just presonal practice.
 * Not allowed to copy without permission.
 * ***********************************************
 */
class Group implements Serializable{
	int health = 0;
	private char status;
	
	HashSet<Chess> chesses = new HashSet<>();
	HashSet<Chess> libertys = new HashSet<>();        //该棋子组的气
	boolean totallyAlive = false;
	
	Group(char status) {
		this.status = status;
	}
	
	void addChess(Chess chess) {
		chesses.add(chess);
		chess.group = this;
		update();
	}
	
	private void updateTotallyAlive() {
		int aliveLiberty = 0;
		for (Chess liberty : libertys) {
			List<Chess> near4Chesses = ChessBoard.getChesses(liberty.coord.getNear4Coord(true));
			if (near4Chesses.size() == near4Chesses.stream().filter(x -> x.status == status).count()) {
				List<Chess> near8Chesses = ChessBoard.getChesses(liberty.coord.getNear8Coord(true));
				long count = near8Chesses.stream().filter(x -> x.status == status).count();
				if (near8Chesses.size() == 8) {
					if (count >= 7)
						aliveLiberty++;
				} else {
					if (count == near8Chesses.size())
						aliveLiberty++;
				}
			}
		}
		totallyAlive = (aliveLiberty >= 2);
	}
	
	private void updateLibertys() {
		libertys.clear();
		for (Chess item : chesses) {
			for (Chess near : ChessBoard.getChesses(item.coord.getNear4Coord(true))) {
				if (near.status != 'b' && near.status != 'w') {
					boolean alreadyInLibertys = false;
					for (Chess qi : libertys)
						if (Coord.equals(qi.coord, near.coord)) {
							alreadyInLibertys = true;
							break;
						}
					if (!alreadyInLibertys) {
						libertys.add(near);
					}
				}
			}
		}
		if (status == 'e')
			health = -1;
		else
			health = libertys.size();
	}
	
	void merge(Group group) {
		group.chesses.addAll(chesses);
		chesses.forEach(item -> item.group = group);
		update();
	}
	
	void update() {
		if (this.chesses.size() == 0) {
			return;
		}
		updateLibertys();
		updateTotallyAlive();
	}
}
