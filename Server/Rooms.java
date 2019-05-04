package Server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Rooms 
{
	private int number, slots, pricePerRoom;
	private ArrayList<Reservations> reserv;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
	
	
	public Rooms(String number, String slots, String roomPrice)
	{
		this.number = Integer.parseInt(number);
		this.slots = Integer.parseInt(slots);
		this.pricePerRoom = Integer.parseInt(roomPrice);
		
		reserv = new ArrayList<Reservations>();
	}
	
	public void addReservation(Reservations res)
	{
		reserv.add(res);
	}
	
	public int getNumber()
	{
		return number;
	}
	
	public int getSlots()
	{
		return slots;
	}
	
	public int getPricePerRoom()
	{
		return pricePerRoom;
	}
	
	public int calculatePrice(double days)
	{
		return (int)(pricePerRoom*days);
	}
	
	public int getSumToPay(int code)
	{
		int i;
		for(i=0; i<reserv.size(); i++)
			if(reserv.get(i).getCode() == code)
				return reserv.get(i).getPrice();
		return 0;
	}
	
	public String getInDate(int code)
	{
		int i;
		String inDate = "";
		
		for(i=0; i<reserv.size(); i++)
			if(reserv.get(i).getCode() == code)
				inDate = reserv.get(i).getStartDate();
		
		return inDate;
	}
	
	public String getOutDate(int code)
	{
		int i;
		String outDate = "";
		
		for(i=0; i<reserv.size(); i++)
			if(reserv.get(i).getCode() == code)
				outDate = reserv.get(i).getEndDate();
		
		return outDate;
	}
	
	public int reservationExists(int code)
	{
		int i;
		
		for(i=0; i<reserv.size(); i++)
			if(reserv.get(i).getCode() == code) return 1;
		
		return 0;
	}
	
	public int getReservationsPerRoom()
	{
		return reserv.size();
	}
	
	public int getReservCode(int poz)
	{
		return reserv.get(poz).getCode();
	}
	
	public int searchDate(String dateIn, String dateOut)
	{
		int i, foundSpace = 1;
		String d1, d2;
		Date dateIn1, dateIn2 = null, dateOut1, dateOut2 = null;
		
		try
		{
			dateIn2 = sdf.parse(dateIn);
			dateOut2 = sdf.parse(dateOut);
		}
		catch(ParseException e)
		{
			System.out.println("Error parsing the date");
		}
		
		for(i=0; i<reserv.size(); i++)
		{
			d1 = reserv.get(i).getStartDate();
			d2 = reserv.get(i).getEndDate();
			
			try 
			{
				dateIn1 = sdf.parse(d1);
				dateOut1 = sdf.parse(d2);
				
				if((dateIn2.before(dateIn1) && dateOut2.after(dateIn1)) || (dateIn2.before(dateOut1) && dateOut2.after(dateOut1)) || (dateIn2.after(dateIn1) && dateOut2.before(dateOut1)))
				{
					foundSpace = 0;
					break;
				}
			}
			catch (ParseException e) 
			{
				System.out.println("Error parsing the date");
			}
		}
		
		if(foundSpace == 1)
		{
			long diff = dateOut2.getTime() - dateIn2.getTime();
			double days = (diff / (1000*60*60*24));
			
			return calculatePrice(days);
		}
		else return -1;
	}
	
	public void deleteRes(int code)
	{
		int i;
		
		for(i=0; i<reserv.size(); i++)
			if(reserv.get(i).getCode() == code)
				reserv.remove(i);
	}
}
