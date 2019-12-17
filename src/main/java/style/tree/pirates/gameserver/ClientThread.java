package style.tree.pirates.gameserver;

//import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Queue;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.util.StringUtils;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.time.StopWatch;

public class ClientThread extends Thread
{	
	private static ArrayList<ClientThread> clientThreads = new ArrayList<ClientThread>();
	private static ArrayList<Socket> clientList = new ArrayList<Socket>();
	
	private DataOutputStream out;
	private DataInputStream in;
	//private BufferedReader br;
	private Queue<Message> msgQueue;
	private Socket socket;
	private ObjectWriter objectWriter;
	//TODO: store clientdata in this thread
	//private ClientData client;
	private ServerState serverState;
	private OutputStream socketOut;
	private InputStream socketIn;
	private boolean open;
	private StopWatch stopWatch;
	//timeout limit for stopwatch, In milliseconds:
	private int timeOutLimit = 5000;
	private StringBuilder stringBuilder;
	private Queue<Integer> killList;
	
	public static ArrayList<ClientThread> getClientThreads()
	{
		return clientThreads;
	}
	
	public static ArrayList<Socket> getClientList()
	{
		return clientList;
	}
		
	public ClientThread(Queue<Message> msgQueue, Queue<Integer> killList, Socket socket, ServerState serverState)
	{
		this.msgQueue = msgQueue;
		this.socket = socket;
		this.serverState = serverState;
		this.killList = killList;
		clientThreads.add(this);
		clientList.add(socket);
		open = true;
		stopWatch = new StopWatch();
		objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
		serverState.deltaPlayers(1);
	}
	
