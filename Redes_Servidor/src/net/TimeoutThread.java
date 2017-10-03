package net;

import java.util.ArrayList;
import java.util.List;

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
				c.getLastConnection().remove(a.getName());
				c.getPlayers().remove(a);
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
