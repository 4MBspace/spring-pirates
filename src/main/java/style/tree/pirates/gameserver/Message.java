package style.tree.pirates.gameserver;

import java.io.Serializable;
import java.net.InetAddress;

//The message objects get passed between the sockets via objectstreams and thus need to serializable
public class Message implements Serializable
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3003381794899574268L;
	public InetAddress ip;
	public int port;
	public String message;
	public String name;
	
	public Message(String name, String message)
	{
		this.name = name;
		this.message = message;
		this.ip = null;
		this.port = -1;
	}
	
	public Message(String message)
	{
		this.name = "JSON";
		this.message = message;
		this.ip = null;
		this.port = -1;
	}	
}


