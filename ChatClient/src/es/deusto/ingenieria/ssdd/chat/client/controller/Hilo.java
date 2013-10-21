package es.deusto.ingenieria.ssdd.chat.client.controller;

import java.net.DatagramSocket;
import java.util.Date;
import es.deusto.ingenieria.ssdd.chat.data.Message;
import es.deusto.ingenieria.ssdd.chat.data.User;

public class Hilo implements Runnable{

	private DatagramSocket udpSocket;
	private ChatClientController controller;	

	public Hilo(DatagramSocket udpSocket, ChatClientController controller) {
		super();
		this.udpSocket = udpSocket;
		this.controller = controller;
	}
	
	@Override
	public void run() {
		
		String message = "";
		String reply;
		User user = new User();
		User user1 = new User();
		Message mensaje = new Message();
		Date date = new Date();
		
				
			while(true){
				
				reply = controller.receiveMessage(this.udpSocket);
				
				String[] parameters = reply.split("&");
				
				//Chat request
				if(parameters[0].trim().equals("TALK")){
					
					user.setNick(parameters[1].trim());
					user1.setNick(parameters[2].trim());
					user.setIp(udpSocket.getInetAddress());
					user.setPort(udpSocket.getPort());
					
					mensaje.setFrom(user);
					mensaje.setTo(user1);
					mensaje.setTimestamp(date.getTime());
					mensaje.setText("TALK");
					
					System.out.println("TALK CLIENTE");
					controller.getObservable().notifyObservers(mensaje);
				}
				else if(parameters[0].trim().equals("NTLK")){
					
				}
				else if(parameters[0].trim().equals("MESG")){
					
				}
				else if(parameters[0].trim().equals("CLSE")){
					
				}
				
			}
		
	}

	
}
