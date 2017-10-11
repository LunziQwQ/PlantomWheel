package sample;

import java.util.ArrayList;
import java.util.List;

/**
 * ***********************************************
 * Created by Lunzi on 10/11/17.
 * Just presonal practice.
 * Not allowed to copy without permission.
 * ***********************************************
 */
class ChessBoard {
	static Chess[][] board;
	
	ChessBoard() {
		int size = 9;
		board = new Chess[9][9];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				board[i][j] = new Chess(i, j, 'e');
			}
		}
	}
	
	static Chess getChess(Coord coord) {
		return board[coord.x][coord.y];
	}
	
	static List<Chess> getChesses(List<Coord> coords) {
		List<Chess> result = new ArrayList<>();
		coords.forEach(x -> result.add(getChess(x)));
		return result;
	}
	
	void print() {
		for (int i = 0; i < 9; i++) {
			if (i == 0) {
				System.out.print("   ");
				for (int j = 0; j < 9; j++)
					System.out.print((char)(j + 'A') + "  ");
				System.out.println();
			}
			for (int j = 0; j < 9; j++) {
				if (j == 0)
					System.out.print(i+1+"  ");
				System.out.print(board[i][j].status + "  ");
			}
			System.out.println();
		}
	}
}

