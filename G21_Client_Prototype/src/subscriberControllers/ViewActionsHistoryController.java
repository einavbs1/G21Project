package subscriberControllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import entity.*;
import client.ChatClient;
import client.ClientUI;
import entity.LogActivity;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import mainControllers.ConnectionSetupController;

/**
 * This class handles the GUI for showing subscriber activity log.
 * It displays the activity history of a specific subscriber.
 */
public class ViewActionsHistoryController {
    
    @FXML
    private TableView<String> activityLogTable;
    
    @FXML
    private TableColumn<String, String> activitySerialColumn;
    @FXML
    private TableColumn<String, String> activityActionColumn;
    @FXML
    private TableColumn<String, String> bookBarcodeColumn;
    @FXML
    private TableColumn<String, String> bookTitleColumn;
    @FXML
    private TableColumn<String, String> bookCopyNoColumn;
    @FXML
    private TableColumn<String, String> activityDateColumn;
    
    @FXML
    private Button btnBack;
    @FXML
    private Button btnExit;
    
    private static List<String> subscriberActivities = new ArrayList<>();
    private Subscriber currSubscriber;

    /**
     * Initialize method to set up the table columns
     */
    @FXML
    public void initialize() {
        setupTableColumns();
        currSubscriber = ChatClient.getCurrectSubscriber();
        loadSubscriberActivities();
    }

    /**
     * Setup all table columns with their respective cell value factories
     */
    private void setupTableColumns() {
        activitySerialColumn.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[0]);
        });
        activityActionColumn.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[2]);
        });
        activityActionColumn.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
					setStyle("");
				} else {
					setText(item);
					
					if (item.toString().toLowerCase().contains("lost") || item.toString().toLowerCase().contains("late") ) {
						setTextFill(javafx.scene.paint.Color.RED);
					}else {
						setTextFill(javafx.scene.paint.Color.BLACK);
					}
				}
			}
		});
        bookBarcodeColumn.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[3]);
        });
        bookTitleColumn.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[4]);
        });
        bookCopyNoColumn.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[5]);
        });
        activityDateColumn.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[6]);
        });
    }

    
    /**
     * Loads and displays activities for the current subscriber
     */
    private void loadSubscriberActivities() {
        subscriberActivities = LogActivity.loadActivitiesBySubscriberId(currSubscriber.getId());
        activityLogTable.setItems(FXCollections.observableArrayList(subscriberActivities));
    }

    
    /**
     * Handles the back button action.
     * Returns to the menu screen.
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
     * Handles the exit button action.
     * Disconnects from the server and exits the application.
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