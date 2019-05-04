package Client;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ClientDesign extends Application
{
	// the window for the scenes
	private Stage mainWindow;
	
	// the scenes
	private Scene menuScene;
	private Scene makeReservationScene;
	private Scene manageReservationScene;
	private Scene hotelDetails;
	private Scene confirmReservation;
	
	enum Scenes { Menu, MakeReservation, ManageReservation, HotelDetails, ConfirmReservation };
	
	// the client
	private Client client;
	
	
	public static void main(String argv[])
	{
		launch(argv);
	}
	
	public void start(Stage primaryStage) throws Exception
	{
		String css;
		mainWindow = primaryStage;
		mainWindow.setTitle("Take-a-Room Reservation");
		
		// instantiate scenes
		menuScene = new Scene(createScene(Scenes.Menu), 1024, 768);
		makeReservationScene = new Scene(createScene(Scenes.MakeReservation), 1024, 768);
		manageReservationScene = new Scene(createScene(Scenes.ManageReservation), 1024, 768);
		hotelDetails = new Scene(createScene(Scenes.HotelDetails), 1024, 768);
		confirmReservation = new Scene(createScene(Scenes.ConfirmReservation), 1024, 768);
		
		// assign each scene a stylesheet page
		css = this.getClass().getResource("/styleSheetMenu.css").toExternalForm();
		menuScene.getStylesheets().add(css);
		css = this.getClass().getResource("/styleSheetMakeReservation.css").toExternalForm();
		makeReservationScene.getStylesheets().add(css);
		css = this.getClass().getResource("/styleSheetManageReservation.css").toExternalForm();
		manageReservationScene.getStylesheets().add(css);
		css = this.getClass().getResource("/styleSheetHotelDetails.css").toExternalForm();
		hotelDetails.getStylesheets().add(css);
		css = this.getClass().getResource("/styleSheetConfirmReservation.css").toExternalForm();
		confirmReservation.getStylesheets().add(css);
		
		// set current scene
		setCurrentScene(Scenes.Menu);
		
		// start the client
		client = new Client();
		
		try
		{
			client.startClient();
		}
		catch(MyConnectionException e)
		{
			alertBox("ERROR","We're sorry!",e.getMessage(),AlertType.ERROR);
			client.closeConnection();
		}
	}
	
	//////////////////////////////////////////////////////////////////
	//Method for setting the scene
	//////////////////////////////////////////////////////////////////
	
	private void setCurrentScene(Scenes scene)
	{
		switch(scene)
		{
			case Menu : {
				mainWindow.setScene(menuScene);
				break;
			}
			
			case MakeReservation: {
				mainWindow.setScene(makeReservationScene);
				break;
			}
			
			case ManageReservation: {
				mainWindow.setScene(manageReservationScene);
				break;
			}
			
			case HotelDetails: {
				mainWindow.setScene(hotelDetails);
				break;
			}
			
			case ConfirmReservation: {
				mainWindow.setScene(confirmReservation);
				break;
			}
			
			default: {
				System.out.println("Error! Wrong scene!!");
				break;
			}
		}
		
		mainWindow.show();
	}
	
	//////////////////////////////////////////////////////////////////
	//Methods for creating the design to each scene along with designing the elements from the scenes
	//////////////////////////////////////////////////////////////////
	
	private BorderPane createScene(Scenes scene)
	{
		BorderPane pane = new BorderPane();
		
		switch(scene)
		{
			case Menu: {
				pane.setCenter(addMenuGridPane());
				break;
			}
			case MakeReservation: {
				pane.setCenter(addMakeReservationGridPane());
				break;
			}
			case ManageReservation: {
				pane.setCenter(addManageReservationGridPane());
				break;
			}
			case HotelDetails: {
				pane.setCenter(addHotelDetailsGridPane());
				break;
			}
			case ConfirmReservation: {
				pane.setCenter(addConfirmReservationGridPane());
				break;
			}
			default: {
				System.out.println("Error!! Wrong Scene!!");
			}
		}
		
		return pane;
	}
	
	private GridPane addMenuGridPane()
	{
		GridPane grid = new GridPane();
		grid.getStyleClass().add("gridMenu");
		
		//Make Reservation Button Design
		final Button makeRezservBtn = new Button();
		makeRezservBtn.setText("Make Reservation");
		makeRezservBtn.getStyleClass().add("allOtherButtons");
		grid.add(makeRezservBtn, 8, 18, 1, 1);
		
		//Check Reservation Button Design
		final Button checkRezservBtn = new Button();
		checkRezservBtn.setText("Check Reservation");
		checkRezservBtn.getStyleClass().add("allOtherButtons");
		grid.add(checkRezservBtn, 8, 19, 1, 1);
		
		//Hotel Details Button Design
		final Button hotelDetailsBtn = new Button();
		hotelDetailsBtn.setText("Hotel Details");
		hotelDetailsBtn.getStyleClass().add("allOtherButtons");
		grid.add(hotelDetailsBtn, 12, 18, 1, 1);
		
		
		//Exit Button Design
		final Button exitButton = new Button();
		exitButton.setText("Exit");
		exitButton.getStyleClass().add("exitButton");
		grid.add(exitButton, 12, 19, 1, 1);
		
		
		// Exit Button Function
		exitButton.setOnAction(e -> {
			client.clientExitAction();
		});
		
		//Hotel Details Button Function
		hotelDetailsBtn.setOnAction(e -> {
			setCurrentScene(Scenes.HotelDetails);
		});
		
		//Make Reservation Button Function
		makeRezservBtn.setOnAction(e -> {
			setCurrentScene(Scenes.MakeReservation);
		});
		
		//Check Reservation Button Function
		checkRezservBtn.setOnAction(e -> {
			setCurrentScene(Scenes.ManageReservation);
		});
		
		return grid;
	}
	
	private GridPane addMakeReservationGridPane()
	{
		GridPane grid = new GridPane();
		
		grid.getStyleClass().add("gridMakeReserv");
		
		//Description Labels
		final Label arriveDate = new Label();
		arriveDate.setText("Arriving Date: ");
		arriveDate.getStyleClass().add("presetLabel");
		grid.add(arriveDate, 5, 4, 3, 1);
		
		final Label leaveDate = new Label();
		leaveDate.setText("Leaving Date: ");
		leaveDate.getStyleClass().add("presetLabel");
		grid.add(leaveDate, 5, 7, 3, 1);
		
		final Label nrPersons = new Label();
		nrPersons.setText("Places (max 4): ");
		nrPersons.getStyleClass().add("presetLabel");
		grid.add(nrPersons, 9, 4, 3, 1);
		
		//TextFields for input
		final TextField arriveDay = new TextField();
		arriveDay.setPromptText("DD");
		arriveDay.getStyleClass().add("dayTextField");
		grid.add(arriveDay, 5, 5);
		
		final TextField arriveMonth = new TextField();
		arriveMonth.setPromptText("MM");
		arriveMonth.getStyleClass().add("monthTextField");
		grid.add(arriveMonth, 6, 5);
		
		final TextField arriveYear = new TextField();
		arriveYear.setPromptText("YYYY");
		arriveYear.getStyleClass().add("yearTextField");
		grid.add(arriveYear, 7, 5);
		
		final TextField pplNr = new TextField();
		pplNr.setPromptText("Nr");
		pplNr.getStyleClass().add("pplNrTextField");
		grid.add(pplNr, 9, 5);
		
		final TextField leaveDay = new TextField();
		leaveDay.setPromptText("DD");
		leaveDay.getStyleClass().add("dayTextField");
		grid.add(leaveDay, 5, 8);
		
		final TextField leaveMonth = new TextField();
		leaveMonth.setPromptText("MM");
		leaveMonth.getStyleClass().add("monthTextField");
		grid.add(leaveMonth, 6, 8);
		
		final TextField leaveYear = new TextField();
		leaveYear.setPromptText("YYYY");
		leaveYear.getStyleClass().add("yearTextField");
		grid.add(leaveYear, 7, 8);
		
		
		//Search Result label
		final Label resultLabel = new Label();
		resultLabel.setText(" ");
		resultLabel.getStyleClass().add("resultLabel");
		grid.add(resultLabel, 7, 10, 3, 1);
		
		
		//Buttons
		final Button searchButton = new Button();
		searchButton.setText("Search");
		searchButton.getStyleClass().add("otherButtons");
		grid.add(searchButton, 7, 14);
		
		final Button backButton = new Button();
		backButton.setText("Back");
		backButton.getStyleClass().add("backButton");
		grid.add(backButton, 8, 14);
		
		final Button reserveButton = new Button();
		reserveButton.setText("Reserve");
		reserveButton.getStyleClass().add("otherButtons");
		reserveButton.setDisable(true);
		grid.add(reserveButton, 9, 14);
		
		//Button Actions
		searchButton.setOnAction(e -> {
			String dateIn = gtTxt(arriveDay) + "." + gtTxt(arriveMonth) + "." + gtTxt(arriveYear);
			String dateOut = gtTxt(leaveDay) + "." + gtTxt(leaveMonth) + "." + gtTxt(leaveYear);
			
			try 
			{
				int persCount = Integer.parseInt(gtTxt(pplNr));
				int dayIn = Integer.parseInt(gtTxt(arriveDay));
				int dayOut = Integer.parseInt(gtTxt(leaveDay));
				int monthIn = Integer.parseInt(gtTxt(arriveMonth));
				int monthOut = Integer.parseInt(gtTxt(leaveMonth));
				int yearIn = Integer.parseInt(gtTxt(arriveYear));
				int yearOut = Integer.parseInt(gtTxt(leaveYear));
				
				if(checkDatesAndPersCount(dayIn,dayOut,monthIn,monthOut,yearIn,yearOut,persCount))
				{
					int rez = client.searchRoom(dateIn, dateOut, persCount);
					if( rez == -1 ) 
					{
						reserveButton.setDisable(true);
						resultLabel.setTextFill(Color.RED);
						resultLabel.setText("Nu exista locuri disponibile! \n \tNe pare rau!");
					}
					else
					{
						reserveButton.setDisable(false);
						resultLabel.setTextFill(Color.GREEN);
						resultLabel.setText("Avem locuri disponibile. Total: " + rez);
					}
				}
			} 
			catch(NumberFormatException err)
			{
				alertBox("ERROR","The following errors have occured:","One or more fields is empty!",AlertType.ERROR);
			}
			catch(MyConnectionException err)
			{
				alertBox("ERROR","We're sorry!",err.getMessage(),AlertType.ERROR);
				client.closeConnection();
			}
			catch(MyCommunicationException err)
			{
				alertBox("ERROR","We're sorry!",err.getMessage(),AlertType.ERROR);
			}
		});
		
		backButton.setOnAction(e -> {
			resultLabel.setText("");
			arriveYear.clear(); arriveMonth.clear(); arriveDay.clear();
			leaveYear.clear(); leaveMonth.clear(); leaveDay.clear();
			pplNr.clear();
			setCurrentScene(Scenes.Menu);
		});
		
		reserveButton.setOnAction(e -> {
			resultLabel.setText("");
			setCurrentScene(Scenes.ConfirmReservation);
		});
		
		return grid;
	}
	
	private GridPane addConfirmReservationGridPane()
	{
		GridPane grid = new GridPane();
		grid.getStyleClass().add("gridConfirmReserv");
		
		//Description labels
		final Label name = new Label();
		name.setText("Name: ");
		name.getStyleClass().add("standardLabel");
		grid.add(name, 5, 5);
		
		final Label cnp = new Label();
		cnp.setText("CNP: ");
		cnp.getStyleClass().add("standardLabel");
		grid.add(cnp, 5, 6);
		
		final Label email = new Label();
		email.setText("Email: ");
		email.getStyleClass().add("standardLabel");
		grid.add(email, 5, 7);
		
		final Label phone = new Label();
		phone.setText("Phone: ");
		phone.getStyleClass().add("standardLabel");
		grid.add(phone, 5, 8);
		
		final Label address = new Label();
		address.setText("Address: ");
		address.getStyleClass().add("standardLabel");
		grid.add(address, 5, 9);
		
		final Label cardNr = new Label();
		cardNr.setText("Card Nr: ");
		cardNr.getStyleClass().add("standardLabel");
		grid.add(cardNr, 5, 10);
		
		final Label cvv = new Label();
		cvv.setText("CVV: ");
		cvv.getStyleClass().add("standardLabel");
		grid.add(cvv, 5, 11);
		
		//Text Fields
		final TextField nameField = new TextField();
		nameField.setPromptText("Enter name here");
		nameField.getStyleClass().add("standardField");
		grid.add(nameField, 6, 5, 2, 1);
		
		final TextField cnpField = new TextField();
		cnpField.setPromptText("Enter cnp here");
		cnpField.getStyleClass().add("standardField");
		grid.add(cnpField, 6, 6, 2, 1);
		
		final TextField emailField = new TextField();
		emailField.setPromptText("Enter email here");
		emailField.getStyleClass().add("standardField");
		grid.add(emailField, 6, 7, 2, 1);
		
		final TextField phoneField = new TextField();
		phoneField.setPromptText("Enter phone here");
		phoneField.getStyleClass().add("standardField");
		grid.add(phoneField, 6, 8, 2, 1);
		
		final TextField addressField = new TextField();
		addressField.setPromptText("Enter address here");
		addressField.getStyleClass().add("standardField");
		grid.add(addressField, 6, 9, 2, 1);
		
		final TextField cardNrField = new TextField();
		cardNrField.setPromptText("Enter card number here");
		cardNrField.getStyleClass().add("standardField");
		grid.add(cardNrField, 6, 10, 2, 1);
		
		final TextField cvvField = new TextField();
		cvvField.setPromptText("Enter cvv here");
		cvvField.getStyleClass().add("standardField");
		grid.add(cvvField, 6, 11, 2, 1);
		
		//Buttons
		final Button backButton = new Button();
		backButton.setText("Back");
		backButton.getStyleClass().add("backButton");
		grid.add(backButton, 6, 12);
		
		final Button reserveButton = new Button();
		reserveButton.setText("Reserve");
		reserveButton.getStyleClass().add("reserveButton");
		grid.add(reserveButton, 7, 12);
		
		//Buttons actions
		backButton.setOnAction(e -> {
			setCurrentScene(Scenes.MakeReservation);
		});
		
		reserveButton.setOnAction(e -> {
			String nameTxt, cnpTxt, emailTxt, phoneTxt, addressTxt, cardNrTxt, cvvTxt;
			nameTxt = gtTxt(nameField);
			cnpTxt = gtTxt(cnpField);
			emailTxt = gtTxt(emailField);
			phoneTxt = gtTxt(phoneField);
			addressTxt = gtTxt(addressField);
			cardNrTxt = gtTxt(cardNrField);
			cvvTxt = gtTxt(cvvField);
			
			try
			{
				if(checkUserData(nameTxt,cnpTxt,emailTxt,phoneTxt, cardNrTxt,cvvTxt))
				{
					if(confirmAlert("Confirm action","Are you sure you want to submit the reservation?"))
					{
						int rez = client.makeReservation(nameTxt,cnpTxt,emailTxt,phoneTxt,addressTxt,cardNrTxt,cvvTxt);
						if(rez == -1)
						{
							alertBox("ERROR", "Following errors have occured:", "Reservation failed! Please try again",AlertType.ERROR);
						}
						else
						{
							System.out.println(rez);
							alertBox("SUCCES","Reservation code: ","-> " + rez,AlertType.INFORMATION);
							nameField.clear(); cnpField.clear(); emailField.clear(); phoneField.clear(); addressField.clear();
							cardNrField.clear(); cvvField.clear();
							setCurrentScene(Scenes.Menu);
						}
					}
				}
			}
			catch(MyConnectionException err)
			{
				alertBox("ERROR","We're sorry!",err.getMessage(),AlertType.ERROR);
				client.closeConnection();
			}
			catch(MyCommunicationException err)
			{
				alertBox("ERROR","We're sorry!",err.getMessage(),AlertType.ERROR);
			}
		});
		
		return grid;
	}
	
	private GridPane addManageReservationGridPane()
	{
		GridPane grid = new GridPane();
		
		grid.getStyleClass().add("gridManageReserv");
		
		//Description Labels -----------------------------------------//
		final Label nameLabel = new Label();
		nameLabel.setText("Name: ");
		nameLabel.getStyleClass().add("presetLabel");
		grid.add(nameLabel, 6, 4);
		
		final Label codeLabel = new Label();
		codeLabel.setText("Code: ");
		codeLabel.getStyleClass().add("presetLabel");
		grid.add(codeLabel, 6, 5);
		
		final Label statusLabel = new Label();
		statusLabel.setText("Status: ");
		statusLabel.getStyleClass().add("presetLabel");
		grid.add(statusLabel, 6, 7);
		
		final Label cnpLabel = new Label();
		cnpLabel.setText("Cnp: ");
		cnpLabel.getStyleClass().add("presetLabel");
		grid.add(cnpLabel, 6, 8);
		
		final Label phoneLabel = new Label();
		phoneLabel.setText("Phone: ");
		phoneLabel.getStyleClass().add("presetLabel");
		grid.add(phoneLabel, 6, 9);
		
		final Label emailLabel = new Label();
		emailLabel.setText("Email: ");
		emailLabel.getStyleClass().add("presetLabel");
		grid.add(emailLabel, 6, 10);
		
		final Label dateLabel = new Label();
		dateLabel.setText("Date");
		dateLabel.getStyleClass().add("presetLabel");
		grid.add(dateLabel, 6, 11);
		
		//Details Labels -----------------------------------------//
		final Label statusLabelText = new Label();
		statusLabelText.setText("");
		statusLabelText.getStyleClass().add("textLabel");
		grid.add(statusLabelText, 7, 7, 3, 1);
		
		final Label cnpLabelText = new Label();
		cnpLabelText.setText("");
		cnpLabelText.getStyleClass().add("textLabel");
		grid.add(cnpLabelText, 7, 8, 3, 1);
		
		final Label phoneLabelText = new Label();
		phoneLabelText.setText("");
		phoneLabelText.getStyleClass().add("textLabel");
		grid.add(phoneLabelText, 7, 9, 3, 1);
		
		final Label emailLabelText = new Label();
		emailLabelText.setText("");
		emailLabelText.getStyleClass().add("textLabel");
		grid.add(emailLabelText, 7, 10, 3, 1);
		
		final Label dateLabelText = new Label();
		dateLabelText.setText("");
		dateLabelText.getStyleClass().add("textLabel");
		grid.add(dateLabelText, 7, 11, 3, 1);
		
		//Buttons -----------------------------------------//
		final Button backBtn = new Button();
		backBtn.setText("Back");
		backBtn.getStyleClass().add("otherButtons");
		grid.add(backBtn, 7, 12);
		
		final Button deleteBtn = new Button();
		deleteBtn.setText("Delete");
		deleteBtn.getStyleClass().add("otherButtons");
		deleteBtn.setDisable(true);
		grid.add(deleteBtn, 8, 12);
		
		final Button searchBtn = new Button();
		searchBtn.setText("Search");
		searchBtn.getStyleClass().add("searchBtn");
		grid.add(searchBtn, 10, 4);
		
		//TextFields for text input -----------------------------------------//
		final TextField name = new TextField();
		name.setPromptText("Enter name here...");
		name.getStyleClass().add("textField");
		grid.add(name, 7, 4, 3, 1);
		
		final TextField code = new TextField();
		code.setPromptText("Enter rezervation code here...");
		code.getStyleClass().add("textField");
		grid.add(code, 7, 5, 3, 1);
		
		//Buttons Actions -----------------------------------------//
		backBtn.setOnAction(e -> {
			name.clear();
			code.clear();
			statusLabelText.setText("");
			cnpLabelText.setText("");
			phoneLabelText.setText("");
			emailLabelText.setText("");
			dateLabelText.setText("");
			setCurrentScene(Scenes.Menu);
		});
		
		deleteBtn.setOnAction(e -> {
			if(confirmAlert("Confirm Action:", "Are you sure you want to delete your reservation?!"))
			{
				try
				{
					int result = client.deleteReservation(name.getText().trim(), code.getText().trim());
					if(result == 1)
					{
						alertBox("SUCCES", "", "Reservation deleted succesfully!",AlertType.CONFIRMATION);
						name.clear(); code.clear();
						
						statusLabelText.setText("");
						cnpLabelText.setText("");
						phoneLabelText.setText("");
						emailLabelText.setText("");
						dateLabelText.setText("");
						
						setCurrentScene(Scenes.Menu);
					}
					else
					{
						alertBox("ERROR!", "Following errors have occured:","Failed to delete reservation! Please try again",AlertType.ERROR);
					}
				}
				catch(MyConnectionException err)
				{
					alertBox("ERROR","We're sorry!",err.getMessage(),AlertType.ERROR);
					client.closeConnection();
				}
				catch(MyCommunicationException err)
				{
					alertBox("ERROR","We're sorry!",err.getMessage(),AlertType.ERROR);
				}
			}
		});
		
		searchBtn.setOnAction(e -> {
			if(!name.getText().trim().equals("") && !code.getText().trim().equals(""))
			{
				try
				{
					String details = client.checkResrvation(name.getText().trim(), code.getText().trim());
					String[] detailsParts = details.split("/");
					
					statusLabelText.setText("");
					cnpLabelText.setText("");
					phoneLabelText.setText("");
					emailLabelText.setText("");
					dateLabelText.setText("");
					
					deleteBtn.setDisable(false);
					
					statusLabelText.setText(detailsParts[0]);
					if(detailsParts.length > 1)
					{
						cnpLabelText.setText(detailsParts[1]);
						phoneLabelText.setText(detailsParts[3]);
						emailLabelText.setText(detailsParts[2]);
						dateLabelText.setText(detailsParts[4]);
					}
					else alertBox("ERROR","","Reservation not found! Please try again",AlertType.ERROR);
				}
				catch(MyConnectionException err)
				{
					alertBox("ERROR","We're sorry!",err.getMessage(),AlertType.ERROR);
					client.closeConnection();
				}
				catch(MyCommunicationException err)
				{
					alertBox("ERROR","We're sorry!",err.getMessage(),AlertType.ERROR);
				}
			}
			else
			{
				alertBox("ERROR","The following errors have occured:","Please fill in all the fields!",AlertType.ERROR);
			}
		});
		
		return grid;
	}
	
	private GridPane addHotelDetailsGridPane()
	{
		GridPane grid = new GridPane();
		
		// Adding images for rooms
		try
		{
			Image img1 = new Image(new FileInputStream("Resources/hotelRoom2.jpg"));
			Image img2 = new Image(new FileInputStream("Resources/hotelRoom3.jpg"));
			Image img3 = new Image(new FileInputStream("Resources/hotelRoom4.jpg"));
			Image img4 = new Image(new FileInputStream("Resources/hotelRestaurant.jpg"));
			Image img5 = new Image(new FileInputStream("Resources/whiteBar.jpg"));
			
			ImageView hotelRoom2 = new ImageView(img1);
			ImageView hotelRoom3 = new ImageView(img2);
			ImageView hotelRoom4 = new ImageView(img3);
			ImageView hotelRestaurant = new ImageView(img4);
			ImageView whiteBar = new ImageView(img5);
			
			hotelRoom2.setFitHeight(100);
			hotelRoom2.setFitWidth(150);
			hotelRoom3.setFitHeight(100);
			hotelRoom3.setFitWidth(150);
			hotelRoom4.setFitHeight(100);
			hotelRoom4.setFitWidth(150);
			hotelRestaurant.setFitHeight(100);
			hotelRestaurant.setFitWidth(150);
			whiteBar.setFitHeight(350);
			whiteBar.setFitWidth(3);
			
			grid.add(hotelRoom2, 5, 4);
			grid.add(hotelRoom3, 5, 5);
			grid.add(hotelRoom4, 5, 6);
			grid.add(hotelRestaurant, 5, 7);
			grid.add(whiteBar, 7, 4, 1, 3);
		}
		catch(Exception e)
		{
			System.out.println("Error loading photos");
			e.printStackTrace();
		}
		
		//text label for each image
		final Label room2 = new Label();
		room2.setText("- 5 rooms for 2 people");
		room2.getStyleClass().add("labelText");
		grid.add(room2, 6, 4);
		
		final Label room3 = new Label();
		room3.setText("- 3 rooms for 3 people");
		room3.getStyleClass().add("labelText");
		grid.add(room3, 6, 5);
		
		final Label room4 = new Label();
		room4.setText("- 2 rooms for 4 people");
		room4.getStyleClass().add("labelText");
		grid.add(room4, 6, 6);
		
		final Label restaurant = new Label();
		restaurant.setText("- restaurant open from 7 to 24 everyday");
		restaurant.getStyleClass().add("labelText");
		grid.add(restaurant, 6, 7, 3, 1);
		
		//text labels for other options
		final Label optionsHeader = new Label();
		optionsHeader.setText("Other options:");
		optionsHeader.getStyleClass().add("headerText");
		grid.add(optionsHeader, 8, 4);
		
		final Label option1 = new Label();
		option1.setText("- Free WiFi\n- Air conditioning");
		option1.getStyleClass().add("labelText");
		grid.add(option1, 8, 5);
		
		final Label option2 = new Label();
		option2.setText("- breakfast \n- lunch\n- dinner");
		option2.getStyleClass().add("labelText");
		grid.add(option2, 8, 6);
		
		//Back Button
		final Button backBtn = new Button();
		backBtn.setText("Back");
		backBtn.getStyleClass().add("backButton");
		grid.add(backBtn, 6, 8);
		
		//Back Button Function
		backBtn.setOnAction(e -> {
			setCurrentScene(Scenes.Menu);
		});
		
		grid.getStyleClass().add("gridHotelDetails");
		
		return grid;
	}
	
	//////////////////////////////////////////////////////////////////
	//Methods for input checking
	//////////////////////////////////////////////////////////////////
	
	private boolean checkDatesAndPersCount(int dayIn, int dayOut, int monthIn, int monthOut, int yearIn, int yearOut, int persCount)
	{
		int foundError = 0;
		String errorMessage = "";
		
		if(persCount < 1 || persCount > 4)
		{
			foundError = 1;
			errorMessage += "Incorrect number of persons!" + System.getProperty("line.separator");
		}
		
		// check for no negative numbers
		if(dayIn < 1 || dayOut < 1 || monthIn < 1 || monthOut < 1 || yearIn < 1 || yearOut < 1)
		{
			foundError = 1;
			errorMessage += "Date values cant be below 1!" + System.getProperty("line.separator");
		}
		// check month value
		if(monthIn > 12 || monthOut > 12)
		{
			foundError = 1;
			errorMessage += "Month can be more than 12!" + System.getProperty("line.separator");
		}
		// check leave dat to be after arrive date
		if(yearOut < yearIn || ((yearIn == yearOut) && monthOut < monthIn) || ((yearIn == yearOut) && (monthIn == monthOut) && dayOut <= dayIn))
		{
			foundError = 1;
			errorMessage += "Leave date must be after arrive date!" + System.getProperty("line.separator");
		}
		// check days number
		if((monthIn == 2 && dayIn > 29) || (monthOut == 2 && dayOut > 29) || dayOut > 31 || dayIn > 31)
		{
			foundError = 1;
			errorMessage += "Day value is too big!" + System.getProperty("line.separator");
		}
		
		if(foundError == 0)
		{
			// check arrive date to not be before current date
			int arriveDate = (yearIn * 100 + monthIn) * 100 + dayIn;
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			Date currentDt = new Date();
			Date arriveDt = null;
			
			try {
				arriveDt = dateFormat.parse(String.valueOf(arriveDate));
			}catch(Exception e) { System.out.println("Error parsing th date"); }
			
			if(arriveDt.before(currentDt))
			{
				foundError = 1;
				errorMessage += "Arrive date cant be before current date!" + System.getProperty("line.separator");
			}
		}
		
		if(foundError == 1)
		{
			alertBox("ERROR","The following errors have occured:",errorMessage,AlertType.ERROR);
			return false;
		}
		
		return true;
	}
	
	private boolean checkUserData(String name, String cnp, String email, String phone, String cardNr, String cvv)
	{
		int foundError = 0;
		String errorMessage = "";
		
		if(name.isEmpty() || cnp.isEmpty() || email.isEmpty() || phone.isEmpty() || cardNr.isEmpty() || cvv.isEmpty())
		{
			foundError = 1;
			errorMessage += "One or more fields are empty!" + System.getProperty("line.separator");
		}
			
		if(!Pattern.compile( "[a-zA-Z ]+" ).matcher( name ).matches())
		{
			foundError = 1;
			errorMessage += "Names can only have letters!" + System.getProperty("line.separator");
		}
		if(cnp.length() != 13 || !Pattern.compile( "[0-9]+" ).matcher( cnp ).matches())
		{
			foundError = 1;
			errorMessage += "Incorrect cnp format!" + System.getProperty("line.separator");
		}
		if(phone.length() != 10 || !Pattern.compile( "[0-9]+" ).matcher( phone ).matches())
		{
			foundError = 1;
			errorMessage += "Incorrect phone number format!" + System.getProperty("line.separator");
		}
		if(!Pattern.compile("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$").matcher(email).matches())
		{
			foundError = 1;
			errorMessage += "Incorrect email format!" + System.getProperty("line.separator");
		}
		if(!Pattern.compile("[0-9]+").matcher(cardNr).matches())
		{
			foundError = 1;
			errorMessage += "Incorrect card number format!" + System.getProperty("line.separator");
		}
		if(cvv.length() != 3 || !Pattern.compile("[0-9]+").matcher(cvv).matches())
		{
			foundError = 1;
			errorMessage += "Incorrect cvv number format!" + System.getProperty("line.separator");
		}
		
		if(foundError == 1)
		{
			alertBox("ERROR","The following errors have occured:",errorMessage,AlertType.ERROR);
			return false;
		}
		return true;
	}
	
	//////////////////////////////////////////////////////////////////
	//Methods for alert message boxes
	//////////////////////////////////////////////////////////////////
	
	private boolean confirmAlert(String title, String message)
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText("");
		alert.setContentText(message);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK)
		{
		    return true;
		}
		else 
		{
		    return false;
		}
	}
	
	private void alertBox(String title, String header, String message, AlertType type)
	{
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	//////////////////////////////////////////////////////////////////
	//Other helping methods
	//////////////////////////////////////////////////////////////////
	
	private String gtTxt(TextField tf)
	{
		return tf.getText().trim();
	}
}