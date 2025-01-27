package subscriberControllers;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import client.ChatClient;
import client.ClientUI;
import common.LibrarianOptions;
import common.SubscriberOptions;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import mainControllers.ConnectionSetupController;

/**
 * This class is controller of the subscriber menu (after subscriber has logged in).
 */
public class SubscriberMenuController {

	@FXML
	private Button btnExit = null;
	@FXML
	private Button btnLogOut = null;
	@FXML
	private Button btnSearchBooks = null;
	@FXML
	private Button btnExtendBorrowRequest = null;
	@FXML
	private Button btnOrderBook = null;
	@FXML
	private Button btnViewReminders = null;
	@FXML
	private Button btnShowMyOrder = null;
	@FXML
	private Button btnUpdateMyData = null;
	@FXML
	private Button btnViewActionsHistory = null;


	@FXML
	private Label lblHello;
	@FXML
	private Label lblConnected;
	@FXML
	private Label lblinstructions;


	/*
	 * initialize the gui with the combo box options and shows a welcome message to the subscriber.
	 */
	public void initialize() {
		lblHello.setText("Hello "+ChatClient.getCurrectSubscriber().getName()+" !");
	}

	
	public void openSearchBooks(ActionEvent event) throws Exception {
		Select(SubscriberOptions.SearchBooks, event);
	}

	public void openExtendBorrowRequest(ActionEvent event) throws Exception {
		Select(SubscriberOptions.ExtendBorrowRequest, event);
	}
	
	public void openOrderBook(ActionEvent event) throws Exception {
		Select(SubscriberOptions.OrderBook, event);
	}
	
	public void openViewReminders(ActionEvent event) throws Exception {
		Select(SubscriberOptions.ViewReminders, event);
	}
	
	public void openShowMyOrders(ActionEvent event) throws Exception {
		Select(SubscriberOptions.ShowMyOrders, event);
	}
	
	public void openUpdateMyData(ActionEvent event) throws Exception {
		Select(SubscriberOptions.UpdateMyData, event);
	}
	
	public void openViewActionsHistory(ActionEvent event) throws Exception {
		Select(SubscriberOptions.ViewActionsHistory, event);
	}
	
	
	
	
	
	/**
	 * This method is for the select option from the menu getting the event of
	 * clicking on the btn and sending the selected option to the server and loading
	 * the selected gui.
	 * 
	 * @param event - the btn click
	 * @throws Exception - problem with upload gui.
	 */
	public void Select(SubscriberOptions x, ActionEvent event) throws Exception {
		// getting the selection
		String userselect = x.getDisplayName();
		userselect = userselect.replace(" ", "");


		/*
		 * for each select we will send something else for the server and gets diffrent
		 * returns
		 */
		switch (x) {
		case SearchBooks:
			
			FXMLLoader Loader = new FXMLLoader(getClass().getResource("/mainGui/" + SubscriberOptions.valueOf(userselect) + ".fxml"));
			Parent Root = Loader.load();
			Stage Stage = new Stage();
			Scene Scene = new Scene(Root);
			Scene.getStylesheets().add(getClass().getResource("/mainGui/" + SubscriberOptions.valueOf(userselect) + ".css").toExternalForm());
			Stage.setScene(Scene);
			Stage.setTitle(userselect + " Tool");
			Stage.show();
			((Node) event.getSource()).getScene().getWindow().hide();
			break;

			
		default:
			FXMLLoader Loader1 = new FXMLLoader(getClass().getResource("/subscriberGui/" + SubscriberOptions.valueOf(userselect) + ".fxml"));
			Parent Root1 = Loader1.load();
			Stage Stage1 = new Stage();
			Scene Scene1 = new Scene(Root1);
			Scene1.getStylesheets().add(getClass().getResource("/subscriberGui/" + SubscriberOptions.valueOf(userselect) + ".css").toExternalForm());
			Stage1.setScene(Scene1);
			Stage1.setTitle(userselect + " Tool");
			Stage1.show();
			((Node) event.getSource()).getScene().getWindow().hide();
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
		Parent root = FXMLLoader.load(getClass().getResource("/subscriberGui/SubscriberMenu.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/subscriberGui/SubscriberMenu.css").toExternalForm());
		primaryStage.setTitle("Subscriber Menu");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/*
	 * This method is for loggin out from the subscriber user and going be to the main login screen.
	 */
	public void LogOutBtn(ActionEvent event) throws Exception {
		ChatClient.setCurrectSubscriber(null);
		
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