	@Override
	public void run()
	{
		try
		{
		//add thread socket to list
		clientList.add(socket);
		socketOut = socket.getOutputStream();
		socketIn = socket.getInputStream();
		out = new DataOutputStream(socketOut);
		in = new DataInputStream(socketIn);
		//br = new BufferedReader(new InputStreamReader(in));
		
		doHandShake();
		//ObjectMapper objectMapper = new ObjectMapper();
		//int inputN;
		//byte[] messageByte;
		//StringBuilder toBecomeAJson;
		//String msgIn;
		
		//client = new ClientData(socket.getInetAddress(), socket.getPort());
		//Message msg; 
		
		//byte[] decoded;
		//byte[] encoded = new byte[] {198, 131, 130, 182, 194, 135};
		//int[] key = new int[] {167, 225, 225, 210};

		
			//client connection loop
			while(serverState.isOpen && open)
			{	
				//Stop and remove the clientthread by exiting this loop, if there is a ping-pong timeout with the client 
				if(stopWatch.isStarted() && stopWatch.getTime() > timeOutLimit)
				{
					stopWatch.stop();
					System.out.println("Ping-pong timeout: closing thread and socket " + this.toString() + "...");
					//Debugging, TODO: uncomment the next 2 lines!
					open = false;
					break;
				}
				decode();
				
				//inputN = 1000;
				//toBecomeAJson = new StringBuilder();
				//messageByte = new byte[1000];
				//while (inputN == 1000 && toBecomeAJson.length() < Integer.MAX_VALUE && !isValidJSON(toBecomeAJson.toString())) {					
				//	inputN = in.read(messageByte);
				//	toBecomeAJson.append(new String(messageByte, 0, inputN));
				//}				
				
				//while((msgIn = br.readLine()) != null)
				//{
					//if(isValidJSON(input))
					//	objectMapper.json
					
					
					//System.out.println("InputStream Line: " + msgIn);
					
					//decoded = new byte[msgIn.getBytes().length];
					
					//for (int i = 0; i < msgIn.getBytes().length; i++) {
					//    decoded[i] = (byte)(msgIn.getBytes()[i] ^ key[i & 0x3]);
					//}	
					// msgIn = new String(decoded);					
				//}
			}
				//throw new SocketDisconnectException();	
		}
		catch (UnsupportedEncodingException | NoSuchAlgorithmException | SocketDisconnectException e){e.printStackTrace();}
		catch (IOException | RuntimeException e){ e.printStackTrace();}
		catch (Exception e){ e.printStackTrace();}		
		
		try 
		{
			this.close();
			System.out.println("Clientthread for " + socket.getPort() + " was closed!" );
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}
	
	private void decode() throws IOException
	{
		Message msg;
		String msgIn, byteString;
		int len = 0;            
        byte[] b = new byte[1000];
        //rawIn is a Socket.getInputStream();
        len = socketIn.read(b);
       if(len > 0)
       {

                byte rLength = 0;
                int rMaskIndex = 2;
                int rDataStart = 0;
                //b[0] is always text in my case so no need to check;
                byte data = b[1];
                byte op = (byte) 127;
                rLength = (byte) (data & op);

                if(rLength==(byte)126) rMaskIndex=4;
                if(rLength==(byte)127) rMaskIndex=10;

                byte[] masks = new byte[4];

                int j=0;
                int i=0;
                for(i=rMaskIndex;i<(rMaskIndex+4);i++)
                {
                    masks[j] = b[i];
                    j++;
                }

                rDataStart = rMaskIndex + 4;

                int messLen = len - rDataStart;

                byte[] message = new byte[messLen];

                for(i=rDataStart, j=0; i<len; i++, j++)
                {
                    message[j] = (byte) (b[i] ^ masks[j % 4]);
                }
                
                //Close notification handler
            	if(message.length > 0)
            	{
            		stringBuilder = new StringBuilder();
            		
            		for(byte m : message)
            		{
            			if(stringBuilder.length() < Integer.MAX_VALUE)
            				stringBuilder.append(m);
            		}
            		byteString = stringBuilder.toString();
            		//System.out.println("Byte message received: " + byteString);
            		
            		//Close notification handler
            		if(byteString.equals("3-23"))
            		{
            			//System.out.println("Close notification received, attempting to close clientThread " + this.toString());
            			open = false;
            		}
            		            		
            	}
                
                msgIn = new String(message);
                //parseMessage(new String(b));
                if(msgIn.length() > 0)
                {
                	//System.out.println("Message in pre json formatter: " + msgIn);
				
                	
                	if(msgIn.equals("pong"))
                	{
                		if(stopWatch.isStarted())
                			stopWatch.stop();
                		
                		//System.out.println("Ping-pong timeout cancelled: ClientThread " + this.toString() + " kept alive!");
                	}
                	else if(isValidJSON(msgIn))
					{
						//System.out.println("Valid JSON in: " + msgIn);
						msg = new Message(msgIn);
						msg.ip = socket.getInetAddress();
						msg.port = socket.getPort();
						msgQueue.add(msg);
					}
                }

                b = new byte[1000]; 
        }
	}
	
	private void encode(String mess) throws IOException
	{		
		    byte[] rawData = mess.getBytes();

		    int frameCount  = 0;
		    byte[] frame = new byte[10];

		    frame[0] = (byte) 129;

		    if(rawData.length <= 125){
		        frame[1] = (byte) rawData.length;
		        frameCount = 2;
		    }else if(rawData.length >= 126 && rawData.length <= 65535){
		        frame[1] = (byte) 126;
		        int len = rawData.length;
		        frame[2] = (byte)((len >> 8 ) & (byte)255);
		        frame[3] = (byte)(len & (byte)255); 
		        frameCount = 4;
		    }else{
		        frame[1] = (byte) 127;
		        int len = rawData.length;
		        frame[2] = (byte)((len >> 56 ) & (byte)255);
		        frame[3] = (byte)((len >> 48 ) & (byte)255);
		        frame[4] = (byte)((len >> 40 ) & (byte)255);
		        frame[5] = (byte)((len >> 32 ) & (byte)255);
		        frame[6] = (byte)((len >> 24 ) & (byte)255);
		        frame[7] = (byte)((len >> 16 ) & (byte)255);
		        frame[8] = (byte)((len >> 8 ) & (byte)255);
		        frame[9] = (byte)(len & (byte)255);
		        frameCount = 10;
		    }

		    int bLength = frameCount + rawData.length;

		    byte[] reply = new byte[bLength];

		    int bLim = 0;
		    for(int i=0; i<frameCount;i++){
		        reply[bLim] = frame[i];
		        bLim++;
		    }
		    for(int i=0; i<rawData.length;i++){
		        reply[bLim] = rawData[i];
		        bLim++;
		    }

		    try
		    {
			    socketOut.write(reply);
			    socketOut.flush();
		    }
		    catch (SocketException se)
		    {
		    	se.printStackTrace();
		    	System.out.println("SocketException: closing socket and clientthread...");
		    	open = false;
		    	
		    	try 
				{
					this.close();
					System.out.println("Clientthread for " + socket.getPort() + " was closed!" );
				} 
				catch (IOException ioe) 
				{
					// TODO Auto-generated catch block
					ioe.printStackTrace();
				}	
		    }
	}
	
	private void doHandShake() throws UnsupportedEncodingException, NoSuchAlgorithmException, IOException
	{
		//translate bytes of request to string
			Scanner scanner = new Scanner(in,"UTF-8");
			String data = scanner.useDelimiter("\\r\\n\\r\\n").next();
	
			Matcher get = Pattern.compile("^GET").matcher(data);
	
			if (get.find()) {
			    Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
			    match.find();
			    byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
			            + "Connection: Upgrade\r\n"
			            + "Upgrade: websocket\r\n"
			            + "Sec-WebSocket-Accept: "
			            + DatatypeConverter
			            .printBase64Binary(
			                    MessageDigest.getInstance("SHA-1").digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))
			            + "\r\n\r\n")
			            .getBytes("UTF-8");
	
			    out.write(response, 0, response.length);	
			    
			    encode("[{\"socketId\" : " + this.getId() + "}]");
			}
			//scanner.close();
	}
	
	//Close input and output streams and socket
	void close() throws IOException
	{				
		if(in != null)
			in.close();
		if(out != null)
			out.close();		
		try {socket.close();}
		catch (IOException e){ e.printStackTrace();}
		serverState.deltaPlayers(-1);
		clientList.remove(socket);
		clientThreads.remove(this);
		killList.add(new Integer((int) this.getId()));
	}
	
	//Broadcast message to connected clients
	void send(Message message)
	{
		//Remove this conditional for the broadcast targets to include the sender (echo)
		if(socket.getInetAddress() != message.ip && socket.getPort() != message.port)
		{			
			try 
			{
				//System.out.println("JSON object out: " + objectWriter.writeValueAsString(message.message));
				//out.writeBytes(message.message);
				
				//Encode message into socket frame and send to socket out
				encode(message.message);				
				
				//System.out.println(message.name + "@" + message.ip + ": " + message.message);
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//else // sender echo behaviour
		//{
		//	
		//}
	}	
	
	//Start check if socket is still alive
	void sendPing()
	{
		try
		{
			encode("ping");
			if(stopWatch.isStarted())
				stopWatch.stop();
			
			stopWatch.reset();
			stopWatch.start();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	//Send message to remaining players to kill off a player id
	void sendKill(Integer id)
	{
		try
		{
			encode("[{\"killId\" : " + id.toString() + "}]");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
		
	private boolean isValidJSON(final String json) {
		if (StringUtils.isEmpty(json) ) 
			return false;
		try
		{
			final JsonParser parser = new ObjectMapper().getFactory().createParser(json);
					//getJsonFactory().createJsonParser(json);
			while (parser.nextToken() != null) { }
			return true;
		} catch (JsonParseException jpe) {
			//jpe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return false;
	}
	
	
}


