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
				e.printStackTrace();
			} //verifica os timers a cada 20 segundos
		}
	}

}
