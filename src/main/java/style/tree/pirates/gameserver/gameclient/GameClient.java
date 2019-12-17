package style.tree.pirates.gameserver.gameclient;
//import style.tree.pirates.gameserver.Message;

import java.net.*;

import java.io.*;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

//import org.codehaus.jackson.JsonParseException;
//import org.codehaus.jackson.JsonParser;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.springframework.util.StringUtils;

public class GameClient 
{

	private Socket socket;
	private boolean open;
	private Thread clientThread;
	private DataOutputStream out;
	private DataInputStream in;
	private BufferedReader br;
	private String name;
	private ObjectWriter objectWriter; 
	
	public GameClient(String ip, int port, Scanner scanner, String name) throws IOException
	{
		this.name = name;
		this.open = true;
		objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
		start(ip, port);
		while(this.open)
		{
			if(socket.isConnected())
			{
				try {
					//System.out.println("sending...");
					System.out.println();
					out.writeBytes(objectWriter.writeValueAsString(scanner.nextLine()));
					//System.out.println("sent!");
				}
				catch (IOException e) {e.printStackTrace(); break;}
			}
			else
			{
				System.out.println("Socket disconnected, closing socket");
				this.open = false;				
			}
		}
		try	{close();}
		catch(IOException e) {e.printStackTrace(); System.out.println("Socket disconnected, exiting");}
	}	
	
	public void close() throws IOException
	{
		try
		{
			out.write((name + " is leaving the conversation").getBytes());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		if(in != null)
			in.close();
		if(out != null)
			out.close();		
		socket.close();
		open = false;
	}
	
	public void start(String ip, int port)
	{			
		clientThread = null;	
		System.out.println("Trying to connect to " + ip + ":" + port + "...");
		try
		{
			//Connect to server
			socket = new Socket(ip, port);
			System.out.println("Connected to the server at " + ip + ":" + port + "!");
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			br = new BufferedReader(new InputStreamReader(in));
			String testMsg = "{\"ip\": \"" + ip + "\",\"port\":" + port + ",\"name\":\"" + name + "\",\"message\":[\"hello\"]}";
			out.writeBytes(testMsg);
			out.writeBytes((name + " joins the conversation"));
			
			clientThread = new Thread(new Runnable()
			{
				String msgIn;
				//int inputN;
				//StringBuilder toBecomeAJson;
				//byte[] messageByte = new byte[1000];
				
				public void run()
				{
					while(open)
					{
						try 
						{
							//inputN = 1000;
							//toBecomeAJson = new StringBuilder();
							//messageByte = new byte[1000];
							//while (inputN == 1000 && toBecomeAJson.length() < Integer.MAX_VALUE && !isValidJSON(toBecomeAJson.toString())) {
							//	inputN = in.read(messageByte);
							//	toBecomeAJson.append(new String(messageByte, 0, inputN));
							//}
							//msgIn = toBecomeAJson.toString();
							while((msgIn = br.readLine()) != null)
							{
								System.out.println(msgIn);
								System.out.println();
							}
						} 
						catch (IOException e) 
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//System.out.println();
						
					}
					try 
					{
						close();
					} catch (IOException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			clientThread.setDaemon(true);
			clientThread.setName("Client");
			clientThread.start();	
			System.out.println("You can start chatting!");
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}		
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/*
	private boolean isValidJSON(final String json) {
		if (StringUtils.isEmpty(json) ) 
			return false;
		try
		{
			final JsonParser parser = new ObjectMapper().getJsonFactory().createJsonParser(json);
			while (parser.nextToken() != null) { }
			return true;
		} catch (JsonParseException jpe) {
			jpe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return false;
	}
	*/
}