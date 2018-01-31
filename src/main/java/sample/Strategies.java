package sample;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ***********************************************
 * Created by Lunzi on 10/11/17.
 * Just presonal practice.
 * Not allowed to copy without permission.
 * ***********************************************
 */
class Strategies {
	private final int
			nearEmptyPrice = 21,
			nearFriendPrice = 30,
			nearEnemyPrice = -28,
			nearWallPrice = 12,

			farEmptyPrice = 17,
			farFriendPrice = 19,
			farEnemyPrice = -18,
			farWallPrice = 16;


	private Random random;

	boolean offensiveFlag;

	private Coord[] staticStart;
	
	private int staticStep = -1;
	
	private char myStatus, enemyStatus;
	
	Strategies() {
		offensiveFlag = false;
		random = new Random(new Date().getTime());
		staticStart = new Coord[6];
		createRandomStart();
		myStatus = Main.isBlackPlayer ? 'b' : 'w';
		enemyStatus = Main.isBlackPlayer ? 'w' : 'b';
	}

	private void createRandomStart(){
		Coord centerPoint = new Coord(4, 4);
		int[][] randMode = {{+2, +2}, {+2, -2}, {-2, +2}, {-2, -2}};
		
		//生成本局的随机静态开局
		int[] mode = randMode[random.nextInt(4)];
		staticStart[0] = new Coord(centerPoint.x + mode[0], centerPoint.y + mode[1]);
		staticStart[1] = new Coord(staticStart[0].x > 4 ? staticStart[0].x - 2 : staticStart[0].x + 2, staticStart[0].y);
		staticStart[2] = new Coord(staticStart[0].x, staticStart[0].y > 4 ? staticStart[0].y - 2 : staticStart[0].y + 2);
		staticStart[3] = new Coord(staticStart[0].x > 4 ? staticStart[0].x - 4 : staticStart[0].x + 4, staticStart[0].y);
		staticStart[4] = new Coord(staticStart[0].x, staticStart[0].y > 4 ? staticStart[0].y - 4 : staticStart[0].y + 4);
		staticStart[5] = centerPoint;
	}
	
	private Coord staticOpen(){
		Coord temp = staticStart[staticStep+1];
		while (!ChessBoard.getChess(temp).canSet(Main.isBlackPlayer)) {
			staticStep++;
		}
		staticStep++;
		return staticStart[staticStep];
	}
	
	Coord getStep() {
		Coord tempStep;

		//静态开局
		if (staticStep < 5) {
			return staticOpen();
		}
		
		tempStep = checkUnknown();
		if(checkUnknown() != null)
			return tempStep;
		
		if (offensiveFlag) {
			tempStep = offensive();
			if (tempStep != null) return tempStep;
			else{
				offensiveFlag = false;
				return fuzzy();
			}
		}

		return fuzzy();
	}
	
