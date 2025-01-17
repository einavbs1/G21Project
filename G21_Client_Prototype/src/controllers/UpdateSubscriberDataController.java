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
public class UpdateSubscriberDataController {

    @FXML
    private Label lblInstruction;
    @FXML
    private Label lblId;
    @FXML
    private Label lblName;
    @FXML
    private Label lblSubscriptionDetails;
    @FXML
    private Label lblPhoneNumber;
    @FXML
    private Label lblEmail;
    @FXML
    private Label lblPassword;
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblMessageStatus;
    @FXML
    private Label lblIdError;

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtSubscriptionDetails;
    @FXML
    private TextField txtPhoneNumber;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtPassword;
    @FXML
    private TextField txtStatus;

    @FXML
    private Button btnLoad = null;
    @FXML
    private Button btnUpdate = null;
    @FXML
    private Button btnExit = null;
    @FXML
    private Button btnBack = null;


	private int loaded = 0;

	/**Author: Yuval.
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

	/**Author: Yuval.
	 * This method is for the update button, sending the information to the server
     * about which subscriber to update and what fields to change.
	 * 
	 * @param event - the click on the update button.
	 */
	public void UpdateBtn(ActionEvent event) {
		if (loaded > 0) {
			
			if (txtName.getText().isEmpty() || txtPhoneNumber.getText().isEmpty() || txtEmail.getText().isEmpty() || 
			        txtPassword.getText().isEmpty() || txtStatus.getText().isEmpty()) {
			        
			        lblMessageStatus.setText("Please fill in all fields before adding a subscriber.");
			        return;
			}

		    if (!txtName.getText().matches("[a-zA-Z ]+")) {
		        lblMessageStatus.setText("Name must contain only letters.");
		        return;
		    }

		    if (!txtPhoneNumber.getText().matches("\\d{10}")) {
		        lblMessageStatus.setText("Phone number must be 10 digits.");
		        return;
		    }
		    
		    if (!txtStatus.getText().equalsIgnoreCase("frozen") && !txtStatus.getText().equalsIgnoreCase("active")) {
		        lblMessageStatus.setText("Status must be either 'frozen' or 'active'.");
		        return;
		    }
		    
			HashMap<String, String> updateHashMap = new HashMap<String, String>();
			String newinfo = new String();
			
	        newinfo = newinfo + txtId.getText() + " "; 
	        newinfo = newinfo + txtName.getText() + " ";
	        newinfo = newinfo + txtSubscriptionDetails.getText() + " ";
	        newinfo = newinfo + txtPhoneNumber.getText() + " ";
	        newinfo = newinfo + txtEmail.getText() + " ";
	        newinfo = newinfo + txtPassword.getText() + " ";
	        newinfo = newinfo + txtStatus.getText();

	        updateHashMap.put("Update Subscriber", newinfo);
			
			// first we want to see on the GUI message to send the data and just then send the data.
			lblMessageStatus.setText("Request sent to the server.\n Please wait...");
			Callable<Void> task = () -> {
				Platform.runLater(() -> {
					ClientUI.chat.accept(updateHashMap);
					String str = ChatClient.getStringfromServer();
					if (str.equals("Updated")) {
						lblMessageStatus.setText("Updated successfully.");
					} else {
						lblMessageStatus.setText("Cant update.");
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
		} else {
			lblIdError.setText("You must load an Id first.");
		}
	}

	/**Author: Yuval.
	 * This method is for the load subscriber button. getting the string from the
	 * server and calling other method "LoadDetails()" to handle it and load into the GUI
	 * 
	 * @param event - the click on the load button.
	 */
	public void Loadbtn(ActionEvent event) {
		
		if (txtId.getText().isEmpty()) {
		        
		        lblMessageStatus.setText("Please fill id before trying to load subscriber data.");
		        return;
		}
		
		String idnumber = txtId.getText();
		// The string contains only numbers.
		if (idnumber.matches("\\d{9}")) {
			HashMap<String, String> loadthisid = new HashMap<String, String>();
			loadthisid.put("Get Subscriber Details", idnumber);
			ClientUI.chat.accept(loadthisid);
			String str = ChatClient.getStringfromServer();
			
			if (!str.contains(",")) {
				lblIdError.setText("Cant load the requested ID. Please make sure you entered the right id.");
			} else {
				lblIdError.setText("Requested ID loaded.\nYou can update the data now:");
				loaded = 1;
				LoadDetails(str);
			}
			// The string contains more chars that not digits or empty.
		} else {
			lblIdError.setText("Please enter id that contains ONLY 9 digits");
		}
	}

	/**Author: Yuval.
	 * This method is getting a string of subscriber and loading that data to the GUI
	 * enabling the only fields that we wants that the user will update.
	 * @param Subscriber
	 */
	public void LoadDetails(String Subscriber) {
		String[] parts = Subscriber.split(", ");
	    this.txtName.setText(parts[1]);
	    this.txtSubscriptionDetails.setText(parts[2]);
	    this.txtPhoneNumber.setText(parts[3]);
	    this.txtEmail.setText(parts[4]);
	    this.txtPassword.setText(parts[5]);
	    this.txtStatus.setText(parts[6]);

	    txtId.setEditable(false);
	    txtName.setEditable(true);
	    txtSubscriptionDetails.setEditable(false);
	    txtPhoneNumber.setEditable(true);
	    txtEmail.setEditable(true);
	    txtPassword.setEditable(true);
	    txtStatus.setEditable(true);
	}

	/*Author: Yuval.
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
