package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerWorker extends Thread
{
	// unique ID
	private int id;
	
	// client connection variables
	private Socket client;
	private Server sv;
	
	// communication variables
	private DataInputStream br;
	private DataOutputStream bw;
	
	// packet variables
	protected enum PACKET_TYPE {ReservationStatus, ReservationRequest, AvailableRoomInquiry, DeleteReservation, Exit};
	private Packet packet;
	
	// running variables
	protected boolean serverWorkerRunning = false;
	private String recvMessage;
	
	// memory variables
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
		initReadWrite(); // initializing the communication with the client (reading and writing)
		
		try
		{
			sv.printStatus("Client " + id + " connected to the server\n");
			
			while(serverWorkerRunning)
			{
				recvMessage = br.readUTF(); // the thread waits for a meesage from the client
				String[] packetParts = recvMessage.split("-"); // we split the message ( with the format: type-length-data ) by the "-" character
				
				packet = new Packet(); // create a new packet
				
				packet.setPacketLength(Integer.parseInt(packetParts[1])); // set the length
				packet.setPacketData(packetParts[2]); // set the data
				
				switch(packetParts[0]) // accordingly to the type we set the type and do the respective action
				{
					case "ReservationStatus": {
						sv.printStatus("Client " + id + " checked a reservation status");
						packet.setPacketType(PACKET_TYPE.ReservationStatus); // setting the type
						String result = sv.checkReservation(packet.getPacketData()); //doing the respective action -> checking the reservation status
						bw.writeUTF(result); // writing to the client the answer which is the result from the method called above
						break;
					}
							
					case "ReservationRequest": {
						sv.printStatus("Client " + id + " made a reservation request");
						packet.setPacketType(PACKET_TYPE.ReservationRequest); // set the type
						int code = sv.makeReservation(packet.getPacketData() + "/" + roomNr + "/" + price + "/" + data); //call the method to make a reservation
						if(code != -1)
							sv.printStatus("Reservation made succesfully! Code: " + code);
						else sv.printStatus("Reservation failed");
						bw.writeUTF(Integer.toString(code)); // writing the answer to the client; -1 on fail or the code on success
						break;
					}
							
					case "AvailableRoomInquiry": {
						sv.printStatus("Client " + id + " made a search for free rooms");
						packet.setPacketType(PACKET_TYPE.AvailableRoomInquiry); // set the type
						String result = sv.searchRoom(packet.getPacketData()); // calls the searchRoom method to see if there are available rooms for the specified input
						if(result.equals("none")) bw.writeUTF("-1"); // if it fails, it returns -1 to the client
						else
						{
							// on success it saves the period, the number of persons and the price in case the client tries to make a reservation for those details
							String[] resultParts = result.split("\\+");
							roomNr = Integer.parseInt(resultParts[0]); price = Integer.parseInt(resultParts[1]);
							data = packet.getPacketData();
							bw.writeUTF(resultParts[1]); // and return to the client the price
						}
						break;
					}
					
					case "DeleteReservation": {
						sv.printStatus("Client " + id + " requested a reservation deletion");
						packet.setPacketType(PACKET_TYPE.DeleteReservation); // set the type
						int result = sv.deleteReservation(packet.getPacketData()); //call the delete reservation method for the specified input (data)
						if(result == 1)
							sv.printStatus("Reservation deleted succesfully");
						else sv.printStatus("Reservation could not be deleted");
						bw.writeUTF(Integer.toString(result)); // return to the client 1 on success and -1 on fail
						break;
					}
					
					case "Exit": {
						// if the client stopped the connection, the exit packet is received and the specified thread is close
						sv.printStatus("Client " + id + " ended the connection");
						serverWorkerRunning = false;
						break;
					}
							
					default: {
						sv.printStatus("Error! Incorrect packet type type received!");
					}
				}	
			}
			sv.removeClient(this);	// and it is removed from the server
		}
		catch(IOException e)
		{
			sv.printStatus("Connection lost");
		}
		
		closeSocket();
	}
	
	private void initReadWrite()
	{
		// initializing the reading and writing streams
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
			// stopping the reading stream, writing stream and the client socket
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
