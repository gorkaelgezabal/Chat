package es.deusto.ingenieria.ssdd.chat.client.controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observer;

import es.deusto.ingenieria.ssdd.util.observer.local.LocalObservable;
import es.deusto.ingenieria.ssdd.chat.data.Message;
import es.deusto.ingenieria.ssdd.chat.data.User;

public class ChatClientController  {
	private String serverIP;
	private int serverPort;
	private User connectedUser;
	private User chatReceiver;
	private LocalObservable observable;
	private DatagramSocket udpSocket;

	public void setChatReceiver(User chatReceiver) {
		this.chatReceiver = chatReceiver;
	}

	public LocalObservable getObservable() {
		return observable;
	}

	public void setObservable(LocalObservable observable) {
		this.observable = observable;
	}


	public ChatClientController() {
		this.observable = new LocalObservable();
		this.serverIP = null;
		this.serverPort = -1;
	}

	public String getConnectedUser() {
		if (this.connectedUser != null) {
			return this.connectedUser.getNick();
		} else {
			return null;
		}
	}

	public void setConnectedUser(User user){
		this.connectedUser=user;
	}

	public String getConnectedUserName(){
		return this.connectedUser.getNick();
	}

	public String getChatReceiverName() {
		return this.chatReceiver.getNick();
	}

	public String getChatReceiver() {
		if (this.chatReceiver != null) {
			return this.chatReceiver.getNick();
		} else {
			return null;
		}
	}

	public String getServerIP() {
		return this.serverIP;
	}

	public int gerServerPort() {
		return this.serverPort;
	}

	public boolean isConnected() {
		return this.connectedUser != null;
	}

	public boolean isChatSessionOpened() {
		return this.chatReceiver != null;
	}

	public void addLocalObserver(Observer observer) {
		this.observable.addObserver(observer);
	}

	public void deleteLocalObserver(Observer observer) {
		this.observable.deleteObserver(observer);
	}

	public boolean connect(String ip, int port, String nick) {

		this.connectedUser = new User();
		this.connectedUser.setNick(nick);
		this.serverIP = ip;
		this.serverPort = port;

		//Usuario que ha enviado la solicitud de conexion
		User user = new User();
		User user1 = new User();
		user.setNick("Server");
		user1.setNick(nick);

		//Preparacion mensaje
		Message mensaje = new Message();
		Date date = new Date();
		mensaje.setFrom(user);
		mensaje.setTo(user1);
		mensaje.setTimestamp(date.getTime());

		String message="CONN&" + nick;
		String reply;
		sendMessage(message);

		reply = receiveMessage(this.udpSocket);

		String[] parametros = reply.split("&");
		//Respuesta positiva
		if(parametros[0].trim().equals("OK")){

			this.connectedUser = user1;

			Hilo hilo = new Hilo(this.udpSocket,this);
			Thread t= new Thread(hilo);
			t.start();

			mensaje.setText("Connected succesfully.");

			this.observable.notifyObservers(mensaje);
		}
		//Respuesta negativa
		else if(parametros[0].equals("ER")){

			errorCheck(parametros[1].trim(), mensaje);
		}

		return true;
	}



	public boolean disconnect(String nick) {

		String message="NCON&" + nick;
		String reply;

		//Usuario que ha enviado la solicitud de conexion
		User user = new User();
		User user1 = new User();
		user1.setNick("Server");
		user.setNick(nick);

		//Preparacion mensaje
		Message mensaje = new Message();
		Date date = new Date();
		mensaje.setFrom(user);
		mensaje.setTo(user1);
		mensaje.setTimestamp(date.getTime());

		sendMessage(message);
		reply = receiveMessage(this.udpSocket);

		String[] parametros = reply.split("&");
		if(parametros[0].equals("OK")){

			this.connectedUser=null;
			this.udpSocket.close();
			mensaje.setText("Connection closed.");
			this.observable.notifyObservers(mensaje);
		}
		else if(parametros[0].equals("ER")){

			errorCheck(parametros[1].trim(),mensaje);
		}

		this.connectedUser = null;
		this.chatReceiver = null;

		return true;
	}



	public List<String> getConnectedUsers() {
		List<String> connectedUsers = new ArrayList<>();

		String message="PLST&" + this.connectedUser.getNick();
		String reply;

		//Usuario que ha enviado la solicitud de conexion
		User user = new User();
		User user1 = new User();
		user.setNick("Server");
		user1.setNick(this.connectedUser.getNick());

		//Preparacion mensaje
		Message mensaje = new Message();
		Date date = new Date();
		mensaje.setFrom(user);
		mensaje.setTo(user1);
		mensaje.setTimestamp(date.getTime());

		sendMessage(message);
		reply = receiveMessage(this.udpSocket);

		String[] parametros = reply.split("&");



		if(parametros[0].equals("USRL")){

			System.out.println("res_usu"+parametros[1]);
			String[] userArray = parametros[1].split(" ");
			
			//Al recivir toda la lista de usuarios conectados, se filtra para que no aparezca nuestro usuario en la lista.
			for(int i=0; i<userArray.length;i++){
				if(!userArray[i].trim().equals(this.connectedUser.getNick())){
					connectedUsers.add(userArray[i].trim());
				}
			}
		}
		else if(parametros[0].equals("ER")){
			connectedUsers.add("Default");
			errorCheck(parametros[1].trim(),mensaje);					
		}
		return connectedUsers;

	}

