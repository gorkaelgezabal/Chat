package es.deusto.ingenieria.ssdd.chat.data;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;


public class Main {
	
	private static final int PORT = 6789;
	

	public static void main(String args[]) {
		//args[0] = Server socket port
		int serverPort = args.length == 0 ? Main.PORT : Integer.parseInt(args[0]);
		User user2 = new User();
		user2.setNick("proba");
		List<User> connectedUsers = new ArrayList<User>();
		connectedUsers.add(user2);
		
		try (DatagramSocket udpSocket = new DatagramSocket(serverPort)) {
			DatagramPacket request = null;
			DatagramPacket reply = null;
			byte[] buffer = new byte[1024];
			User user = new User();
			user.setNick("Server");

			while (true) {
				
				System.out.println("Running...");
				String mensaje;
				String userList= "";
				
				request = new DatagramPacket(buffer, buffer.length);
				udpSocket.receive(request);		
				
				System.out.println("received...");

				//Se procesa el mensaje
				String message = new String(request.getData());
				String[] parameters = message.split("&");
				
				System.out.println("mensaje"+parameters[0]);
				System.out.println(parameters[0]);
				
				//Se ejecuta dependiendo del comando
				if(parameters[0].trim().equals("CONN")){
					
					mensaje = "OK";
					reply = new DatagramPacket(mensaje.getBytes(), mensaje.length(), request.getAddress(), request.getPort());

					//Se añade a la lista de conectados
					User user1 = new User();
					user1.setNick(parameters[1].trim());
					user1.setIp(request.getAddress());
					user1.setPort(request.getPort());
					connectedUsers.add(user1);	
					
					udpSocket.send(reply);
					System.out.println("sent");
				}
				else if(parameters[0].trim().equals("NCON")){
					boolean berdin;
					//Se elimina el usuario de la lista de conectados
					for (int i=0;i<connectedUsers.size();i++){
						if(berdin = parameters[1].trim().equals(connectedUsers.get(i).getNick())){
							connectedUsers.remove(i);
						}
					}
					
					mensaje = "OK";
					reply = new DatagramPacket(mensaje.getBytes(), mensaje.length(), request.getAddress(), request.getPort());
					udpSocket.send(reply);
					
				}
				else if(parameters[0].trim().equals("PLST")){
					
					//Se consigue la lista de conectados en un String
					for(int i=0; i<connectedUsers.size();i++){
						userList = userList.concat(" "+connectedUsers.get(i).getNick());
					}
						
					mensaje = "USRL&"+userList;
					System.out.println();
					reply = new DatagramPacket(mensaje.getBytes(), mensaje.length(), request.getAddress(), request.getPort());

					udpSocket.send(reply);
					
				}
				//Solo si esta conectado al chat recive, mensajes y mensajes para cerrar chat (hay que hacer)
				// Si recive una solicitud de chat, una denegacion de chat o un mensaje tiene que encontrar su destinatario y enviar el mensaje original
				else if (parameters[0].trim().equals("TALK") ||parameters[0].trim().equals("NTLK") || parameters[0].trim().equals("CLSE") || parameters[0].trim().equals("MESG") ){
					
					for(int i=0;i<connectedUsers.size();i++){
						
						if(parameters[2].equals(connectedUsers.get(i).getNick())){
							
							reply = new DatagramPacket(message.getBytes(), message.length(), connectedUsers.get(i).getIp(), connectedUsers.get(i).getPort());
						}
					}
				}
				
			}
		} catch (SocketException e) {
			System.err.println("# UDPServer Socket error: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("# UDPServer IO error: " + e.getMessage());
		}
	}

}
