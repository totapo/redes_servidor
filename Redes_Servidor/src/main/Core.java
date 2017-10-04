package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import game.Game;
import game.Player;
import net.TCPServer;
import net.TimeoutThread;
import net.Worker;

public class Core {
	public static ExecutorService pool;
	private static final int PORTA=12345;
	public static final long TimeoutWindow=60000; //1 min
	public static final int DefaultBoardSize=8;
	
	public static void main(String[] args) {
		Core m = new Core();
		
		pool = Executors.newCachedThreadPool();
		
		ServerSocket welcome=null;
		try {
			welcome = new ServerSocket(PORTA);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(welcome!=null){
			pool.execute(new TCPServer(m,welcome));
			pool.execute(new TimeoutThread(m));
			
			Scanner s = new Scanner(System.in);
			System.out.println("Digite qualquer coisa para encerrar o servidor");
			s.nextLine();
			s.close();
			
			try {
				welcome.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			} 
			
			for(Runnable worker : pool.shutdownNow()){
				if(worker.getClass().equals(Worker.class)){
					try {
						((Worker)worker).closeSocket(); //mata os sockets ainda abertos
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private long id;
	
	public Core(){
		players = new ConcurrentHashMap<Player,ConcurrentLinkedQueue<String>>();
		lastConnection=new ConcurrentHashMap<String, Player>();
		games = new ConcurrentHashMap<Long,Game>();
		id=0;
	}
	
	public synchronized long getNewGameId(){
		id++;
		return id;
	}
	
	private ConcurrentHashMap<Player, ConcurrentLinkedQueue<String>> players; //chave = nome do jogador
										//o array eh a lista de mensagens que temos que enviar ao jogador quando recebermos um keepAlive
	
	private ConcurrentHashMap<String, Player> lastConnection;
	private ConcurrentHashMap<Long, Game> games; //chave = game id
	
	public ConcurrentHashMap<Player, ConcurrentLinkedQueue<String>> getPlayers(){
		return players;
	}
	
	public ConcurrentHashMap<Long, Game> getGames(){
		return games;
	}

	public ConcurrentHashMap<String, Player> getLastConnection() {
		return lastConnection;
	}

}
