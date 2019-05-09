package Client;

public class MyCommunicationException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public MyCommunicationException(String message)
	{
		// custom made RunTime exception
		super(message);
	}
}
