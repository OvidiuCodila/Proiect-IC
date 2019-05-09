package Client;

public class MyConnectionException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public MyConnectionException(String message)
	{
		// custom made RunTime exception
		super(message);
	}
}
