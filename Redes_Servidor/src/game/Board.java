package game;

public class Board {
	
	private int size;
	private int[][] board;
	

	public Board(int boardSize) {
		size = boardSize;
		board = new int[size][size];
	}
	
	public void setPiece(int l, int c, int value){
		if(l>=0 && l<size && c>=0 && c<size)
			board[l][c]=value;
	}
	
	public int getPiece(int l, int c){
		if(l>=0 && l<size && c>=0 && c<size)
			return board[l][c];
		return 0;
	}

	public int getSize(){
		return size;
	}
	
	public int[][] getConfiguration(){
		return board;
	}
}
