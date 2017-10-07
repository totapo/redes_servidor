package net;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import game.Game;
import game.Player;
import main.Core;

public class TimeoutThread implements Runnable {

	private Core c;
	private long timestamp;
	private List<Player> removerP;
	
	public TimeoutThread(Core core) {
		c = core;
		removerP = new ArrayList<Player>();
	}
	
	@Override
	public void run() {
		while(true){
			if (Thread.currentThread().isInterrupted()) {
                System.out.println("interrupted");
                break;
            }
			timestamp = System.currentTimeMillis();
			removerP.clear();
			
			
			for(Player a:c.getLastConnection().values()){
				if(timestamp-a.getLastCon()>=Core.TimeoutWindow){
					removerP.add(a);
				}
			}
			
			
			for(Player a:removerP){
				
				if(a.isInGame()){
					Game g = c.getGames().get(a.getGameId());
					Player other = a.equals(g.getBlack())?g.getWhite():g.getBlack();
					ConcurrentLinkedQueue<String> aux = c.getPlayers().get(other);
					if(aux!=null){
						System.out.println("Adicionei a mensagem");
						aux.add("7,O outro jogador está offline.");
						other.setInGame(false, -1);
					}
					c.getGames().remove(a.getGameId());
				}
				
				c.getLastConnection().remove(a.getName());
				c.getPlayers().remove(a);
				System.out.println("timeout "+a.getName());
			}
			
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				break; //quando sleep solta a excecao de interrupcao, o status interrompido é desfeito, por isso o break aqui  
			} //verifica os timers a cada 20 segundos
		}
		System.out.println("timeout terminou");
	}

}
