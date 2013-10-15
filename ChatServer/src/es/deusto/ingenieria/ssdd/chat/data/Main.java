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
		List<User> connectedUsers = new ArrayList<User>();
		
		try (DatagramSocket udpSocket = new DatagramSocket(serverPort)) {
			DatagramPacket request = null;
			DatagramPacket reply = null;
			byte[] buffer = new byte[1024];
			User user = new User();
			user.setNick("Server");

			while (true) {
				
				System.out.println("Running...");
				request = new DatagramPacket(buffer, buffer.length);
				
				udpSocket.receive(request);				
				System.out.println("received...");

				String message = new String(request.getData());
				String[] parameters = message.split("&");
				

				String mensaje;
				String userList= "";

				System.out.println("mensaje"+parameters[0]);
				System.out.println(parameters[0]);
				
				if(parameters[0].trim().equals("CONN")){
					
					mensaje = "OK";
					reply = new DatagramPacket(mensaje.getBytes(), mensaje.length(), request.getAddress(), request.getPort());

					User user1 = new User();
					user1.setNick(parameters[1]);
					connectedUsers.add(user1);	
					udpSocket.send(reply);
					System.out.println("sent");
				}
				else if(parameters[0].trim().equals("NCON")){
					
					mensaje = "OK";
					reply = new DatagramPacket(mensaje.getBytes(), mensaje.length(), request.getAddress(), request.getPort());

					udpSocket.send(reply);
					
				}
				else if(parameters[0].trim().equals("PLST")){
					
					for(int i=0; i<connectedUsers.size();i++){
						userList = userList.concat(" "+connectedUsers.get(i).getNick());
					}
					System.out.println("lenght"+userList.getBytes().length);
						
					mensaje = "USRL&"+userList;
					reply = new DatagramPacket(mensaje.getBytes(), mensaje.length(), request.getAddress(), request.getPort());

					udpSocket.send(reply);
					
				}
				
			}
		} catch (SocketException e) {
			System.err.println("# UDPServer Socket error: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("# UDPServer IO error: " + e.getMessage());
		}
	}

}
