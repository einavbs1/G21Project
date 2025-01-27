package librarianControllers;

import java.util.HashMap;

import client.ChatClient;
import client.ClientUI;
import common.LibrarianOptions;
import common.SubscriberOptions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import mainControllers.ConnectionSetupController;

/**
 * This class is controller of the librarian menu (after librarian has logged in).
 */
public class LibrarianMenuController {

	@FXML
	private Button btnExit = null;
	@FXML
	private Button btnLogOut = null;
	@FXML
	private Button btnNewSubscriber = null;
	@FXML
	private Button btnViewAllSubscribers = null;
	@FXML
	private Button btnUpdateSubscriberData = null;
	@FXML
	private Button btnBorrowBooks = null;
	@FXML
	private Button btnShowCurrentBorrowBooks = null;
	@FXML
	private Button btnReturnBook = null;
	@FXML
	private Button btnViewNotifications = null;
	@FXML
	private Button btnGenerateReports = null;
	@FXML
	private Button btnSearchBooks = null;


	@FXML
	private Label lblHello;
	@FXML
	private Label lblConnected;
	@FXML
	private Label lblinstructions;



	/*
	 * initialize the gui with the combo box options and shows a welcome message to the librarian.
	 */
	public void initialize() {
		lblHello.setText("Hello "+ChatClient.getCurrectLibrarian().getName()+" !");
	}


	public void openRegisterNewSubscriber(ActionEvent event) throws Exception {
		Select(LibrarianOptions.RegisterNewSubscriber, event);
	}
	
	public void openViewAllSubscribers(ActionEvent event) throws Exception {
		Select(LibrarianOptions.ViewAllSubscribers, event);
	}
	
	public void openUpdateSubscriberData(ActionEvent event) throws Exception {
		Select(LibrarianOptions.UpdateSubscriberData, event);
	}
	
	public void openBorrowBook(ActionEvent event) throws Exception {
		Select(LibrarianOptions.BorrowBook, event);
	}
	
	public void openShowCurrentBooks(ActionEvent event) throws Exception {
		Select(LibrarianOptions.CurrentBorrowBooks, event);
	}
	
	public void openReturnBook(ActionEvent event) throws Exception {
		Select(LibrarianOptions.ReturnBook, event);
	}
	
	public void openViewNotifications(ActionEvent event) throws Exception {
		Select(LibrarianOptions.ViewNotifications, event);
	}
	
	public void openGenerateReports(ActionEvent event) throws Exception {
		Select(LibrarianOptions.GenerateReports, event);
	}
	
	public void openSearchBooks(ActionEvent event) throws Exception {
		Select(LibrarianOptions.SearchBooks, event);
	}
	
	
	
	
	/**
	 * This method is for the select option from the menu getting the event of
	 * clicking on the btn and sending the selected option to the server and loading
	 * the selected gui.
	 * 
	 * @param event - the btn click
	 * @throws Exception - problem with upload gui.
	 */
	private void Select(LibrarianOptions x, ActionEvent event) throws Exception {
		// getting the selection
		String selectedActionFromMenu = x.getDisplayName();
		selectedActionFromMenu = selectedActionFromMenu.replace(" ", "");

		// uploading the gui of the selected option by the user
		
		//LibrarianOptions x = LibrarianOptions.getSelectionFromEnumName(selectedActionFromMenu);

		/*
		 * for each select we will send something else for the server and gets diffrent
		 * returns
		 */
		switch (x) {
		case SearchBooks:
			FXMLLoader loader = new FXMLLoader();
			((Node) event.getSource()).getScene().getWindow().hide();
			Stage primaryStage = new Stage();
			Pane root = loader
			.load(getClass().getResource("/mainGui/" + LibrarianOptions.valueOf(selectedActionFromMenu) + ".fxml").openStream());
			Scene scene = new Scene(root);
			scene.getStylesheets()
					.add(getClass().getResource("/mainGui/" + LibrarianOptions.valueOf(selectedActionFromMenu) + ".css").toExternalForm());
			primaryStage.setTitle(selectedActionFromMenu + " Tool");
			primaryStage.setScene(scene);
			primaryStage.show();
			//System.out.println("you selected " + selectedActionFromMenu);
			break;
/*
		case ViewAllSubscribers:
			break;
	*/		
		default:
			FXMLLoader loader1 = new FXMLLoader();
			((Node) event.getSource()).getScene().getWindow().hide();
			Stage primaryStage1 = new Stage();
			Pane root1 = loader1
					.load(getClass().getResource("/librarianGui/" + LibrarianOptions.valueOf(selectedActionFromMenu) + ".fxml").openStream());
			// continue show the gui of the selected option by the user.
			Scene scene1 = new Scene(root1);
			scene1.getStylesheets()
					.add(getClass().getResource("/librarianGui/" + LibrarianOptions.valueOf(selectedActionFromMenu) + ".css").toExternalForm());
			primaryStage1.setTitle(selectedActionFromMenu + " Tool");
			primaryStage1.setScene(scene1);
			primaryStage1.show();
			//System.out.println("error? you selected: " + selectedActionFromMenu);
			break;
		}
		
	}

	/**
	 * This method is starting automaticly when this menu gui is up.
	 * 
	 * @param primaryStage
	 * @throws Exception
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/librarianGui/LibrarianMenu.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/librarianGui/LibrarianMenu.css").toExternalForm());
		primaryStage.setTitle("Librarian Menu");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/*
	 * This method is for loggin out from the librarian user and going be to the main login screen.
	 */
	public void LogOutBtn(ActionEvent event) throws Exception {
		ChatClient.setCurrectLibrarian(null);
		FXMLLoader Loader = new FXMLLoader(getClass().getResource("/mainGui/MainLoginScreen.fxml"));
		Parent Root = Loader.load();
		Stage Stage = new Stage();
		Scene Scene = new Scene(Root);
		Scene.getStylesheets().add(getClass().getResource("/mainGui/MainLoginScreen.css").toExternalForm());
		Stage.setScene(Scene);
		Stage.setTitle("Main Login Screen");
		Stage.show();
		((Node) event.getSource()).getScene().getWindow().hide();
	}
	
	

	/*
	 * This method is for the exit button sending a message to the server that now
	 * we are disconnecting, closing the GUI and the connection for the server.
	 */
	public void getExitBtn(ActionEvent event) throws Exception {
		ConnectionSetupController.stopConnectionToServer();
		System.exit(0);
	}

}
