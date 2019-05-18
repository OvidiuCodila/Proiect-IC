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

import javafx.scene.control.ListView;

public class Server extends Thread
{
	// server connection variables
	private int port = 1700;
	private ServerSocket serverSocket;
	private ArrayList<ServerWorker> clients;
	
	
	// server memory variables
	// server memory files
	private String roomsFileName = "Rooms.txt";
	private String resFileName = "Reservations.txt";
	private String resDetailsFileName = "ReservationDetails.txt";
	private File roomsFile, resFile, resDetailsFile;
	
	// server date and time
	private String currentDate;
	private String currentTime;
	
	// server memory folders
	private String logsDirName = "Logs"; 
	private String dataDirName = "Data";
	private File logsDir, dataDir;
	
	private PrintWriter logs;
	
	// linked lists for rooms, reservation details
	private ArrayList<Rooms> rooms;
	private ArrayList<Person> resDetails;
	
	
	// server running variables
	private boolean running = true;
	private static int id;
	private ServerResChecker checker;
	
	
	// server design
	private ServerDesign sg;
	
	
	public Server(ServerDesign sg)
	{
		this.sg = sg;
		// get the current date when the server starts
		currentDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		
		// initiate the folders
		logsDir = new File(logsDirName);
		dataDir = new File(dataDirName);
		
		// initiate the files in their respective folders
		roomsFile = new File(dataDir + "/" + roomsFileName);
		resFile = new File(dataDir + "/" + resFileName);
		resDetailsFile = new File(dataDir + "/" + resDetailsFileName);
		
		// initiate the lists for the rooms and reservations
		rooms = new ArrayList<Rooms>();
		resDetails = new ArrayList<Person>();
		
		id = 0;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////
	// Server running functions
	///////////////////////////////////////////////////////////////////////////////
	
	public void run()
	{
		this.initMemory(); // initiating the memory
		this.printStatus("Starting server...");
		
		this.printStatus("Starting reservation checker..");
		checker = new ServerResChecker(this);
		checker.start(); // starting the server checker
		this.printStatus("Reservation checker started..");
		
		openServerSocket(); // opening the server sockets
		clients = new ArrayList<ServerWorker>(); // initiating the list for the connected clients
		
		this.verifyDates(); // checking if there are reservations incoming less than 24h away or if reservations have already passed while the server was down
		
		while(running)
		{
			Socket clientSocket = null; // initiating the client socket
			
			try
			{
				clientSocket = serverSocket.accept(); // waiting for a client to connect 
				
				ServerWorker sw = new ServerWorker(clientSocket, this, id++);
				clients.add(sw); // adding the client to the clients list
				sw.start(); // starting the respective client thread
			}
			catch(Exception e)
			{
				if(running)
				{
					// if an error occurs while the server is running , it stops and gives a message
					this.printStatus("Error acceptiong new client connection");
					e.printStackTrace();
					this.stopRunning();
				}
			}
		}
	}
	
	public synchronized void removeClient(ServerWorker sw)
	{
		// called when a client connection has ended and it needs to be removed
		int i;
		for(i=0; i<clients.size(); i++)
			if(clients.get(i).equals(sw)) clients.remove(i); // searches the client end removes it
	}
	
	////////////////////////////////////////////////////////////////////////////////
	// Server initializing functions
	///////////////////////////////////////////////////////////////////////////////
	
	private void openServerSocket()
	{
		try
		{
			serverSocket = new ServerSocket(port); // initiating the server socket on the predetermined port
			this.printStatus("Server opened on port " + port + "\n");
		}
		catch(IOException e)
		{
			throw new RuntimeException("Can't open server on port" + port + "\n",e); // throwing and error if something bad happens
		}
	}
	
	public boolean initMemory()
	{
		// checking if the folders exist and if not, creat them
		if(!logsDir.exists())
			logsDir.mkdir();
		
		if(!dataDir.exists())
			dataDir.mkdir();
		
		// creating the logs file with the date the server was opened on
		try
		{
			logs = new PrintWriter(new FileOutputStream(new File(logsDirName + "/" + "log" + currentDate + ".txt"), true));
			this.printStatus("Logs file created succesfully..");
		}
		catch(Exception e)
		{
			//in case of error what!?
		}
		
		//checking if the rooms file exists
		if(!roomsFile.exists())
		{
			try
			{
				// if not, we create it
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
			// if it exists we attempt to read it
			this.printStatus("Rooms file found..");
			this.printStatus("Starting reading the file..");
			if(!readRooms())
			{
				this.printStatus("Error reading the rooms!");
				return false;
			}
			this.printStatus("Rooms done reading succesfully..");
			
		}
		
		// same process for the reservations file as for the rooms one
		if(!resFile.exists())
		{
			try
			{
				this.printStatus("Reservations file not found..");
				this.printStatus("Creating reservations file...");
				PrintWriter pw = new PrintWriter(dataDirName + "/" + resFileName); // creating the reservations file
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
			if(!readReservations()) // attempting to read the reservations
			{
				this.printStatus("Error reading the reservations!");
				return false;
			}
			this.printStatus("Reservations done reading succesfully..");
		}
		
		// same process for the reservation details file as for the rooms one
		if(!resDetailsFile.exists())
		{
			try
			{
				this.printStatus("Reservations details file not found..");
				this.printStatus("Creating reservations details file...");
				PrintWriter pw = new PrintWriter(dataDirName + "/" + resDetailsFileName); // creating the reservation details file
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
			if(!readReservationsDetails()) // attempting to read the reservation details file
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
			BufferedReader br = new BufferedReader(new FileReader(roomsFile)); // creating a buffer reader for the rooms file to read from it
			String roomNr, roomSlots, roomPrice;
			
			while((roomNr = br.readLine()) != null) // while there are still room numbers left to read it means there are also room details to read
			{
				roomSlots = br.readLine(); // getting the number of places in a room
				roomPrice = br.readLine(); // getting the price per night for the room
				rooms.add(new Rooms(roomNr, roomSlots, roomPrice)); // adding the room to the list of rooms
			}
			
			br.close(); //  closing the buffer reader
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
			BufferedReader br = new BufferedReader(new FileReader(resFile)); // creating a buffer reader for the reservations file
			String roomNr, dateStart, dateEnd, resPrice;
			int resCode;
			
			while((roomNr = br.readLine()) != null) // while there are still room numbers left to read we continue to read the other details for the reservation
			{
				dateStart = br.readLine();
				dateEnd = br.readLine();
				resCode = Integer.parseInt(br.readLine().trim());
				resPrice = br.readLine();
				
				resAdded = 0;
				for(i=0; i<rooms.size(); i++)
					if(rooms.get(i).getNumber() == Integer.parseInt(roomNr)) // we search to what room does the reservation belong to
					{
						rooms.get(i).addReservation(new Reservations(dateStart,dateEnd,resCode,resPrice)); // and add it to the respective room
						resAdded = 1;
					}
				if(resAdded == 0)
					this.printStatus("Error adding a reservation! Room not found!");
			}
			
			br.close(); // closing the buffer reader
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
			BufferedReader br = new BufferedReader(new FileReader(resDetailsFile)); // buffer reader for the reservation details file
			String code, name, cnp, email, phone, address, cardNr, cvv;
			
			while((code = br.readLine()) != null) // reading the code and then the other details
			{
				name = br.readLine();
				cnp = br.readLine();
				email = br.readLine();
				phone = br.readLine();
				address = br.readLine();
				cardNr = br.readLine();
				cvv = br.readLine();
				
				resDetails.add(new Person(Integer.parseInt(code),name,cnp,email,phone,address,cardNr,cvv)); // adding the reservation to its corresponding linked list
			}
			
			br.close(); // closing the buffer reader
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
		running = false; // stopping the infinite loop in the run() method
		closeServer(); // closing server stuff
	}
	
	private void closeServer()
	{
		int i;
		
		// stopping all the client threads and removing them from the clients list
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
		checker.closeChecker(); // stopping the server checker
		this.printStatus("Reservation checker stopped succesfully");
		
		this.printStatus("Starting saving the memory...");
		saveMemory(); // saving the current reservation database in the data folder in the according files
		this.printStatus("Memory saved succesfully");
		
		try
		{ serverSocket.close(); } // closing the server socket
		catch(Exception e)
		{ e.printStackTrace(); }
		
		this.printStatus("Server closed\n");
		
		this.printStatus("Logs file closed succesfully");
		this.printStatus(" ");
		logs.close(); // closing the logs folder
		
		System.exit(1); // closing the application
	}
	
	private void saveMemory()
	{
		// rooms file does not need to be saved. It remains the same
		
		// reservations saving
		try
		{
			PrintWriter pw = new PrintWriter(dataDir + "/" + resFileName); // opening the reservations file in the data folder
			
			int reservPerRoom;
			int code;
			
			this.printStatus("Started saving the reservations..");
			for(int i=0; i<rooms.size(); i++)
			{
				// for each reservation from each room we get the coresponding that that needs to be saved in this file
				reservPerRoom = rooms.get(i).getReservationsPerRoom();
				for(int j=0; j<reservPerRoom; j++)
				{
					// and we print it in the file
					code = rooms.get(i).getReservCode(j);
					pw.println(rooms.get(i).getNumber());
					pw.println(rooms.get(i).getInDate(code));
					pw.println(rooms.get(i).getOutDate(code));
					pw.println(code);
					pw.println(rooms.get(i).getSumToPay(code));
				}
			}
			this.printStatus("Reservations done saving..");
			pw.close(); // closing the file
		}
		catch(Exception e)
		{
			this.printStatus("Error saving the reservations!!");
		}
		
		// reservations details saving
		try
		{
			PrintWriter pw = new PrintWriter(dataDir + "/" + resDetailsFileName); // opening the coresponding file in the data folder
			
			this.printStatus("Started saving the reservations details..");
			for(int i=0; i<resDetails.size(); i++)
			{
				// printing the reservation details in the file
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
			pw.close(); // closing the file
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
		String[] dataParts = data.split("/"); // the data has the format: dateIn/dateOut/numberOfPeople so it gets split by the slash character
		String dateIn = dataParts[0], dateOut = dataParts[1];
		int persCount = Integer.parseInt(dataParts[2]); // the person count gets converted to int
		int price = -1; // price is initially -1
		String outRes = "none"; // and the return string is none
		
		for(i=0; i<rooms.size(); i++)
			if(rooms.get(i).getSlots() == persCount) // search the room by the person count 
			{
				if((price = rooms.get(i).searchDate(dateIn, dateOut)) != -1) // if there is an available room for that person count we search to see if the inserted date is free or not
				{
					// if it is, the return string becomes the room number and the price with the format: roomNumber+price
					outRes = rooms.get(i).getNumber() + "+" + price;
					break;
				}
			}
		
		return outRes; // otherwise we return "none"
	}
	
	public synchronized int makeReservation(String data)
	{
		String[] dataParts = data.split("/"); // the data has the format: name/cnp/email/phone/address/cardNr/cvv/roomNr/price/data so it gets split by "/"
		int i, code = -1; // code is initially -1
		
		for(i=0; i<rooms.size(); i++)
			if(rooms.get(i).getNumber() == Integer.parseInt(dataParts[7])) // we search the room where the reservations is going to be
			{
				do
				{
					code = ( rooms.get(i).getNumber() * 1000 ) + ( this.getRandCode() / Integer.parseInt(dataParts[8]) ); // we create a unique code based on the room number and price
				}while(codeExists(code));
				rooms.get(i).addReservation(new Reservations(dataParts[9], dataParts[10], code, dataParts[8])); // we add the reservation to the respective room 
				
				resDetails.add(new Person(code, dataParts[0], dataParts[1], dataParts[2], dataParts[3], dataParts[4], dataParts[5], dataParts[6])); // we add the reservation details to the list
				
				return code; // we return the code when success
			}
		
		return -1; // or -1 in case of fail
	}
	
	public synchronized String checkReservation(String data)
	{
		String[] dataParts = data.split("/"); // the data format is: name/code and it gets split by "/"
		int i;
		String result = "No";
		int code;
		
		try 
		{
			code = Integer.parseInt(dataParts[1]); // code gets converted to int
		}
		catch(NumberFormatException err)
		{
			return result;
		}
		String name = dataParts[0]; // we remember the name
		dataParts = null;
		
		for(i=0; i<resDetails.size(); i++)
			if(resDetails.get(i).getName().equals(name) && resDetails.get(i).getCode() == code) // we check if there exists a reservation with the name AND code inserted
			{
				Person p = resDetails.get(i);
				result = "Yes /" + p.getCnp() + "/" + p.getEmail() + "/" + p.getPhone(); // if it does we add the affirative "Yes" and the details of the reservation
				
				for(i=0; i<rooms.size(); i++)
				{
					if(rooms.get(i).reservationExists(code) == 1)
					{
						result += "/" + rooms.get(i).getInDate(code) + " - " + rooms.get(i).getOutDate(code); // plus we add the reservation period
					}
				}
			}
		
		return result; // we return the resulted string or "No" in case of fail
	}
	
	public synchronized int deleteReservation(String data)
	{
		int i;
		String[] dataParts = data.split("/"); // data format: name/code -> gets split by "/"
		
		int code = Integer.parseInt(dataParts[1]); // save the code as int
		String name = dataParts[0]; // save the name 
		dataParts = null;
		
		for(i=0; i<resDetails.size(); i++)
			if(resDetails.get(i).getName().equals(name) && resDetails.get(i).getCode() == code) // search the code AND the name
			{
				resDetails.remove(i); // we remove the reservation details from the list
				
				for(i=0; i<rooms.size(); i++)
				{
					if(rooms.get(i).reservationExists(code) == 1) // and we search the reservation between the rooms and when it is found , it gets removed from there as well
					{
						rooms.get(i).deleteRes(code);
						return 1; // 1 is returned on success
					}
				}
			}
		
		return -1; // -1 is returned on fail
	}
	
	private int getRandCode()
	{
		// generating a random number of 4 digits for the code
		Random rand = new Random();
		int nr;
		
		nr = rand.nextInt(8999) + 1000;
		
		return nr;
	}
	
	public boolean codeExists(int code)
	{
		int i;
		// checking the generated code doesnt exist between the already generated and still available codes
		for(i=0; i<resDetails.size(); i++)
			if(resDetails.get(i).getCode() == code) return true;
		return false;
	}
	
	//---------- Server Requests ----------//
	
	public synchronized int delRes(String details)
	{		
		String newStr = details.replace("-", "/"); // the details format is name-code so we replace the "-" with "/" so we can reuse the deleteReservation() method from above
		
		this.printStatus("Admin requested to delete a reservation");
		
		// we return 1 on success and 0 on fail
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
		
		lst.getItems().clear(); // clear the list so it will be rebuilt from 0
		// according to the type we search by name, code or room number
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
						if( r.reservationExists(code) == 1 && r.getNumber() == Integer.parseInt(txt)) // the rooms are searched if they have a reservation with the specified code
						{
							// if yes, and out string is formed with the format: name - code and it is added to the list
							outTxt = name + " - " + code;
							lst.getItems().add(outTxt);
						}
					}
					r = null;
				}
				if(lst.getItems().size() < 1) lst.getItems().add("No reservation found"); // if no reservations have been added to the list, a meesage relating that is added instead
				break;
			}
			case 2: {
				for(i=0; i<resDetails.size(); i++)
				{
					code = resDetails.get(i).getCode();
					name = resDetails.get(i).getName();
					if(code == Integer.parseInt(txt)) // we search by the code and make the same out text and add it to the reservation list
					{
						outTxt = name + " - " + code;
						lst.getItems().add(outTxt);
					}
				}
				if(lst.getItems().size() < 1) lst.getItems().add("No reservation found"); // if nothing is found an according message is added to the list
				break;
			}
			case 3: {
				for(i=0; i<resDetails.size(); i++)
				{
					code = resDetails.get(i).getCode();
					name = resDetails.get(i).getName();
					if(name.equals(txt)) // search the reservations by name and make the same out text to add the reservation to the list
					{
						outTxt = name + " - " + code;
						lst.getItems().add(outTxt);
					}
				}
				if(lst.getItems().size() < 1) lst.getItems().add("No reservation found"); // the no found message added if there is nothing found
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
		
		lst.getItems().clear(); // the reservation list is cleared
		
		if(resDetails.size() < 1)
			lst.getItems().add("No reservations"); // if there are not reservations we add a coresponding message to the list
		else
		{
			// if there are reservations
			for(i=0; i<resDetails.size(); i++)
			{
				// we add them in the list with the format: name - code
				code = resDetails.get(i).getCode();
				name = resDetails.get(i).getName();
				
				lst.getItems().add(name + " - " + code);
			}
		}
	}
	
	public synchronized String getReservationDetails(String input)
	{
		String result = "";
		
		if(input == null || input.equals("No reservations")) return result; // if no reservaion was selected on the list, null is returned
		
		String[] inputParts = input.split("-"); // otherwise we get the name and code from the selected reservation by spliting after the "-"
		String name = inputParts[0].trim();
		int code = Integer.parseInt(inputParts[1].trim());
		Person p = null;
		Rooms r;
		
		int i;
		// we search the reservations by code and name and make a result string to be added in the reservation details text area
		for(i=0; i<resDetails.size(); i++)
		{
			p = resDetails.get(i);
			if(p.getName().equals(name) && p.getCode() == code)
			{
				result += "Name: " + name + System.getProperty("line.separator"); // adding the name and enter after each line
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
		// we get the date format and the current date after this format
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Date dateIn, dateOut, currentDate;
		String crtDate = sdf.format(new Date());
		
		int i, j, code;
		Rooms r;
		// we go through all the rooms and all the reservations
		for(i=0; i<rooms.size(); i++)
		{
			r = rooms.get(i);
			for(j=0; j<r.getReservationsPerRoom(); j++)
			{
				code = r.getReservCode(j);
				try
				{
					// we get the reservation starting and ending dates and format them by the format defined above
					dateIn = sdf.parse(r.getInDate(code));
					dateOut = sdf.parse(r.getOutDate(code));
					currentDate = sdf.parse(crtDate);
					
					// we compare if the current date is after the ending date of the reservation
					if(currentDate.after(dateOut))
					{
						for(int k=0; k<resDetails.size(); k++)
							if(resDetails.get(k).getCode() == code)
							{
								String name = resDetails.get(k).getName();
								deleteReservation(name + "/" + code); // if yes, we get the code and name and detele the reservation
								break;
							}
					}
					else
					{
						// if the reservation didnt pass, we see if its going to happen in less than 24 hours
						long diffInMillies = Math.abs(currentDate.getTime() - dateIn.getTime());
					    long diff = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
					    
					    if(diff <= 24 && diff >= 1)
					    {
					    	this.printStatus("Reservation for room " + r.getNumber() + " is less than 24 hours away!");
					    	this.printStatus("Code : " + code); // if yes, we alert the admin in the logs text area on the menu scene
					    }
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
		sg.editTextArea(message); // we add the text to the logs text area on the menu scene
		
		currentTime = new SimpleDateFormat("HH:mm").format(new Date()); // we get the current time
		logs.append(currentTime + ": ");
		logs.append(message);
		logs.append(System.getProperty("line.separator")); // and add to the logs file the time : message + \n
		
		currentTime = null;
	}
}
