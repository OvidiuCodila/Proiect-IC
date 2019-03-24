package Client;

import java.io.FileInputStream;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
	
	enum Scenes { Menu, MakeReservation, ManageReservation, HotelDetails };
	
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
		
		// assign each scene a stylesheet page
		css = this.getClass().getResource("/styleSheetMenu.css").toExternalForm();
		menuScene.getStylesheets().add(css);
		css = this.getClass().getResource("/styleSheetMakeReservation.css").toExternalForm();
		makeReservationScene.getStylesheets().add(css);
		css = this.getClass().getResource("/styleSheetManageReservation.css").toExternalForm();
		manageReservationScene.getStylesheets().add(css);
		css = this.getClass().getResource("/styleSheetHotelDetails.css").toExternalForm();
		hotelDetails.getStylesheets().add(css);
		
		// set current scene
		setCurrentScene(Scenes.Menu);
		
		// start the client
		client = new Client("Ion" + Math.random() *10);
		client.start();
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
		HBox hboxUp = new HBox();
		HBox hboxDown = new HBox();
		
		hboxUp.getStyleClass().add("hboxUp");
		hboxDown.getStyleClass().add("hboxDown");
		
		pane.setTop(hboxUp);
		pane.setBottom(hboxDown);
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
			System.exit(1);
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
		grid.add(leaveDate, 8, 4, 3, 1);
		
		final Label nrPersons = new Label();
		nrPersons.setText("Places (max 4): ");
		nrPersons.getStyleClass().add("presetLabel");
		grid.add(nrPersons, 11, 4, 3, 1);
		
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
		grid.add(pplNr, 8, 5);
		
		
		//RadioButtons for available options
		
		
		//Buttons
		
		
		//Button Actions
		
		
		return grid;
	}
	
	private GridPane addManageReservationGridPane()
	{
		GridPane grid = new GridPane();
		
		grid.getStyleClass().add("gridManageReserv");
		
		//Description Lables -----------------------------------------//
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
			//
		});
		
		searchBtn.setOnAction(e -> {
			if(!name.getText().trim().equals("") && !code.getText().trim().equals(""))
			{
				statusLabelText.setText("Rezervation Made");
				cnpLabelText.setText("1931022054050");
				phoneLabelText.setText("0771993765");
				emailLabelText.setText("cineva.altcineva@gmail.com");
				dateLabelText.setText("date1 - date2");
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
}