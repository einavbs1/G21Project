package mainControllers;

import java.io.IOException;
import java.util.HashMap;

import client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainLoginScreenController {

	@FXML
	private Button btnExit = null;
	@FXML
	private Button btnSubscriberLogin = null;
	@FXML
	private Button btnLibrarianLogin = null;
	@FXML
	private Button btnGuestLogin = null;

	@FXML
	private Label lblWelcome;

	/**
	 * This method is for upload the librarian login screen
	 * @param event - the click on librarian login button 
	 */
	public void LibrarianButton(ActionEvent event) {
		try {
			FXMLLoader Loader = new FXMLLoader(getClass().getResource("/librarianGui/LibrarianLoginScreen.fxml"));
			Parent Root = Loader.load();
			Stage Stage = new Stage();
			Scene Scene = new Scene(Root);
			Scene.getStylesheets().add(getClass().getResource("/librarianGui/LibrarianLoginScreen.css").toExternalForm());
			Stage.setScene(Scene);
			Stage.setTitle("Librarian Login Screen");
			Stage.show();
			((Node) event.getSource()).getScene().getWindow().hide();
		} catch (Exception e) {
		}
	}
	
	
	/**
	 * This method is for upload the Subscriber login screen
	 * @param event - the click on Subscriber login button
	 */
	public void SubscriberButton(ActionEvent event) {
		try {
			FXMLLoader Loader = new FXMLLoader(getClass().getResource("/subscriberGui/SubscriberLoginScreen.fxml"));
			Parent Root = Loader.load();
			Stage Stage = new Stage();
			Scene Scene = new Scene(Root);
			Scene.getStylesheets().add(getClass().getResource("/subscriberGui/SubscriberLoginScreen.css").toExternalForm());
			Stage.setScene(Scene);
			Stage.setTitle("Subscriber Login Screen");
			Stage.show();
			((Node) event.getSource()).getScene().getWindow().hide();
		} catch (Exception e) {
		}
	}
	
	/**
	 * This method is for upload the search books page without login
	 * @param event - the click on continue as a guest button
	 */
	public void GuestButton(ActionEvent event) {
		try {
			FXMLLoader Loader = new FXMLLoader(getClass().getResource("/mainGui/SearchBooks.fxml"));
			Parent Root = Loader.load();
			Stage Stage = new Stage();
			Scene Scene = new Scene(Root);
			Scene.getStylesheets().add(getClass().getResource("/mainGui/SearchBooks.css").toExternalForm());
			Stage.setScene(Scene);
			Stage.setTitle("Search Books");
			Stage.show();
			((Node) event.getSource()).getScene().getWindow().hide();
		} catch (Exception e) {
		}
	}

	/**
	 * Constructor of this class, starting the GUI
	 * @param primaryStage -Stage of this GUI to upload all the data.
	 */
	public void start(Stage primaryStage) {
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("/mainGui/MainLoginScreen.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/mainGui/MainLoginScreen.css").toExternalForm());
			primaryStage.setTitle("Main Login Screen");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method is for the exit button sending a message to the server that now we are disconnecting,
	 * closing the GUI and the connection for the server.
	 * @param event
	 * @throws Exception
	 */
	public void getExitBtn(ActionEvent event) throws Exception {
		ConnectionSetupController.stopConnectionToServer();
		System.exit(0);
	}

}
