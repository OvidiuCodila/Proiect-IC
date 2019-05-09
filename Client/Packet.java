package Client;

import Client.Client.PACKET_TYPE;

public class Packet 
{
	private PACKET_TYPE type;
	private int length;
	private String data;
	
	
	// getters and setter for the packet fields
	public void setPacketType(PACKET_TYPE type)
	{
		this.type = type;
	}
	
	public void setPacketLength(int length)
	{
		this.length = length;
	}
	
	public void setPacketData(String message)
	{
		this.data = message;
	}
	
	public String getPacketData()
	{
		return data;
	}
	
	public int getPacketLength()
	{
		return this.length;
	}
	
	public PACKET_TYPE getPacketType()
	{
		return this.type;
	}
}
