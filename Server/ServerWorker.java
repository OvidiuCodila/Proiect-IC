package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerWorker extends Thread
{
	//Unique ID
	private int id;
	
	//Client connection variables
	private Socket client;
	private Server sv;
	
	//Communication variables
	private DataInputStream br;
	private DataOutputStream bw;
	
	//Packet variables
	protected enum PACKET_TYPE {ReservationStatus, ReservationRequest, AvailableRoomInquiry, DeleteReservation, Exit};
	private Packet packet;
	
	//Running variables
	protected boolean serverWorkerRunning = false;
	private String recvMessage;
	
	//Memory variables
	private String data;
	private int price;
	private int roomNr;
	
	
	public ServerWorker(Socket client, Server sv, int id)
	{
		this.client = client;
		this.sv = sv;
		serverWorkerRunning = true;
		
		this.id = id;
	}
	
	public void run()
	{
		initReadWrite();
		
		try
		{
			sv.printStatus("Client " + id + " connected to the server\n");
			
			while(serverWorkerRunning)
			{
				recvMessage = br.readUTF();
				String[] packetParts = recvMessage.split("-");
				
				packet = new Packet();
				
				packet.setPacketLength(Integer.parseInt(packetParts[1]));
				packet.setPacketData(packetParts[2]);
				
				switch(packetParts[0])
				{
					case "ReservationStatus": {
						sv.printStatus("Client " + id + " checked a reservation status");
						packet.setPacketType(PACKET_TYPE.ReservationStatus);
						String result = sv.checkReservation(packet.getPacketData()); //packetParts[2]
						bw.writeUTF(result);
						break;
					}
							
					case "ReservationRequest": {
						sv.printStatus("Client " + id + " made a reservation request");
						packet.setPacketType(PACKET_TYPE.ReservationRequest);
						int code = sv.makeReservation(packet.getPacketData() + "/" + roomNr + "/" + price + "/" + data); //packetParts[2]
						if(code != -1)
							sv.printStatus("Reservation made succesfully! Code: " + code);
						else sv.printStatus("Reservation failed");
						bw.writeUTF(Integer.toString(code));
						break;
					}
							
					case "AvailableRoomInquiry": {
						sv.printStatus("Client " + id + " made a search for free rooms");
						packet.setPacketType(PACKET_TYPE.AvailableRoomInquiry);
						String result = sv.searchRoom(packet.getPacketData()); //packetParts[2]
						if(result.equals("none")) bw.writeUTF("-1");
						else
						{
							String[] resultParts = result.split("\\+");
							roomNr = Integer.parseInt(resultParts[0]); price = Integer.parseInt(resultParts[1]);
							data = packet.getPacketData(); //packetParts[2]
							bw.writeUTF(resultParts[1]);
						}
						break;
					}
					
					case "DeleteReservation": {
						sv.printStatus("Client " + id + " requested a reservation deletion");
						packet.setPacketType(PACKET_TYPE.DeleteReservation);
						int result = sv.deleteReservation(packet.getPacketData()); //packetParts[2]
						if(result == 1)
							sv.printStatus("Reservation deleted succesfully");
						else sv.printStatus("Reservation could not be deleted");
						bw.writeUTF(Integer.toString(result));
						break;
					}
					
					case "Exit": {
						sv.printStatus("Client " + id + " ended the connection");
						serverWorkerRunning = false;
						break;
					}
							
					default: {
						sv.printStatus("Error! Incorrect packet type type received!");
					}
				}	
			}
			sv.removeClient(this);	
		}
		catch(IOException e)
		{
			sv.printStatus("Connection lost");
		}
		
		closeSocket();
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
			if( br != null) this.br.close();
			if( bw != null) this.bw.close();
			if( client != null ) client.close();
			
			sv.printStatus("Client " + id + " has been removed succesfully\n");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
