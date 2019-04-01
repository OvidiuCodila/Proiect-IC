package Server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
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
	private ListView<String> eventsList;
	
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
		
		eventsList = new ListView<String>();
		
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
			Platform.exit();
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
		eventsList.getStyleClass().add("eventsList");
		grid.add(eventsList, 3, 3, 5, 3);
		
		//Details field
		final TextArea detailsField = new TextArea();
		detailsField.getStyleClass().add("detailsField");
		detailsField.setText("");
		detailsField.setEditable(false);
		grid.add(detailsField, 11, 3, 4, 1);
		
		//Buttons
		final Button backButton = new Button();
		backButton.setText("Back");
		backButton.getStyleClass().add("Btn");
		grid.add(backButton, 14, 5);
				
		final Button deleteButton = new Button();
		deleteButton.setText("Delete");
		deleteButton.getStyleClass().add("Btn");
		grid.add(deleteButton, 11, 5);
		
		//Event list function
		eventsList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event)
			{
				Event ev;
				/*if((ev = sysUI.checkEventExists(eventsList.getSelectionModel().getSelectedItem())) != null)
				{
					String outText = "";
					
					outText += ev.getDate() + "\n";
					outText += ev.getType() + "\n";
					
					eventDetailsField.setText(outText);
				}*/
			}
		});
		
		//Buttons actions
		backButton.setOnAction(e -> {
			setCurrentScene(Scenes.Menu);
		});
				
		deleteButton.setOnAction(e -> {
			//do nothing yet
		});
		
		return grid;
	}
	
	public void editTextArea(String msg)
	{
		textArea.appendText(msg + "\n");
	}
}
