package sample;

import java.util.Date;
import java.util.Random;

/**
 * ***********************************************
 * Created by Lunzi on 7/4/2017.
 * Just presonal practice.
 * Not allowed to copy without permission.
 * ***********************************************
 */
class Strategies {
	final int nearEmptyPrice = 21,
			nearFriendPrice = 30,
			nearEnemyPrice = -28,
			nearWallPrice = 12,
	
			farEmptyPrice = 17,
			farFriendPrice = 19,
			farEnemyPrice = -18,
			farWallPrice = 16;
	
	
	private Random random = new Random(new Date().getTime());
	
	boolean offensiveFlag = false;
	final int[][] randMode = {{+2, +2}, {+2, -2}, {-2, +2}, {-2, -2}};
	final Coord centerPoint = new Coord(4, 4);
	
	Coord[] staticStart = new Coord[6];
	
	private GameEngine gameEngine;
	
	public Strategies(GameEngine gameEngine){
		this.gameEngine = gameEngine;
		int mode = random.nextInt(4);
		staticStart[0] = new Coord(centerPoint.x + randMode[mode][0], centerPoint.y + randMode[mode][1]);
		staticStart[1] = new Coord(staticStart[0].x > 4 ? staticStart[0].x - 2 : staticStart[0].x + 2, staticStart[0].y);
		staticStart[2] = new Coord(staticStart[0].x, staticStart[0].y > 4 ? staticStart[0].y - 2 : staticStart[0].y + 2);
		staticStart[3] = new Coord(staticStart[0].x > 4 ? staticStart[0].x - 4 : staticStart[0].x + 4, staticStart[0].y);
		staticStart[4] = new Coord(staticStart[0].x, staticStart[0].y > 4 ? staticStart[0].y - 4 : staticStart[0].y + 4);
		staticStart[5] = centerPoint;
	}
	
	private int staticStep = -1;
	Coord getStep() {
		
		//静态开局
		if (staticStep < 5) {
			Coord temp = staticStart[staticStep+1];
			while (!gameEngine.chessBoard.board[temp.x][temp.y].canSet(gameEngine.imBlack)) {
				staticStep++;
			}
			staticStep++;
			return staticStart[staticStep];
		}
		Coord temp = null;
		if (offensiveFlag) {
			temp = offensive();
			if (temp != null) return temp;
			else{
				offensiveFlag = false;
				return fuzzy();
			}
		}
		
		if (GameEngine.stepCount > 30) {
			temp = defensive();
			if (temp != null) return temp;
			else return fuzzy();
		}
		
		return fuzzy();
	
	}
	
