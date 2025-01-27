package librarianControllers;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.management.Notification;

import client.ChatClient;
import client.ClientUI;
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
 * This class handles the GUI for viewing notifications. It displays all
 * notifications in the system in a table format.
 * 
 * @author chen
 */
public class ViewNotificationsController {

	@FXML
	private Button btnExit = null;
	@FXML
	private Button btnBack = null;
	
	@FXML
	private Label lblLastCheckedDate;

	@FXML
	private TableView<String> newNotificationsTable;
	@FXML
	private TableColumn<String, String> serialColumn;
	@FXML
	private TableColumn<String, String> messageColumn;
	@FXML
	private TableColumn<String, String> subscriberIdColumn;
	@FXML
	private TableColumn<String, String> dateColumn;
	@FXML
	private TableColumn<String, String> borrowNumberColumn;

	@FXML
	private TableView<String> oldNotificationsTable;
	@FXML
	private TableColumn<String, String> serialColumn1;
	@FXML
	private TableColumn<String, String> messageColumn1;
	@FXML
	private TableColumn<String, String> subscriberIdColumn1;
	@FXML
	private TableColumn<String, String> dateColumn1;
	@FXML
	private TableColumn<String, String> borrowNumberColumn1;

	/**
	 * Initializes the controller and sets up the table columns. This method is
	 * automatically called after the FXML file has been loaded.
	 */
	public void initialize() {
		lblLastCheckedDate.setText("Last date checked Notifications: " + ChatClient.getCurrectLibrarian().getLibrarian_lastCheckedNotifications());
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

		subscriberIdColumn.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[3]);
		});

		dateColumn.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[4]);
		});

		borrowNumberColumn.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[5]);
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

		subscriberIdColumn1.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[3]);
		});

		dateColumn1.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[4]);
		});

		borrowNumberColumn1.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[5]);
		});

	}

	private void loadNotifications() {
		List<String> notificationsList = Notifications
				.getNewOldNotificationsFromDB(ChatClient.getCurrectLibrarian().getLibrarian_lastCheckedNotifications());

		List<String> newNotifications = new ArrayList<>();
		List<String> oldNotifications = new ArrayList<>();

		for (String notification : notificationsList) {
			if (notification.startsWith("new")) {
				newNotifications.add(notification);
			} else if (notification.startsWith("old")) {
				oldNotifications.add(notification);
			}
		}
		newNotificationsTable.setItems(FXCollections.observableArrayList(newNotifications));
		oldNotificationsTable.setItems(FXCollections.observableArrayList(oldNotifications));

		if (ChatClient.getCurrectLibrarian().getLibrarian_lastCheckedNotifications().toLocalDate()
				.isBefore(LocalDate.now())) {
			ChatClient.getCurrectLibrarian().setLibrarian_lastCheckedNotifications(Date.valueOf(LocalDate.now()));
			ChatClient.getCurrectLibrarian().UpdateLibrarianDetails();
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
	 * Handles the exit button action. Disconnects from the server and exits the
	 * application.
	 * 
	 * @param event The action event
	 * @throws Exception If there is an error during disconnect
	 */
	@FXML
	public void getExitBtn(ActionEvent event) throws Exception {
		ConnectionSetupController.stopConnectionToServer();
		System.exit(0);
	}
}