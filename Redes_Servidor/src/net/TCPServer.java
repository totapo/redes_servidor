package net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor;

import main.Core;

public class TCPServer extends Thread {
	
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
			Socket s = welcome.accept(); //bloqueante... o esuqema de interromper com o ThreadInterrupted só funciona depois disso rodar
			(new Worker(s,core)).start();
			if( Thread.interrupted() ) {
				break;
			}
		}
	}
	
}