	private Coord offensive() {//距离己方棋子两格之内的进攻（优先吃子，然后补全，发现不是单一棋子优先补全
		for (GameEngine.Chess[] x : gameEngine.chessBoard.board) {
			for (GameEngine.Chess item : x) {
				int distance = 0;   //距离目标距离
				if (item.color == (gameEngine.imBlack ? '2' : '1')) {
					//进攻目标item
					//检测两格内是否有右方棋子
					boolean canAttack = false;
					Coord friend = null;
					for (int i = 0; i < 8; i++) {
						int tempX = item.x + gameEngine.nearChesses[i][0];
						int tempY = item.y + gameEngine.nearChesses[i][1];
						if (Coord.isCoordLegal(tempX, tempY)) {
							if (gameEngine.chessBoard.board[tempX][tempY].color == (gameEngine.imBlack ? '1' : '2')) {
								if (friend == null ||
										Math.abs(tempX - item.x) + Math.abs(tempY - item.y)
												< Math.abs(friend.x - item.x) + Math.abs(friend.y - item.y))
									friend = new Coord(tempX, tempY);
								canAttack = true;
								distance = 1;
							}
						}
					}
					if (!canAttack) {
						for (int i = 0; i < 16; i++) {
							int tempX = item.x + gameEngine.farNearChesses[i][0];
							int tempY = item.y + gameEngine.farNearChesses[i][1];
							if (Coord.isCoordLegal(tempX, tempY)) {
								if (gameEngine.chessBoard.board[tempX][tempY].color == (gameEngine.imBlack ? '1' : '2')) {
									if (friend == null ||
											Math.abs(tempX - item.x) + Math.abs(tempY - item.y)
													< Math.abs(friend.x - item.x) + Math.abs(friend.y - item.y))
										friend = new Coord(tempX, tempY);
									canAttack = true;
									distance = 2;
								}
							}
						}
					}
					if (canAttack) {
						if (distance == 1) {
							if (item.group == null) {
								for (int i = 0; i < 8; i++) {
									int tempX = item.x + gameEngine.nearChesses[i][0];
									int tempY = item.y + gameEngine.nearChesses[i][1];
									if (Coord.isCoordLegal(tempX, tempY)) {
										if (gameEngine.chessBoard.board[tempX][tempY].canSet(gameEngine.imBlack)) {
											boolean isNearFriend = false;
											for (GameEngine.Chess tempItem : gameEngine.chessBoard.board[tempX][tempY].getNearChess()) {
												if (tempItem.color == (gameEngine.imBlack ? '1' : '2')) {
													isNearFriend = true;
												}
											}
											if (isNearFriend)
												return new Coord(tempX, tempY);
										}
									}
									
								}
							} else {
								if (item.group.totallyAlive) return null;
								boolean libertyHaveNear = false;            //进攻目标的liberty有相邻的右方棋子
								for (GameEngine.Chess liberty : item.group.libertys) {
									if (liberty.canSet(gameEngine.imBlack)) {
										boolean tempNear = false;
										for (GameEngine.Chess tempItem : liberty.getNearChess()) {
											if (tempItem.color == (gameEngine.imBlack ? '1' : '2')) {
												tempNear = true;
												break;
											}
										}
										if (tempNear)
											return new Coord(liberty.x, liberty.y);
										
									}
								}
								for (GameEngine.Chess liberty : item.group.libertys) {
									if (liberty.canSet(gameEngine.imBlack)) {
										for (GameEngine.Chess tempItem : liberty.getNearChess()) {
											if (tempItem.canSet(gameEngine.imBlack)) {
												boolean tempNear = false;
												for (GameEngine.Chess _temp : tempItem.getNearChess()) {
													if (_temp.color == (gameEngine.imBlack ? '1' : '2')) {
														tempNear = true;
														break;
													}
												}
												if (tempNear) {
													return new Coord(tempItem.x, tempItem.y);
												}
											}
										}
									}
								}
							}
						} else {        //distance = 2
							if (Math.abs(item.x - friend.x) == 2 && Math.abs(item.y - friend.y) == 2)
								continue;
							if (Math.abs(item.x - friend.x) == 2) {
								if (friend.x - item.x > 0
										&& gameEngine.chessBoard.board[friend.x - 1][friend.y]
										.canSet(gameEngine.imBlack))
									return new Coord(friend.x - 1, friend.y);
								else if (gameEngine.chessBoard.board[friend.x + 1][friend.y]
										.canSet(gameEngine.imBlack))
									return new Coord(friend.x + 1, friend.y);
							} else {
								if (friend.y - item.y > 0) {
									if (gameEngine.chessBoard.board[friend.x][friend.y - 1]
											.canSet(gameEngine.imBlack))
										return new Coord(friend.x, friend.y - 1);
								} else if (gameEngine.chessBoard.board[friend.x][friend.y + 1].canSet(gameEngine.imBlack)) {
									return new Coord(friend.x, friend.y + 1);
								}
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	Coord defensive(){
		return null;
	}
	
	Coord fuzzy(){
		int[][] priceMap = new int[9][9];
		int max = -1000;
		for (GameEngine.Chess[] x : gameEngine.chessBoard.board) {
			for (GameEngine.Chess item : x) {
				if (item.color == '?') {
					int nearFriend = 0;
					for (int i = 0; i < 8; i++) {
						int tempX = item.x + gameEngine.nearChesses[i][0];
						int tempY = item.y + gameEngine.nearChesses[i][1];
						if (Coord.isCoordLegal(tempX, tempY)) {
							GameEngine.Chess tempChess = gameEngine.chessBoard.board[tempX][tempY];
							if (tempChess.color == '?') {
								priceMap[item.x][item.y] += nearEmptyPrice;     //空白
							}
							if (tempChess.color == (gameEngine.imBlack ? '1' : '2')) {
								priceMap[item.x][item.y] += nearFriendPrice;     //我方棋子
								nearFriend++;
							}
							if (tempChess.color == (gameEngine.imBlack ? '2' : '1')) {
								priceMap[item.x][item.y] += nearEnemyPrice;     //对手棋子
							}
						} else {
							priceMap[item.x][item.y] += nearWallPrice;         //棋盘边界
							nearFriend++;
						}
					}
					for (int i = 0; i < 16; i++) {
						int tempX = item.x + gameEngine.farNearChesses[i][0];
						int tempY = item.y + gameEngine.farNearChesses[i][1];
						if (Coord.isCoordLegal(tempX, tempY)) {
							GameEngine.Chess tempChess = gameEngine.chessBoard.board[tempX][tempY];
							if(tempChess.color == '?') priceMap[item.x][item.y] += farEmptyPrice;
							if(tempChess.color == (gameEngine.imBlack?'1':'2')) priceMap[item.x][item.y] += farFriendPrice;
							if(tempChess.color == (gameEngine.imBlack?'2':'1')) priceMap[item.x][item.y] +=farEnemyPrice;
							
						} else priceMap[item.x][item.y] += farWallPrice;
					}
					//防止自己填眼
					if (nearFriend >= 3) {
						priceMap[item.x][item.y] -= 100;
					}
				}
				if (max < priceMap[item.x][item.y]) {
					max = priceMap[item.x][item.y];
				}
			}
		}
		System.out.println("+=================================+");
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				System.out.print(priceMap[i][j] + "  ");
				if(priceMap[i][j] == 0) System.out.print("  ");
			}
			System.out.println();
		}
		System.out.println("+=================================+");
		
		
		if (max < 0) return null;
		int randCount = random.nextInt(81);
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				GameEngine.Chess item = gameEngine.chessBoard.board[i][j];
				if (item.color == '?' && priceMap[item.x][item.y] == max) {
					if(randCount == 0) return new Coord(i, j);
					randCount--;
				}
			}
			if(i == 8) i = 0;
		}
		return null;
	}
	
	//进攻型策略，围杀，当得到Oops或oops指令时，激活offensiveFlag，若无可落子进攻位置，取消flag，采用其他策略
	//防御性策略，扫描自己的气与Group，尽可能的连接group，在不存在两个活眼的地方补子
	//Fuzzy，模糊评估策略，把每个棋子以及其周边设为其影响力，遍历所有可落子的点，取落子后影响力最大的点。相连的子分数加成。
}
