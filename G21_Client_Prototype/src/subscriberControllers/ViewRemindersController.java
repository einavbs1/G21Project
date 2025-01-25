package subscriberControllers;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import client.ChatClient;
import entity.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import mainControllers.ConnectionSetupController;

/**
 * This class handles the GUI for viewing reminders. It displays all
 * notifications in the system in a table format.
 * 
 * @author Einav
 */
public class ViewRemindersController {

	@FXML
	private Button btnExit = null;
	@FXML
	private Button btnBack = null;
	
	@FXML
	private Label lblLastCheckedIn;

	@FXML
	private TableView<String> newRemindersTable;
	@FXML
	private TableColumn<String, String> serialColumn;
	@FXML
	private TableColumn<String, String> messageColumn;
	@FXML
	private TableColumn<String, String> dateColumn;
	@FXML
	private TableColumn<String, String> sentToColumn;
	@FXML
	private TableColumn<String, String> phoneNumberColumn;
	@FXML
	private TableColumn<String, String> emailColumn;

	@FXML
	private TableView<String> oldRemindersTable;
	@FXML
	private TableColumn<String, String> serialColumn1;
	@FXML
	private TableColumn<String, String> messageColumn1;
	@FXML
	private TableColumn<String, String> dateColumn1;
	@FXML
	private TableColumn<String, String> sentToColumn1;
	@FXML
	private TableColumn<String, String> phoneNumberColumn1;
	@FXML
	private TableColumn<String, String> emailColumn1;

	/**
	 * Initializes the controller and sets up the table columns. This method is
	 * automatically called after the FXML file has been loaded.
	 */
	public void initialize() {
		lblLastCheckedIn.setText("Last date checked Reminders: " + ChatClient.getCurrectSubscriber().getLastCheckedReminders());
		initNewTable();
		initOldTable();
		loadNotifications();
	}

	private void initNewTable() {
		serialColumn.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[1]);
		});

		messageColumn.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[2]);
		});

		dateColumn.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[5]);
		});

		phoneNumberColumn.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[3]);
		});

		emailColumn.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[4]);
		});
	}

	private void initOldTable() {
		serialColumn1.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[1]);
		});

		messageColumn1.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[2]);
		});

		dateColumn1.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[5]);
		});

		phoneNumberColumn1.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[3]);
		});

		emailColumn1.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[4]);
		});

	}

	private void loadNotifications() {
		List<String> remindersList = Reminders
				.getNewOldRemindersFromDB(ChatClient.getCurrectSubscriber().getLastCheckedReminders(), ChatClient.getCurrectSubscriber().getId());

		List<String> newReminders = new ArrayList<>();
		List<String> oldReminders = new ArrayList<>();

		for (String reminder : remindersList) {
			if (reminder.startsWith("new")) {
				newReminders.add(reminder);
			} else if (reminder.startsWith("old")) {
				oldReminders.add(reminder);
			}
		}
		newRemindersTable.setItems(FXCollections.observableArrayList(newReminders));
		oldRemindersTable.setItems(FXCollections.observableArrayList(oldReminders));

		if (ChatClient.getCurrectSubscriber().getLastCheckedReminders().toLocalDate()
				.isBefore(LocalDate.now())) {
			ChatClient.getCurrectSubscriber().setLastCheckedReminders(Date.valueOf(LocalDate.now()));
			ChatClient.getCurrectSubscriber().UpdateDetails();
		}
	}

	/**
	 * Handles the back button action. Returns to the menu screen.
	 * 
	 * @param event The action event
	 * @throws IOException If there is an error loading the menu screen
	 */
	@FXML
	public void Back(ActionEvent event) throws IOException {
		FXMLLoader Loader = new FXMLLoader(getClass().getResource("/subscriberGui/SubscriberMenu.fxml"));
		Parent Root = Loader.load();
		Stage Stage = new Stage();
		Scene Scene = new Scene(Root);
		Scene.getStylesheets().add(getClass().getResource("/subscriberGui/SubscriberMenu.css").toExternalForm());
		Stage.setScene(Scene);
		Stage.setTitle("Subscriber Menu");
		Stage.show();
		((Node) event.getSource()).getScene().getWindow().hide();
	}

	/**
	 * Handles the exit button action. Disconnects from the server and exits the
	 * application.
	 * 
	 * @param event The action event
	 * @throws Exception If there is an error during disconnect
	 */
	@FXML
	public void getExitBtn(ActionEvent event) {
		ConnectionSetupController.stopConnectionToServer();
		System.exit(0);
	}
}