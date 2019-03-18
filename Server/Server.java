package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server 
{
	//Server connection variables
	private int port = 1700;
	private ServerSocket serverSocket;
	private ArrayList<ServerWorker> clients;
	
	//Server memory variables
	
	
	//Server running variables
	private boolean running = true;
	
	
	////////////////////////////////////////////////////////////////////////////////
	// Server running functions
	///////////////////////////////////////////////////////////////////////////////
	
	public void runServer()
	{
		openServerSocket();
		clients = new ArrayList<ServerWorker>();
		
		while(running)
		{
			Socket clientSocket = null;
			
			try
			{
				clientSocket = serverSocket.accept();
				
				ServerWorker sw = new ServerWorker(clientSocket, this);
				clients.add(sw);
				sw.start();
			}
			catch(Exception e)
			{
				this.printStatus("Error acceptiong new client connection");
				e.printStackTrace();
				this.stopRunning();
			}
		}
	}
	
	public synchronized void removeClient(ServerWorker sw)
	{
		int i;
		for(i=0; i<clients.size(); i++)
			if(clients.get(i).equals(sw)) clients.remove(i);
	}
	
	////////////////////////////////////////////////////////////////////////////////
	// Server initializing functions
	///////////////////////////////////////////////////////////////////////////////
	
	private void openServerSocket()
	{
		try
		{
			serverSocket = new ServerSocket(port);
			this.printStatus("Server opened on port " + port + "\n");
		}
		catch(IOException e)
		{
			throw new RuntimeException("Can't open server on port" + port + "\n",e);
		}
	}
	
	public void initMemory()
	{
		//no memory yet
		readMemory();
	}
	
	private void readMemory()
	{
		//no memory yet
	}
	
	
	////////////////////////////////////////////////////////////////////////////////
	// Server closing functions
	///////////////////////////////////////////////////////////////////////////////
	
	public synchronized void stopRunning()
	{
		running = false;
		closeServer();
	}
	
	private void closeServer()
	{
		int i;
		
		this.printStatus("Removing all clients from the server...");
		for(i=0; i<clients.size(); i++)
		{
			if(clients.get(i) != null)
			{
				clients.get(i).closeSocket();
				clients.remove(i);
			}
		}
		this.printStatus("All clients removed succesfully");
		
		this.printStatus("Starting saving the memory...");
		saveMemory();
		this.printStatus("Memory saved succesfully");
		
		try
		{ serverSocket.close(); }
		catch(Exception e)
		{ e.printStackTrace(); }
		
		this.printStatus("Server closed\n");
		System.exit(1);
	}
	
	private void saveMemory()
	{
		//no memory yet
	}
	
	
	////////////////////////////////////////////////////////////////////////////////
	// Server debugging
	///////////////////////////////////////////////////////////////////////////////
	
	public void printStatus(String message)
	{
		System.out.println(message);
	}
	
	
	////////////////////////////////////////////////////////////////////////////////
	// Main function
	///////////////////////////////////////////////////////////////////////////////
	public static void main(String argv[])
	{
		Server server = new Server();
		
		server.initMemory();
		server.printStatus("Starting server...");
		server.runServer();
	}
}