	private Coord checkUnknown() {
		List<Chess> unknownList =getUnknownList();
		if (unknownList != null) {
			for (Chess item : unknownList) {
				ChessBoard.getChesses(item.coord.getNear4Coord(true)).forEach(nearChess ->{
					if(nearChess.status == myStatus) {
						item.status = enemyStatus;
					}
				});
				
				if(item.status == '?'){
					Coord nearest = null;  //距离己方棋子最近的near4坐标
					for (Chess x : ChessBoard.getChesses(item.coord.getNear4Coord(true))) {
						if (x.status == 'e') {
							if (ChessBoard.getChesses(x.coord.getNear8Coord(true))
									.stream().anyMatch(n -> n.status == myStatus)) {
								return x.coord;
							}
						}
					}
					for (Chess x : ChessBoard.getChesses(item.coord.getNear4Coord(true))) {
						if (x.status == 'e') {
							if (ChessBoard.getChesses(x.coord.getNear16Coord(true))
									.stream().anyMatch(n -> n.status == myStatus)) {
								return x.coord;
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	private List<Chess> getUnknownList() {
		List<Chess> unknownList = new ArrayList<>();
		for (Chess[] chesses : ChessBoard.board) {
			for (Chess x : chesses) {
				if (x.status == '?') {
					unknownList.add(x);
				}
			}
		}
		return unknownList.size() > 0 ? unknownList : null;
	}

	private Coord offensive() {//距离己方棋子两格之内的进攻（优先吃子，然后补全，发现不是单一棋子优先补全
		for (Chess[] chesses : ChessBoard.board) {
			for (Chess item : chesses) {
				if (item.status == enemyStatus) {
					//最后一步直接执行
					if (item.health == 1) {
						if (item.group != null && !item.group.totallyAlive) {
							return item.group.libertys.iterator().next().coord;
						} else {
							return ChessBoard.getChesses(item.coord.getNear4Coord(true))
									.stream().filter(l -> l.status == 'e').collect(Collectors.toList()).get(0).coord;
						}
					}
				}
			}
		}
		for (Chess[] chesses : ChessBoard.board) {
			for (Chess item : chesses) {
				int distance = 0;   //距离目标距离
				if (item.status == enemyStatus) {
					
					//进攻目标item
					//检测两格内是否有右方棋子
					boolean canAttack = false;
					Coord friend = null;
					List<Chess> near8Chesses = ChessBoard.getChesses(item.coord.getNear8Coord(true));
					for (Chess chess : near8Chesses) {
						if (chess.status == myStatus) {
							if (friend == null ||
									Math.abs(chess.coord.x - item.coord.x) + Math.abs(chess.coord.y - item.coord.y)
											< Math.abs(friend.x - item.coord.x) + Math.abs(friend.y - item.coord.y))
								friend = new Coord(chess.coord);
							canAttack = true;
							distance = 1;
						}
					}
					if (!canAttack) {
						List<Chess> near16Chesses = ChessBoard.getChesses(item.coord.getNear16Coord(true));
						for (Chess chess : near16Chesses) {
							if (chess.status == (myStatus)) {
								if (friend == null ||
										Math.abs(chess.coord.x - item.coord.x) + Math.abs(chess.coord.y - item.coord.y)
												< Math.abs(friend.x - item.coord.x) + Math.abs(friend.y - item.coord.y))
									friend = new Coord(chess.coord);
								canAttack = true;
								distance = 2;
							}
						}
					}
					
					//若两格内有己方棋子，开始进攻
					if (canAttack) {
						if (distance == 1) {
							if (item.group == null) {
								for (Chess chess : near8Chesses) {
									if (chess.canSet(Main.isBlackPlayer)) {
										boolean isNearFriend = false;
										for (Chess tempItem : ChessBoard.getChesses(chess.coord.getNear4Coord(true))) {
											if (tempItem.status == (myStatus)) {
												isNearFriend = true;
											}
										}
										if (isNearFriend)
											return new Coord(chess.coord);
									}
								}
							} else {
								if (item.group.totallyAlive) return null;
								boolean libertyHaveNear = false;            //进攻目标的liberty有相邻的右方棋子
								for (Chess liberty : item.group.libertys) {
									if (liberty.canSet(Main.isBlackPlayer)) {
										boolean tempNear = false;
										for (Chess tempItem : ChessBoard.getChesses(liberty.coord.getNear4Coord(true))) {
											if (tempItem.status == (myStatus)) {
												tempNear = true;
												break;
											}
										}
										if (tempNear)
											return new Coord(liberty.coord.x, liberty.coord.y);
										
									}
								}
								for (Chess liberty : item.group.libertys) {
									if (liberty.canSet(Main.isBlackPlayer)) {
										for (Chess tempItem : ChessBoard.getChesses(liberty.coord.getNear4Coord(true))) {
											if (tempItem.canSet(Main.isBlackPlayer)) {
												boolean tempNear = false;
												for (Chess _temp : ChessBoard.getChesses(tempItem.coord.getNear4Coord(true))) {
													if (_temp.status == (myStatus)) {
														tempNear = true;
														break;
													}
												}
												if (tempNear) {
													return new Coord(tempItem.coord.x, tempItem.coord.y);
												}
											}
										}
									}
								}
							}
						} else {        //distance = 2
							if (Math.abs(item.coord.x - friend.x) == 2 && Math.abs(item.coord.y - friend.y) == 2)
								continue;
							if (Math.abs(item.coord.x - friend.x) == 2) {
								if (friend.x - item.coord.x > 0
										&& ChessBoard.board[friend.x - 1][friend.y]
										.canSet(Main.isBlackPlayer))
									return new Coord(friend.x - 1, friend.y);
								else if (ChessBoard.board[friend.x + 1][friend.y]
										.canSet(Main.isBlackPlayer))
									return new Coord(friend.x + 1, friend.y);
							} else {
								if (friend.y - item.coord.y > 0) {
									if (ChessBoard.board[friend.x][friend.y - 1]
											.canSet(Main.isBlackPlayer))
										return new Coord(friend.x, friend.y - 1);
								} else if (ChessBoard.board[friend.x][friend.y + 1].canSet(Main.isBlackPlayer)) {
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

	private Coord fuzzy(){
		int[][] priceMap = new int[9][9];
		int max = -1000;
		for (Chess[] x : ChessBoard.board) {
			for (Chess item : x) {
				if (item.status == 'e') {
					int nearFriend = 0;
					List<Coord> near8Chesses = item.coord.getNear8Coord(false);
					for (Coord coord : near8Chesses) {
						if (coord.isLegal()) {
							if (ChessBoard.getChess(coord).status == 'e') {
								priceMap[item.coord.x][item.coord.y] += nearEmptyPrice;     //空白
							}
							if (ChessBoard.getChess(coord).status == (myStatus)) {
								priceMap[item.coord.x][item.coord.y] += nearFriendPrice;     //我方棋子
								nearFriend++;
							}
							if (ChessBoard.getChess(coord).status == (enemyStatus)) {
								priceMap[item.coord.x][item.coord.y] += nearEnemyPrice;     //对手棋子
							}
						} else {
							priceMap[item.coord.x][item.coord.y] += nearWallPrice;         //棋盘边界
							nearFriend++;
						}
					}
					List<Coord> near16Chesses = item.coord.getNear16Coord(false);
					for (Coord coord : near16Chesses) {
						if (coord.isLegal()) {
							if (ChessBoard.getChess(coord).status == 'e') priceMap[item.coord.x][item.coord.y] += farEmptyPrice;
							if (ChessBoard.getChess(coord).status == myStatus) priceMap[item.coord.x][item.coord.y] += farFriendPrice;
							if (ChessBoard.getChess(coord).status == enemyStatus) priceMap[item.coord.x][item.coord.y] +=farEnemyPrice;
						} else priceMap[item.coord.x][item.coord.y] += farWallPrice;
					}
					//防止自己填眼
					if (nearFriend >= 3) {
						priceMap[item.coord.x][item.coord.y] -= 500;
					}
				}
				if (max < priceMap[item.coord.x][item.coord.y]) {
					max = priceMap[item.coord.x][item.coord.y];
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


		if (max <= 0) return null;
		int randCount = random.nextInt(81);
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				Chess item = ChessBoard.board[i][j];
				if (item.status == 'e' && priceMap[item.coord.x][item.coord.y] == max) {
					if(randCount == 0) return new Coord(i, j);
					randCount--;
				}
			}
			if(i == 8) i = 0;
		}
		return null;
	}

	//进攻型策略，围杀，当得到Oops或oops指令时，激活offensiveFlag，若无可落子进攻位置，取消flag，采用其他策略
	//TODO: 防御性策略，扫描自己的气与Group，尽可能的连接group，在不存在两个活眼的地方补子
	//TODO: Fuzzy()，模糊评估策略，将棋盘分为九个区域，根据区域己方棋子数量决定势力判断，选择区域落子。
	//FIXME: 测试发现的特殊情况，当尝试落子自身group的最后一个气眼失败时，认定为敌方棋子（加判定or防止填眼）、建议后者
	//FIXME: 进攻flag可以让策略函数自行检测，不需要手动维护flag
}

