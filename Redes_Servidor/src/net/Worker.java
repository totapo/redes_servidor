package net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import game.Game;
import game.Player;
import main.Core;

public class Worker implements Runnable {
	private Socket sock;
	private Core core;
	
	public Worker(Socket s, Core c){
		sock = s;
		core=c;
	}
	
	public void closeSocket() throws IOException{
		sock.close();
	}
	
	@Override
	public void run(){
		BufferedReader in=null;
		DataOutputStream out=null;
		try {
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new DataOutputStream(sock.getOutputStream());
			
			String request = in.readLine();
			System.out.println(request);
			String[] params = request.split(";");
			if(params.length>0){
				switch(Integer.parseInt(params[0])){
				
				case 1: //login-formato "1;[playerid]"
					login(params, out);
					break;
				case 2: //keepAlive-formato: "2;[playerId]"
					keepAlive(params,out);
					break;
				case 3: //challenge-formato: "3;[playerId];[targetPlayerId]"
					challenge(request,params,out);
					break;
				case 4: //challResponse-formato: "4;[matchId];[boolAccept]" bool=0 ok; bool=1 false
					challResp(params,out);
					break;
				case 5: //getBoard-formato: "5;[playerId];[matchId]"
					getGameInfo(params,out);
					break;
				case 6: //makeMove-formato: "6;[playerId];[matchId];[xCoordinate];[yCoordinate]"
					break;
				case 7: //endMatch-formato: "7:[playerId]:[matchId]"
					break;
				}
					
			} else {
				String resp = "fuck you\n";
				out.writeBytes(resp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			/*try {
				/*
				System.out.println("Fechando streams "+System.currentTimeMillis());
				
				if(!sock.isClosed()){
					sock.shutdownOutput();
					sock.close();
				}/
			} catch (IOException e) {
				System.out.println(e.getMessage());
				System.out.println("N�o consegui fechar o socket!!!!! -------------------------------------");
			}*/
		}
	}

	private void getGameInfo(String[] params, DataOutputStream out) throws IOException {
		String player = params[1];
		long gId = Long.parseLong(params[2]);
		int value=0;
		Game g = core.getGames().get(gId);
		//"5;[blackId];[whiteId];[board];[possibleMoves];[blackCount];[whiteCount];[turn]" turn=0 black; turn=1 white; turn=-1 encerrado
		if(player.equals(g.getBlack().getName())){
			value=1;
		} else
			value=-1;
		
		
		List<Integer[]>moves=g.getPossibleMoves(value);
		String possMoves=" ";
		if(moves.size()>0){
			for(Integer[] a:moves){
				possMoves+=a[0]+","+a[1]+"|";
			}
			possMoves = possMoves.substring(0, possMoves.length()-1);
		}
		
		g.getBoard();
		
		String resp="5;"+g.getBlack().getName()+";"+g.getWhite().getName()+";"+g.getBoard().toString()+";"+possMoves+";"+g.getBoard().getBlackCount()+";"+g.getBoard().getWhiteCount()+";"+((g.isBlackTurn())?"0":"1");
		out.writeBytes(resp+"\n");
	}

	private void challResp(String[] params, DataOutputStream out) throws IOException {
		int r = Integer.parseInt(params[2]);
		long id = Long.parseLong(params[1]);
		Game g = core.getGames().get(id);
		if(r==0){
			//come�a jogo
			Player a,b;
			a = g.getBlack();
			b = g.getWhite();
			if(core.getLastConnection().contains(a) && core.getLastConnection().contains(b)){
				core.getPlayers().get(g.getBlack()).add("5,0");
				core.getPlayers().get(g.getWhite()).add("5,0");	
				out.writeBytes("4;0\n");
			} else {
				endGame(a.getGameId(),"Outro jogador est� offline.");
				out.writeBytes("\n");
			}
		} else {
			g.getBlack().setInGame(false,-1);
			g.getWhite().setInGame(false,-1);
			core.getLastConnection().remove(g.getId());
			core.getPlayers().get(g.getBlack()).add("5,1");
			core.getPlayers().get(g.getWhite()).add("5,1");
			out.writeBytes("4;1\n");
		}
	}

	private void challenge(String request, String[] params, DataOutputStream out) throws IOException {
		Player p1 = core.getLastConnection().get(params[1]);
		Player p2 = core.getLastConnection().get(params[2]);
		String response;
		if(p1!=null && p2!=null) {
			long id = core.getNewGameId();
			Game g = new Game(p1,p2,Core.DefaultBoardSize,id);
			core.getGames().put(id,g);
			core.getPlayers().get(p2).add("3,"+p1.getName()+","+id); //parametros de mensagens devem ser inseridos na lista com o separador ','
			response="3;0;"+id+";"+p2.getName(); //challenge enviado com sucesso
		} else {
			response="3;1"; //challenge naoenviado ao usuario
		}
		out.writeBytes(response+"\n");
	}

	private void keepAlive(String[] params, DataOutputStream out) throws IOException {
		Player p = new Player(params[1],System.currentTimeMillis());
		String response="";
		String a;
		
		while((a=core.getPlayers().get(p).poll())!=null){
			response+=a+"|";
		}
		if(response.length()>0)
			response=response.substring(0,response.length()-1);
		else
			response+=" ";
		response+="; ";
		
		boolean entrou=false;
		for(Player player : core.getLastConnection().values()){
			if(!player.getName().equals(p.getName())){
				response+=player.getName()+"|";
				entrou=true;
			}
		}
		if(entrou)
			response=response.substring(0,response.length()-1);
		
		core.getLastConnection().get(p.getName()).setLastCon(p.getLastCon()); //reescreve o timeout
		System.out.println("2;"+response);
		out.writeBytes("2;"+response+"\n"); //resposta do keepAlive (envia todas as mensagens enfileiradas pro jogador, e a lista de jogadores online)
	}

	private void login(String[] params, DataOutputStream out) throws IOException {
		Player p = new Player(params[1],System.currentTimeMillis());
		ConcurrentLinkedQueue<String> test = core.getPlayers().put(p, new ConcurrentLinkedQueue<String>()); 
		if(test!=null){ //ja tinha gente cadastrada com esse nick
			core.getPlayers().put(p, test);
			out.writeBytes("1;1\n"); //resposta que indica que algo deu errado
		} else {
			out.writeBytes("1;0\n"); //resposta que indica sucesso
			core.getLastConnection().put(p.getName(), p);
		}
	}
	
	private void endGame(long gameId,String msg) {
		Player a, b;
		Game g = core.getGames().get(gameId);
		if(g!=null){
			a = g.getBlack();
			b = g.getWhite();
			if((a=core.getLastConnection().get(a.getName()))!=null){
				core.getPlayers().get(a).add("7,"+msg);
				a.setInGame(false, -1);
			}
			if((b=core.getLastConnection().get(b.getName()))!=null){
				core.getPlayers().get(b).add("7,"+msg);
				b.setInGame(false, -1);
			}
			
			core.getGames().remove(g);
		}
		
	}
}
