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
	// the window for the scenes
	private Stage mainWindow;
		
	// the scenes
	private Scene menuScene;
	private Scene manageReservationScene;
	
	//Fields for design
	private TextArea textArea;
	private ListView<String> reservList;
	
	//Scenes
	enum Scenes { Menu, ManageReservation };
	
	//Server
	private Server server;
	
	
	public static void main(String argv[])
	{
		launch(argv);
	}
	
	public void start(Stage primaryStage) throws Exception
	{
		String css;
		mainWindow = primaryStage;
		mainWindow.setTitle("Take-a-Room Hotel");
		
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
		
		// start the client
		server = new Server(this);
		server.start();
	}
	
	private void setCurrentScene(Scenes scene)
	{
		switch(scene)
		{
			case Menu : {
				mainWindow.setScene(menuScene);
				break;
			}
			
			case ManageReservation: {
				server.getReservList(reservList);
				mainWindow.setScene(manageReservationScene);
				break;
			}
			
			default: {
				System.out.println("Error! Wrong scene!!");
				break;
			}
		}
		
		mainWindow.show();
	}
	
	private BorderPane createScene(Scenes scene)
	{
		BorderPane pane = new BorderPane();
		
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
		
		return pane;
	}
	
	private GridPane addMenuGridPane()
	{
		GridPane grid = new GridPane();
		grid.getStyleClass().add("gridMenu");
		
		//Text area for the server actions and status
		textArea = new TextArea();
		textArea.getStyleClass().add("textArea");
		textArea.setText("");
		textArea.setEditable(false);
		grid.add(textArea, 3, 2, 5, 6);
		
		//Buttons
		final Button exitButton = new Button();
		exitButton.setText("Close");
		exitButton.getStyleClass().add("exitButton");
		grid.add(exitButton, 11, 7);
		
		final Button manageReservationsButton = new Button();
		manageReservationsButton.setText("Manage");
		manageReservationsButton.getStyleClass().add("otherButtons");
		grid.add(manageReservationsButton, 11, 6);
		
		//Buttons actions
		exitButton.setOnAction(e -> {
			server.stopRunning();
		});
		
		manageReservationsButton.setOnAction(e -> {
			setCurrentScene(Scenes.ManageReservation);
		});
		
		return grid;
	}
	
	private GridPane addManageReservationGridPane()
	{
		GridPane grid = new GridPane();
		grid.getStyleClass().add("gridManageReservation");
		
		//Events list
		reservList.getStyleClass().add("reservList");
		grid.add(reservList, 3, 3, 5, 3);
		
		//Search text field
		final TextField searchField = new TextField();
		searchField.setPromptText("Enter name/code/room here..");
		searchField.getStyleClass().add("standardField");
		grid.add(searchField, 3, 6, 5, 1);
		
		//Details field
		final TextArea detailsField = new TextArea();
		detailsField.getStyleClass().add("detailsField");
		detailsField.setText("");
		detailsField.setEditable(false);
		grid.add(detailsField, 10, 3, 4, 1);
		
		//Buttons
		final Button backButton = new Button();
		backButton.setText("Back");
		backButton.getStyleClass().add("Btn");
		grid.add(backButton, 13, 5);
				
		final Button deleteButton = new Button();
		deleteButton.setText("Delete");
		deleteButton.getStyleClass().add("Btn");
		grid.add(deleteButton, 10, 5);
		
		final Button searchButton = new Button();
		searchButton.setText("Search");
		searchButton.getStyleClass().add("Btn");
		grid.add(searchButton, 10, 6);
		
		//Event list function
		reservList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public synchronized void handle(MouseEvent event)
			{
				String txt;
				if(!(txt = server.getReservationDetails(reservList.getSelectionModel().getSelectedItem())).equals(""))
				{	
					detailsField.setText(txt);
				}
				else
					detailsField.setText("No reservation selected");
			}
		});
		
		//Buttons actions
		backButton.setOnAction(e -> {
			detailsField.clear();
			reservList.getItems().clear();
			setCurrentScene(Scenes.Menu);
		});
				
		deleteButton.setOnAction(e -> {
			if(confirmAlert("Confirm action: ", "Are you sure you want to delete the reservation?"))
			{
				if(server.delRes(reservList.getSelectionModel().getSelectedItem()) == 1)
					alertBox("SUCCES","","Reservation deleted succesfully!",AlertType.CONFIRMATION);
				else alertBox("ERROR","","Action failed! Reservation not deleted!",AlertType.ERROR);
				
				server.getReservList(reservList);
				detailsField.clear();
			}
		});
		
		searchButton.setOnAction(e -> {
			String src;
			if(!(src = searchField.getText().trim()).isEmpty())
			{
				if(Pattern.compile( "[0-9]+" ).matcher( src ).matches() && src.length() == 3)
					server.searchList(reservList,1,src); //  1 means for rooms
				else if(Pattern.compile( "[0-9]+" ).matcher( src ).matches() && src.length() == 6)
					server.searchList(reservList,2,src); // 2 means for code
				else if(Pattern.compile( "[a-zA-Z ]+" ).matcher( src ).matches())
					server.searchList(reservList,3,src); // 3 means for name
				else alertBox("ERROR","Following errors have occured: ","Incorrect search or the search failed!",AlertType.ERROR);
			}
			else server.getReservList(reservList);
			
			detailsField.clear();
		});
		
		return grid;
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
	//Other methods
	//////////////////////////////////////////////////////////////////
	
	public synchronized void editTextArea(String msg)
	{
		javafx.application.Platform.runLater( () -> textArea.appendText(msg + "\n") );
		//textArea.appendText(msg + "\n");
	}
}
