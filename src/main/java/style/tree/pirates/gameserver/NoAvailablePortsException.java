package style.tree.pirates.gameserver;

public class NoAvailablePortsException extends RuntimeException {

	private String message;
	
	@Override
	public String getMessage()
	{
		return this.message;
	}
	
	public NoAvailablePortsException(String message) {
		this.message = message;
	}
	
	public NoAvailablePortsException() {
		this("No ports were available for use");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4023271750854305058L;
	
}
