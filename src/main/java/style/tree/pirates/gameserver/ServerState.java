package style.tree.pirates.gameserver;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

public class ServerState implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7005052367781839811L;

	//Server properties (package private!):
	boolean isRunning;
	boolean isOpen;
	Integer port;
	String stateMessage;
	int players;
	String ip;
	
	//Game properties:
	private String name = "game";
	private String[][] map = {{"~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"},{"~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"},{"~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"},{"~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"},{"~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"},{"~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"},{"~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"},{"~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"},{"~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"},{"~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"},{"~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"},{"~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"},{"~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"},{"~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"},{"~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"},{"~","~","~","~","~","~","~","~","~","~","~","~","~","~","~","~"}};
	private int treeSpawn = 1;
	private int bootySpawn = 1;
	private int crateSpawn = 1;
	private int bottleSpawn = 1;
	private String mode = "MODE_DM";	
	
	public ServerState()
	{
		isRunning = false;
		isOpen = false;
		players = 0;
		stateMessage = "init state";
		ip = "10.0.0.127";
	}
	
	/*<----Getters---->*/
	
	public Integer getPort() {
		return port;
	}

	public int getPlayers()
	{
		return players;
	}

	public String getStateMessage() {
		return stateMessage;
	}
/*
	public boolean isRunning()
	{
		return isRunning;
	}

	public boolean isOpen()
	{
		return isOpen;
	}
	*/
	
	public String getIp()
	{
		return ip;
	}	

	public String getMode()
	{
		return mode;
	}	
	
	public String getName()
	{
		return name;
	}

	public String[][] getMap()
	{
		return map;
	}

	public int getTreeSpawn()
	{
		return treeSpawn;
	}

	public int getBootySpawn()
	{
		return bootySpawn;
	}

	public int getCrateSpawn()
	{
		return crateSpawn;
	}

	public int getBottleSpawn()
	{
		return bottleSpawn;
	}

	
	/*<----Setters---->*/
	
	/*
	public void setRunning(boolean isRunning)
	{
		this.isRunning = isRunning;
	}

	public void setOpen(boolean isOpen)
	{
		this.isOpen = isOpen;
	}

	public void setPort(Integer port)
	{
		this.port = port;
	}

	public void setStateMessage(String stateMessage)
	{
		this.stateMessage = stateMessage;
	}	

	public void setPlayers(int players)
	{
		this.players = players;
	}
	*/
	
	public void setMode(String mode)
	{
		this.mode = mode;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	public void setMap(String[][] map)
	{
		this.map = map;
	}

	public void setTreeSpawn(int treeSpawn)
	{
		this.treeSpawn = treeSpawn;
	}

	public void setBootySpawn(int bootySpawn)
	{
		this.bootySpawn = bootySpawn;
	}

	public void setCrateSpawn(int crateSpawn)
	{
		this.crateSpawn = crateSpawn;
	}

	public void setBottleSpawn(int bottleSpawn)
	{
		this.bottleSpawn = bottleSpawn;
	}
	
	public void updatePlayers(int players)
	{
		this.players = players;
	}
	
	public void deltaPlayers(int delta)
	{
		this.players += delta;
	}
	
	public void updateIp() throws UnknownHostException, SocketException
	{
		//Static ip for pi server:
		String result = "77.173.120.94";
		
		/*
		ArrayList<String> allAddresses = new ArrayList<String>();
		//ArrayList<String> externalAddresses = new ArrayList<String>();
		
		Integer n;
		result = InetAddress.getLocalHost().getHostAddress();
		
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		System.out.println("Ip addresses discovered on local networkinterfaces:");
		
		while(networkInterfaces.hasMoreElements())
		{
		    NetworkInterface networkInterface = (NetworkInterface) networkInterfaces.nextElement();
		    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
		    while (inetAddresses.hasMoreElements())
		    {
		        InetAddress i = (InetAddress) inetAddresses.nextElement();
		        allAddresses.add(i.toString());
		        System.out.println(i.getHostAddress());		        
		    }
		}		
		
		for(String ip : allAddresses)
		{
			try
			{
				n = Integer.parseInt(ip.split(".")[0]);
				if (!n.equals(192) && !n.equals(10) && !n.equals(127) && !n.equals(100) && !n.equals(169))
				{
					result = ip;
				}
			}
			catch (NumberFormatException e)
			{
				System.out.println(e.getMessage());
				continue;
			}
			catch (RuntimeException e)
			{
				System.out.println(e.getMessage());
				continue;
			}
		}	
		*/
		ip = result;
	}	
}
