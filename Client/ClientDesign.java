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
		launch(argv); // staring method that creates the stage(window) and calls the start method below
	}
	
	public void start(Stage primaryStage) throws Exception
	{
		String css;
		
		// setting the stage and giving it a title
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
			client.startClient(); // starting the client
		}
		catch(MyConnectionException e)
		{
			// catch possible error and give an error message with a pop-up box
			alertBox("ERROR","We're sorry!",e.getMessage(),AlertType.ERROR);
			client.closeConnection(); // if error occured, close the client
		}
	}
	
	//////////////////////////////////////////////////////////////////
	//Method for setting the scene
	//////////////////////////////////////////////////////////////////
	
	private void setCurrentScene(Scenes scene)
	{
		// sets the current scene to be displayed in the window(stage) depending on the scene that is gets
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
		
		mainWindow.show(); // displaying the updated window with the new scene
	}
	
	//////////////////////////////////////////////////////////////////
	//Methods for creating the design to each scene along with designing the elements from the scenes
	//////////////////////////////////////////////////////////////////
	
	private BorderPane createScene(Scenes scene)
	{
		// each scene has as a layout a BorderPane
		BorderPane pane = new BorderPane();
		
		// to each BorderPane, depending on the scene, we add a particular GridPane to the center
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
		
		return pane; // returning the created BorderPane
	}
	
	private GridPane addMenuGridPane()
	{
		// creating the grid pane for the menu scene
		GridPane grid = new GridPane();
		grid.getStyleClass().add("gridMenu"); // get style by the specified id
		
		// makeReservation button design
		final Button makeRezservBtn = new Button();
		makeRezservBtn.setText("Make Reservation"); // set text
		makeRezservBtn.getStyleClass().add("allOtherButtons"); // set style by id
		grid.add(makeRezservBtn, 8, 18, 1, 1); // position on grid pane
		
		// checkReservation button design
		final Button checkRezservBtn = new Button();
		checkRezservBtn.setText("Check Reservation"); // set text
		checkRezservBtn.getStyleClass().add("allOtherButtons"); // set style
		grid.add(checkRezservBtn, 8, 19, 1, 1); // position
		
		// hotelDetails button design
		final Button hotelDetailsBtn = new Button();
		hotelDetailsBtn.setText("Hotel Details"); // set text
		hotelDetailsBtn.getStyleClass().add("allOtherButtons"); // set style
		grid.add(hotelDetailsBtn, 12, 18, 1, 1); // position
		
		
		// exit button design
		final Button exitButton = new Button();
		exitButton.setText("Exit"); // set text
		exitButton.getStyleClass().add("exitButton"); // set style
		grid.add(exitButton, 12, 19, 1, 1); // position
		
		
		// exit button action
		exitButton.setOnAction(e -> {
			client.clientExitAction(); // call the closing method for the client ( application )
		});
		
		// hotelDetails button action
		hotelDetailsBtn.setOnAction(e -> {
			setCurrentScene(Scenes.HotelDetails); // change the current scene to the hotel details one
		});
		
		// makeReservation button method
		makeRezservBtn.setOnAction(e -> {
			setCurrentScene(Scenes.MakeReservation); // change the current scene to the make reservation scene
		});
		
		// checkReservation button method
		checkRezservBtn.setOnAction(e -> {
			setCurrentScene(Scenes.ManageReservation); // change the current scene to the manage reservation scene
		});
		
		return grid; // returning the grid pane to be placed in the Border Pane
	}
	
	private GridPane addMakeReservationGridPane()
	{
		GridPane grid = new GridPane();
		
		grid.getStyleClass().add("gridMakeReserv"); // get style
		
		//Description Labels
		
		// arrive date label
		final Label arriveDate = new Label();
		arriveDate.setText("Arriving Date: "); // set text
		arriveDate.getStyleClass().add("presetLabel"); // get style
		grid.add(arriveDate, 5, 4, 3, 1); // position
		
		// leave date label
		final Label leaveDate = new Label();
		leaveDate.setText("Leaving Date: "); // set text
		leaveDate.getStyleClass().add("presetLabel"); // get style
		grid.add(leaveDate, 5, 7, 3, 1); // position
		
		// number of persons label
		final Label nrPersons = new Label();
		nrPersons.setText("Places (max 4): "); // set text
		nrPersons.getStyleClass().add("presetLabel"); // get style
		grid.add(nrPersons, 9, 4, 3, 1); // position
		
		//TextFields for input
		
		// arrive day text field
		final TextField arriveDay = new TextField();
		arriveDay.setPromptText("DD"); // set default text
		arriveDay.getStyleClass().add("dayTextField"); // set style
		grid.add(arriveDay, 5, 5); // position
		
		// arrive month text field
		final TextField arriveMonth = new TextField();
		arriveMonth.setPromptText("MM");
		arriveMonth.getStyleClass().add("monthTextField");
		grid.add(arriveMonth, 6, 5);
		
		// arrive year text field
		final TextField arriveYear = new TextField();
		arriveYear.setPromptText("YYYY");
		arriveYear.getStyleClass().add("yearTextField");
		grid.add(arriveYear, 7, 5);
		
		// number of people text field
		final TextField pplNr = new TextField();
		pplNr.setPromptText("Nr");
		pplNr.getStyleClass().add("pplNrTextField");
		grid.add(pplNr, 9, 5);
		
		// leave day text field
		final TextField leaveDay = new TextField();
		leaveDay.setPromptText("DD");
		leaveDay.getStyleClass().add("dayTextField");
		grid.add(leaveDay, 5, 8);
		
		// leave month text field
		final TextField leaveMonth = new TextField();
		leaveMonth.setPromptText("MM");
		leaveMonth.getStyleClass().add("monthTextField");
		grid.add(leaveMonth, 6, 8);
		
		// leave year text field
		final TextField leaveYear = new TextField();
		leaveYear.setPromptText("YYYY");
		leaveYear.getStyleClass().add("yearTextField");
		grid.add(leaveYear, 7, 8);
		
		
		//Search Result label
		final Label resultLabel = new Label();
		resultLabel.setText(" "); // default text is empty
		resultLabel.getStyleClass().add("resultLabel"); // style
		grid.add(resultLabel, 7, 10, 3, 1); // position
		
		
		//Buttons
		
		// search button
		final Button searchButton = new Button();
		searchButton.setText("Search");
		searchButton.getStyleClass().add("otherButtons");
		grid.add(searchButton, 7, 14);
		
		// back button
		final Button backButton = new Button();
		backButton.setText("Back");
		backButton.getStyleClass().add("backButton");
		grid.add(backButton, 8, 14);
		
		// make reservation button 
		final Button reserveButton = new Button();
		reserveButton.setText("Reserve");
		reserveButton.getStyleClass().add("otherButtons");
		reserveButton.setDisable(true); // by default it is inactive until the inserted period is available
		grid.add(reserveButton, 9, 14);
		
		//Button Actions
		
		//search button action
		searchButton.setOnAction(e -> {
			String dateIn = gtTxt(arriveDay) + "." + gtTxt(arriveMonth) + "." + gtTxt(arriveYear); // get date in with format: day.month.year
			String dateOut = gtTxt(leaveDay) + "." + gtTxt(leaveMonth) + "." + gtTxt(leaveYear); // get date out with format: day.month.year
			
			try 
			{
				// convert all the field to int for an easier way to verify them
				int persCount = Integer.parseInt(gtTxt(pplNr));
				int dayIn = Integer.parseInt(gtTxt(arriveDay));
				int dayOut = Integer.parseInt(gtTxt(leaveDay));
				int monthIn = Integer.parseInt(gtTxt(arriveMonth));
				int monthOut = Integer.parseInt(gtTxt(leaveMonth));
				int yearIn = Integer.parseInt(gtTxt(arriveYear));
				int yearOut = Integer.parseInt(gtTxt(leaveYear));
				
				if(checkDatesAndPersCount(dayIn,dayOut,monthIn,monthOut,yearIn,yearOut,persCount)) // check if the input is according to the "rules"
				{
					int rez = client.searchRoom(dateIn, dateOut, persCount); // if yes, check if the period is available
					if( rez == -1 ) 
					{
						// if not
						reserveButton.setDisable(true); // keep the reservation button disables
						resultLabel.setTextFill(Color.RED); // set the result label text color to red
						resultLabel.setText("Nu exista locuri disponibile! \n \tNe pare rau!"); // set the label text
					}
					else
					{
						// if yes
						reserveButton.setDisable(false); // enable the reservation button
						resultLabel.setTextFill(Color.GREEN); // set the result label text color to green
						resultLabel.setText("Avem locuri disponibile. Total: " + rez + "$"); // set the label text
					}
				}
			}
			// catch the exceptions that might appear
			catch(NumberFormatException err)
			{
				alertBox("ERROR","The following errors have occured:","One or more fields is not a number!",AlertType.ERROR);
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
		
		// back button action
		backButton.setOnAction(e -> {
			resultLabel.setText(""); // set the result label to empty
			arriveYear.clear(); arriveMonth.clear(); arriveDay.clear();
			leaveYear.clear(); leaveMonth.clear(); leaveDay.clear(); // clear all the text fields
			pplNr.clear();
			setCurrentScene(Scenes.Menu); // set the current scene to the menu
		});
		
		// reserve button action 
		reserveButton.setOnAction(e -> {
			resultLabel.setText(""); // set the result label to empty
			setCurrentScene(Scenes.ConfirmReservation); // set the current scene to the confirm reservation
		});
		
		return grid;
	}
	
	private GridPane addConfirmReservationGridPane()
	{
		GridPane grid = new GridPane();
		grid.getStyleClass().add("gridConfirmReserv");
		
		//Description labels
		
		//name label
		final Label name = new Label();
		name.setText("Name: ");
		name.getStyleClass().add("standardLabel");
		grid.add(name, 5, 5);
		
		//cnp label
		final Label cnp = new Label();
		cnp.setText("CNP: ");
		cnp.getStyleClass().add("standardLabel");
		grid.add(cnp, 5, 6);
		
		// email label
		final Label email = new Label();
		email.setText("Email: ");
		email.getStyleClass().add("standardLabel");
		grid.add(email, 5, 7);
		
		// phone label
		final Label phone = new Label();
		phone.setText("Phone: ");
		phone.getStyleClass().add("standardLabel");
		grid.add(phone, 5, 8);
		
		// address label
		final Label address = new Label();
		address.setText("Address: ");
		address.getStyleClass().add("standardLabel");
		grid.add(address, 5, 9);
		
		// card number label
		final Label cardNr = new Label();
		cardNr.setText("Card Nr: ");
		cardNr.getStyleClass().add("standardLabel");
		grid.add(cardNr, 5, 10);
		
		// cvv label
		final Label cvv = new Label();
		cvv.setText("CVV: ");
		cvv.getStyleClass().add("standardLabel");
		grid.add(cvv, 5, 11);
		
		//Text Fields
		
		// name text field
		final TextField nameField = new TextField();
		nameField.setPromptText("Enter name here");
		nameField.getStyleClass().add("standardField");
		grid.add(nameField, 6, 5, 2, 1);
		
		// cnp text field
		final TextField cnpField = new TextField();
		cnpField.setPromptText("Enter cnp here");
		cnpField.getStyleClass().add("standardField");
		grid.add(cnpField, 6, 6, 2, 1);
		
		// email text field
		final TextField emailField = new TextField();
		emailField.setPromptText("Enter email here");
		emailField.getStyleClass().add("standardField");
		grid.add(emailField, 6, 7, 2, 1);
		
		// phone text field
		final TextField phoneField = new TextField();
		phoneField.setPromptText("Enter phone here");
		phoneField.getStyleClass().add("standardField");
		grid.add(phoneField, 6, 8, 2, 1);
		
		// address text field
		final TextField addressField = new TextField();
		addressField.setPromptText("Enter address here");
		addressField.getStyleClass().add("standardField");
		grid.add(addressField, 6, 9, 2, 1);
		
		// card number text field
		final TextField cardNrField = new TextField();
		cardNrField.setPromptText("Enter card number here");
		cardNrField.getStyleClass().add("standardField");
		grid.add(cardNrField, 6, 10, 2, 1);
		
		// cvv text field
		final TextField cvvField = new TextField();
		cvvField.setPromptText("Enter cvv here");
		cvvField.getStyleClass().add("standardField");
		grid.add(cvvField, 6, 11, 2, 1);
		
		//Buttons
		
		// back button
		final Button backButton = new Button();
		backButton.setText("Back");
		backButton.getStyleClass().add("backButton");
		grid.add(backButton, 6, 12);
		
		// reserve button
		final Button reserveButton = new Button();
		reserveButton.setText("Reserve");
		reserveButton.getStyleClass().add("reserveButton");
		grid.add(reserveButton, 7, 12);
		
		//Buttons actions
		
		// back button action
		backButton.setOnAction(e -> {
			setCurrentScene(Scenes.MakeReservation); // set current scene to the make reservation scene
		});
		
		// reserve button action
		reserveButton.setOnAction(e -> {
			// get all the text field inputs
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
				if(checkUserData(nameTxt,cnpTxt,emailTxt,phoneTxt, cardNrTxt,cvvTxt)) // verify the inpus respects the "rules"
				{
					if(confirmAlert("Confirm action","Are you sure you want to submit the reservation?")) // confirm pop-up appears
					{
						// if OK button is clicked
						int rez = client.makeReservation(nameTxt,cnpTxt,emailTxt,phoneTxt,addressTxt,cardNrTxt,cvvTxt); // call the make reservation method
						if(rez == -1)
						{
							alertBox("ERROR", "Following errors have occured:", "Reservation failed! Please try again",AlertType.ERROR); // if it fails it has an error pop-up
						}
						else
						{
							// on success
							System.out.println(rez);
							alertBox("SUCCES","Reservation code: ","-> " + rez,AlertType.INFORMATION); // it return the reservation code
							nameField.clear(); cnpField.clear(); emailField.clear(); phoneField.clear(); addressField.clear();
							cardNrField.clear(); cvvField.clear(); // clears all the fields
							setCurrentScene(Scenes.Menu); // sets the scene to the menu
						}
					}
				}
			}
			// error catching
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
		
		// name label
		final Label nameLabel = new Label();
		nameLabel.setText("Name: ");
		nameLabel.getStyleClass().add("presetLabel");
		grid.add(nameLabel, 6, 4);
		
		// code label
		final Label codeLabel = new Label();
		codeLabel.setText("Code: ");
		codeLabel.getStyleClass().add("presetLabel");
		grid.add(codeLabel, 6, 5);
		
		// status label
		final Label statusLabel = new Label();
		statusLabel.setText("Status: ");
		statusLabel.getStyleClass().add("presetLabel");
		grid.add(statusLabel, 6, 7);
		
		// cnp label
		final Label cnpLabel = new Label();
		cnpLabel.setText("Cnp: ");
		cnpLabel.getStyleClass().add("presetLabel");
		grid.add(cnpLabel, 6, 8);
		
		// phone label
		final Label phoneLabel = new Label();
		phoneLabel.setText("Phone: ");
		phoneLabel.getStyleClass().add("presetLabel");
		grid.add(phoneLabel, 6, 9);
		
		// email label
		final Label emailLabel = new Label();
		emailLabel.setText("Email: ");
		emailLabel.getStyleClass().add("presetLabel");
		grid.add(emailLabel, 6, 10);
		
		// period label (dateIn - dateOut)
		final Label dateLabel = new Label();
		dateLabel.setText("Date");
		dateLabel.getStyleClass().add("presetLabel");
		grid.add(dateLabel, 6, 11);
		
		//Details Labels -----------------------------------------//
		
		// status result label
		final Label statusLabelText = new Label();
		statusLabelText.setText("");
		statusLabelText.getStyleClass().add("textLabel");
		grid.add(statusLabelText, 7, 7, 3, 1);
		
		// cnp result label
		final Label cnpLabelText = new Label();
		cnpLabelText.setText("");
		cnpLabelText.getStyleClass().add("textLabel");
		grid.add(cnpLabelText, 7, 8, 3, 1);
		
		// phone result label
		final Label phoneLabelText = new Label();
		phoneLabelText.setText("");
		phoneLabelText.getStyleClass().add("textLabel");
		grid.add(phoneLabelText, 7, 9, 3, 1);
		
		// email result label
		final Label emailLabelText = new Label();
		emailLabelText.setText("");
		emailLabelText.getStyleClass().add("textLabel");
		grid.add(emailLabelText, 7, 10, 3, 1);
		
		// period result label
		final Label dateLabelText = new Label();
		dateLabelText.setText("");
		dateLabelText.getStyleClass().add("textLabel");
		grid.add(dateLabelText, 7, 11, 3, 1);
		
		//Buttons -----------------------------------------//
		
		// back button
		final Button backBtn = new Button();
		backBtn.setText("Back");
		backBtn.getStyleClass().add("otherButtons");
		grid.add(backBtn, 7, 12);
		
		// delete button
		final Button deleteBtn = new Button();
		deleteBtn.setText("Delete");
		deleteBtn.getStyleClass().add("otherButtons");
		deleteBtn.setDisable(true); // set to disabled until an available search has been introduced
		grid.add(deleteBtn, 8, 12);
		
		// search button
		final Button searchBtn = new Button();
		searchBtn.setText("Search");
		searchBtn.getStyleClass().add("searchBtn");
		grid.add(searchBtn, 10, 4);
		
		//TextFields for text input -----------------------------------------//
		
		// name text field
		final TextField name = new TextField();
		name.setPromptText("Enter name here...");
		name.getStyleClass().add("textField");
		grid.add(name, 7, 4, 3, 1);
		
		// code text field
		final TextField code = new TextField();
		code.setPromptText("Enter rezervation code here...");
		code.getStyleClass().add("textField");
		grid.add(code, 7, 5, 3, 1);
		
		//Buttons Actions -----------------------------------------//
		
		// back button action
		backBtn.setOnAction(e -> {
			name.clear();
			code.clear(); // clear the text field
			statusLabelText.setText("");
			cnpLabelText.setText("");
			phoneLabelText.setText("");
			emailLabelText.setText("");
			dateLabelText.setText(""); // clear the labels
			setCurrentScene(Scenes.Menu); // setting the current scene to the menu
		});
		
		// delete button action
		deleteBtn.setOnAction(e -> {
			if(confirmAlert("Confirm Action:", "Are you sure you want to delete your reservation?!")) // confirm pop-up appears
			{
				// if the OK button is pressed
				try
				{
					int result = client.deleteReservation(name.getText().trim(), code.getText().trim()); // the reservation is deleted
					if(result == 1)
					{
						alertBox("SUCCES", "", "Reservation deleted succesfully!",AlertType.CONFIRMATION); // on success a corresponding pop-up appears
						name.clear(); code.clear();
						
						statusLabelText.setText("");
						cnpLabelText.setText("");
						phoneLabelText.setText("");
						emailLabelText.setText("");
						dateLabelText.setText(""); // the labels are cleared
						
						setCurrentScene(Scenes.Menu); // scene is set to menu
					}
					else
					{
						alertBox("ERROR!", "Following errors have occured:","Failed to delete reservation! Please try again",AlertType.ERROR); // on error a corresponding pop-up appears
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
		
		// search button action
		searchBtn.setOnAction(e -> {
			if(!name.getText().trim().equals("") && !code.getText().trim().equals("")) // checks the fields so they are not empty
			{
				try
				{
					String details = client.checkResrvation(name.getText().trim(), code.getText().trim()); // check the reservation exists
					String[] detailsParts = details.split("/");
					
					statusLabelText.setText("");
					cnpLabelText.setText("");
					phoneLabelText.setText("");
					emailLabelText.setText("");
					dateLabelText.setText(""); // clears the labels
					
					statusLabelText.setText(detailsParts[0]);
					if(detailsParts.length > 1)
					{
						// if the reservation was found, the labels are completed accordingly
						cnpLabelText.setText(detailsParts[1]);
						phoneLabelText.setText(detailsParts[3]);
						emailLabelText.setText(detailsParts[2]);
						dateLabelText.setText(detailsParts[4]);
						
						deleteBtn.setDisable(false); // enables the button
					}
					else
					{
						alertBox("ERROR","","Reservation not found! Please try again",AlertType.ERROR); // else an error pop-up appears
						deleteBtn.setDisable(true); // disables the button
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
			else
			{
				alertBox("ERROR","The following errors have occured:","Please fill in all the fields!",AlertType.ERROR); // if at least one field is empty an error pop-up appears
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
			// getting the images from the folder
			Image img1 = new Image(new FileInputStream("Resources/hotelRoom2.jpg"));
			Image img2 = new Image(new FileInputStream("Resources/hotelRoom3.jpg"));
			Image img3 = new Image(new FileInputStream("Resources/hotelRoom4.jpg"));
			Image img4 = new Image(new FileInputStream("Resources/hotelRestaurant.jpg"));
			Image img5 = new Image(new FileInputStream("Resources/whiteBar.jpg"));
			
			// setting the images in the image view fields
			ImageView hotelRoom2 = new ImageView(img1);
			ImageView hotelRoom3 = new ImageView(img2);
			ImageView hotelRoom4 = new ImageView(img3);
			ImageView hotelRestaurant = new ImageView(img4);
			ImageView whiteBar = new ImageView(img5);
			
			// setting the images dimensions
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
			
			// adding the images to the grid pane
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
		
		// text label for each image
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
		
		// text labels for other options
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
		
		// back button
		final Button backBtn = new Button();
		backBtn.setText("Back");
		backBtn.getStyleClass().add("backButton");
		grid.add(backBtn, 6, 8);
		
		// back button function
		backBtn.setOnAction(e -> {
			setCurrentScene(Scenes.Menu); // set the current scene to the menu
		});
		
		grid.getStyleClass().add("gridHotelDetails");
		
		return grid;
	}
	
	//////////////////////////////////////////////////////////////////
	//Methods for input checking
	//////////////////////////////////////////////////////////////////
	
	private boolean checkDatesAndPersCount(int dayIn, int dayOut, int monthIn, int monthOut, int yearIn, int yearOut, int persCount)
	{
		int foundError = 0; // foundError is set to 0 initially and if an error is found it becomes 1
		String errorMessage = ""; // in errorMessage we add all the error message that appear
		
		if(persCount < 1 || persCount > 4) // check the person count to be more then 0 and less than 4
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
			int arriveDate = (yearIn * 100 + monthIn) * 100 + dayIn; // get the date as an int with the format: YearMonthDay
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			Date currentDt = new Date();
			Date arriveDt = null;
			
			try {
				arriveDt = dateFormat.parse(String.valueOf(arriveDate)); // convert the date to a date object with the specified format
			}catch(Exception e) { System.out.println("Error parsing th date"); }
			
			if(arriveDt.before(currentDt))
			{
				foundError = 1;
				errorMessage += "Arrive date cant be before current date!" + System.getProperty("line.separator");
			}
		}
		
		if(foundError == 1)
		{
			alertBox("ERROR","The following errors have occured:",errorMessage,AlertType.ERROR); // if an error has been encountered, the pop-up appears with it (or them if there are more)
			return false;
		}
		
		return true;
	}
	
	private boolean checkUserData(String name, String cnp, String email, String phone, String cardNr, String cvv)
	{
		int foundError = 0;
		String errorMessage = "";
		
		if(name.isEmpty() || cnp.isEmpty() || email.isEmpty() || phone.isEmpty() || cardNr.isEmpty() || cvv.isEmpty()) // check the fields so they are not empty
		{
			foundError = 1;
			errorMessage += "One or more fields are empty!" + System.getProperty("line.separator");
		}
			
		if(!Pattern.compile( "[a-zA-Z ]+" ).matcher( name ).matches()) // pattern match so the name has only letters
		{
			foundError = 1;
			errorMessage += "Names can only have letters!" + System.getProperty("line.separator");
		}
		if(cnp.length() != 13 || !Pattern.compile( "[0-9]+" ).matcher( cnp ).matches()) // pattern match the cnp to be 13 long and only digits
		{
			foundError = 1;
			errorMessage += "Incorrect cnp format!" + System.getProperty("line.separator");
		}
		if(phone.length() != 10 || !Pattern.compile( "[0-9]+" ).matcher( phone ).matches()) // pattern match phone number to be 10 long with only digits
		{
			foundError = 1;
			errorMessage += "Incorrect phone number format!" + System.getProperty("line.separator");
		}
		if(!Pattern.compile("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$").matcher(email).matches()) // email pattern match
		{
			foundError = 1;
			errorMessage += "Incorrect email format!" + System.getProperty("line.separator");
		}
		if(!Pattern.compile("[0-9]+").matcher(cardNr).matches()) // pattern match car number to be only digits
		{
			foundError = 1;
			errorMessage += "Incorrect card number format!" + System.getProperty("line.separator");
		}
		if(cvv.length() != 3 || !Pattern.compile("[0-9]+").matcher(cvv).matches()) // pattern match cvv to be 3 long with digits only
		{
			foundError = 1;
			errorMessage += "Incorrect cvv number format!" + System.getProperty("line.separator");
		}
		
		if(foundError == 1)
		{
			alertBox("ERROR","The following errors have occured:",errorMessage,AlertType.ERROR); // if error/errors were found the pop-up appears
			return false;
		}
		return true;
	}
	
	//////////////////////////////////////////////////////////////////
	//Methods for alert message boxes
	//////////////////////////////////////////////////////////////////
	
	private boolean confirmAlert(String title, String message)
	{
		Alert alert = new Alert(AlertType.CONFIRMATION); // confirmation pop-up initialization
		alert.setTitle(title); // setting the title
		alert.setHeaderText(""); // setting the header to null
		alert.setContentText(message); // setting the message

		Optional<ButtonType> result = alert.showAndWait(); // creating the buttons and waiting for them to be pressed
		if (result.get() == ButtonType.OK)
		{
			// if OK button pressed
		    return true;
		}
		else 
		{
			// if CANCEL button pressed
		    return false;
		}
	}
	
	private void alertBox(String title, String header, String message, AlertType type)
	{
		// creating the error pop-up
		Alert alert = new Alert(type);
		alert.setTitle(title); // set title
		alert.setHeaderText(header); // set header
		alert.setContentText(message); // set message
		alert.showAndWait(); // show it
	}
	
	//////////////////////////////////////////////////////////////////
	//Other helping methods
	//////////////////////////////////////////////////////////////////
	
	private String gtTxt(TextField tf)
	{
		return tf.getText().trim(); // get the text from the specified text field
	}
}