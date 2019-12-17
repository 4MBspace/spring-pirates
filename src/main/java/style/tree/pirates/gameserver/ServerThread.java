package style.tree.pirates.gameserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Queue;

public class ServerThread extends Thread {

	private ServerSocket serverSocket;
	private Queue<Message> msgQueue;
	private Queue<Integer> killList;
	ServerState serverState;
	
	//Server listening thread for accepting new client connections
	public ServerThread(ServerSocket serverSocket, Queue<Message> msgQueue, Queue<Integer> killList, ServerState serverState)
	{
		//Message msg;
		this.serverSocket = serverSocket;
		this.msgQueue = msgQueue;
		this.serverState = serverState;
		this.killList = killList;
	}
	
	public void run()
	{
		//server new client listening loop
		System.out.println("Starting server...");
		while(serverState.isOpen)
		{
			try
			{
				System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
				Socket socket = serverSocket.accept();						
				
				System.out.println("Just connected to " + socket.getInetAddress());						
				
				//Create a separate thread for each connecting client to handle their input and output streams
				ClientThread cT = new ClientThread(msgQueue, killList, socket, serverState);									
				
				cT.setDaemon(true);
				cT.setName("ClientThread " + socket.getInetAddress().toString());
				cT.start();	
				
				//msg = new Message("[server]", socket.getInetAddress().toString() + "");
				//msgQueue.add(msg);
			}
			catch (SocketTimeoutException s)
			{				
				System.out.println("Socket timed out!");
			}					
			catch (IOException e)
			{				
				e.printStackTrace();
				break;
			}
		}
	}
	
}
