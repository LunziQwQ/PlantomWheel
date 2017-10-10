package sample;

import java.util.ArrayList;
import java.util.List;

/**
 * ***********************************************
 * Created by Lunzi on 2/20/2017.
 * Just presonal practice.
 * Not allowed to copy without permission.
 * ***********************************************
 */
class GameEngine {
	private Strategies strategies = new Strategies(this);
	ChessBoard chessBoard = new ChessBoard();
	List<Group> groups = new ArrayList<>();
	 static int stepCount = 0;
	 boolean imBlack;
	private Coord temp;     //等待输入验证是否落子成功的坐标
	int[][] nearChesses = {{+1, 0}, {+1, +1}, {0, +1}, {-1, +1}, {-1, 0}, {-1, -1}, {0, -1}, {+1, -1}};
	int[][] farNearChesses = {
			{-2, 2}, {-1, 2}, {0, 2}, {1, 2},
			{2, 2}, {2, 1}, {2, 0}, {2, -1},
			{2, -2}, {1, -2}, {0, -2}, {-1, -2},
			{-2, -2}, {-2, -1}, {-2, 0}, {-2, 1}
	};
	
	void gameStart(boolean imBlack) {
		this.imBlack = imBlack;
		if(imBlack) go();
	}
	
	void replyYep(){
		chessBoard.setChess(temp, imBlack ? '1' : '2');
		stepCount++;
		MainFormController.stepCount.setValue(String.valueOf(stepCount));
	}
	
	void replyoops(){
		chessBoard.setChess(temp, 'x');
		strategies.offensiveFlag = true;
		go();
	}
	
	void replyOops(){
		chessBoard.setChess(temp, imBlack ? '2' : '1');
		strategies.offensiveFlag = true;
	}
	
	void go(){
		temp = strategies.getStep();
		if (temp == null) {
			System.out.println("Pass");
			return;
		}
		System.out.printf("(%s,%s)\n", temp.x + 1, (char) (temp.y + 'A'));
	}
	
	void capture(Coord[] coords){
		chessBoard.capture(coords);
	}
	
	class Chess {
		int x, y;
		Group group = null;
		int health;
		char color = '?';      //1黑,2白,?为空，x为空但不可用
		
		Chess(int x, int y, char color) {
			this.x = x;
			this.y = y;
			this.color = color;
		}
		
		//更新本棋子的信息
		void updateThisChess() {
			updateGroup();
			updateHealth();
			if (this.health == 0 && (this.color == '1' || this.color == '2')) {
				Coord[] tempCoords = {new Coord(x, y)};
				capture(tempCoords);
			}
		}
		
		//获得相邻的四个棋子
		List<Chess> getNearChess() {
			int[][] para = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
			List<Chess> nearChesses = new ArrayList<>();
			for (int i = 0; i < 4; i++) {
				if (isNearChessLegal(this, para[i][0], para[i][1])) {
					nearChesses.add(chessBoard.board[x + para[i][0]][y + para[i][1]]);
				}
			}
			return nearChesses;
		}
		
		//更新本棋子的Group信息
		private void updateGroup() {
			List<Chess> nearChesses = getNearChess();
			for (Chess x : nearChesses) {
				if (x.color == this.color) {
					if (x.group == null) {
						Group temp = new Group(this.color);
						groups.add(temp);
						temp.addChess(this);
						temp.addChess(x);
					} else {
						x.group.addChess(this);
					}
					return;
				}
			}
		}
		
		
		private boolean isNearChessLegal(Chess origin, int x, int y) {
			return origin.x + x >= 0 && origin.x + x < 9 && origin.y + y >= 0 && origin.y + y < 9;
		}
		
		/**
		 * 更新本棋子的气
		 */
		private void updateHealth() {
			health = 0;
			if (group == null)
				getNearChess().stream().filter(x -> (x.color != '1' && x.color != '2')).forEach(x -> health++);
			else health = group.health;
		}
		
		void setChess(char color) {
			this.color = color;
			updateThisChess();
			this.getNearChess().forEach(Chess::updateThisChess);
		}
		
		//判断该Chess是否可以落子
		 boolean canSet(boolean isBlack) {
			 if (this.color == '1' || this.color == '2') return false;
			//检查周边棋子是否有空
			List<Chess> near = getNearChess();
			for (Chess x : near) {
				if (x.color != '1' && x.color != '2') {
					return true;
				}
			}
			
			for (Chess x : near) {
				
				//检查周边是否有友方棋子且落子后是否有气
				if (x.color == (isBlack ? '1' : '2')) {
					if (x.group != null) {
						if (x.group.health >= 2)
							return true;
					} else if (x.health >= 2)
						return true;
					
				//检查周边知否有对方棋子且落子后可提子
				} else if (x.color == (isBlack ? '2' : '1')) {
					if (x.group != null) {
						if (x.group.health == 1)
							return true;
					} else if (x.health == 1)
						return true;
				}
			}
			return false;
		}
		
	}
	
	
	class Group {
		int health = 0;
		int size = 0;
		List<Chess> chesses = new ArrayList<>();
		List<Chess> libertys = new ArrayList<>();        //该棋子组的气
		char color = '?';
		boolean totallyAlive = false;
		
