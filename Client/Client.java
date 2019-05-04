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
	//Connection variables
	private Socket socket;
	private String host;
	private int port;
	
	//Communication variables
	private DataOutputStream bw;
	private DataInputStream br;
	
	//Packet variables
	protected enum PACKET_TYPE {ReservationStatus, ReservationRequest, AvailableRoomInquiry, DeleteReservation, Exit};
	private Packet packet;

	
	public void startClient()
	{
		try
 		{
 			host = "localhost";
 			port = 1700;
 			InetAddress address = InetAddress.getByName(host);
 			
 			socket = new Socket(address, port);
 			
 			initCommunication();
 		}
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
		
		sendMessage += packet.getPacketType() + "-" + packet.getPacketLength() + "-" + packet.getPacketData();
		
		try 
		{
			bw.writeUTF(sendMessage);
		}
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
			recvMessage = br.readUTF();
		}
		catch (SocketException e)
		{
			recvMessage = null;
			throw new MyConnectionException("Ooops! Connection was lost! Action not completed!");
		}
		catch (IOException e) 
		{
			throw new MyCommunicationException("Error reading packet");
		}
		
		return recvMessage;
	}
	
	private void initCommunication()
	{
		try
		{
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
		
		packet.setPacketType(PACKET_TYPE.Exit);
		packet.setPacketLength(0);
		packet.setPacketData("0");
		
		this.sendPacket();
		this.closeConnection();
	}
	
	public void closeConnection()
	{
		try
		{
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
		Platform.exit();
	}
	
	public int searchRoom(String dateIn, String dateOut, int personCount)
	{
		String recvMessage;
		
		packet = new Packet();
		
		packet.setPacketType(PACKET_TYPE.AvailableRoomInquiry);
		packet.setPacketLength(3);
		packet.setPacketData(dateIn + "/" + dateOut + "/" + personCount);
		
		try
		{
			this.sendPacket();
			recvMessage = this.recvPacket();
		}
		catch(MyConnectionException e)
		{
			throw new MyConnectionException(e.getMessage());
		}
		catch(MyCommunicationException e)
		{
			throw new MyCommunicationException(e.getMessage());
		}
		
		return Integer.parseInt(recvMessage);
	}
	
	public int makeReservation(String name, String cnp, String email, String phone, String address, String cardNr, String cvv)
	{
		String recvMessage;
		
		packet = new Packet();
		
		packet.setPacketType(PACKET_TYPE.ReservationRequest);
		packet.setPacketLength(8);
		packet.setPacketData(name + "/" + cnp + "/" + email + "/" + phone + "/" + address + "/" + cardNr + "/" + cvv);
		
		try
		{
			this.sendPacket();
			recvMessage = this.recvPacket();
		}
		catch(MyConnectionException e)
		{
			throw new MyConnectionException(e.getMessage());
		}
		catch(MyCommunicationException e)
		{
			throw new MyCommunicationException(e.getMessage());
		}
				
		return Integer.parseInt(recvMessage);
	}
	
	public String checkResrvation(String name, String code)
	{
		String recvMessage;
		
		packet = new Packet();
		
		packet.setPacketType(PACKET_TYPE.ReservationStatus);
		packet.setPacketLength(2);
		packet.setPacketData(name + "/" + code);
		
		try
		{
			this.sendPacket();
			recvMessage = this.recvPacket();
		}
		catch(MyConnectionException e)
		{
			throw new MyConnectionException(e.getMessage());
		}
		catch(MyCommunicationException e)
		{
			throw new MyCommunicationException(e.getMessage());
		}
		
		return recvMessage;
	}
	
	public int deleteReservation(String name, String code)
	{
		String recvMessage;
		
		packet = new Packet();
		
		packet.setPacketType(PACKET_TYPE.DeleteReservation);
		packet.setPacketLength(2);
		packet.setPacketData(name + "/" + code);
		
		try
		{
			this.sendPacket();
			recvMessage = this.recvPacket();
		}
		catch(MyConnectionException e)
		{
			throw new MyConnectionException(e.getMessage());
		}
		catch(MyCommunicationException e)
		{
			throw new MyCommunicationException(e.getMessage());
		}
		
		return Integer.parseInt(recvMessage);
	}
}
