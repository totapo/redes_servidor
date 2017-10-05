package game;

import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class Game {
	private Player white, black;
	private Board board;
	private boolean turn; //true pretas; false brancas
	private int pieceCount;
	
	public Game(Player a, Player b, int boardSize){
		Random r= new Random();
		if(r.nextInt()%2==0) {
			white = a;
			black = b;
		} else {
			white = b;
			black = a;
		}
		white.setInGame(true);
		black.setInGame(true);
		turn = true;
		board = new Board(boardSize);
		setupBoard();
	}
	
	public boolean isBlackTurn(){
		return turn;
	}
	
	private void setupBoard(){
		int center = (board.getSize()/2)-1;
		board.setPiece(center,center,1);
		board.setPiece(center,center+1,-1);
		board.setPiece(center+1,center,-1);
		board.setPiece(center+1,center+1,1);
		pieceCount=4;
	}
	
	public int getWinner(){
		int resp=0;
		int size = board.getSize();
		for(int l=0; l<size; l++)
			for(int c=0; c<size; c++)
				resp+=board.getPiece(l,c);
		return resp;
	}
	
	public void addPiece(int l, int c, int value){
		boolean player = value==1;
		if(player==turn){
			int size = board.getSize();
			if(l>=0 && l<size && c>=0 && c<size){
				if(board.getPiece(l, c)==0){
					board.setPiece(l,c,value);
					
					changePieces(l, c, value, 0, -1); // <-
					changePieces(l, c, value, 0, +1); // ->
					changePieces(l, c, value, -1, 0); // /\
					changePieces(l, c, value, 1, -0); // \/
					
					changePieces(l, c, value, -1, -1); // <- /\
					changePieces(l, c, value, +1, -1); // -> /\
					changePieces(l, c, value, -1, +1); // <- \/
					changePieces(l, c, value, +1, +1); // -> \/
					
					pieceCount++;
				}
			}
		}
	}
	
	public boolean isItOver(){
		if(pieceCount == board.getSize()*board.getSize())
			return true;
		
		if(!hasMoves(1) && !hasMoves(-1))
			return true;
		
		return false;
	}
	
	private boolean hasMoves(int value) {//value=1 pretas, value=-1 brancas
		int size = board.getSize();
		for(int l=0; l<size; l++){
			for(int c=0; c<size; c++){
				if(board.getPiece(l,c)==value){
					boolean checkFirstPiece = true;
					boolean r;
					r = hasMove(l, c, value, 0, -1, checkFirstPiece); // <-
					r = r || hasMove(l, c, value, 0, +1, checkFirstPiece); // ->
					r = r || hasMove(l, c, value, -1, 0, checkFirstPiece); // /\
					r = r || hasMove(l, c, value, 1, -0, checkFirstPiece); // \/
					
					r = r || hasMove(l, c, value, 0, -1, checkFirstPiece); // <- /\
					r = r || hasMove(l, c, value, 0, -1, checkFirstPiece); // -> /\
					r = r || hasMove(l, c, value, 0, -1, checkFirstPiece); // <- \/
					r = r || hasMove(l, c, value, 0, -1, checkFirstPiece); // -> \/
					
					if(r)
						return r;
				}
			}
		}
		
		return false;
	}

	private boolean hasMove(int l, int c, int value, int lJump, int cJump, boolean checkFirstPiece) {
		l+=lJump;
		c+=cJump;
		if(!(l>=0 && l<board.getSize() && c>=0 && c<board.getSize())) //out of bounds
			return false;
		int piece = board.getPiece(l,c);
		
		if(piece==0 && checkFirstPiece)
			return false;
		else if(piece==0){
			return true;
		} else if(piece==value)
			return false;
		else {
			return hasMove(l,c,value,lJump,cJump,false);
		}
	}

	public boolean nextTurn(){
		boolean nxtPlayer = !turn;
		
		int value = (nxtPlayer)?1:-1;
		
		if(hasMoves(value)){
			turn = nxtPlayer;
			return nxtPlayer;	
		} else{
			return !nxtPlayer;
		}
		
	}

	private boolean changePieces(int l, int c, int value, int linJump, int colJump) {
		l+=linJump;
		c+=colJump;
		if(!(l>=0 && l<board.getSize() && c>=0 && c<board.getSize()))
			return false;
		int piece = board.getPiece(l,c);
		
		if(piece==0)
			return false;
		else if(piece==value)
			return true;
		else if(changePieces(l,c,value,linJump,colJump)){
			board.setPiece(l,c,value);
			return true;
		} else
			return false;
			
	}
	
	public Set<int[]> getPossibleMoves(int value){
		Set<int[]> response = new TreeSet<int[]>();
		if((value==1 && this.turn) || (value==-1 && !this.turn)){
			int size = board.getSize();
			for(int l=0; l<size; l++){
				for(int c=0; c<size; c++){
					if(board.getPiece(l,c)==value){
						boolean checkFirstPiece = true;
						findMove(l, c, value, 0, -1, response,checkFirstPiece); // <-
						findMove(l, c, value, 0, +1, response,checkFirstPiece); // ->
						findMove(l, c, value, -1, 0, response,checkFirstPiece); // /\
						findMove(l, c, value, 1, -0, response,checkFirstPiece); // \/
						
						findMove(l, c, value, 0, -1, response,checkFirstPiece); // <- /\
						findMove(l, c, value, 0, -1, response,checkFirstPiece); // -> /\
						findMove(l, c, value, 0, -1, response,checkFirstPiece); // <- \/
						findMove(l, c, value, 0, -1, response,checkFirstPiece); // -> \/
					}
				}
			}
		}
		return response;
	}

	private void findMove(int l, int c, int value, int linJump, int colJump, Set<int[]> response, boolean checkFirstPiece) {
		l+=linJump;
		c+=colJump;
		if(!(l>=0 && l<board.getSize() && c>=0 && c<board.getSize())) //out of bounds
			return;
		int piece = board.getPiece(l,c);
		
		if(piece==0 && checkFirstPiece)
			return;
		else if(piece==0){
			response.add(new int[]{l,c});
			return;
		} else if(piece==value)
			return;
		else {
			findMove(l,c,value,linJump,colJump,response,false);
		}
			
	}

	public Player getWhite() {
		return white;
	}

	public Player getBlack() {
		return black;
	}
	
	public Board getBoard(){
		return board;
	}
	
}
