package gui;

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

/**
 * This class is showing the GUI of updating email address of subscriber.
 */
public class UpdateEmailAddressController {

	@FXML
	private Label lblinstruction;
	@FXML
	private Label lblId;
	@FXML
	private Label lblName;
	@FXML
	private Label lblHistory;
	@FXML
	private Label lblPhoneNum;
	@FXML
	private Label lblEmail;
	@FXML
	private Label lblStatus;
	@FXML
	private Label lbliderr;

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtHistory;
	@FXML
	private TextField txtPhoneNum;
	@FXML
	private TextField txtEmail;

	@FXML
	private Button btnLoad = null;
	@FXML
	private Button btnUpdate = null;
	@FXML
	private Button btnExit = null;
	@FXML
	private Button btnBack = null;

	private int loaded = 0;

	/**
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

	/**
	 * This method is for the update button is sending the information to the server
	 * which subscriber to update and what to change.
	 * 
	 * @param event - the click on the update button.
	 */
	public void UpdateBtn(ActionEvent event) {
		if (loaded > 0) {
			HashMap<String, String> updateHashMap = new HashMap<String, String>();
			String newinfo = new String();
			newinfo = newinfo + txtId.getText() + " ";
			newinfo = newinfo + txtEmail.getText();
			updateHashMap.put("UpdateEmailAddress", newinfo);
			// first we want to see on the GUI message to send the data and just then send
			// the data.
			lblStatus.setText("Request sent to the server.\n Please wait...");
			Callable<Void> task = () -> {
				Platform.runLater(() -> {
					ClientUI.chat.accept(updateHashMap);
					if (ChatClient.fromserverString.equals("Updated")) {
						lblStatus.setText("Updated successfully.");
					} else {
						lblStatus.setText("Cant update.");
					}
					ChatClient.ResetServerString();
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
			lbliderr.setText("You must load an Id first.");
		}
	}

	/**
	 * This method is for the load subscriber button. getting the string from the
	 * server and calling other method "LoadDetails()" to handle it and load into the GUI
	 * 
	 * @param event - the click on the load button.
	 */
	public void Loadbtn(ActionEvent event) {
		String idnumber = txtId.getText();
		// The string contains only numbers.
		if (idnumber.matches("\\d+")) {
			HashMap<String, String> loadthisid = new HashMap<String, String>();
			loadthisid.put("LoadSubscriber", idnumber);
			ClientUI.chat.accept(loadthisid);
			while (ChatClient.fromserverString.equals(new String())) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (!ChatClient.fromserverString.contains(",")) {
				lbliderr.setText("Cant load the requested ID. Please make sure you entered the right id.");
			} else {
				lbliderr.setText("Requested ID loaded.\nYou can update ONLY the email address:");
				loaded = 1;
				LoadDetails(ChatClient.fromserverString);
			}
			ChatClient.ResetServerString();
			// The string contains more chars that not digits or empty.
		} else {
			lbliderr.setText("Please enter id that contains ONLY digits");
		}
	}

	/** This method is getting a string of subscriber and loading that data to the GUI
	 * enabling the only fields that we wants that the user will update.
	 * @param Subscriber
	 */
	public void LoadDetails(String Subscriber) {
		String[] parts = Subscriber.split(", ");
		this.txtName.setText(parts[1]);
		this.txtHistory.setText(parts[2]);
		this.txtPhoneNum.setText(parts[3]);
		this.txtEmail.setText(parts[4]);
		txtId.setEditable(false);
		txtName.setEditable(false);
		txtHistory.setEditable(false);
		txtPhoneNum.setEditable(false);
		txtEmail.setEditable(true);
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
