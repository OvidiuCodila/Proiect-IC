package Server;

import java.io.BufferedReader;
import java.text.ParseException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.scene.control.ListView;

public class Server extends Thread
{
	//Server connection variables
	private int port = 1700;
	private ServerSocket serverSocket;
	private ArrayList<ServerWorker> clients;
	
	
	//Server memory variables
	private String roomsFileName = "Rooms.txt";
	private String resFileName = "Reservations.txt";
	private String resDetailsFileName = "ReservationDetails.txt";
	
	private File roomsFile, resFile, resDetailsFile;
	private String currentDate;
	private String currentTime;
	
	private String logsDirName = "Logs"; 
	private String dataDirName = "Data";
	private File logsDir, dataDir;
	
	private PrintWriter logs;
	
	private ArrayList<Rooms> rooms;
	private ArrayList<Person> resDetails;
	
	
	//Server running variables
	private boolean running = true;
	private static int id;
	private ServerResChecker checker;
	
	
	//Server design variable
	private ServerDesign sg;
	
	
	public Server(ServerDesign sg)
	{
		this.sg = sg;
		currentDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		
		logsDir = new File(logsDirName);
		dataDir = new File(dataDirName);
		
		roomsFile = new File(dataDir + "/" + roomsFileName);
		resFile = new File(dataDir + "/" + resFileName);
		resDetailsFile = new File(dataDir + "/" + resDetailsFileName);
		
		rooms = new ArrayList<Rooms>();
		resDetails = new ArrayList<Person>();
		
		id = 0;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////
	// Server running functions
	///////////////////////////////////////////////////////////////////////////////
	
	public void run()
	{
		this.initMemory();
		this.printStatus("Starting server...");
		
		this.printStatus("Starting reservation checker..");
		checker = new ServerResChecker(this);
		checker.start();
		this.printStatus("Reservation checker started..");
		
		openServerSocket();
		clients = new ArrayList<ServerWorker>();
		
		this.verifyDates();
		
		while(running)
		{
			Socket clientSocket = null;
			
			try
			{
				clientSocket = serverSocket.accept();
				
				ServerWorker sw = new ServerWorker(clientSocket, this, id++);
				clients.add(sw);
				sw.start();
			}
			catch(Exception e)
			{
				if(running)
				{
					this.printStatus("Error acceptiong new client connection");
					e.printStackTrace();
					this.stopRunning();
				}
			}
		}
	}
	
	public synchronized void removeClient(ServerWorker sw)
	{
		int i;
		for(i=0; i<clients.size(); i++)
			if(clients.get(i).equals(sw)) clients.remove(i);
	}
	
	////////////////////////////////////////////////////////////////////////////////
	// Server initializing functions
	///////////////////////////////////////////////////////////////////////////////
	
	private void openServerSocket()
	{
		try
		{
			serverSocket = new ServerSocket(port);
			this.printStatus("Server opened on port " + port + "\n");
		}
		catch(IOException e)
		{
			throw new RuntimeException("Can't open server on port" + port + "\n",e);
		}
	}
	
	public boolean initMemory()
	{
		//Directories creation
		if(!logsDir.exists())
			logsDir.mkdir();
		
		if(!dataDir.exists())
			dataDir.mkdir();
		
		//Logs file creation
		try
		{
			logs = new PrintWriter(new FileOutputStream(new File(logsDirName + "/" + "log" + currentDate + ".txt"), true));
			this.printStatus("Logs file created succesfully..");
		}
		catch(Exception e)
		{
			//in case of error what!?
		}
		
		//Rooms file initing
		if(!roomsFile.exists())
		{
			try
			{
				this.printStatus("Rooms file not found..");
				this.printStatus("Creating rooms file...");
				PrintWriter pw = new PrintWriter(dataDirName + "/" + roomsFileName);
				pw.close();
				this.printStatus("File created succesfully..");
			}
			catch(IOException e)
			{
				this.printStatus("Error creating the file for the rooms!");
				return false;
			}
		}
		else
		{
			this.printStatus("Rooms file found..");
			this.printStatus("Starting reading the file..");
			if(!readRooms())
			{
				this.printStatus("Error reading the rooms!");
				return false;
			}
			this.printStatus("Rooms done reading succesfully..");
			
		}
		
		//Reservations file initing
		if(!resFile.exists())
		{
			try
			{
				this.printStatus("Reservations file not found..");
				this.printStatus("Creating reservations file...");
				PrintWriter pw = new PrintWriter(dataDirName + "/" + resFileName);
				pw.close();
				this.printStatus("File created succesfully..");
			}
			catch(IOException e)
			{
				this.printStatus("Error creating the file for the reservations!");
				return false;
			}
		}
		else
		{
			this.printStatus("Reservations file found..");
			this.printStatus("Starting reading the file..");
			if(!readReservations())
			{
				this.printStatus("Error reading the reservations!");
				return false;
			}
			this.printStatus("Reservations done reading succesfully..");
		}
		
		//Reservations details file initing
		if(!resDetailsFile.exists())
		{
			try
			{
				this.printStatus("Reservations details file not found..");
				this.printStatus("Creating reservations details file...");
				PrintWriter pw = new PrintWriter(dataDirName + "/" + resDetailsFileName);
				pw.close();
				this.printStatus("File created succesfully..");
			}
			catch(IOException e)
			{
				this.printStatus("Error creating the file for the reservations details!");
				return false;
			}
		}
		else
		{
			this.printStatus("Reservations details file found..");
			this.printStatus("Starting reading the file..");
			if(!readReservationsDetails())
			{
				this.printStatus("Error reading the reservations details!");
				return false;
			}
			this.printStatus("Reservations details done reading succesfully..");
		}
		
		return true;
	}
	
	private boolean readRooms()
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(roomsFile));
			String roomNr, roomSlots, roomPrice;
			
