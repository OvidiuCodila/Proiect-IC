package Client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.ConnectException;
import java.util.Scanner;


public class Client extends Thread
{
	//Connection variables
	private Socket socket;
	private String host;
	private int port;
	
	//Communication variables
	private DataOutputStream bw;
	private ClientListener cl;
	private Scanner scn;
	
	//Running variables
	private boolean running;
	private String sendMessage;
	
	//Testing variables
	private String name;
	
	
	public Client(String name)
	{
		this.name = name;
		running = true;
	}
	
	public void run()
	{
		try
 		{
 			host = "localhost";
 			port = 1700;
 			InetAddress address = InetAddress.getByName(host);
 			
 			socket = new Socket(address, port);
 			
 			initWrite();
 			scn = new Scanner(System.in);
 			
 			cl = new ClientListener(socket, this);
 			cl.start();
 			
 			bw.writeUTF(name);	
			while(running && cl.isListenerClose())
			{
				try { sendMessage = scn.nextLine(); }
				catch(Exception e) { sendMessage = null; }
					
				if(sendMessage != null)
				{
					bw.writeUTF(sendMessage);
					bw.flush();
						
					System.out.println("Message sent to server: " + sendMessage);
				}
					
				if(sendMessage.equals("Exit") || sendMessage == null)
						running = false;
			}
			
			if(cl.isListenerClose())
				cl.closeListener();
 		}
		catch(ConnectException e)
		{
			System.out.println("Connection refused. Server might not be online right now!");
			//e.printStackTrace();
		}
		catch(SocketException e)
		{
			System.out.println("Ooops! Seems the server has closed! We are sorry!");
		}
 		catch(Exception e)
 		{
 			System.out.println("Error starting the client");
 			e.printStackTrace();
 		}
		finally
		{
			if( scn != null ) scn.close();
		}
	}
	
	private void initWrite()
	{
		try
		{
			bw = new DataOutputStream(socket.getOutputStream());
		}
		catch(IOException e)
		{
			System.out.println("Error at initializing the client writing\n");
			e.printStackTrace();
		}
	}
	
	public synchronized void clientExitAction()
	{
		running = false;
		if(cl != null) cl.closeListener();
	}
	
	public synchronized void closeConnection()
	{
		try
		{
			if(bw != null) this.bw.close();
			if(socket != null) socket.close();
		}
		catch(IOException e)
		{
			System.out.println("Error closing the client\n");
			e.printStackTrace();
		}
		
		if(sendMessage == null || sendMessage.equals("Exit"))
			System.out.println("You have disconnected from the server\n");
		else
		{
			System.out.println("Oops! It seems the server has closed! We are sorry for the inconvenience!");
			System.out.println("You have been succesfully disconnected from the server\n");
		}
		System.exit(1);
	}
}
