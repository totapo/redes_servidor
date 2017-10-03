package net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import main.Core;

public class TCPServer implements Runnable {
	
	private ServerSocket welcome;
	private Core core;
	
	public TCPServer(Core core, ServerSocket serv){
		this.core = core;
		this.welcome = serv;
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
		try {
			while(true){
				if (Thread.currentThread().isInterrupted()) {
	                System.out.println("interrupted");
	                break;
	            }
				Socket s = welcome.accept(); //bloqueante...
				Core.pool.execute(new Worker(s,core));
			}
        } catch (Exception e) { 
        	
        }
		System.out.println("Servidor terminou");
	}
	
}
