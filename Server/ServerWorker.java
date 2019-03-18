package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerWorker extends Thread
{
	//Client connection variables
	private Socket client;
	private Server sv;
	
	//Communication variables
	private DataInputStream br;
	private DataOutputStream bw;
	
	//Helping variables
	protected boolean serverWorkerRunning = false;
	private String message;
	
	//Testing variables
	private String name;
	private String returnMessage;
	
	
	public ServerWorker(Socket client, Server sv)
	{
		this.client = client;
		this.sv = sv;
		serverWorkerRunning = true;
	}
	
	public void run()
	{
		initReadWrite();
		
		try
		{
			name = br.readUTF();
			sv.printStatus("Client (" + name + ") connected to the server\n");
			
			while(serverWorkerRunning)
			{
				message = br.readUTF();
				returnMessage = "Please insert a valid message\n";
				
				sv.printStatus("Client (" + name + ") said: " + message);
				switch(message)
				{
					case "Hello": {
						returnMessage = "Hello\n";
						break;
					}
							
					case "Yolo": {
						returnMessage = "What!?\n";
						break;
					}
							
					case "Exit": {
						returnMessage = "Disconnecting...\n";
						break;
					}
							
					default: {
						sv.printStatus("Error! Incorrect message received!");
					}
				}
				
				if(serverWorkerRunning)
				{
					bw.writeUTF(returnMessage);
					sv.printStatus("Message sent to client (" + name + "): " + returnMessage);
					bw.flush();
				}
				
				if(message.equals("Exit"))
					break;	
			}
			sv.removeClient(this);
			closeSocket();	
		}
		catch(IOException e)
		{
			sv.printStatus("Client (" + name + ") disconnected");
		}
	}
	
	private void initReadWrite()
	{
		try
		{
			br = new DataInputStream(client.getInputStream());
			bw = new DataOutputStream(client.getOutputStream());
		}
		catch(IOException e)
		{
			sv.printStatus("Error initializing reading and writing for a client\n");
			e.printStackTrace();
		}
	}
	
	public synchronized void closeSocket()
	{
		serverWorkerRunning = false;
		try
		{
			if(!message.equals("Exit"))
				bw.writeUTF("CloseConnection");
			bw.flush();
			
			if( br != null) this.br.close();
			if( bw != null) this.bw.close();
			if( client != null ) client.close();
			
			if(message.equals("Exit"))
				sv.printStatus("Client (" + name + ") disconnected\n");
			else sv.printStatus("Client (" + name + ") has been disconnected\n");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