			while((roomNr = br.readLine()) != null)
			{
				roomSlots = br.readLine();
				roomPrice = br.readLine();
				rooms.add(new Rooms(roomNr, roomSlots, roomPrice));
			}
			
			br.close();
		}
		catch(IOException e)
		{
			this.printStatus("Error reading the rooms!");
			return false;
		}
		return true;
	}
	
	private boolean readReservations()
	{
		try
		{
			int i, resAdded;
			BufferedReader br = new BufferedReader(new FileReader(resFile));
			String roomNr, dateStart, dateEnd, resPrice;
			int resCode;
			
			while((roomNr = br.readLine()) != null)
			{
				dateStart = br.readLine();
				dateEnd = br.readLine();
				resCode = Integer.parseInt(br.readLine().trim());
				resPrice = br.readLine();
				
				resAdded = 0;
				for(i=0; i<rooms.size(); i++)
					if(rooms.get(i).getNumber() == Integer.parseInt(roomNr))
					{
						rooms.get(i).addReservation(new Reservations(dateStart,dateEnd,resCode,resPrice));
						resAdded = 1;
					}
				if(resAdded == 0)
					this.printStatus("Error adding a reservation! Room not found!");
			}
			
			br.close();
		}
		catch(IOException e)
		{
			this.printStatus("Error reading the reservations!");
			return false;
		}
		return true;
	}
	
	private boolean readReservationsDetails()
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(resDetailsFile));
			String code, name, cnp, email, phone, address, cardNr, cvv;
			
			while((code = br.readLine()) != null)
			{
				name = br.readLine();
				cnp = br.readLine();
				email = br.readLine();
				phone = br.readLine();
				address = br.readLine();
				cardNr = br.readLine();
				cvv = br.readLine();
				
				resDetails.add(new Person(Integer.parseInt(code),name,cnp,email,phone,address,cardNr,cvv));
			}
			
			br.close();
		}
		catch(IOException e)
		{
			this.printStatus("Error reading the reservations!");
			return false;
		}
		return true;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////
	// Server closing functions
	///////////////////////////////////////////////////////////////////////////////
	
	public synchronized void stopRunning()
	{
		running = false;
		closeServer();
	}
	
	private void closeServer()
	{
		int i;
		
		this.printStatus("Removing all clients from the server...");
		for(i=0; i<clients.size(); i++)
		{
			if(clients.get(i) != null)
			{
				clients.get(i).closeSocket();
				clients.remove(i);
			}
		}
		this.printStatus("All clients removed succesfully");
		
		this.printStatus("Stopping reservation checker..");
		checker.closeChecker();
		this.printStatus("Reservation checker stopped succesfully");
		
		this.printStatus("Starting saving the memory...");
		saveMemory();
		this.printStatus("Memory saved succesfully");
		
		try
		{ serverSocket.close(); }
		catch(Exception e)
		{ e.printStackTrace(); }
		
		this.printStatus("Server closed\n");
		
		this.printStatus("Logs file closed succesfully");
		this.printStatus(" ");
		logs.close();
		
		Platform.exit();
	}
	
	private void saveMemory()
	{
		//Rooms file doesnt need saving. It remains the same
		
		//Reservations saving
		try
		{
			PrintWriter pw = new PrintWriter(dataDir + "/" + resFileName);
			
			int reservPerRoom;
			int code;
			
			this.printStatus("Started saving the reservations..");
			for(int i=0; i<rooms.size(); i++)
			{
				reservPerRoom = rooms.get(i).getReservationsPerRoom();
				for(int j=0; j<reservPerRoom; j++)
				{
					code = rooms.get(i).getReservCode(j);
					pw.println(rooms.get(i).getNumber());
					pw.println(rooms.get(i).getInDate(code));
					pw.println(rooms.get(i).getOutDate(code));
					pw.println(code);
					pw.println(rooms.get(i).getSumToPay(code));
				}
			}
			this.printStatus("Reservations done saving..");
			pw.close();
		}
		catch(Exception e)
		{
			this.printStatus("Error saving the reservations!!");
		}
		
		//Reservations details saving
		try
		{
			PrintWriter pw = new PrintWriter(dataDir + "/" + resDetailsFileName);
			
			this.printStatus("Started saving the reservations details..");
			for(int i=0; i<resDetails.size(); i++)
			{
				pw.println(resDetails.get(i).getCode());
				pw.println(resDetails.get(i).getName());
				pw.println(resDetails.get(i).getCnp());
				pw.println(resDetails.get(i).getEmail());
				pw.println(resDetails.get(i).getPhone());
				pw.println(resDetails.get(i).getAddress());
				pw.println(resDetails.get(i).getCardNr());
				pw.println(resDetails.get(i).getCvv());
			}
			this.printStatus("Reservations details done saving..");
			pw.close();
		}
		catch(Exception e)
		{
			this.printStatus("Error saving the events!!");
		}
		
	}
	
	////////////////////////////////////////////////////////////////////////////////
	// Server reservations managing functions
	///////////////////////////////////////////////////////////////////////////////
	
	
	//---------- Client Requests ----------//
	
	public synchronized String searchRoom(String data)
	{
		int i;
		String[] dataParts = data.split("/");
		String dateIn = dataParts[0], dateOut = dataParts[1];
		int persCount = Integer.parseInt(dataParts[2]);
		int price = -1;
		String outRes = "none";
		
		for(i=0; i<rooms.size(); i++)
			if(rooms.get(i).getSlots() == persCount)
			{
				if((price = rooms.get(i).searchDate(dateIn, dateOut)) != -1)
				{
					outRes = rooms.get(i).getNumber() + "+" + price;
					break;
				}
			}
		
		return outRes;
	}
	
	public synchronized int makeReservation(String data)
	{
		String[] dataParts = data.split("/");
		int i, code = -1;
		
		for(i=0; i<rooms.size(); i++)
			if(rooms.get(i).getNumber() == Integer.parseInt(dataParts[7]))
			{
				code = ( rooms.get(i).getNumber() * 1000 ) + ( this.getRandCode() / Integer.parseInt(dataParts[8]) );
				rooms.get(i).addReservation(new Reservations(dataParts[9], dataParts[10], code, dataParts[8]));
				
				resDetails.add(new Person(code, dataParts[0], dataParts[1], dataParts[2], dataParts[3], dataParts[4], dataParts[5], dataParts[6]));
				
				return code;
			}
		
		return -1;
	}
	
	public synchronized String checkReservation(String data)
	{
		String[] dataParts = data.split("/");
		int i;
		String result = "No";
		
		int code = Integer.parseInt(dataParts[1]);
		String name = dataParts[0];
		dataParts = null;
		
		for(i=0; i<resDetails.size(); i++)
			if(resDetails.get(i).getName().equals(name) && resDetails.get(i).getCode() == code)
			{
				Person p = resDetails.get(i);
				result = "Yes /" + p.getCnp() + "/" + p.getEmail() + "/" + p.getPhone();
				
				for(i=0; i<rooms.size(); i++)
				{
					if(rooms.get(i).reservationExists(code) == 1)
					{
						result += "/" + rooms.get(i).getInDate(code) + " - " + rooms.get(i).getOutDate(code);
					}
				}
			}
		
		return result;
	}
	
	public synchronized int deleteReservation(String data)
	{
		int i;
		String[] dataParts = data.split("/");
		
		int code = Integer.parseInt(dataParts[1]);
		String name = dataParts[0];
		dataParts = null;
		
		for(i=0; i<resDetails.size(); i++)
			if(resDetails.get(i).getName().equals(name) && resDetails.get(i).getCode() == code)
			{
				resDetails.remove(i);
				
				for(i=0; i<rooms.size(); i++)
				{
					if(rooms.get(i).reservationExists(code) == 1)
					{
						rooms.get(i).deleteRes(code);
						return 1;
					}
				}
			}
		
		return 0;
	}
	
	private int getRandCode()
	{
		Random rand = new Random();
		int nr;
		
		nr = rand.nextInt(8999) + 1000;
		
		return nr;
	}
	
	//---------- Server Requests ----------//
	
	public synchronized int delRes(String details)
	{		
		String newStr = details.replace("-", "/");
		
		this.printStatus("Admin requested to delete a reservation");
		
		if(deleteReservation(newStr) == 1)
		{
			this.printStatus("Reservation deleted succesfully by admin");
			return 1;
		}
		else
		{
			this.printStatus("Action failed! Reservation could not be deleted");
			return 0;
		}
	}

	public synchronized void searchList(ListView<String> lst, int type, String txt)
	{
		// type 1 = rooms, 2 = code, 3 = name
		
		int i, j, code;
		String name, outTxt;
		
		lst.getItems().clear();
		switch(type)
		{
			case 1: {
				for(i=0; i<resDetails.size(); i++)
				{
					code = resDetails.get(i).getCode();
					name = resDetails.get(i).getName();
					Rooms r;
					
					for(j=0; j<rooms.size(); j++)
					{
						r = rooms.get(j);
						if( r.reservationExists(code) == 1 && r.getNumber() == Integer.parseInt(txt))
						{
							outTxt = name + " - " + code;
							lst.getItems().add(outTxt);
						}
					}
					r = null;
				}
				if(lst.getItems().size() < 1) lst.getItems().add("No reservation found");
				break;
			}
			case 2: {
				for(i=0; i<resDetails.size(); i++)
				{
					code = resDetails.get(i).getCode();
					name = resDetails.get(i).getName();
					if(code == Integer.parseInt(txt))
					{
						outTxt = name + " - " + code;
						lst.getItems().add(outTxt);
					}
				}
				if(lst.getItems().size() < 1) lst.getItems().add("No reservation found");
				break;
			}
			case 3: {
				for(i=0; i<resDetails.size(); i++)
				{
					code = resDetails.get(i).getCode();
					name = resDetails.get(i).getName();
					if(name.equals(txt))
					{
						outTxt = name + " - " + code;
						lst.getItems().add(outTxt);
					}
				}
				if(lst.getItems().size() < 1) lst.getItems().add("No reservation found");
				break;
			}
			default: {
				//do nothing
				break;
			}
		}
	}
	
	public synchronized void getReservList(ListView<String> lst)
	{
		int i;
		String name;
		int code;
		
		lst.getItems().clear();
		
		if(resDetails.size() < 1)
			lst.getItems().add("No reservations");
		else
		{
			for(i=0; i<resDetails.size(); i++)
			{
				code = resDetails.get(i).getCode();
				name = resDetails.get(i).getName();
				
				lst.getItems().add(name + " - " + code);
			}
		}
	}
	
	public synchronized String getReservationDetails(String input)
	{
		String result = "";
		
		if(input == null || input.equals("No reservations")) return result;
		
		String[] inputParts = input.split("-");
		String name = inputParts[0].trim();
		int code = Integer.parseInt(inputParts[1].trim());
		Person p = null;
		Rooms r;
		
		int i;
		for(i=0; i<resDetails.size(); i++)
		{
			p = resDetails.get(i);
			if(p.getName().equals(name) && p.getCode() == code)
			{
				result += "Name: " + name + System.getProperty("line.separator");
				result += "CNP: " + p.getCnp() + System.getProperty("line.separator");
				result += "Email: " + p.getEmail() + System.getProperty("line.separator");
				result += "Phone: " + p.getPhone() + System.getProperty("line.separator");
				result += "Address: " + p.getAddress() + System.getProperty("line.separator");
				break;
			}
		}
		
		if(p != null)
		{
			for(i=0; i<rooms.size(); i++)
			{
				r = rooms.get(i);
				if(r.reservationExists(code) == 1)
				{
					result += "Room number: " + r.getNumber() + System.getProperty("line.separator");
					result += "Number of people: " + r.getSlots() + System.getProperty("line.separator");
					result += "Period: " + r.getInDate(p.getCode()) + " - " + r.getOutDate(p.getCode()) + System.getProperty("line.separator");
					result += System.getProperty("line.separator");
					result += "Price: " + r.getSumToPay(p.getCode()) + System.getProperty("line.separator");
				}
			}
		}
		
		return result;
	}
	
	public void verifyDates()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Date dateIn, dateOut, currentDate;
		String crtDate = sdf.format(new Date());
		
		int i, j, code;
		Rooms r;
		for(i=0; i<rooms.size(); i++)
		{
			r = rooms.get(i);
			for(j=0; j<r.getReservationsPerRoom(); j++)
			{
				code = r.getReservCode(j);
				try
				{
					dateIn = sdf.parse(r.getInDate(code));
					dateOut = sdf.parse(r.getOutDate(code));
					currentDate = sdf.parse(crtDate);
					
					if(currentDate.after(dateOut))
					{
						for(int k=0; k<resDetails.size(); k++)
							if(resDetails.get(i).getCode() == code)
							{
								String name = resDetails.get(i).getName();
								deleteReservation(name + "/" + code);
								break;
							}
					}
					else
					{
						long diffInMillies = Math.abs(currentDate.getTime() - dateIn.getTime());
					    long diff = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
					    
					    if(diff <= 24 && diff >= 1)
					    	this.printStatus("Reservation for room " + r.getNumber() + " is less than 24 hours away!");
					}
				}
				catch(ParseException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////
	// Server status update
	///////////////////////////////////////////////////////////////////////////////
	
	public synchronized void printStatus(String message)
	{
		sg.editTextArea(message);
		
		currentTime = new SimpleDateFormat("HH:mm").format(new Date());
		logs.append(currentTime + ": ");
		logs.append(message);
		logs.append(System.getProperty("line.separator"));
		
		currentTime = null;
	}
}
