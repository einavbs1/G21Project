package librarianControllers;

import java.io.IOException;

import client.ChatClient;
import entity.Librarian;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

/*
 * This class is the controller of the librarian login screen
 */
public class LibrarianLoginScreenController {

	@FXML
	private Button btnBack = null;
	@FXML
	private Button btnLogin = null;
	
	@FXML
	private Label lblLibrarianID;
	@FXML
	private Label lblLibrarianPassword;
	
	@FXML
	private TextField LibIdtxt;
	
	@FXML
	private PasswordField LibPasswordfield;

	@FXML
	private Label lblinstruction;
	@FXML
	private Label lblerrmsg;
	
	
	
	
	/**
	 * This method is return the main screen of our library.
	 * @param event - the button action
	 */
	public void BackToMainScreen(ActionEvent event) {
		try {
			FXMLLoader Loader = new FXMLLoader(getClass().getResource("/mainGui/MainLoginScreen.fxml"));
			Parent Root = Loader.load();
			Stage Stage = new Stage();
			Scene Scene = new Scene(Root);
			Scene.getStylesheets().add(getClass().getResource("/mainGui/MainLoginScreen.css").toExternalForm());
			Stage.setScene(Scene);
			Stage.setTitle("Main Login Screen");
			Stage.show();
			((Node) event.getSource()).getScene().getWindow().hide();
		} catch (Exception e) {
		}
	}
	
	
	/**
	 * This method is for the "login" button.
	 * its verify the username and password and if correct it login.
	 * @param event - the button action
	 */
	public void Login(ActionEvent event) {
		Librarian worker = VerifyInput();
		if(worker != null) {
			ChatClient.setCurrectLibrarian(worker);
			try {				
				FXMLLoader Loader = new FXMLLoader(getClass().getResource("/librarianGui/LibrarianMenu.fxml"));
				Parent Root = Loader.load();
				Stage Stage = new Stage();
				Scene Scene = new Scene(Root);
				Scene.getStylesheets().add(getClass().getResource("/librarianGui/LibrarianMenu.css").toExternalForm());
				Stage.setScene(Scene);
				Stage.setTitle("Librarian Menu");
				Stage.show();
				((Node) event.getSource()).getScene().getWindow().hide();		
			} catch (Exception e) {
			}
		}
	}
	
	
	
	/**
	 * This private method is verifing the username and the password.
	 * @return The Librarian that logged in to the system if the ID exists.
	 * 			or null if ID not exists.
	 */
	private Librarian VerifyInput() {
		if(LibIdtxt.getText() ==  "") {
			changeString("Must enter ID to login.");
		}else if(!(LibIdtxt.getText().matches("\\d+"))) {
			changeString("ID can contain only numbers.");
		}else if(LibPasswordfield.getText() == ""){
			changeString("Must enter Password to login.");
		}else{
			int libID = Integer.parseInt(LibIdtxt.getText());
			try {
				Librarian Worker = new Librarian(libID);
				if(Worker.getId() == libID) {
					if(Worker.getPassword().equals(LibPasswordfield.getText())) {
						return Worker;
					}else {
						changeString("Incorrect Password.");
					}
				}
			}catch (Exception e) {
				changeString("Incorrect ID.");
			}
		}
		return null;
	}
	
	
	/** This method is changing the message on the String to 10 seconds
	 * @param s - the message we want to see on the GUI
	 */
	private void changeString(String s) {
		Platform.runLater(() -> {
			lblerrmsg.setText(s);
		});
		PauseTransition pause = new PauseTransition(Duration.seconds(10));
		pause.setOnFinished(event -> {
			lblerrmsg.setText("");
		});
		pause.play();
	}
	

	/**
	 * Constructor of this class, starting the GUI
	 * 
	 * @param primaryStage -Stage of this GUI to upload all the data.
	 */
	public void start(Stage primaryStage) {
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("/librarianGui/LibrarianLoginScreen.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/librarianGui/LibrarianLoginScreen.css").toExternalForm());
			primaryStage.setTitle("Librarian Login Screen");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	

}