		Group(char color) {
			this.color = color;
		}
		
		void addChess(Chess chess) {
			size++;
			chesses.add(chess);
			chess.group = this;
			update();
		}
		
		private boolean checkTotallyAlive() {
			
			int aliveLiberty = 0;
			for (Chess liberty : libertys) {
				int nearCount = 0;
				int fakeCount = 0;
				for (int i = 0; i < 8; i++) {
					int tempX = liberty.x + nearChesses[i][0];
					int tempY = liberty.x + nearChesses[i][1];
					if (Coord.isCoordLegal(tempX, tempY)) {
						if (chessBoard.board[tempX][tempY].color == (imBlack ? '1' : '2')) {
							nearCount++;
						}else {
							tempX += nearChesses[i][0];
							tempY += nearChesses[i][1];
							if (Coord.isCoordLegal(tempX, tempY)) {
								if (chessBoard.board[tempX][tempY].color == (imBlack ? '1' : '2')) {
									fakeCount++;
									if (fakeCount <= 2) nearCount++;
								}
							} else fakeCount++;
						}
					} else fakeCount++;
				}

				if (nearCount >= 7) aliveLiberty++;
			}
			return aliveLiberty >= 2;
		}
		
		private void update() {
			if (this.chesses.size() == 0) {
				groups.remove(this);
				return;
			}
			this.totallyAlive = checkTotallyAlive();
			this.health = 0;
			for (Chess item : chesses) {
				for (Chess near : item.getNearChess()) {
					if (near.color != '1' && near.color != '2') {
						for (Chess qi : libertys)
							if (qi.x == near.x && qi.y == near.y) {
								break;
							}
						libertys.add(near);
						this.health++;
					}
				}
			}
			
			if (this.health == 0) {
				groups.remove(this);
				chesses.forEach(x -> {
					chessBoard.board[x.x][x.y].color = '?';
					chessBoard.board[x.x][x.y].group = null;
				});
			}
		}
	}
	
	
	class ChessBoard {
		Chess[][] board;
		
		ChessBoard() {
			board = new Chess[9][9];
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					board[i][j] = new Chess(i, j, '?');
				}
			}
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
					System.out.print(board[i][j].color + "  ");
				}
				System.out.println();
			}
		}
		
		/**
		 * 设置棋子信息（落子，不可下）
		 */
		void setChess(Coord coord, char color) {
			Chess temp = chessBoard.board[coord.x][coord.y];
			temp.setChess(color);
			if (color == 'x') {
				boolean haveNearFriend = false;
				for(Chess x : temp.getNearChess()){
					if (x.color == (imBlack ? '1' : '2')) {
						haveNearFriend = true;
						break;
					}
				}
				if (haveNearFriend) {
					for (Chess x : temp.getNearChess()) {
						if (x.color == (imBlack ? '1' : '2')) {
							if (x.group == null) {
								x.getNearChess().forEach(item -> {
									if (item.color == '?') {
										item.setChess(imBlack ? '2' : '1');
									}
								});
							} else {
								for (Chess item:x.group.libertys){
									if(item.color == '?')
										item.setChess(imBlack ? '2' : '1');
								}
							}
						}
					}
				} else
					chessBoard.board[coord.x][coord.y].getNearChess().forEach(item -> item.setChess(imBlack ? '2' : '1'));
			}
		}
		
		/**
		 * 提子
		 */
		void capture(Coord[] coords) {
			boolean coordsIsOurs = (chessBoard.board[coords[0].x][coords[0].y].color == '1') == imBlack;
			if (coords.length == 1) {
				if (coordsIsOurs) {
					strategies.offensiveFlag = true;
					setChess(coords[0], 'x');
					chessBoard.board[coords[0].x][coords[0].y].getNearChess().forEach(item -> item.setChess(imBlack ? '2' : '1'));
				} else {
					setChess(coords[0], '?');
				}
			} else {
				Group tempGroup = null;
				for (Coord coord : coords) {
					Chess tempChess = chessBoard.board[coord.x][coord.y];
					if (tempChess.group == null) {
						setChess(coord, coordsIsOurs ? 'x' : '?');
					} else {
						if (tempGroup == null) {
							tempGroup = tempChess.group;
							captureGroup(tempGroup, coordsIsOurs);
						} else {
							if (tempGroup != tempChess.group) {
								tempGroup = tempChess.group;
								captureGroup(tempGroup, coordsIsOurs);
							}
						}
					}
				}
			}
		}
		
		private void captureGroup(Group group, boolean coordsIsOurs) {
			try {
				if (coordsIsOurs) {
					strategies.offensiveFlag = true;
					for (Chess item : group.libertys) {
						item.color = imBlack ? '2' : '1';
						item.updateThisChess();
					}
				}
			
			for (Chess item : group.chesses) {
				item.setChess('?');
				item.group = null;
			}
			groups.remove(group);
			} catch (Exception e) {
				System.out.println("-->" + e.getMessage());
			}
		}
	}
}