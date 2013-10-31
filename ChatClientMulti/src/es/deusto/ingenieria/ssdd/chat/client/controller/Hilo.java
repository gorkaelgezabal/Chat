package es.deusto.ingenieria.ssdd.chat.client.controller;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Hilo implements Runnable{

	private MulticastSocket multicastSocket;
	private ChatClientController controller;	

	public Hilo(MulticastSocket multicastSocket, ChatClientController controller) {
		super();
		this.multicastSocket = multicastSocket;
		this.controller = controller;
	}

	@Override
	public void run() {


		String reply;
		User user = new User();
		User user1 = new User();
		Message mensaje = new Message();
		Date date = new Date();
		String cadena="";
		String mensajeSend;

		System.out.println("running");
		while(true){

			reply = controller.receiveMessage(this.multicastSocket);

			System.out.println("receieved");
			String[] parameters = reply.split("&");

			String from = parameters[1].trim();
			String to = parameters[2].trim();


			mensaje.setFrom(from);
			mensaje.setTo(to);
			mensaje.setTimestamp(date.getTime());
			mensaje.setText("Unkown error");

			if(parameters[2].trim().equals("ALL") || parameters[2].trim().equals(this.controller.getConnectedUser())){
				System.out.println("message for me");
				if(parameters[0].trim().equals("CONN")){
					System.out.println("new user");
					List<String> connectedUsers = 	this.controller.getConnectedUsers();
					boolean found = false;
					for(int i = 0; i< connectedUsers.size();i++){
						if(connectedUsers.get(i).equals(from)){
							found = true;
						}
					}
					if(!found){					
						connectedUsers.add(from);
						for(int z = 0;z<connectedUsers.size();z++){
							cadena = "&";
							cadena = cadena + connectedUsers.get(z) + " ";

						}

						String usuariosCon = "PLST&"+to +from+ cadena;


						this.controller.sendMessage(usuariosCon);
					}
					//FALTA ERROR FOUND=TRUE

				}
				//Peticion de chat
				if(parameters[0].trim().equals("PLST")){

					String[] arrconnectedUsers = parameters[3].split(" ");
					ArrayList<String> connectedUsers = new ArrayList<String>();
					for(int i=0;i<arrconnectedUsers.length;i++){
						connectedUsers.add(arrconnectedUsers[i].trim());
					}
					this.controller.setConnectedUsers(connectedUsers);
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

					//					this.controller.errorCheck(parameters[1].trim(), mensaje);
				}

				//				controller.getObservable().notifyObservers(mensaje);

			}

		}




	}
}
