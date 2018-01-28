package sample;

import java.util.*;

/**
 * ***********************************************
 * Created by Lunzi on 1/28/2018.
 * Just presonal practice.
 * Not allowed to copy without permission.
 * ***********************************************
 */
public class History {
	Stack<Chess[][]> chessboard;
	Stack<Coord> step;
	
	
	
	History(){
		chessboard = new Stack<>();
		step = new Stack<>();
	}
	
	boolean isPreviousStep(Coord pre) {
		return pre.equals(step.peek());
	}
	//TODO: 历史操作和棋盘的保存，防止重复争子，增加回退功能
	//TODO: 可能的情况下，添加保存棋谱，复盘功能
}
