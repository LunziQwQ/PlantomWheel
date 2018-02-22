import org.junit.Assert;
import org.junit.Test;
import sample.ChessBoard;
import sample.Main;
import sample.Strategies;

import java.lang.reflect.Method;

/**
 * ***********************************************
 * Created by Lunzi on 2/15/18.
 * Just presonal practice.
 * Not allowed to copy without permission.
 * ***********************************************
 */
public class TestStrategies {
	@Test
	public void testGetAreaScore() {
		Main.isBlackPlayer = true;
		new ChessBoard();
		try {
			Strategies strategies = new Strategies();
			
			Class[] args = new Class[1];
			args[0] = boolean.class;
			Method method = strategies.getClass().getDeclaredMethod("getAreaScore", args);
			method.setAccessible(true);
			
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					ChessBoard.board[i][j].setChess('b');
				}
			}
			int[][] answer = {{9, 9, 9}, {9, 9, 9}, {9, 9, 9}};
			int[][] testAnswer = (int[][]) method.invoke(strategies, true);
			Assert.assertArrayEquals(testAnswer, answer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
