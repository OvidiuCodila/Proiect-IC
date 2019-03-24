package Server;

import java.util.Scanner;

public class CommandInput extends Thread
{
	private Server sv;
	private boolean running;
	private Scanner scn;
	
	public CommandInput(Server sv)
	{
		this.sv = sv;
		running = true;
	}
	
	public void run()
	{
		scn = new Scanner(System.in);
		try
		{
			String command;
			
			while(running)
			{
				command = scn.nextLine();
				
				switch(command)
				{
					case "/stop": { sv.printStatus("Stopping server..."); sv.stopRunning(); break; }
					case "/connected": { sv.showConnected(); break; }
					default: { sv.printStatus("Command not found! Please try again"); break; }
				}
			}
		}
		catch(Exception e)
		{
			//do nothing
		}
	}
	
	public synchronized void closeCmdInp()
	{
		running = false;
		if( scn != null ) scn.close();
	}
}
