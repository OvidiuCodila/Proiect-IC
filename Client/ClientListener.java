package Client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientListener extends Thread
{
	//Connection variables
	private Socket socket;
	private Client client;
	
	//Running variables
	private boolean clRunning;
	private String message;
	
	//Communication variables
	private DataInputStream br;
	
	
	public ClientListener(Socket socket, Client client)
	{
		this.socket = socket;
		this.client = client;
		
		this.clRunning = true;
	}
	
	public void run()
	{
		initRead();
		
		while(clRunning)
		{
			try
			{
				message = null;
				message = br.readUTF();
				
				if(message != null)
					if(message.equals("CloseConnection"))
						clRunning = false;
					else System.out.println("Message received from the server: " + message);
			}
			catch(IOException e)
			{
				clRunning = false;
				break;
			}
		}
		closeListener();
	}
	
	private void initRead()
	{
		try
		{
			br = new DataInputStream(socket.getInputStream());
		}
		catch(IOException e)
		{
			System.out.println("Error at initializing the client reading\n");
			e.printStackTrace();
		}
	}
	
	public synchronized boolean isListenerClose()
	{
		if (clRunning) return true;
		else return false;
	}
	
	public synchronized void closeListener()
	{
		try
		{
			if(br != null) this.br.close();
		}
		catch(IOException e)
		{
			System.out.println("Error closing the client\n");
			e.printStackTrace();
		}
		
		System.out.println("Listener closed");
		
		client.closeConnection();
	}
}
