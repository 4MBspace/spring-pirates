package style.tree.pirates.gameserver;

import java.net.InetAddress;

import java.net.UnknownHostException;

//Data struct/object for storing client address data
public class ClientData 
{
	//TODO: Add name property
	public InetAddress ip;
	public int port;
	public ClientData(InetAddress ip, int port)
	{
		this.ip = ip;
		this.port = port;
	}
	
	//Overloaded constructor makes sure to parse the type String ip to type InetAddress
	public ClientData(String ip, int port) 
	{
		this.ip = null;
		try	{ this.ip = parseIP(ip);	}
		catch (IllegalArgumentException e){	e.printStackTrace(); }
		
		this.port = port;
	}
	
	//Try to parse the type string ip address to a type InetAddress
	public InetAddress parseIP(String ip) throws IllegalArgumentException
	{	
		if(ip.toLowerCase() == "localhost")
		{
			try 
			{
				return InetAddress.getLocalHost();
			} 
			catch (UnknownHostException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
			
		try 
		{
			return InetAddress.getByAddress(splitIP(ip));
		} 
		catch (UnknownHostException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new IllegalArgumentException("Type String argument could not be parsed to type InetAddress");		
	}
	
	private byte[] splitIP(String ip)
	{		
		String[] ipArray = ip.split(".");
		byte[] result = new byte[ipArray.length];
		
		for(int i = 0; i < ipArray.length; i++)
		{
			try
			{
				result[i] = Byte.parseByte(ipArray[i]);
			}
			catch (NumberFormatException e){ e.printStackTrace(); }
		}		
		return result;
	}
		
	@Override public String toString(){ return ip.toString()+":"+port; }
}