	public boolean sendMessage(String message) {


		try  {

			this.udpSocket = new DatagramSocket();
			InetAddress serverHost = InetAddress.getByName(serverIP);			
			byte[] byteMsg = message.getBytes();
			DatagramPacket request = new DatagramPacket(byteMsg, byteMsg.length, serverHost, serverPort);
			udpSocket.send(request);

			System.out.println("Mensaje enviado");

		} catch (SocketException e) {
			System.err.println("# UDPClient Socket error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("# UDPClient IO error: " + e.getMessage());
		}

		return true;
	}

	public String receiveMessage(DatagramSocket udpSocket) {

		String message = "";
		try {

			byte[] buffer = new byte[1024];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			udpSocket.receive(reply);
			message = new String(reply.getData());


		} catch (SocketException e) {
			System.err.println("# UDPClient Socket error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("# UDPClient IO error: " + e.getMessage());
		}	


		return message;
	}	

	public boolean sendChatRequest(String to, String nick) {

		String message="TALK&" + nick+"&"+to;

		//Usuario que ha enviado la solicitud de conexion
		User user = new User();
		User user1 = new User();
		user.setNick("Server");
		user1.setNick(nick);

		//Preparacion mensaje
		Message mensaje = new Message();
		Date date = new Date();
		mensaje.setFrom(user);
		mensaje.setTo(user1);
		mensaje.setTimestamp(date.getTime());
		mensaje.setText("Chat session opened");
		sendMessage(message);

		return true;
	}	


	public boolean acceptChatRequest(String nick, String to) {

		String message="SCHT&" + this.connectedUser.getNick()+"&"+this.chatReceiver.getNick();


		//Preparacion mensaje
		Message mensaje = new Message();
		Date date = new Date();
		mensaje.setFrom(this.connectedUser);
		mensaje.setTo(this.chatReceiver);
		mensaje.setTimestamp(date.getTime());

		sendMessage(message);

		return true;
	}

	public boolean refuseChatRequest(String from, String to) {

		this.chatReceiver = new User();
		this.chatReceiver.setNick(to);

		String message="NCHT&" + from+"&"+to;

		//Usuario que ha enviado la solicitud de conexion
		User user = new User();
		User user1 = new User();
		user.setNick(from);
		user1.setNick(to);

		//Preparacion mensaje
		Message mensaje = new Message();
		Date date = new Date();
		mensaje.setFrom(user);
		mensaje.setTo(user1);
		mensaje.setTimestamp(date.getTime());

		sendMessage(message);		

		return true;
	}	

	public boolean sendChatClosure() {


		String from = this.connectedUser.getNick();
		String to = this.chatReceiver.getNick();

		String message="CLSE&" + from+"&"+to;

		//Usuario que ha enviado la solicitud de conexion
		User user = new User();
		User user1 = new User();
		user.setNick(from);
		user1.setNick(to);

		//Preparacion mensaje
		Message mensaje = new Message();
		Date date = new Date();
		mensaje.setFrom(user);
		mensaje.setTo(user1);
		mensaje.setTimestamp(date.getTime());

		this.chatReceiver = null;
		sendMessage(message);		

		return true;
	}


	public void errorCheck(String comand, Message mensaje){

		switch (comand){

		case "1": 
			mensaje.setText("Connection error.");
			this.observable.notifyObservers(mensaje);
			break;
		case "2": 
			mensaje.setText("Error disconnecting.");
			this.observable.notifyObservers(mensaje);
			break;

		case "3": 
			mensaje.setText("Chat request error.");
			this.observable.notifyObservers(mensaje);
			break;

		case "4": 

			mensaje.setText("Error disconnecting from chat.");
			this.observable.notifyObservers(mensaje);
			break;

		case "5": 
			mensaje.setText("Error denying chat request.");
			this.observable.notifyObservers(mensaje);
			break;

		case "6": 
			mensaje.setText("User already in use.");
			this.observable.notifyObservers(mensaje);
			break;

		case "7": 
			mensaje.setText("Error sending message.");
			this.observable.notifyObservers(mensaje);
			break;	

		default : 

			mensaje.setText("Unknown error.");
			this.observable.notifyObservers(mensaje);
			break;

		}
	}
}