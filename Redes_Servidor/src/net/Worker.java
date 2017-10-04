package net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
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
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			DataOutputStream out = new DataOutputStream(sock.getOutputStream());
			
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
				case 4: //challResponse-formato: "4;[playerId];[challengerId];[matchId];[boolAccept]"
					challResp(params,out);
					break;
				case 5: //getBoard-formato: "5;[playerId];[matchId]"
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
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				sock.close();
			} catch (IOException e) {
				System.out.println("Não consegui fechar o socket!!!!! -------------------------------------");
			}
		}
	}

	private void challResp(String[] params, DataOutputStream out) {
		
	}

	private void challenge(String request, String[] params, DataOutputStream out) throws IOException {
		Player p1 = new Player(params[1],0);
		Player p2 = new Player(params[2],0);
		Game g = new Game(p1,p2,Core.DefaultBoardSize);
		long id = core.getNewGameId();
		core.getGames().put(id,g);
		String response;
		if(core.getLastConnection().contains(p2)) {
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
		response+=";";
		for(Player player : core.getLastConnection().values()){
			if(!player.getName().equals(p.getName()))
				response+=player.getName()+"|";
		}
		
		core.getLastConnection().get(p.getName()).setLastCon(p.getLastCon()); //reescreve o timeout
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
}
