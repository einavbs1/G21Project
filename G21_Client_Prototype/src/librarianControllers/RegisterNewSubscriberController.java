package librarianControllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
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
import mainControllers.ConnectionSetupController;
import entity.*;

/**
 * This class is showing the GUI for updating subscriber details.
 */
public class RegisterNewSubscriberController {

	@FXML
	private Label lblTitle;
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
	private Label lblErrMsg;

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

	private boolean needToRegistered = true;

	/**
	 * Author: Avishag. This method is for the back button closing the current GUI
	 * and uploading the menu GUI.
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

	/**
	 * Author: Avishag. This method is for the Added button, sending the information
	 * to the server after checking about the subscriber to add.
	 * 
	 * @param event - the click on the Add Subscriber button.
	 */
	public void AddSubscriberBtn(ActionEvent event) {
		if (needToRegistered && VerifyInput()) {
			try {
				Subscriber newOne = new Subscriber(Integer.parseInt(txtId.getText()), txtName.getText(),
						txtPhoneNumber.getText(), txtEmail.getText(), txtPassword.getText());
				SubscribersStatusReport reportToUpdate = new SubscribersStatusReport(LocalDate.now().getMonthValue(), LocalDate.now().getYear());
				reportToUpdate.addNewSubscriber();
				reportToUpdate.addtoTotal();
				reportToUpdate.UpdateDetails();
				
				new LogActivity(newOne.getId(), "Welcome! You registered to our library.", null, null, 0);
				
				if (newOne != null) {
					lblMessageStatus.setText("Subscriber " + txtName.getText() + " has been registered successfully.");
					needToRegistered = false;
				}
			} catch (Exception e) {
				changeString(e.getMessage(), lblErrMsg);
			}
		} else if (!needToRegistered) {
			lblMessageStatus.setText("");
			changeString("To register new subscriber please enter again to this screen.", lblErrMsg);
		}
	}

	/**
	 * This method is changing the message on the String to 10 seconds
	 * 
	 * @param s - the message we want to see on the GUI
	 */
	private void changeString(String s, Label lbl) {
		Platform.runLater(() -> {
			lbl.setText(s);
		});
		PauseTransition pause = new PauseTransition(Duration.seconds(10));
		pause.setOnFinished(event -> {
			lbl.setText("");
		});
		pause.play();
	}

	public boolean VerifyInput() {
		if (txtId.getText().isEmpty() || txtName.getText().isEmpty() || txtPhoneNumber.getText().isEmpty()
				|| txtEmail.getText().isEmpty() || txtPassword.getText().isEmpty()) {

			changeString("Please fill all The fields.", lblErrMsg);
			return false;
		}

		if (!txtId.getText().matches("\\d{9}")) {
			changeString("ID must be 9 digits.", lblErrMsg);
			return false;
		}

		if (!txtName.getText().matches("[a-zA-Z ]+")) {
			changeString("Name must contain only letters.", lblErrMsg);
			return false;
		}

		if (!txtPhoneNumber.getText().matches("\\d{10}")) {
			changeString("Phone number must be 9-10 digits.", lblErrMsg);
			return false;
		}

		if (!txtEmail.getText().matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
			changeString("Invalid email format. Please enter a valid email.", lblErrMsg);
			return false;
		}

		return true;
	}
	
	/**
	 * This method is starting automaticly when this menu gui is up.
	 * 
	 * @param primaryStage
	 * @throws Exception
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/librarianGui/RegisterNewSubscriber.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/librarianGui/RegisterNewSubscriber.css").toExternalForm());
		primaryStage.setTitle("Register New Subscriber");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/*
	 * Author: Avishag. This method is for the exit button sending a message to the
	 * server that now we are disconnecting, closing the GUI and the connection for
	 * the server.
	 */
	public void getExitBtn(ActionEvent event) throws Exception {
		ConnectionSetupController.stopConnectionToServer();
		System.exit(0);
	}
}
