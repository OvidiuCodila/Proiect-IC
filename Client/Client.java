package Client;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import javafx.application.Platform;

import java.net.ConnectException;


public class Client
{
	// connection variables
	private Socket socket;
	private String host;
	private int port;
	
	// communication variables
	private DataOutputStream bw;
	private DataInputStream br;
	
	// packet variables
	protected enum PACKET_TYPE {ReservationStatus, ReservationRequest, AvailableRoomInquiry, DeleteReservation, Exit};
	private Packet packet;

	
	public void startClient()
	{
		try
 		{
 			host = "localhost";
 			port = 1700; // setting the predetermined port
 			InetAddress address = InetAddress.getByName(host); // getting the ip address by the local host
 			
 			socket = new Socket(address, port); // opening the client socket for the specified ip and port
 			
 			initCommunication(); // initiating the buffers for communication
 		}
		// catching errors when initiating the communication or when it attempts to connect to the server
		catch(ConnectException e)
		{
			throw new MyConnectionException("Connection refused. Server might not be online right now!");
		}
		catch(SocketException e)
		{
			throw new MyConnectionException("Ooops! Seems the server has closed! We are sorry!");
		}
		catch(MyConnectionException e)
		{
			throw new MyConnectionException(e.getMessage());
		}
 		catch(Exception e)
 		{
 			throw new MyConnectionException("Error starting the client");
 		}
	}
	
	private void sendPacket()
	{
		String sendMessage = "";
		
		sendMessage += packet.getPacketType() + "-" + packet.getPacketLength() + "-" + packet.getPacketData(); // creating the message from the packet with the format: type-length-data
		
		try 
		{
			bw.writeUTF(sendMessage); // writing the message to the server
		}
		// catching exceptions that may appear
		catch (SocketException e)
		{
			throw new MyConnectionException("Ooops! Connection was lost! Action not completed!");
		}
		catch (IOException e) 
		{
			throw new MyCommunicationException("Error sending packet");
		}
		
		packet = null;
	}
	
	private String recvPacket()
	{
		String recvMessage;
		
		try 
		{
			recvMessage = br.readUTF(); // waiting to receive a response from the server
		}
		// catching the errors that may appear
		catch (SocketException e)
		{
			recvMessage = null;
			throw new MyConnectionException("Ooops! Connection was lost! Action not completed!");
		}
		catch (IOException e) 
		{
			throw new MyCommunicationException("Error reading packet");
		}
		
		return recvMessage; // returning the received message
	}
	
	private void initCommunication()
	{
		try
		{
			// opening the reading and writing streams to communicate with the server
			bw = new DataOutputStream(socket.getOutputStream());
			br = new DataInputStream(socket.getInputStream());
		}
		catch(IOException e)
		{
			throw new MyConnectionException("Error starting communication channels!");
		}
	}
	
	public void clientExitAction()
	{
		packet = new Packet();
		
		// creating and Exit packet with no data and no length, only with type
		packet.setPacketType(PACKET_TYPE.Exit);
		packet.setPacketLength(0);
		packet.setPacketData("0");
		
		this.sendPacket(); // sending this packet to the server
		this.closeConnection(); // closing the connection, no matter the response
	}
	
	public void closeConnection()
	{
		try
		{
			// stopping the reading/writing streams and the closing the socket
			if(bw != null) this.bw.close();
			if(br != null) this.br.close();
			if(socket != null) socket.close();
		}
		catch(IOException e)
		{
			System.out.println("Error closing the client\n");
			e.printStackTrace();
		}
		
		System.out.println("You have been succesfully disconnected from the server\n");
		Platform.exit(); // closing the application
	}
	
	public int searchRoom(String dateIn, String dateOut, int personCount)
	{
		String recvMessage;
		
		// creates a new packet
		packet = new Packet();
		
		packet.setPacketType(PACKET_TYPE.AvailableRoomInquiry); // setting the type according to the requested action
		packet.setPacketLength(3); // setting the length
		packet.setPacketData(dateIn + "/" + dateOut + "/" + personCount); // setting the data with the format: dateIn/dateOut/personNumber
		
		try
		{
			this.sendPacket(); // sending the packet to the server
			recvMessage = this.recvPacket(); // waiting for a response
		}
		// catching exceptions that may appear
		catch(MyConnectionException e)
		{
			throw new MyConnectionException(e.getMessage());
		}
		catch(MyCommunicationException e)
		{
			throw new MyCommunicationException(e.getMessage());
		}
		
		return Integer.parseInt(recvMessage); // returning the received message (-1 on fail, reservation price on success)
	}
	
	public int makeReservation(String name, String cnp, String email, String phone, String address, String cardNr, String cvv)
	{
		String recvMessage;
		
		// creating the packet for the requested action
		packet = new Packet();
		
		packet.setPacketType(PACKET_TYPE.ReservationRequest); // setting the type
		packet.setPacketLength(8); // setting the length 
		packet.setPacketData(name + "/" + cnp + "/" + email + "/" + phone + "/" + address + "/" + cardNr + "/" + cvv); // setting the data with the respective format
		
		try
		{
			this.sendPacket(); // sending the packet
			recvMessage = this.recvPacket(); // waiting for the response
		}
		// catching exceptions
		catch(MyConnectionException e)
		{
			throw new MyConnectionException(e.getMessage());
		}
		catch(MyCommunicationException e)
		{
			throw new MyCommunicationException(e.getMessage());
		}
				
		return Integer.parseInt(recvMessage); // returning the answer from the server (-1 on fail, code on success)
	}
	
	public String checkResrvation(String name, String code)
	{
		String recvMessage;
		
		// creating the packet
		packet = new Packet();
		
		packet.setPacketType(PACKET_TYPE.ReservationStatus); // set type
		packet.setPacketLength(2); // set length
		packet.setPacketData(name + "/" + code); // set data
		
		try
		{
			this.sendPacket(); // send packet
			recvMessage = this.recvPacket(); // receive answer
		}
		// catch exception
		catch(MyConnectionException e)
		{
			throw new MyConnectionException(e.getMessage());
		}
		catch(MyCommunicationException e)
		{
			throw new MyCommunicationException(e.getMessage());
		}
		
		return recvMessage; // return answer from the server (No on fail, Yes+details on success)
	}
	
	public int deleteReservation(String name, String code)
	{
		String recvMessage;
		
		// create packet 
		packet = new Packet();
		
		packet.setPacketType(PACKET_TYPE.DeleteReservation); // set type
		packet.setPacketLength(2); // set length
		packet.setPacketData(name + "/" + code); // set data
		
		try
		{
			this.sendPacket(); // send packet
			recvMessage = this.recvPacket(); // receive answer
		}
		// catch exception
		catch(MyConnectionException e)
		{
			throw new MyConnectionException(e.getMessage());
		}
		catch(MyCommunicationException e)
		{
			throw new MyCommunicationException(e.getMessage());
		}
		
		return Integer.parseInt(recvMessage); // return answer from the server ( -1 on fail, 1 on successs )
	}
}
