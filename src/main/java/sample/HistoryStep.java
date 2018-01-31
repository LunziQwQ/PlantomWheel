package sample;

import java.io.Serializable;

/**
 * ***********************************************
 * Created by Lunzi on 1/31/2018.
 * Just presonal practice.
 * Not allowed to copy without permission.
 * ***********************************************
 */
public class HistoryStep implements Serializable {
	Chess[][] board;
	Coord coord;
	String behavior;
	
	HistoryStep(Coord coord,String behavior, Chess[][] board) {
		this.coord = new Coord(coord);
		this.behavior = behavior;
		this.board = new Chess[board.length][];
		for (int i = 0; i < board.length; i++) {
			this.board[i] = board[i].clone();
		}
	}
}
