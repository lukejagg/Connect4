// © 2018 Luke Jagg
// MIT License

package Connect4;

public class Board {

	public int winLength = 4;
			
	public int[][] board;
	public int winner;
	
	public Board(Board copied) {
		
		this.board = new int[copied.board.length][copied.board[0].length];
		for (int i = 0; i < copied.board.length; i++) {
			for (int j = 0; j < copied.board[0].length; j++) {
				board[i][j] = copied.board[i][j];
			}
		}
		winner = 0;
		winLength = copied.winLength;
		
	}
	
	public Board(int length, int width) {
		
		board = new int[length][width];
		winner = 0;
		
	}
	
	public boolean PlacePiece(int x, int y, int turn) {
		
		if (x >= board.length || y >= board[0].length || x < 0 || y < 0)
			return false;
		
		if (board[x][y] != 0)
			return false;
		
		board[x][y] = turn;
		
		int winner = CheckWinners();
		
		if (winner != 0) {
			
			this.winner = winner;
			return true;
			
		}
		
		return true;
		
	}
	
	public int CheckWinners() {
		
		int winner = 0;
		
		// HORIZONTAL
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j <= board[0].length - winLength; j++) {
				if (board[i][j] != 0) {
				
					int previousValue = board[i][j];
					
					for (int x = 0; x < winLength; x++) {
						
						if (board[i][j + x] == previousValue) {
							
							if (x >= winLength - 1) {
								
								winner = previousValue;
								break;
								
							}
							
							continue;
							
						}
						
						else {
							
							break;
							
						}
						
					}
					
				}
			}
		}
		
		// Vertical
		if (winner == 0) for (int j = 0; j < board[0].length; j++) {
			for (int i = 0; i <= board.length - winLength; i++) {
				if (board[i][j] != 0) {
					
					int previousValue = board[i][j];
					
					for (int x = 0; x < winLength; x++) {
						
						if (board[i + x][j] == previousValue) {
							
							if (x >= winLength - 1) {
								
								winner = previousValue;
								break;
								
							}
							
							continue;
							
						}
						
						else {
							
							break;
							
						}
						
					}
					
				}
			}
		}
		
		// Diagonal \
		if (winner == 0) for (int i = 0; i <= board.length - winLength; i++) {
			for (int j = 0; j <= board[0].length - winLength; j++) {
				if (board[i][j] != 0) {
				
					int previousValue = board[i][j];
					
					for (int x = 0; x < winLength; x++) {
						
						if (board[i + x][j + x] == previousValue) {
							
							if (x >= winLength - 1) {
								
								winner = previousValue;
								break;
								
							}
							
							continue;
							
						}
						
						else {
							
							break;
							
						}
						
					}
					
				}
			}
		}
		
		// Diagonal /
		if (winner == 0) for (int i = board.length - 1; i >= winLength - 1; i--) {
			for (int j = 0; j <= board[0].length - winLength; j++) {
				if (board[i][j] != 0) {
				
					int previousValue = board[i][j];
					
					for (int x = 0; x < winLength; x++) {
						
						if (board[i - x][j + x] == previousValue) {
							
							if (x >= winLength - 1) {
								
								winner = previousValue;
								break;
								
							}
							
							continue;
							
						}
						
						else {
							
							break;
							
						}
						
					}
					
				}
			}
		}
		
		int placed = 0;
		
		if (winner == 0) {
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board[0].length; j++) {
					if (board[i][j] != 0) {
						placed++;
					}
				}
			}
		}
		
		if (winner == 0 && placed == board.length * board[0].length) {
			return -1; // CAT'S GAME
		}
		
		return winner;
		
	}
	
}
