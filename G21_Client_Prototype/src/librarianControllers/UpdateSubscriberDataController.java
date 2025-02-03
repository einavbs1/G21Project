package librarianControllers;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;
import mainControllers.ConnectionSetupController;
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
	private Label lblFrozenUntil;
	@FXML
	private Label lblMessage;

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
	private TextField txtFrozenUntil;

	@FXML
	private RadioButton radioBtnActive;
	@FXML
	private RadioButton radioBtnFrozen;

	@FXML
	private Button btnLoad = null;
	@FXML
	private Button btnUpdate = null;
	@FXML
	private Button btnExit = null;
	@FXML
	private Button btnBack = null;
	@FXML
	private Button btnManageBorrows = null;
	@FXML
	private Button btnSubscriberActivityLog = null;

	private boolean needToLoad = true;
	private boolean updated = false;

	private Subscriber subtoload = null;

	/**
	 * Author: Yuval. This method is for the back button closing the current GUI and
	 * uploading the menu GUI.
	 * 
	 * @param event - click on the back button.
	 * @throws IOException
	 */
	public void Back(ActionEvent event) throws IOException {
		FXMLLoader Loader = new FXMLLoader(getClass().getResource("/librarianGui/LibrarianMenu.fxml"));
		Parent Root = Loader.load();
		Stage Stage = new Stage();
		Scene Scene = new Scene(Root);
		Scene.getStylesheets().add(getClass().getResource("/librarianGui/LibrarianMenu.css").toExternalForm());
		Stage.setScene(Scene);
		Stage.setTitle("Librarian Menu");
		Stage.show();
		((Node) event.getSource()).getScene().getWindow().hide();
	}

	public void OpenCurrectBorrows(ActionEvent event) throws IOException {
		CurrentBorrowBooksController.mySubscriber = subtoload;
		FXMLLoader Loader = new FXMLLoader(getClass().getResource("/librarianGui/CurrentBorrowBooks.fxml"));
		Parent Root = Loader.load();
		Stage Stage = new Stage();
		Scene Scene = new Scene(Root);
		Scene.getStylesheets().add(getClass().getResource("/librarianGui/CurrentBorrowBooks.css").toExternalForm());
		Stage.setScene(Scene);
		Stage.setTitle("Current Borrow Books");
		Stage.show();
		((Node) event.getSource()).getScene().getWindow().hide();
	}
	
	public void OpenSubscriberActivityLog(ActionEvent event) throws IOException {
		CurrentBorrowBooksController.mySubscriber = subtoload;
		ChatClient.setCurrectSubscriber(subtoload);
		FXMLLoader Loader = new FXMLLoader(getClass().getResource("/subscriberGui/ViewActionsHistory.fxml"));
		Parent Root = Loader.load();
		Stage Stage = new Stage();
		Scene Scene = new Scene(Root);
		Scene.getStylesheets().add(getClass().getResource("/subscriberGui/ViewActionsHistory.css").toExternalForm());
		Stage.setScene(Scene);
		Stage.setTitle("View Actions History");
		Stage.show();
		((Node) event.getSource()).getScene().getWindow().hide();
	}

	/**
	 * Author: Yuval. This method is for the update button, sending the information
	 * to the server about which subscriber to update and what fields to change.
	 * 
	 * @param event - the click on the update button.
	 */
	public void UpdateBtn(ActionEvent event) {
		if (!needToLoad && !updated && VerifyInput()) {
			String oldString = subtoload.toString();
			subtoload.setName(txtName.getText());
			subtoload.setEmail(txtEmail.getText());
			subtoload.setPhoneNumber(txtPhoneNumber.getText());
			subtoload.setPassword(txtPassword.getText());

			if (radioBtnActive.isSelected()) {
				if (subtoload.getFrozenUntil() != null) {
					String recordToChnageString = Subscriber.getSpecificFrozenRecord(subtoload.getId(), subtoload.getFrozenUntil());
					String[] parts = recordToChnageString.split(", ");
					int recordTochange = Integer.parseInt(parts[0]);
					Subscriber.updateRecordOfFrozen(recordTochange, Date.valueOf(parts[2]) ,Date.valueOf(LocalDate.now()));
					SubscribersStatusReport reportToUpdate = new SubscribersStatusReport(LocalDate.now().getMonthValue(), LocalDate.now().getYear());
					reportToUpdate.addUnfrozed();
					reportToUpdate.UpdateDetails();
				}
				subtoload.setStatus(Subscriber.ACTIVE);
				subtoload.setFrozenUntil(null);
			} else {
				if (subtoload.getFrozenUntil() != null) {
					String recordToChnageString = Subscriber.getSpecificFrozenRecord(subtoload.getId(), subtoload.getFrozenUntil());
					String[] parts = recordToChnageString.split(", ");
					int recordTochange = Integer.parseInt(parts[0]);
					Subscriber.updateRecordOfFrozen(recordTochange, Date.valueOf(parts[2]) ,Date.valueOf(txtFrozenUntil.getText()));
				}else {
					 System.out.println("you enter to add froze record");
					SubscribersStatusReport reportToUpdate = new SubscribersStatusReport(LocalDate.now().getMonthValue(), LocalDate.now().getYear());
					reportToUpdate.addGotFroze();
					reportToUpdate.UpdateDetails();
					Subscriber.addingNewRecordOfFrozen(subtoload.getId(), Date.valueOf(LocalDate.now()),
							Date.valueOf(txtFrozenUntil.getText()));
				}
				subtoload.setStatus(Subscriber.FROZEN);
				subtoload.setFrozenUntil(Date.valueOf(txtFrozenUntil.getText()));
				
			}
			if (subtoload.UpdateDetails()) {
				changeString("Subscriber details has been updated successfully.", "#086f03");
				
				String activityMsg = "the librarian: \""+ ChatClient.getCurrectLibrarian().getName() +"\" changed your personal information.";
				new LogActivity(subtoload.getId(), activityMsg, null, null, null);
				
			} else {
				changeString("Error while saving updating details", "#bf3030");
			}
			updated = true;

		} else if (updated) {
			// lblMessageStatus.setText(" ");
			changeString("To update new information you have to enter this screen again.", "#bf3030");
		}

	}

	private boolean VerifyInput() {
		if (needToLoad) {
			if (txtId.getText().isEmpty()) {
				changeString("Please fill id before trying to load subscriber data.", "#bf3030");
				return false;
			}
		} else {
			if (txtName.getText().isEmpty() || txtPhoneNumber.getText().isEmpty() || txtEmail.getText().isEmpty()
					|| txtPassword.getText().isEmpty()
					|| (!radioBtnActive.isSelected() && !radioBtnFrozen.isSelected())) {

				changeString("Please fill all The fields.", "#bf3030");
				return false;
			}

			if (!txtId.getText().matches("\\d{9}")) {
				changeString("ID must be 9 digits.", "#bf3030");
				return false;
			}

			if (!txtName.getText().matches("[a-zA-Z ]+")) {
				changeString("Name must contain only letters.", "#bf3030");
				return false;
			}

			if (!txtPhoneNumber.getText().matches("\\d{10}")) {
				changeString("Phone number must be 9-10 digits.", "#bf3030");
				return false;
			}

			if (!txtEmail.getText().matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
				changeString("Invalid email format. Please enter a valid email.", "#bf3030");
				return false;
			}
			if ((radioBtnActive.isSelected() && radioBtnFrozen.isSelected())) {
				changeString("You need to choose only one status.", "#bf3030");
				return false;
			}
			if (radioBtnActive.isSelected() && !(txtFrozenUntil.getText().equals("null"))) {
				changeString("If you select status: \"Active\" please leave the frozen until date: \"null\" .",
						"#bf3030");
				return false;
			}
			if (radioBtnFrozen.isSelected()) {
				if (txtFrozenUntil.getText().equals("null")) {
					changeString("You have to select date to froze in the formart: yyyy-MM-dd .", "#bf3030");
					return false;
				}
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				try {
					LocalDate.parse(txtFrozenUntil.getText(), formatter);
					LocalDate today = LocalDate.now();
					Date frozeUntil = Date.valueOf(txtFrozenUntil.getText());
					LocalDate frozeUntilLocalDate = frozeUntil.toLocalDate();

					if (!today.isBefore(frozeUntilLocalDate)) {
						changeString("You can set the date only with at least one day ahead. ", "#bf3030");
						return false;
					}

					return true;

				} catch (DateTimeParseException e) {
					changeString("You have to select date to froze in the formart: yyyy-MM-dd .", "#bf3030");
					return false;
				}

			}
		}

		return true;
	}

	/**
	 * This method is changing the message on the String to 10 seconds
	 * 
	 * @param s - the message we want to see on the GUI
	 */
	private void changeString(String s, String color) {
		Platform.runLater(() -> {
			lblMessage.setText(s);
			lblMessage.setTextFill(Paint.valueOf(color));
		});
		PauseTransition pause = new PauseTransition(Duration.seconds(10));
		pause.setOnFinished(event -> {
			if (lblMessage.getText().equals(s)) {
				lblMessage.setText(" ");
			}
		});
		pause.play();
	}

	/**
	 * Author: Yuval. This method is for the load subscriber button. getting the
	 * string from the server and calling other method "LoadDetails()" to handle it
	 * and load into the GUI
	 * 
	 * @param event - the click on the load button.
	 */
	public void Loadbtn(ActionEvent event) {

		if (needToLoad && VerifyInput()) {
			try {
				subtoload = new Subscriber(Integer.parseInt(txtId.getText()));
				txtName.setText(subtoload.getName());
				txtEmail.setText(subtoload.getEmail());
				txtPhoneNumber.setText(subtoload.getPhoneNumber());
				txtPassword.setText(subtoload.getPassword());
				String status = subtoload.getStatus();
				if (status.toLowerCase().equals("active")) {
					radioBtnActive.setSelected(true);
					radioBtnFrozen.setSelected(false);
				} else {
					radioBtnActive.setSelected(false);
					radioBtnFrozen.setSelected(true);
				}
				if (subtoload.getFrozenUntil() == null) {
					txtFrozenUntil.setText("null");
				} else {
					txtFrozenUntil.setText(String.valueOf(subtoload.getFrozenUntil()));
				}
				txtFrozenUntil.setEditable(true);
				txtId.setEditable(false);
				txtName.setEditable(true);
				txtPhoneNumber.setEditable(true);
				txtEmail.setEditable(true);
				txtPassword.setEditable(true);
				needToLoad = false;
				btnUpdate.setDisable(false);
				btnManageBorrows.setDisable(false);
				btnSubscriberActivityLog.setDisable(false);
			} catch (Exception e) {
				changeString(e.getMessage(), "#bf3030");
			}
		}
	}

	/*
	 * Author: Yuval. This method is for the exit button sending a message to the
	 * server that now we are disconnecting, closing the GUI and the connection for
	 * the server.
	 */
	public void getExitBtn(ActionEvent event) throws Exception {
		ConnectionSetupController.stopConnectionToServer();
		System.exit(0);
	}
}
