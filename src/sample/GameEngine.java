package sample;

/**
 * ***********************************************S
 * Created by Lunzi on 10/11/17.
 * Just presonal practice.
 * Not allowed to copy without permission.
 * ***********************************************
 */
class GameEngine {
//	private Strategies strategies = new Strategies(this);
	ChessBoard chessBoard = new ChessBoard();
	static int stepCount = 0;
	boolean imBlack;
	boolean gameisStart = false;
	private Coord temp;     //等待输入验证是否落子成功的坐标
	
	
	void gameStart(boolean imBlack) {
		if (gameisStart) {
			System.out.println("--> Game is already be started.");
			return;
		}
		this.imBlack = imBlack;
		gameisStart = true;
		System.out.println("--> Game start! I'm " + (imBlack ? "black" : "white"));
		if(imBlack) go();
	}
	
	void replyYep(){
//		chessBoard.setChess(temp, imBlack ? '1' : '2');
		stepCount++;
	}
	
	void replyoops(){
//		chessBoard.setChess(temp, 'x');
//		strategies.offensiveFlag = true;
		go();
	}
	
	void replyOops(){
//		chessBoard.setChess(temp, imBlack ? '2' : '1');
//		strategies.offensiveFlag = true;
	}
	
	void go(){
//		temp = strategies.getStep();
		if (temp == null) {
			System.out.println("Pass");
			return;
		}
		System.out.printf("(%s,%s)\n", temp.x + 1, (char) (temp.y + 'A'));
	}
	
	void capture(Coord[] coords){
//		chessBoard.capture(coords);
	}
}
