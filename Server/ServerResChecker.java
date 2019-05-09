package Server;

public class ServerResChecker extends Thread
{
	private boolean running;
	private Server server;
	
	private long startTimer, currentTimer;
	private long interval = 86400000; // 24 hours interval
	
	
	public ServerResChecker(Server server)
	{
		this.server = server;
		running = true;
	}
	
	public void run()
	{	
		startTimer = System.currentTimeMillis(); // getting the start time in milliseconds
		
		while(running)
		{
			currentTimer = System.currentTimeMillis(); // getting the current time in milliseconds
			
			if(currentTimer-startTimer >= interval) // if the difference between the 2 is greater than the predetermined interval
			{
				server.verifyDates(); // we verify the dates
				startTimer = currentTimer; // and se the start timer to the current one
			}
		}
	}
	
	public void closeChecker()
	{
		running = false; // we stop the while loop
	}
}
