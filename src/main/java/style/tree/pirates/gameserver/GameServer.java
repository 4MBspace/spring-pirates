package style.tree.pirates.gameserver;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.commons.lang3.time.StopWatch;

public class GameServer
{
	// Specify the port range on which to run:
	private static int firstPort = 9001;
	private static int finalPort = 9099;
	private static ArrayList<Integer> usedPorts = new ArrayList<Integer>();
	private static Stack<Integer> availablePorts = new Stack<Integer>();
	static
	{
		for (int i = finalPort; i >= firstPort; i--)
		{
			availablePorts.push(new Integer(i));
		}
	}
	// Store all references to object instances of this class in this static
	// list:
	private static ArrayList<GameServer> gameServers = new ArrayList<GameServer>();

	private ServerSocket serverSocket;
	private ServerState serverState;
	private Queue<Message> msgQueue;
	private Queue<Integer> killList;
	
	public boolean getOpen()
	{
		return serverState.isOpen;
	}

	public GameServer()
	{
		msgQueue = new LinkedBlockingQueue<Message>();
		killList = new LinkedBlockingQueue<Integer>();
		serverState = new ServerState();
		gameServers.add(this);
	}

	public ServerState getServerState()
	{
		return serverState;
	}

	public static ArrayList<GameServer> getGameServers()
	{
		return gameServers;
	}
	
	public void setMap(String[][] map)
	{
		serverState.setMap(map);
	}

	public void setName(String name)
	{
		serverState.setName(name);
	}
	
	public void setSpawn(int treeSpawn, int bootySpawn, int crateSpawn, int bottleSpawn)
	{
		serverState.setTreeSpawn(treeSpawn);
		serverState.setBootySpawn(bootySpawn);
		serverState.setCrateSpawn(crateSpawn);
		serverState.setBottleSpawn(bottleSpawn);
	}
	
	
	private static Integer findPort() throws NoAvailablePortsException
	{
		if (availablePorts.isEmpty())
		{
			throw new NoAvailablePortsException("No designated ports available for use, too many gameservers running");
		} else
		{
			// TODO: concurrency handling
			Integer port = availablePorts.pop();
			usedPorts.add(port);
			return port;
		}
	}

	private static void freePort(Integer port)
	{
		// TODO: concurrency handling
		usedPorts.remove(port);
		availablePorts.push(port);
	}

	public ServerState startServer()
	{
		Integer port = null;

		while (!serverState.isOpen)
		{
			try
			{
				port = findPort();
			} catch (NoAvailablePortsException e)
			{
				serverState.stateMessage = e.getMessage();
				System.out.println("Cannot start server, no ports available now in designated range");
				break;
			}
			// If a port is not available, keep trying to start the server on
			// another port

			try
			{
				serverSocket = new ServerSocket(port.intValue());
				serverState.port = port; //package private!
				serverState.updateIp();
				serverState.stateMessage = "serverSocket succesfully created at port:" + port.intValue() + "!"; //package private!
				System.out.println(serverState.stateMessage);

				serverSocket.setSoTimeout(30000);
				ServerThread serverThread = new ServerThread(serverSocket, msgQueue, killList, serverState);
				serverThread.setDaemon(true);
				serverThread.setName("Server:" + port.intValue());
				serverThread.start();
				serverState.isOpen = true;
				broadcast();
			} catch (BindException e)
			{
				System.out.println(e.getMessage());
				serverState.isOpen = false;
				serverState.stateMessage = "port: " + port + ", not available, trying new port";
				System.out.println(serverState.stateMessage);
			} catch (SocketException e)
			{
				System.out.println(e.getMessage());
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return serverState;
	}
	
	private void broadcast() {
		
		Thread broadcastThread = new Thread(new Runnable()
		{				
			public void run()
			{
				int pingInterval = 10000;
				StopWatch stopWatch = new StopWatch();
				stopWatch.start();				
				
				while(getOpen())
				{
					updatePlayers();
					//Broadcast messages in queue
					if(!msgQueue.isEmpty()) synch();
					//Signal players to remove all local enemies belong to idle players
					if(!killList.isEmpty()) synchKill();
					//Send routine ping to remove idle players
					if(stopWatch.getTime() > pingInterval)
					{
						if(stopWatch.isStarted())
							stopWatch.stop();
						
						stopWatch.reset();
						stopWatch.start();
						
						synchPing();
					}
				}
			}
		});
		broadcastThread.setDaemon(true);
		broadcastThread.setName("Broadcast");
		broadcastThread.start();	
	}
	
	// Send all messages in message-queue to all connected sockets except sender
	private void synch()
	{		
		for (Message msg : msgQueue)
		{
			for (ClientThread t : ClientThread.getClientThreads())
			{
				t.send(msg);
			}
		}
		msgQueue.clear();
	}
	
	private void synchKill()
	{			
		for (Integer id : killList)
		{
			System.out.println("Sending kill message for idle player: " + id.toString());
			for (ClientThread t : ClientThread.getClientThreads())
			{
				t.sendKill(id);
			}
		}
		killList.clear();
	}
	
	//Routine ping to check if players are still alive
	private void synchPing()
	{
		for (ClientThread t : ClientThread.getClientThreads())
		{
			t.sendPing();
		}
	}
	
	public void updatePlayers()
	{
		serverState.updatePlayers(ClientThread.getClientThreads().size());
	}

	public void closeServer()
	{
		close();
		if (serverSocket != null)
		{
			try
			{
				serverSocket.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		freePort(serverState.port);
		gameServers.remove(this);
	}

	// TODO: make closing threads and sockets safer for connected clients
	private void close()
	{
		// Close server sockets and threads
		for (ClientThread c : ClientThread.getClientThreads())
		{
			try
			{
				c.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		ClientThread.getClientThreads().clear();
		ClientThread.getClientList().clear();
	}	

	// TODO collect more server stats
	public void printStats()
	{
		System.out.println("SERVER STATS");
		System.out.println("Players connected: " + ClientThread.getClientList().size());
	}

	public void printPlayers()
	{
		System.out.println("PLAYERS CONNECTED: " + ClientThread.getClientList().size());
		for (Socket s : ClientThread.getClientList())
		{
			System.out.println(s.getInetAddress() + ":" + s.getPort());
		}
		System.out.println("");
	}

	// overloaded method to kick a single client
	public boolean kick(ClientData client)
	{
		Socket s;
		for (int i = ClientThread.getClientList().size() - 1; i >= 0; i--)
		{
			s = ClientThread.getClientList().get(i);
			if (s.getInetAddress() == client.ip && s.getPort() == client.port)
			{
				try
				{
					s.shutdownOutput();
					s.close();
				} catch (Exception e)
				{
					e.printStackTrace();
				}

				ClientThread.getClientList().remove(s);
				msgQueue.add(new Message("[server]", client.ip + " was kicked from server..."));
				return true;
			}
		}
		return false;
	}

	// overloaded method for kick all
	public boolean kick()
	{
		for (Socket s : ClientThread.getClientList())
		{
			try
			{
				s.shutdownOutput();
				s.close();
			} catch (Exception e)
			{
				e.printStackTrace();
			}

		}
		ClientThread.getClientList().clear();
		System.out.println("Everyone was kicked from server");
		return true;
	}

	// TODO: make safer
	public void stopServer()
	{
		kick();
		serverState.isOpen = false;
		close();
		closeServer();
	}
}
