package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

public class ServerMain extends Observable {
	
	private ConcurrentHashMap<String, Boolean> availableClients = new ConcurrentHashMap<String, Boolean>();;
	
	public static void main(String[] args) {
		try {
			new ServerMain().setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(4242);
		while (true) {
			Socket clientSocket = serverSock.accept();
			ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
			Thread t = new Thread(new ClientHandler(clientSocket));
			t.start();
			this.addObserver(writer);
			System.out.println("got a connection");
		}
	}
	
	class ClientHandler implements Runnable {
		private BufferedReader reader;

		public ClientHandler(Socket clientSocket) {
			Socket sock = clientSocket;
			try {
				reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			String message, filtered;
			try {
				while ((message = reader.readLine()) != null) {
					System.out.println("server read " + message);
					
					ServerProtocol sp = new ServerProtocol(message, availableClients);
					
					if (message.charAt(0) == '?'){
						availableClients.put(sp.getSenderClientName(), true);
					}
					
					filtered = sp.formatOutgoing();
					if (filtered != null){
						setChanged(); //here edit for chatboxes and stuff
						notifyObservers(filtered);
					}
					
					if (message.charAt(0) == '*'){
						availableClients.remove(sp.getSenderClientName());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
