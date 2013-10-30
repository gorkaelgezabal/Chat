package es.deusto.ingenieria.ssdd.chat.client.controller;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Date;


public class Hilo implements Runnable{

	private DatagramSocket multicastSocket;
	private ChatClientController controller;	

	public Hilo(DatagramSocket multicastSocket, ChatClientController controller) {
		super();
		this.multicastSocket = multicastSocket;
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
		String cadena;
		String mensajeSend;


		while(true){

			reply = controller.receiveMessage(this.multicastSocket);

			String[] parameters = reply.split("&");

			user.setNick(parameters[1].trim());
			user1.setNick(parameters[2].trim());


			mensaje.setFrom(user);
			mensaje.setTo(user1);
			mensaje.setTimestamp(date.getTime());
			mensaje.setText("Unkown error");
			
			if(parameters[2].trim().equals("ALL") || parameters[2].trim().equals(this.controller.getConnectedUser())){
				
				if(parameters[0].trim().equals("CONN")){
					String nick = parameters[1].trim();
					String to = parameters[2].trim();
					ArrayList<User> arrConUs = 	this.controller.getArrConectedUsers();
					boolean found = false;
					for(int i = 0; i< arrConUs.size();i++){
						if(arrConUs.get(i).getNick().equals(nick)){
							found = true;
						}
					}
					if(!found){
						User us = new User();
						us.setNick(nick);
						arrConUs.add(us);		
						for(int z = 0;z<arrConUs.size();z++){
						 cadena = "&";
							cadena = cadena + arrConUs.get(z).getNick() + " ";
							
						}
						
						String mensajesend = "PLST&"+ us.getNick()+to+ cadena;
					}
					//FALTA ERROR FOUND=TRUE

					mensaje.setText("CONN");
				}

				//Peticion de chat
				if(parameters[0].trim().equals("TALK")){

					mensaje.setText("TALK");
				}
				//Mensaje
				else if(parameters[0].trim().equals("MESG")){
					mensaje.setText(parameters[3].trim());
				}
				//Cierre de chat
				else if(parameters[0].trim().equals("CLSE")){
					mensaje.setText("CLSE");
				}
				//Aceptar chat
				else if(parameters[0].trim().equals("SCHT")){

					mensaje.setText("SCHT");
				}
				//Denegar chat
				else if(parameters[0].trim().equals("NCHT")){

					mensaje.setText("NCHT");
				}
				//Error
				else if(parameters[0].trim().equals("ER")){

					this.controller.errorCheck(parameters[1].trim(), mensaje);
				}

				controller.getObservable().notifyObservers(mensaje);

			}
			
		}
	}
}
