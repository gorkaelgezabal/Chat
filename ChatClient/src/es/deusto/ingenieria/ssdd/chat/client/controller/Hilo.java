package es.deusto.ingenieria.ssdd.chat.client.controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import es.deusto.ingenieria.ssdd.chat.*;

public class Hilo implements Runnable{

	private DatagramSocket udpSocket;
	private ChatClientController controller;

	public Hilo(DatagramSocket udpSocket, ChatClientController controller) {
		super();
		this.udpSocket = udpSocket;
	}
	
	@Override
	public void run() {
		
		//ENTER YOUR CODE TO RECEIVE A MESSAGE
		String message = "";
		String reply;
				
			while(true){
				reply = controller.receiveMessage(this.udpSocket);
				
				String[] parametros = reply.split("&");
				
				if(parametros[0].trim().equals("TALK")){
					
				}
				else if(parametros[0].trim().equals("NTLK")){
					
				}
				else if(parametros[0].trim().equals("MESG")){
					
				}
				else if(parametros[0].trim().equals("CLSE")){
					
				}
				
			}
		
	}

	
}
