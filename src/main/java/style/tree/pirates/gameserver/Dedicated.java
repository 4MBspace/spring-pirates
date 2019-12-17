package style.tree.pirates.gameserver;

//import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Dedicated 
{	
	private static GameServer server;
	private static Scanner scanner;
	
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		ArrayList<String> cmdLine = new ArrayList<String>();
		scanner = new Scanner(System.in);	
		//int port = -1;
		System.out.println("     .:|CHAT-SERVER|:.");
		//In case the command-line argument holds a port value
		/*
		if(args != null && args.length == 1)
		{
			try
			{
				port = Integer.parseInt(args[0]);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Enter the server port number you want to use...");
			port = scanner.nextInt();
		}
		*/
				
		startServer();
				
		//Thread for command line interface interaction with server			
		Thread cmdLineThread = new Thread(new Runnable()
		{
			public void run()
			{
				System.out.println("Command-line interface started, for a list of commands type: help ");
				while(server.getOpen())
				{
					cmdLine.clear();
					for (String word : scanner.nextLine().toLowerCase().split(" "))
					{
						cmdLine.add(word);
					}			
					//TODO make command combo's safer
					if(cmdLine.size() == 1)
					{
						doCommands(cmdLine.get(0));
					}			
					else
					{
						doCommands(cmdLine);
					}
				}
			}
		});
		cmdLineThread.setDaemon(true);
		cmdLineThread.setName("Command-Line-Scanner-Thread");
		cmdLineThread.start();
		
		System.out.println("Press <Enter> to exit application");
		scanner.nextLine();
		scanner.close();			
		System.exit(0);		
			
		
	}
	
	//Initialize a new GameServer Object
	private static void startServer()
	{					
		server = new GameServer();
		server.startServer();
	}
	
	//Switch input commands and interact with server
	private static void doCommands(String command)
	{
		switch (command)
		{
			case "help":					
			
				System.out.println("Dedicated Server HELP, list of commands:");
				System.out.println("kick all | kicks all connected clients");
				System.out.println("kick <ipaddress> <port> | kicks client connected on <ipaddress>:<port>");
				System.out.println("playerlist | show player list");
				System.out.println("stats | show server stats");
				System.out.println("start | start a new server");
				System.out.println("stop | stop server");
				System.out.println("exit | exit application");
				break;
			case "playerlist":					
				server.printPlayers();
				break;
			case "stats":					
				server.printStats();
				break;
			case "exit":
				if(server.getOpen())
					server.stopServer();
				scanner.close();
				System.exit(0);
				break;
			case "stop":
				if(server.getOpen())
					server.stopServer();
				else System.out.println("Server was not active...");
				break;
		}		
	}
	
	//Overloaded method for multiple command arguments
	private static void doCommands(ArrayList<String> cmdLine)
	{
		boolean var;
		//TODO make safer
		switch (cmdLine.get(0))
		{
		//TODO make it work properly
			case "kick":
				if (cmdLine.get(1) == "all")
				{
					var = server.kick();
					if(!var)
						System.out.println("unable to kick all...");
				}
				else if(cmdLine.size() >= 3)
				{
					var = server.kick(new ClientData(cmdLine.get(1), Integer.parseInt(cmdLine.get(2))));
					if(!var)
						System.out.println("unable to kick " + cmdLine.get(1) + ":" + cmdLine.get(2) + ", please use valid ip and port arguments...");						
				}
				else 
				{ 
					System.out.println("please use proper formatting: kick all || kick <ip-address> <port number>..."); 
				}
				
			break;
			
			case "start":	//Try to close the open server and initialize a new server, TODO: make concurrent servers available					
				try
				{
					if(server.getOpen())
					{
						server.stopServer(); 
					}
					startServer();
				}
				catch (Exception e) {System.out.println("please provide a valid port number as argument...");}
			break;
		}
	}
	
}

