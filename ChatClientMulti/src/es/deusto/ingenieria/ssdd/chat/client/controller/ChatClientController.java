package es.deusto.ingenieria.ssdd.chat.client.controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import es.deusto.ingenieria.ssdd.util.observer.local.LocalObservable;


public class ChatClientController {
	private String IP;
	private int Port;
	private String connectedUser;
	private String chatReceiver;
	private LocalObservable observable;
	private MulticastSocket multiSocket;
	private ArrayList<String> connectedUsers = new ArrayList<String>();
	
	public ChatClientController() {
		this.observable = new LocalObservable();
		this.IP = null;
		this.Port = -1;
	}
	
	
	
	public String getIP() {
		return IP;
	}



	public void setIP(String iP) {
		IP = iP;
	}



	public int getPort() {
		return Port;
	}



	public void setPort(int port) {
		Port = port;
	}



	public void setConnectedUsers(ArrayList<String> connectedUsers) {
		this.connectedUsers = connectedUsers;
	}
	
	public String getConnectedUser() {
		return connectedUser;
	}

	public void setConnectedUser(String connectedUser) {
		this.connectedUser = connectedUser;
	}

	public String getChatReceiver() {
		return chatReceiver;
	}

	public void setChatReceiver(String chatReceiver) {
		this.chatReceiver = chatReceiver;
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
		
		//ENTER YOUR CODE TO CONNECT
		
		
		try {
			
			System.out.println("connecting");
			this.connectedUser = nick;
			this.IP = ip;
			this.Port = port;
			
			MulticastSocket socket = new MulticastSocket(port);
			socket.setLoopbackMode(true);
			InetAddress group = InetAddress.getByName(ip);
			socket.joinGroup(group);
			this.multiSocket = socket;
			
			String message = "CONN&"+ nick + "&ALL";
			sendMessage(message);
//			DatagramPacket messageOut = new DatagramPacket(message.getBytes(), message.length(), group, port);
//			socket.send(messageOut);
			Hilo hilo = new Hilo(socket,this);
			Thread t= new Thread(hilo);
			t.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
		
	}
	
	public boolean disconnect() {
		
		//ENTER YOUR CODE TO DISCONNECT
		
		this.connectedUser = null;
		this.chatReceiver = null;
		
		return true;
	}
	
	public List<String> getConnectedUsers() {

		
		return this.connectedUsers;
	}
	
	public boolean sendMessage(String message) {


		try  {
		
			byte[] byteMsg = message.getBytes();
			InetAddress group = InetAddress.getByName(this.IP);
			DatagramPacket request = new DatagramPacket(byteMsg, byteMsg.length, group, this.Port);
			multiSocket.send(request);

			System.out.println("Mensaje enviado");

		} catch (SocketException e) {
			System.err.println("# UDPClient Socket error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("# UDPClient IO error: " + e.getMessage());
		}

		return true;
	}
	
	public String receiveMessage(MulticastSocket multiSocket) {

		String message = "";
		try {

			byte[] buffer = new byte[1024];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			multiSocket.receive(reply);
			message = new String(reply.getData());


		} catch (SocketException e) {
			System.err.println("# UDPClient Socket error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("# UDPClient IO error: " + e.getMessage());
		}	


		return message;
	}	
	
	public boolean sendChatRequest(String to) {
		
		//ENTER YOUR CODE TO SEND A CHAT REQUEST
		

		this.chatReceiver= to;
		
		return true;
	}	
	
	public void receiveChatRequest() {
		
		//ENTER YOUR CODE TO RECEIVE A CHAT REQUEST
		
		String message = "Chat request details";
		
		//Notify the chat request details to the GUI
		this.observable.notifyObservers(message);
	}
	
	public boolean acceptChatRequest() {
		
		//ENTER YOUR CODE TO ACCEPT A CHAT REQUEST
		
		return true;
	}
	
	public boolean refuseChatRequest() {
		
		//ENTER YOUR CODE TO REFUSE A CHAT REQUEST
		
		return true;
	}	
	
	public boolean sendChatClosure() {
		
		//ENTER YOUR CODE TO SEND A CHAT CLOSURE
		
		this.chatReceiver = null;
		
		return true;
	}
	
	public void receiveChatClosure() {
		
		//ENTER YOUR CODE TO RECEIVE A CHAT REQUEST
		
		String message = "Chat request details";
		
		//Notify the chat request details to the GUI
		this.observable.notifyObservers(message);
	}
}