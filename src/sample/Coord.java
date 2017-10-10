package sample;

/**
 * ***********************************************
 * Created by Lunzi on 10/10/2017.
 * Just presonal practice.
 * Not allowed to copy without permission.
 * ***********************************************
 */
public class Coord {
	int x, y;
	
	Coord(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	static boolean isCoordLegal(int x, int y) {
		return x >= 0 && x < 9 && y >= 0 && y < 9;
	}

	boolean isLegal(){
		return x >= 0 && x < 9 && y >= 0 && y < 9;
	}
}
