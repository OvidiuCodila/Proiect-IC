package Server;

public class Person 
{
	private String name, cnp, email, phone, address, cardNr, cvv;
	private int code;
	
	
	public Person(int code, String name, String cnp, String email, String phone, String address, String cardNr, String cvv)
	{
		this.code = code;
		this.name = name;
		this.cnp = cnp;
		this.email = email;
		this.phone = phone;
		this.address = address;
		this.cardNr = cardNr;
		this.cvv = cvv;
	}
	
	public int getCode()
	{
		return code;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getCnp()
	{
		return cnp;
	}
	
	public String getEmail()
	{
		return email;
	}
	
	public String getPhone()
	{
		return phone;
	}
	
	public String getAddress()
	{
		return address;
	}
	
	public String getCardNr()
	{
		return cardNr;
	}
	
	public String getCvv()
	{
		return cvv;
	}
}
