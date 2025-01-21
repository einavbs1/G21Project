package controllers;

import java.io.IOException;
import java.util.HashMap;

import client.ChatClient;
import client.ClientUI;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import entity.*;

/**
 * This class is showing the GUI for updating subscriber details.
 */
public class UpdateMyDataController {

	@FXML
	private Label lblTitle;
	@FXML
	private Label lblMessage;
	@FXML
	private Label lblErrMessage;

	@FXML
	private Label lblId;
	@FXML
	private Label lblName;
	@FXML
	private Label lblPhoneNumber;
	@FXML
	private Label lblEmail;
	@FXML
	private Label lblPassword;
	@FXML
	private Label lblStatus;

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtPhoneNumber;
	@FXML
	private TextField txtEmail;
	@FXML
	private TextField txtPassword;
	@FXML
	private TextField txtStatus;

	@FXML
	private Button btnUpdate = null;
	@FXML
	private Button btnExit = null;
	@FXML
	private Button btnBack = null;

	private Subscriber me = ChatClient.getCurrectSubscriber();

	/*
	 * Author: Einav
	 * 
	 * loading the subscriber details.
	 * 
	 */
	public void initialize() {
		txtId.setText(String.valueOf(me.getId()));
		txtName.setText(me.getName());
		txtPhoneNumber.setText(me.getPhoneNumber());
		txtEmail.setText(me.getEmail());
		txtPassword.setText(me.getPassword());
		txtStatus.setText(me.getStatus());
		txtId.setEditable(false);
		txtName.setEditable(false);
		txtStatus.setEditable(false);

	}

	/**
	 * Author: Avishag. This method is for the back button closing the current GUI
	 * and uploading the menu GUI.
	 * 
	 * @param event - click on the back button.
	 * @throws IOException
	 */
	public void Back(ActionEvent event) throws IOException {
		FXMLLoader Loader = new FXMLLoader(getClass().getResource("/gui/SubscriberMenu.fxml"));
		Parent Root = Loader.load();
		Stage Stage = new Stage();
		Scene Scene = new Scene(Root);
		Scene.getStylesheets().add(getClass().getResource("/gui/SubscriberMenu.css").toExternalForm());
		Stage.setScene(Scene);
		Stage.setTitle("Subscriber Menu");
		Stage.show();
		((Node) event.getSource()).getScene().getWindow().hide();
	}

	/**
	 * This method is changing the message on the String to 10 seconds
	 * 
	 * @param s - the message we want to see on the GUI
	 */
	private void changeString(String s) {
		Platform.runLater(() -> {
			lblErrMessage.setText(s);
		});
		PauseTransition pause = new PauseTransition(Duration.seconds(10));
		pause.setOnFinished(event -> {
			lblErrMessage.setText("");
		});
		pause.play();
	}

	/**
	 * Author: Avishag. This method is for the update button, sending the
	 * information to the server about which subscriber to update and what fields to
	 * change.
	 * 
	 * @param event - the click on the update button.
	 */
	public void UpdateBtn(ActionEvent event) {
		if(VerifyInput()) {
			me.setPhoneNumber(txtPhoneNumber.getText());
			me.setEmail(txtEmail.getText());
			me.setPassword(txtPassword.getText());
			
	
			if(me.UpdateDetails()) {
				lblMessage.setText("Updated successfully.");
				ChatClient.setCurrectSubscriber(me);
			} else {
				lblMessage.setText("Error while update the details.");
			}
		}
	}
	
	
	private boolean VerifyInput() {
		
		if (txtPhoneNumber.getText().isEmpty() || txtEmail.getText().isEmpty() || txtPassword.getText().isEmpty()) {

			changeString("Please fill all the fields to update your details.");
			return false;
		}

		if (!txtPhoneNumber.getText().matches("\\d{10}")) {
			changeString("Phone number must be 10 digits.");
			return false;
		}
		
		if (!txtEmail.getText().matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
		    changeString("Invalid email format. Please enter a valid email.");
		    return false;
		}
		
		
		return true;
	}

	/*
	 * Author: Avishag. This method is for the exit button sending a message to the
	 * server that now we are disconnecting, closing the GUI and the connection for
	 * the server.
	 */
	public void getExitBtn(ActionEvent event) throws Exception {
		System.out.println("Disconnecting from the Server and ending the program.");
		HashMap<String, String> EndingConnections = new HashMap<String, String>();
		EndingConnections.put("Disconnect", "");
		ClientUI.chat.accept(EndingConnections);
		System.exit(0);
	}
}
