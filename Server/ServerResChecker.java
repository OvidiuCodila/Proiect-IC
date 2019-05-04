package Server;

public class ServerResChecker extends Thread
{
	private boolean running;
	private Server server;
	
	private long startTimer, currentTimer;
	private long interval = 86400000; // 24 hours
	
	
	public ServerResChecker(Server server)
	{
		this.server = server;
		running = true;
	}
	
	public void run()
	{	
		startTimer = System.currentTimeMillis();
		
		while(running)
		{
			currentTimer = System.currentTimeMillis();
			
			if(currentTimer-startTimer >= interval)
			{
				server.verifyDates();
				startTimer = currentTimer;
			}
		}
	}
	
	public void closeChecker()
	{
		running = false;
	}
}
