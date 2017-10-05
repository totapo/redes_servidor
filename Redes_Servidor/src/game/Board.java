package game;

public class Board {
	
	private int size;
	private int[][] board; //-1 = brancas; 0 = vazio; 1=pretas
	private int bCount,wCount;
	

	public Board(int boardSize) {
		size = boardSize;
		board = new int[size][size];
		bCount=0;
		wCount=0;
	}
	
	public int getWhiteCount(){
		return wCount;
	}
	
	public int getBlackCount(){
		return bCount;
	}
	
	public void setPiece(int l, int c, int value){
		if(l>=0 && l<size && c>=0 && c<size){
			int aux=board[l][c];
			board[l][c]=value;
			if(value==1){
				bCount++;
			} else if(value==-1){
				wCount++;
			}
			
			if(aux==1){
				bCount--;
			} else if(aux==-1){
				wCount--;
			}
		}
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
	
	@Override
	public String toString(){
		String resp="";
		for(int i=0;i<size; i++){
			for(int p=0; p<size; p++)
				resp+=board[i][p]+((p==size-1)?"":",");
			resp+=(i==size-1)?"":"|";
		}
		return resp;
	}
}
