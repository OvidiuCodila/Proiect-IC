package Server;

public class Reservations 
{
	private String startDate, endDate;
	private int price;
	private int code;
	
	
	public Reservations(String startDate, String endDate, int code, String price)
	{
		// setting the reservation fields when the object is made
		this.startDate = startDate;
		this.endDate = endDate;
		this.code = code;
		this.price = Integer.parseInt(price);
	}
	
	// getters for the reservations fields
	public int getPrice()
	{
		return price;
	}
	
	public int getCode()
	{
		return code;
	}
	
	public String getStartDate()
	{
		return startDate;
	}
	
	public String getEndDate()
	{
		return endDate;
	}
}
