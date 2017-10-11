package sample;

import java.util.ArrayList;
import java.util.List;

/**
 * ***********************************************
 * Created by Lunzi on 10/10/2017.
 * Just presonal practice.
 * Not allowed to copy without permission.
 * ***********************************************
 */
class Coord {
	int x, y;
	private int[][] near4Coords = {
			{0, 1}, {1, 0}, {0, -1}, {-1, 0}
	};
	private int[][] near8Coords = {
			{+1, 0}, {+1, +1}, {0, +1}, {-1, +1},
			{-1, 0}, {-1, -1}, {0, -1}, {+1, -1}
	};
	private int[][] near16Coords = {
			{-2, 2}, {-1, 2}, {0, 2}, {1, 2},
			{2, 2}, {2, 1}, {2, 0}, {2, -1},
			{2, -2}, {1, -2}, {0, -2}, {-1, -2},
			{-2, -2}, {-2, -1}, {-2, 0}, {-2, 1}
	};
	
	
	
	Coord(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	Coord(Coord coord) {
		x = coord.x;
		y = coord.y;
	}
	
	static boolean isCoordLegal(int x, int y) {
		return x >= 0 && x < 9 && y >= 0 && y < 9;
	}
	
	static boolean equals(Coord a, Coord b) {
		return a.x == b.x && a.y == b.y;
	}
	
	
	boolean equals(Coord coord) {
		return x == coord.x && y == coord.y;
	}
	
	boolean isLegal(){
		return x >= 0 && x < 9 && y >= 0 && y < 9;
	}
	
	private List<Coord> getNearCoord(int[][] paras, boolean isLegal) {
		List<Coord> nearCoords = new ArrayList<>();
		for (int[] para : paras) {
			Coord temp = new Coord(x + para[0], y + para[1]);
			if (isLegal) {
				if (temp.isLegal()) nearCoords.add(temp);
			} else {
				nearCoords.add(temp);
			}
		}
		return nearCoords;
	}
	
	List<Coord> getNear4Coord(boolean onlyLegal) { return getNearCoord(near4Coords, onlyLegal); }
	
	List<Coord> getNear8Coord(boolean onlyLegal) { return getNearCoord(near8Coords, onlyLegal); }
	
	List<Coord> getNear16Coord(boolean onlyLegal) { return getNearCoord(near16Coords,onlyLegal); }
}
