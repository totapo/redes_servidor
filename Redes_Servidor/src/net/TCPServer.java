package net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import main.Core;

public class TCPServer implements Runnable {
	
	private int porta;
	private ServerSocket welcome;
	private Core core;
	
	public TCPServer(int porta, Core core){
		this.porta=porta;
		this.core = core;
		try {
			welcome = new ServerSocket(this.porta);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run(){
		try {
			startServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void startServer() throws IOException{
		while(true){
			System.out.println("derp");
			Socket s = welcome.accept(); //bloqueante...
			Core.pool.execute(new Worker(s,core));
		}
	}
	
}
