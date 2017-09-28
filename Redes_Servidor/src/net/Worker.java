package net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import main.Core;

public class Worker extends Thread {
	private Socket sock;
	private Core core;
	
	public Worker(Socket s, Core c){
		sock = s;
		core=c;
	}
	
	@Override
	public void run(){
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			DataOutputStream out = new DataOutputStream(sock.getOutputStream());
			
			String request = in.readLine();
			
			System.out.println(request);
			
			String resp = "fuck you\n";
			
			out.writeBytes(resp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
