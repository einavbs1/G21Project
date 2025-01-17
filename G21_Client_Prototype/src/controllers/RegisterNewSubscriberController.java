package controllers;

import java.io.IOException;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import client.ChatClient;
import client.ClientUI;
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

import entity.*;

/**
 * This class is showing the GUI for updating subscriber details.
 */
public class RegisterNewSubscriberController {

    @FXML
    private Label lblInstruction;
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
    private Label lblMessageStatus;
    @FXML
    private Label lblIdError;

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
    private Button btnAddSubscriber = null;
    @FXML
    private Button btnExit = null;
    @FXML
    private Button btnBack = null;


	/** Author: Avishag.
	 * This method is for the back button closing the current GUI and uploading the
	 * menu GUI.
	 * 
	 * @param event - click on the back button.
	 * @throws IOException
	 */
	public void Back(ActionEvent event) throws IOException {
		FXMLLoader studentLoader = new FXMLLoader(getClass().getResource("/gui/Menu.fxml"));
		Parent studentRoot = studentLoader.load();
		Stage studentStage = new Stage();
		Scene studentScene = new Scene(studentRoot);
		studentScene.getStylesheets().add(getClass().getResource("/gui/Menu.css").toExternalForm());
		studentStage.setScene(studentScene);
		studentStage.setTitle("Menu");
		studentStage.show();
		((Node) event.getSource()).getScene().getWindow().hide();
	}

	/** Author: Avishag.
	 * This method is for the Added button, sending the information to the server after checking
     * about the subscriber to add.
	 * 
	 * @param event - the click on the Add Subscriber button.
	 */
	public void AddSubscriberBtn(ActionEvent event) {
		
		if (txtId.getText().isEmpty() || txtName.getText().isEmpty() || 
		        txtPhoneNumber.getText().isEmpty() || txtEmail.getText().isEmpty() || 
		        txtPassword.getText().isEmpty()) {
		        
		        lblMessageStatus.setText("Please fill in all fields before adding a subscriber.");
		        return;
		}
		
	    if (!txtId.getText().matches("\\d{9}")) {
	        lblMessageStatus.setText("ID must be 9 digits.");
	        return;
	    }

	    if (!txtName.getText().matches("[a-zA-Z ]+")) {
	        lblMessageStatus.setText("Name must contain only letters.");
	        return;
	    }

	    if (!txtPhoneNumber.getText().matches("\\d{10}")) {
	        lblMessageStatus.setText("Phone number must be 9-10 digits.");
	        return;
	    }
		
		HashMap<String, String> addSubscriberHashMap = new HashMap<String, String>();
	    String newSubscriberData = new String();

	    newSubscriberData = newSubscriberData + txtId.getText() + " ";  // Add ID field here
	    newSubscriberData = newSubscriberData + txtName.getText() + " "; 
	    newSubscriberData = newSubscriberData + txtPhoneNumber.getText() + " ";
	    newSubscriberData = newSubscriberData + txtEmail.getText() + " ";
	    newSubscriberData = newSubscriberData + txtPassword.getText() + " ";
	    addSubscriberHashMap.put("Add New Subscriber", newSubscriberData);
		
	    // first we want to see on the GUI message to send the data and just then send
	    // the data.
		lblMessageStatus.setText("Request sent to the server.\n Please wait...");
		Callable<Void> task = () -> {
			Platform.runLater(() -> {
				ClientUI.chat.accept(addSubscriberHashMap);
				String str = ChatClient.getStringfromServer();
					if (str.equals("Added")) {
						lblMessageStatus.setText("Added successfully.");
					} else {
						lblMessageStatus.setText("Cant Added.");
					}
				});
				return null;
			};
			FutureTask<Void> futureTask = new FutureTask<>(task);
			new Thread(futureTask).start();
			try {
				futureTask.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
	}

	/*Author: Avishag.
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
