package Server;

import java.util.Optional;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ServerDesign extends Application
{
	// the window(stage) for the scenes
	private Stage mainWindow;
		
	// the scenes
	private Scene menuScene;
	private Scene manageReservationScene;
	
	enum Scenes { Menu, ManageReservation };
	
	// fields for design
	private TextArea textArea;
	private ListView<String> reservList;
	
	// the server
	private Server server;
	
	
	//////////////////////////////////////////////////////////////////
	//Starting methods
	//////////////////////////////////////////////////////////////////
	
	public static void main(String argv[])
	{
		launch(argv); // calls the start method and creates the window(stage)
	}
	
	public void start(Stage primaryStage) throws Exception
	{
		String css;
		
		// setting the stage and giving it a title
		mainWindow = primaryStage;
		mainWindow.setTitle("Take-a-Room Hotel");
		
		// initiating the list where we show all reservations in the manage scene
		reservList = new ListView<String>();
		
		// instantiate scenes
		menuScene = new Scene(createScene(Scenes.Menu), 1024, 768);
		manageReservationScene = new Scene(createScene(Scenes.ManageReservation), 1024, 768);
		
		// assign each scene a stylesheet page
		css = this.getClass().getResource("/styleSheetMenu.css").toExternalForm();
		menuScene.getStylesheets().add(css);
		css = this.getClass().getResource("/styleSheetManageReservation.css").toExternalForm();
		manageReservationScene.getStylesheets().add(css);
		
		// set current scene
		setCurrentScene(Scenes.Menu);
		
		// start the server backend
		server = new Server(this);
		server.start();
	}
	
	//////////////////////////////////////////////////////////////////
	//Methods for the design and layouts
	//////////////////////////////////////////////////////////////////
	
	private BorderPane createScene(Scenes scene)
	{
		// all the scene will have the same layout
		// the layout is BorderPane
		BorderPane pane = new BorderPane();
		
		// according to what scene it is, the center of the BorderPane is set to a GridPane
		switch(scene)
		{
			case Menu: {
				pane.setCenter(addMenuGridPane());
				break;
			}
			case ManageReservation: {
				pane.setCenter(addManageReservationGridPane());
				break;
			}
			default: {
				System.out.println("Error!! Wrong Scene!!");
			}
		}
		
		// return the layout for the respective scene
		return pane;
	}
	
	private GridPane addMenuGridPane()
	{
		// each scene has a GridPane layout inside the BorderPane
		// the menu grid pane is created here
		GridPane grid = new GridPane();
		grid.getStyleClass().add("gridMenu"); // we add the style to the respective grid pane by that id
		
		// creating the text area where we print the logs file content
		textArea = new TextArea();
		textArea.getStyleClass().add("textArea"); // getting the style frome the stylesheet by the id
		textArea.setText(""); // setting the initial content of the text area to null
		textArea.setEditable(false); // set the text area not editable
		grid.add(textArea, 3, 2, 5, 6); // positioning the text area inside the grid pane
		
		// the exit button
		final Button exitButton = new Button();
		exitButton.setText("Close"); // set the button text
		exitButton.getStyleClass().add("exitButton"); // get the style by its id
		grid.add(exitButton, 11, 7); // position it in the grid
		
		// the manage scene button -> this changes from menu scene to manage scene
		final Button manageReservationsButton = new Button();
		manageReservationsButton.setText("Manage"); // set text
		manageReservationsButton.getStyleClass().add("otherButtons"); // get style
		grid.add(manageReservationsButton, 11, 6); // position
		
		// the action for the exit button
		exitButton.setOnAction(e -> {
			server.stopRunning(); // call the stop function from the server, stopping all the processes and closing the program
		});
		
		// the manage scene button action
		manageReservationsButton.setOnAction(e -> {
			setCurrentScene(Scenes.ManageReservation); // set the current scene to the manage scene
		});
		
		// return the grip pane to place it inside the border pane
		return grid;
	}
	
	private GridPane addManageReservationGridPane()
	{
		// set the grid pane for the manage scene
		GridPane grid = new GridPane();
		grid.getStyleClass().add("gridManageReservation"); // get style by id
		
		// get style the style for the list where we show all the reservations and add it to the grid pane
		reservList.getStyleClass().add("reservList");
		grid.add(reservList, 3, 3, 5, 3);
		
		// search text field creation
		final TextField searchField = new TextField();
		searchField.setPromptText("Enter name/code/room here.."); // set the default text of the text field
		searchField.getStyleClass().add("standardField"); // get style by id
		grid.add(searchField, 3, 6, 5, 1); // position
		
		// details text area to print the details from the reservation we click on in the list
		final TextArea detailsField = new TextArea();
		detailsField.getStyleClass().add("detailsField"); // get style
		detailsField.setText(""); // set default text
		detailsField.setEditable(false); // set it so it cant be edited by standard input
		grid.add(detailsField, 10, 3, 4, 1); // position
		
		// back button creation
		final Button backButton = new Button();
		backButton.setText("Back"); // button text
		backButton.getStyleClass().add("Btn"); // style
		grid.add(backButton, 13, 5); // position
				
		// delete button creation
		final Button deleteButton = new Button();
		deleteButton.setText("Delete");
		deleteButton.getStyleClass().add("Btn");
		grid.add(deleteButton, 10, 5);
		
		// search button creation
		final Button searchButton = new Button();
		searchButton.setText("Search");
		searchButton.getStyleClass().add("Btn");
		grid.add(searchButton, 10, 6);
		
		// function for the reservation list -> deciding what happens when the list is clicked
		reservList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public synchronized void handle(MouseEvent event)
			{
				String txt;
				if(!(txt = server.getReservationDetails(reservList.getSelectionModel().getSelectedItem())).equals("")) // verify that the clicked item in list is a reservation
				{	
					detailsField.setText(txt); // if yes, the details from that reservation are printed in the details text area
				}
				else
					detailsField.setText("No reservation selected"); // if no, an according message is printed
			}
		});
		
		// back button action
		backButton.setOnAction(e -> {
			detailsField.clear(); // clears the details text area
			reservList.getItems().clear(); // clears the reservations list items
			setCurrentScene(Scenes.Menu); // sets the current scene to the menu one
		});
		
		// delete button action 
		deleteButton.setOnAction(e -> {
			if(confirmAlert("Confirm action: ", "Are you sure you want to delete the reservation?")) // calling the confirmation pop-up
			{
				// if the OK button is clicked
				if(server.delRes(reservList.getSelectionModel().getSelectedItem()) == 1) // the reservation is deleted and a message is printed accordingly to that
					alertBox("SUCCES","","Reservation deleted succesfully!",AlertType.CONFIRMATION);
				else alertBox("ERROR","","Action failed! Reservation not deleted!",AlertType.ERROR);
				
				server.getReservList(reservList); // the the reservation list is refreshed
				detailsField.clear(); // and the details field is cleared
			}
			// is the CANCEL button is pressed. nothing happens
		});
		
		// search button action
		searchButton.setOnAction(e -> {
			String src;
			if(!(src = searchField.getText().trim()).isEmpty()) // check if the search text field is not empty when the button is pressed
			{
				// and a pattern matching is done on the content of the text field
				if(Pattern.compile( "[0-9]+" ).matcher( src ).matches() && src.length() == 3) // if its a room number
					server.searchList(reservList,1,src); //  1 means for rooms
				else if(Pattern.compile( "[0-9]+" ).matcher( src ).matches() && src.length() == 6) // if its a code
					server.searchList(reservList,2,src); // 2 means for code
				else if(Pattern.compile( "[a-zA-Z ]+" ).matcher( src ).matches()) // if its a name
					server.searchList(reservList,3,src); // 3 means for name
				else alertBox("ERROR","Following errors have occured: ","Incorrect search or the search failed!",AlertType.ERROR); // if its not any of the above an error pop-up appears
				// if its one of the above the reservations list is updated with the content that matched the search
			}
			else server.getReservList(reservList); // if the text field is empty the list is restored to its default with all the reservations
			
			detailsField.clear(); // the details text area is cleared
		});
		
		return grid;
	}
	
	//////////////////////////////////////////////////////////////////
	//Methods for alert message boxes
	//////////////////////////////////////////////////////////////////

	private boolean confirmAlert(String title, String message)
	{
		// create the alert pop-up for confirmation
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title); // set the title
		alert.setHeaderText(""); // set the header
		alert.setContentText(message); // set the message

		Optional<ButtonType> result = alert.showAndWait(); // create the buttons and wait for one to be pressed
		if (result.get() == ButtonType.OK)
		{
			return true; // if its the OK button return true
		}
		else 
		{
			return false; // if its the CANCEL button return false
		}
	}

	private void alertBox(String title, String header, String message, AlertType type)
	{
		// create the allert pop-up for errors
		Alert alert = new Alert(type);
		alert.setTitle(title); // set the title
		alert.setHeaderText(header); // set the header
		alert.setContentText(message); // set the messahe
		alert.showAndWait(); // show the pop-up
	}
	
	//////////////////////////////////////////////////////////////////
	//Other methods
	//////////////////////////////////////////////////////////////////
	
	private void setCurrentScene(Scenes scene)
	{
		// according to what scene is it, we set that scene in the stage(window)
		switch(scene)
		{
			case Menu : {
				mainWindow.setScene(menuScene);
				break;
			}
			
			case ManageReservation: {
				// when the manage scene is set the list with reservation is created again from scratch
				server.getReservList(reservList);
				mainWindow.setScene(manageReservationScene);
				break;
			}
			
			default: {
				System.out.println("Error! Wrong scene!!");
				break;
			}
		}
		
		// we show the stage updated with the new scene in it
		mainWindow.show();
	}
	
	public synchronized void editTextArea(String msg)
	{
		javafx.application.Platform.runLater( () -> textArea.appendText(msg + "\n") );
		// add the logs actions to the text area on the menu scene
		// use the runLater method to append the text because otherwise the app might fail synchronizing the threads
	}
}
