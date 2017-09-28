package main;

import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import game.Game;
import game.Player;
import net.TCPServer;

public class Core {

	public static void main(String[] args) {
		Core m = new Core();
		TCPServer server= new TCPServer(12345,m);
		server.start();
		Scanner s = new Scanner(System.in);
		System.out.println("Digite qualquer coisa para encerrar o servidor");
		s.nextLine();
		server.interrupt();
	}
	
	private long id;
	
	public Core(){
		players = new ConcurrentHashMap<Player,ConcurrentLinkedQueue<String>>();
		games = new ConcurrentHashMap<Long,Game>();
		id=0;
	}
	
	public synchronized long getNewGameId(){
		id++;
		return id;
	}
	
	private ConcurrentHashMap<Player, ConcurrentLinkedQueue<String>> players; //chave = nome do jogador
										//o array eh a lista de mensagens que temos que enviar ao jogador quando recebermos um keepAlive
	private ConcurrentHashMap<Long, Game> games; //chave = game id
	
	public ConcurrentHashMap<Player, ConcurrentLinkedQueue<String>> getPlayers(){
		return players;
	}
	
	public ConcurrentHashMap<Long, Game> getGames(){
		return games;
	}

}
