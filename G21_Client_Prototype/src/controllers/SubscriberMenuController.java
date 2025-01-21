package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import client.ChatClient;
import client.ClientUI;
import common.LibrarianOptions;
import common.SubscriberOptions;
import common.UserSelect;
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

/**
 * This class is controller of the subscriber menu (after subscriber has logged in).
 */
public class SubscriberMenuController {

	@FXML
	private Button btnExit = null;
	@FXML
	private Button btnLogOut = null;
	@FXML
	private Button btnSelect = null;

	@FXML
	private ComboBox<String> comboBoxSubscriberOptions;
	

	@FXML
	private Label lblHello;
	@FXML
	private Label lblConnected;
	@FXML
	private Label lblinstructions;

	/*
	 * This method is setting the comboBox with the librarian options by
	 * using the enum "SubscriberOptions".
	 */
	private void setComboBox() {
		SubscriberOptions SelectOptions[] = SubscriberOptions.values();
		for (int i = 0; i < SelectOptions.length; i++) {
			comboBoxSubscriberOptions.getItems().add(SelectOptions[i].getDisplayName());
		}
		comboBoxSubscriberOptions.setValue(SubscriberOptions.UpdateMyData.getDisplayName()); // Default selected value
	}

	/*
	 * initialize the gui with the combo box options and shows a welcome message to the subscriber.
	 */
	public void initialize() {
		setComboBox();
		lblHello.setText("Hello "+ChatClient.getCurrectSubscriber().getName()+" !");
	}

	/**
	 * This method is returning the select from the comboBox as a string
	 * 
	 * @return String of the selection
	 */
	private String getSelection() {
		return (String) comboBoxSubscriberOptions.getValue();
	}

	/**
	 * This method is for the select option from the menu getting the event of
	 * clicking on the btn and sending the selected option to the server and loading
	 * the selected gui.
	 * 
	 * @param event - the btn click
	 * @throws Exception - problem with upload gui.
	 */
	public void Select(ActionEvent event) throws Exception {
		// getting the selection
		String userselect = getSelection();
		userselect = userselect.replace(" ", "");

		// uploading the gui of the selected option by the user
		FXMLLoader loader = new FXMLLoader();
		((Node) event.getSource()).getScene().getWindow().hide();
		Stage primaryStage = new Stage();
		Pane root = loader
				.load(getClass().getResource("/gui/" + SubscriberOptions.valueOf(userselect) + ".fxml").openStream());
		SubscriberOptions x = SubscriberOptions.getSelectionFromEnumName(userselect);

		/*
		 * for each select we will send something else for the server and gets diffrent
		 * returns
		 */
		switch (x) {
		case SearchBooks:
			System.out.println("you selected " + userselect);
			break;
			
		case UpdateMyData:
			System.out.println("you selected " + userselect);
			break;

		case ExtendBorrowRequest:
			System.out.println("you selected " + userselect);
			break;
			
		default:
			System.out.println("error? you selected: " + userselect);
			break;
		}
		// continue show the gui of the selected option by the user.
		Scene scene = new Scene(root);
		scene.getStylesheets()
				.add(getClass().getResource("/gui/" + SubscriberOptions.valueOf(userselect) + ".css").toExternalForm());
		primaryStage.setTitle(userselect + " Tool");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * This method is starting automaticly when this menu gui is up.
	 * 
	 * @param primaryStage
	 * @throws Exception
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/SubscriberMenu.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/SubscriberMenu.css").toExternalForm());
		primaryStage.setTitle("Subscriber Menu");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/*
	 * This method is for loggin out from the subscriber user and going be to the main login screen.
	 */
	public void LogOutBtn(ActionEvent event) throws Exception {
		ChatClient.setCurrectSubscriber(null);
		
		FXMLLoader Loader = new FXMLLoader(getClass().getResource("/gui/MainLoginScreen.fxml"));
		Parent Root = Loader.load();
		Stage Stage = new Stage();
		Scene Scene = new Scene(Root);
		Scene.getStylesheets().add(getClass().getResource("/gui/MainLoginScreen.css").toExternalForm());
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
		System.out.println("Disconnecting from the Server and ending the program.");
		HashMap<String, String> EndingConnections = new HashMap<String, String>();
		EndingConnections.put("Disconnect", "");
		ClientUI.chat.accept(EndingConnections);
		System.exit(0);
	}

}
