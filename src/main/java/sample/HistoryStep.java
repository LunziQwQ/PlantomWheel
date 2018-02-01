package sample;

import java.io.Serializable;
import java.util.Arrays;

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
	
	public HistoryStep(Coord coord,String behavior, Chess[][] board) {
		this.coord = new Coord(coord);
		this.behavior = behavior;
		this.board = new Chess[board.length][];
		for (int i = 0; i < board.length; i++) {
			this.board[i] = board[i].clone();
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof HistoryStep)) return false;
		HistoryStep that = (HistoryStep) o;
		return Arrays.deepEquals(board, that.board) &&
				coord.equals(that.coord) &&
				behavior.equals(that.behavior);
	}
}
