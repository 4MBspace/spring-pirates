package style.tree.pirates.gameserver.gameclient;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class Game 
{
	
	public static GameClient gameClient;

	public static void main(String[] args) 
	{	
		Random random = new Random();
		String name = "Anonymous" + random.nextInt(1000);
		String ip;
		int port = -1;
		Scanner scanner = new Scanner(System.in);
		System.out.println("     .:|CHAT-CLIENT|:.");
		
		if (args != null && args.length == 2)
		{
			ip = args[0];
			try
			{
				port = Integer.parseInt(args[1]);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Enter custom chat-name:");
			name = scanner.next();
			System.out.println("Please enter the server ip address you want to connect to: <int>.<int>.<int>.<int>");
			ip = scanner.next();
			System.out.println("Please enter the server port number you want to connect to: <int>");
			port = scanner.nextInt();
			
		}
		
		gameClient = null;
		try
		{
			gameClient = new GameClient(ip, port, scanner, name);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			try 
			{
				gameClient.close();
			} 
			catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		System.out.println("Press <Enter> to exit application");
		scanner.nextLine();
		scanner.close();
		System.exit(0);		
		
	}

}
