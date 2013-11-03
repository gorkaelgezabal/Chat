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
		Message mensaje = new Message();
		Date date = new Date();

		int conCount = 0;
		boolean process = true;

		System.out.println("running");
		while(this.controller.getConnectedUser() != null){

			reply = controller.receiveMessage(this.multicastSocket);

			System.out.println("receieved");
			String[] parameters = reply.split("&");

			String from = parameters[1].trim();
			String to = parameters[2].trim();


			mensaje.setFrom(from);
			mensaje.setTo(to);
			mensaje.setTimestamp(date.getTime());
			mensaje.setText("Unkown error");


			//Control de usuario conectado
			if(from.equals(this.controller.getConnectedUser()) && parameters[0].trim().equals("CONN") ){
				conCount++;
				if(conCount>1){
					String userExst = "EXST&"+this.controller.getConnectedUser()+"1"+"&"+from;
					this.controller.sendMessage(userExst);
					process = false;
				}

			}

			if(!from.equals(this.controller.getConnectedUser())){//Si no es mi mensaje

				if(to.equals("ALL") || to.equals(this.controller.getConnectedUser())){//Si es para todos o para mi
					System.out.println("message for me");
					if(parameters[0].trim().equals("CONN")){

						System.out.println("new user");
						ArrayList<String> connectedUsers = 	this.controller.getConnectedUsers();

						boolean found = false;
						for(int i = 0; i< connectedUsers.size();i++){
							if(connectedUsers.get(i).equals(from)){
								found = true;
							}
						}

						if(!found){//Si el usuario que no esta conectado no esta en la lista ,añado el nuevo usuario a la lista y					

							//Si soy el ultimo de la lista, mando la lista de usuarios
							int lastPosition = 0;

							if(connectedUsers.size() != 0){
								lastPosition = connectedUsers.size()-1;
							}

							connectedUsers.add(from);
							String cadena="";
							
							for(int z = 0;z<connectedUsers.size();z++){
								cadena = cadena + connectedUsers.get(z) + " ";
							}


							if(connectedUsers.get(lastPosition).equals(this.controller.getConnectedUser())){

								String usuariosCon = "PLST&"+this.controller.getConnectedUser() +"&"+from+"&"+ cadena;
								this.controller.sendMessage(usuariosCon);
								mensaje.setText("PLST");
								controller.getObservable().notifyObservers(mensaje);

							}

							this.controller.setConnectedUsers(connectedUsers);
							mensaje.setText("PLST");
							controller.getObservable().notifyObservers(mensaje);
						}

					}
					if(parameters[0].trim().equals("PLST")){

						String[] arrconnectedUsers = parameters[3].split(" ");
						ArrayList<String> connectedUsers = new ArrayList<String>();

						for(int i=0;i<arrconnectedUsers.length-1;i++){
							connectedUsers.add(arrconnectedUsers[i].trim());
						}

						this.controller.setConnectedUsers(connectedUsers);

						mensaje.setText("PLST");
						controller.getObservable().notifyObservers(mensaje);
					}
					//Peticion de chat
					if(parameters[0].trim().equals("TALK")){

						mensaje.setText("TALK");
						controller.getObservable().notifyObservers(mensaje);
					}
					//Mensaje
					else if(parameters[0].trim().equals("MESG")){
						mensaje.setText(parameters[3].trim());
						controller.getObservable().notifyObservers(mensaje);
					}
					//Desconexion
					else if(parameters[0].trim().equals("DESC")){
						mensaje.setText("DESC");
						controller.getObservable().notifyObservers(mensaje);
					}
					//Cierre de chat
					else if(parameters[0].trim().equals("CLSE")){
						mensaje.setText("CLSE");
						controller.getObservable().notifyObservers(mensaje);
					}
					//Aceptar chat
					else if(parameters[0].trim().equals("SCHT")){
						mensaje.setText("SCHT");
						controller.getObservable().notifyObservers(mensaje);
					}
					//Denegar chat
					else if(parameters[0].trim().equals("NCHT")){

						mensaje.setText("NCHT");
						controller.getObservable().notifyObservers(mensaje);
					}
					//Usuario ya utulizado
					else if(parameters[0].trim().equals("EXST") && process){
						mensaje.setText("EXST");
						controller.getObservable().notifyObservers(mensaje);
					}

				}
			} 
			else if(parameters[0].trim().equals("DESC")){

				System.out.println("desc recivido");
				this.controller.setConnectedUser(null);
				this.controller.setChatReceiver(null);
				this.controller.setConnectedUsers(new ArrayList<String>());
			}


		}

		//No estamos conectados y cerramos el socket
		System.out.println("closed");
		this.controller.closeSocket();



	}
}
