package librarianControllers;

import java.io.IOException;

import java.util.HashMap;
import java.util.List;

import client.ClientUI;
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

import entity.*;

/**
 * This class is showing us the GUI with table of all the subscribers
 */
public class ViewAllSubscribersController {

	@FXML
	private Label lblTitle;
	
	@FXML
	private Button btnExit = null;
	@FXML
	private Button btnBack = null;

	@FXML
	private TableView<String> subscriberTable;
	@FXML
	private TableColumn<String, String> idColumn;
	@FXML
	private TableColumn<String, String> nameColumn;
	@FXML
	private TableColumn<String, String> subscriptionDetailsColumn;
	@FXML
	private TableColumn<String, String> phoneColumn;
	@FXML
	private TableColumn<String, String> emailColumn;
	@FXML
	private TableColumn<String, String> passwordColumn;
	@FXML
	private TableColumn<String, String> statusColumn;

	/*
	 * Author: Avishag. initialize the GUI with the data to the table view.
	 */
	public void initialize() {
		loadSubscribers(Subscriber.showAllSubscribers());
		initTheTable();
	}

	private void initTheTable() {
		idColumn.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[0]);
		});

		nameColumn.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[1]);
		});

		phoneColumn.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[2]);
		});

		emailColumn.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[3]);
		});

		passwordColumn.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[4]);
		});

		statusColumn.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			return new javafx.beans.property.SimpleStringProperty(parts[5]);
		});

		statusColumn.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
					setStyle(""); // Clear style
				} else {
					setText(item);
					if (item.equalsIgnoreCase("Active")) {
						setTextFill(javafx.scene.paint.Color.GREEN); // Set green for "Active"
					} else {
						setTextFill(javafx.scene.paint.Color.RED); // Set red for other statuses
					}
				}
			}
		});
	}

	/**
	 * Author: Avishag. This method is getting list of the subscribers and uploading
	 * it to the table view
	 * 
	 * @param subs - list of the subscribers.
	 */
	private void loadSubscribers(List<String> subs) {
		// Convert the list to an observable list and set it to the table
		subscriberTable.setItems(FXCollections.observableArrayList(subs));
	}

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
