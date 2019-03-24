package Client;

public class Packet 
{
	private enum PACKET_TYPE { Authentication, AuthenticationAccepted, RezervationRequest, RezervationAccepted, RezervationDenied, InguiryRooms, InquiryRoomsResult };
	
	private PACKET_TYPE type;
	private int length;
	private String data;
	
	
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
