package style.tree.pirates.gameserver;

public class SocketDisconnectException extends RuntimeException {

	/**
	 * 
	 */
	private String message;
	
	@Override
	public String getMessage()
	{
		return message;
	}
	
	private static final long serialVersionUID = 1784564096075291451L;

	public SocketDisconnectException()
	{
		this("Socket was disconnected unexpectedly");
	}
	public SocketDisconnectException(String msg)
	{
		this.message = msg;
	}
		
	public String toString()
	{		
		return super.toString() + ": " + message;
	}

	
}
