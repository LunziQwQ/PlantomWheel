package sample;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * ***********************************************
 * Created by Lunzi on 10/11/17.
 * Just presonal practice.
 * Not allowed to copy without permission.
 * ***********************************************
 */
public class Strategies {
	/**
	 * 策略参数设置
	 */
	private final int
			centerCompleteCount = 7,    //中心3*3区域的彻底占领棋子数
			sideCompleteCount = 6,      //四个边3*3区域的彻底占领棋子数
			angleCompleteCount = 5,     //四个角3*3区域的彻底占领棋子数
			defenciveOpenStep = 27;     //总步数多少步后强制启动defencive策略
	
	private Random random;
	
	/**
	 * 静态开局棋谱
	 */
	private Coord[] staticStart;
	
	/**
	 * 缓存的本局我方、敌方棋子颜色
	 */
	private char myStatus, enemyStatus;
	
	
	public Strategies() {
		random = new Random(new Date().getTime());
		staticStart = new Coord[6];
		createRandomStart();
		myStatus = Main.isBlackPlayer ? 'b' : 'w';
		enemyStatus = Main.isBlackPlayer ? 'w' : 'b';
	}
	
	/**
	 * 随机选择四个角其中一个为方向生成静态开局
	 */
	private void createRandomStart() {
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
	
	/**
	 * 静态开局策略
	 *
	 * @return 策略所得的坐标
	 */
	private Coord staticOpen() {
		int staticStep = 0;
		while (!ChessBoard.getChess(staticStart[staticStep]).canSet(Main.isBlackPlayer)
				|| ChessBoard.getChess(staticStart[staticStep]).status == '?') {
			staticStep++;
			if (staticStep >= 6) return null;
		}
		return staticStart[staticStep];
	}
	
	/**
	 * 自动选择不同策略得到落子坐标
	 *
	 * @return 通过策略得到的落子坐标，所有策略不可用时返回null
	 */
	Coord getStep() {
		int nowStepCount = Integer.parseInt(MainFormController.stepCount.getValue());
		Coord tempStep;
		
		//尝试使用静态开局
		if (nowStepCount < 6) {
			tempStep = staticOpen();
			if (tempStep != null) return tempStep;
		}
		
		//尝试检查是否存在未知敌方坐标（绘制问号的坐标），探测具体信息
		tempStep = checkUnknown();
		if (checkUnknown() != null)
			return tempStep;
		
		//尝试检查是否达到强制防御策略的步数，使用防御策略
		if (nowStepCount > defenciveOpenStep) {
			tempStep = defensive();
			if (tempStep != null) return tempStep;
		}
		
		//尝试使用进攻策略
		tempStep = offensive();
		if (tempStep != null) return tempStep;
		
		//尝试使用评估策略，若评估策略不可用使用防御策略
		tempStep = fuzzy();
		return tempStep != null ? tempStep : defensive();
	}
	
	/**
	 * 检查棋盘上是否有未知敌方坐标，若存在返回距离其他己方棋子最近的探测坐标
	 *
	 * @return 最优的探测坐标，若无位置地方坐标或无合适探测位置返回null
	 */
	private Coord checkUnknown() {
		List<Chess> unknownList = getUnknownList();
		if (unknownList != null) {
			for (Chess item : unknownList) {
				ChessBoard.getChesses(item.coord.getNear4Coord(true)).forEach(nearChess -> {
					if (nearChess.status == myStatus) {
						item.status = enemyStatus;
					}
				});
				
				if (item.status == '?') {
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
	
	/**
	 * 获取棋盘上未知敌方棋子
	 *
	 * @return 未知敌方棋子的List，若无未知敌方棋子返回null
	 */
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
	
	/**
	 * 进攻策略
	 * 1、遍历整个棋盘，遇到敌方棋子，检测生命值是否仅剩1，是则尝试提子
	 * 2、遍历整个棋盘，遇到敌方棋子，检测两格范围内是否有友方棋子，有则从最近的友方棋子开始伸展包围
	 * 3、
	 * @return 基于进攻策略的落子坐标，若策略不可用返回null
	 */
	private Coord offensive() {
		
		//搜索整个棋盘的敌方棋子，若可提子，返回能提子的坐标
		for (Chess[] chesses : ChessBoard.board) {
			for (Chess item : chesses) {
				if (item.status == enemyStatus) {
					
					//若该敌方棋子生命值仅为1，尝试提子
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
		
		//遍历整个棋盘的地方棋子
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
							Coord temp;
							
							if (Math.abs(item.coord.x - friend.x) == 2 && Math.abs(item.coord.y - friend.y) == 2)
								continue;
							if (Math.abs(item.coord.x - friend.x) == 2) {
								if (friend.x - item.coord.x > 0
										&& (temp = new Coord(friend.x - 1, friend.y)).isLegal()
										&& ChessBoard.getChess(temp).canSet(Main.isBlackPlayer)) {
									return temp;
								} else if ((temp = new Coord(friend.x + 1, friend.y)).isLegal()
										&& ChessBoard.getChess(temp).canSet(Main.isBlackPlayer))
									return temp;
							} else {
								if (friend.y - item.coord.y > 0) {
									if ((temp = new Coord(friend.x, friend.y - 1)).isLegal()
											&& ChessBoard.getChess(temp).canSet(Main.isBlackPlayer))
										return temp;
								} else if ((temp = new Coord(friend.x, friend.y + 1)).isLegal()
										&& ChessBoard.getChess(temp).canSet(Main.isBlackPlayer)) {
									return temp;
								}
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	/**TODO：Finish it
	 * 防御策略
	 * 1、遍历整个棋盘，寻找友方棋子
	 *      1.1 - 若友方棋子为独子，寻找周围8个坐标是否存在友方棋子，若存在则连接起来
	 *      1.2 - 若友方棋子为group且该group并不确定存活，遍历所有liberty，逐一检查，在周围存在最多友方棋子的liberty周围完成为活眼
	 *      1.3 - 若友方棋子为group且该group确定存活，遍历group所有Chess，逐一检查每个Chess两格内是否存在友方非必定存活棋子，若存在尝试连接
	 * @return 基于进攻策略的落子坐标，若策略不可用返回null
	 */
	private Coord defensive() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				Chess temp = ChessBoard.board[i][j];
				if (temp.status == myStatus) {
					if (temp.group == null) {
						for (Chess x : ChessBoard.getChesses(temp.coord.getNear8Coord(true))) {
							if (x.status == myStatus) {
								Coord dir = new Coord(x.coord.x - temp.coord.x, x.coord.y - temp.coord.y);
								Coord connect = new Coord(temp.coord.x + dir.x, temp.coord.y);
								if (!connect.isLegal()) continue;
								if (ChessBoard.getChess(connect).status == 'e') {
									return connect;
								}
								connect = new Coord(x.coord.x, x.coord.y + dir.y);
								if (ChessBoard.getChess(connect).status == 'e') {
									return connect;
								}
							}
						}
					}
				}
			}
		}
		
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				Chess temp = ChessBoard.board[i][j];
				if (temp.status == 'e') {
					long friendNearCount = ChessBoard.getChesses(temp.coord.getNear4Coord(true)).stream().filter(x -> x.status == myStatus).count();
					if (friendNearCount >= (long) ChessBoard.getChesses(temp.coord.getNear4Coord(true)).size() - 2) {
						for (Chess chess : ChessBoard.getChesses(temp.coord.getNear4Coord(true))) {
							if (chess.status == 'e') {
								long chessFriendNearCount = ChessBoard.getChesses(chess.coord.getNear4Coord(true)).stream().filter(x -> x.status == myStatus).count();
								if (chessFriendNearCount < (long) ChessBoard.getChesses(chess.coord.getNear4Coord(true)).size() - 1) {
									return chess.coord;
								}
							}
						}
					}
					friendNearCount = ChessBoard.getChesses(temp.coord.getNear8Coord(true)).stream().filter(x -> x.status == myStatus).count();
					if (friendNearCount < (long) ChessBoard.getChesses(temp.coord.getNear8Coord(true)).size() - 1) {
						for (Chess chess : ChessBoard.getChesses(temp.coord.getNear8Coord(true))) {
							if (chess.status == 'e') {
								long chessFriendNearCount = ChessBoard.getChesses(chess.coord.getNear4Coord(true)).stream().filter(x -> x.status == myStatus).count();
								if (chessFriendNearCount < (long) ChessBoard.getChesses(chess.coord.getNear4Coord(true)).size() - 1) {
									return chess.coord;
								}
							}
						}
					}
				}
				
			}
		}
		return null;
	}
	
	private int[][] getAreaScore(boolean isMyStatus) {
		int[][] areaScore = new int[3][3];
		for (int mid_i = 0; mid_i < 3; mid_i++) {
			for (int mid_j = 0; mid_j < 3; mid_j++) {
				int tempCount = 0;
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						if (ChessBoard.board[mid_i * 3 + i][mid_j * 3 + j].status == (isMyStatus ? myStatus : enemyStatus)) {
							tempCount++;
						}
					}
				}
				areaScore[mid_i][mid_j] = tempCount;
			}
		}
		return areaScore;
	}
	
	private Coord fuzzyMidArea(int midX, int midY) {
		int directionX = midX - 1;
		int directionY = midY - 1;
		int midCenterX = midX * 3 + 1;
		int midCenterY = midY * 3 + 1;
		int[][] orderList = {
				{midCenterX - directionX, midCenterY - directionY},
				{midCenterX - directionX, midCenterY}, {midCenterX, midCenterY - directionY},
				{midCenterX - directionX, midCenterY + directionY}, {midCenterX + directionX, midCenterY - directionY},
				{midCenterX + directionX, midCenterY}, {midCenterX, midCenterY + directionY},
				{midCenterX + directionX, midCenterY + directionY}
		};
		
		Chess midCenter = ChessBoard.getChess(new Coord(midCenterX, midCenterY));
		if (midCenter.status == 'e' && ChessBoard.getChesses(midCenter.coord.getNear4Coord(true)).stream().filter(x -> x.status == enemyStatus).count() <= 1) {
			return midCenter.coord;
		}
		
		//按orderlist顺序遍历，若存在相邻且不填眼则选择
		for (int i = 0; i < 8; i++) {
			Chess temp = ChessBoard.getChess(new Coord(orderList[i][0], orderList[i][1]));
			if (temp.status == 'e') {
				long nearCount = ChessBoard.getChesses(temp.coord.getNear4Coord(true)).stream().filter(x -> x.status == myStatus).count();
				if (nearCount > 0 && nearCount < 2) {
					return temp.coord;
				}
			}
		}
		
		//若整个orderList都不存在相邻，重新遍历，选择不填眼的坐标
		for (int i = 0; i < 8; i++) {
			Chess temp = ChessBoard.getChess(new Coord(orderList[i][0], orderList[i][1]));
			if (temp.status == 'e') {
				long nearCount = ChessBoard.getChesses(temp.coord.getNear4Coord(true)).stream().filter(x -> x.status == myStatus).count();
				if (nearCount < 2) {
					return temp.coord;
				}
			}
		}
		return null;
	}
	
	private Coord checkMidSet(int[][] midSet, int completeCount) {
		int[][] myAreaScore = getAreaScore(true);
		int[][] enemyAreaScore = getAreaScore(false);
		
		for (int[] aMidSet : midSet) {
			int tempX = aMidSet[0];
			int tempY = aMidSet[1];
			if (myAreaScore[tempX][tempY] < completeCount && enemyAreaScore[tempX][tempY] < completeCount) {
				Coord tempCoord = fuzzyMidArea(tempX, tempY);
				if (tempCoord != null) return tempCoord;
			}
		}
		return null;
	}
	
	private Coord fuzzy() {
		Coord result;
		
		//first check angle
		int[][] angleSet = {{0, 0}, {2, 0}, {0, 2}, {2, 2}};
		int[][] sideSet = {{0, 1}, {1, 0}, {2, 1}, {1, 2}};
		int[][] centerSet = {{1, 1}};
		
		result = checkMidSet(angleSet, angleCompleteCount);
		if (result != null) return result;
		
		result = checkMidSet(sideSet, sideCompleteCount);
		if (result != null) return result;
		
		result = checkMidSet(centerSet, centerCompleteCount);
		if (result != null) return result;
		
		return null;
	}
	
	//进攻型策略，围杀，当得到Oops或oops指令时，激活offensiveFlag，若无可落子进攻位置，取消flag，采用其他策略
	//TODO: 防御性策略，扫描自己的气与Group，尽可能的连接group，在不存在两个活眼的地方补子
	//FIXME: 测试发现的特殊情况，当尝试落子自身group的最后一个气眼失败时，认定为敌方棋子（加判定or防止填眼）、建议后者
	//FIXME: 进攻flag可以让策略函数自行检测，不需要手动维护flag
}

/* 旧版Fuzzy函数

    private final int
			nearEmptyPrice = 21,
			nearFriendPrice = 30,
			nearEnemyPrice = -28,
			nearWallPrice = 12,

			farEmptyPrice = 17,
			farFriendPrice = 19,
			farEnemyPrice = -18,
			farWallPrice = 16;


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
 */


