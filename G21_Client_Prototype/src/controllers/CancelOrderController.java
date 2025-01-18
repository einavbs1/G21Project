package controllers;
import entity.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;



import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import client.ChatClient;
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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * This class handles the GUI for cancelling orders.
 * It displays active orders for a specific subscriber and allows them to cancel orders.
 * 
 * @author chen
 */
public class CancelOrderController {
    @FXML
    private Button btnExit = null;
    @FXML
    private Button btnBack = null;
    @FXML
    private Button btnLoadOrder = null;
    @FXML
    private Button btnCancelOrder = null;
    
    @FXML
    private TextField txtOrderNumber;
    
    @FXML
    private Label lblBookName;
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblError;

    @FXML
    private TableView<String> ordersTable;
    @FXML
    private TableColumn<String, String> orderNumberColumn;
    @FXML
    private TableColumn<String, String> subscriberIdColumn;
    @FXML
    private TableColumn<String, String> bookBarcodeColumn;
    @FXML
    private TableColumn<String, String> requestedDateColumn;
    @FXML
    private TableColumn<String, String> statusColumn;
    @FXML
    private TableColumn<String, String> arrivedDateColumn;

    private int currentSubscriberId; // Store the current subscriber's ID

    /**
     * Sets the subscriber ID and loads their active orders.
     * This method should be called after creating the controller.
     * 
     * @param subscriberId The ID of the subscriber whose orders to display
     */
    public void setSubscriber(int subscriberId) {
        this.currentSubscriberId = subscriberId;
        loadSubscriberOrders();
    }

    /**
     * Initializes the controller and sets up the table columns.
     * This method is automatically called after the FXML file has been loaded.
     */
    public void initialize() {
        orderNumberColumn.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[0]);
        });
        subscriberIdColumn.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[1]);
        });
        bookBarcodeColumn.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[2]);
        });
        requestedDateColumn.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[3]);
        });
        statusColumn.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(getStatusText(Integer.parseInt(parts[4])));
        });
        arrivedDateColumn.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[5]);
        });
    }

    /**
     * Loads active orders for the current subscriber from the server.
     */
    private void loadSubscriberOrders() {
        HashMap<String, String> showOrdersMap = new HashMap<>();
        showOrdersMap.put("ShowSubscriberOrders", String.valueOf(currentSubscriberId));
        ClientUI.chat.accept(showOrdersMap);
        List<String> ordersList = ChatClient.getListfromServer();
        ordersTable.setItems(FXCollections.observableArrayList(ordersList));
    }

    /**
     * Handles the Load Order button click event.
     * Loads and displays details for the specified order number.
     * 
     * @param event The action event
     */
    @FXML
    public void LoadOrderBtn(ActionEvent event) {
        String orderNumber = txtOrderNumber.getText();
        if (orderNumber.matches("\\d+")) {
            try {
                Orders order = new Orders(Integer.parseInt(orderNumber));
                // Verify this order belongs to the current subscriber
                if (order.getSubscriberId() == currentSubscriberId) {
                    lblBookName.setText("Book Name: " + order.getBookBarcode());
                    lblStatus.setText("Status: " + getStatusText(order.getStatus()));
                    lblError.setText("");
                } else {
                    lblError.setText("This order does not belong to you");
                }
            } catch (Exception e) {
                lblError.setText("Error loading order: Order not found");
            }
        } else {
            lblError.setText("Please enter a valid order number");
        }
    }

    /**
     * Handles the Cancel Order button click event.
     * Cancels the specified order if it is in a cancellable state.
     * 
     * @param event The action event
     */
    @FXML
    public void CancelOrderBtn(ActionEvent event) {
        String orderNumber = txtOrderNumber.getText();
        if (orderNumber.matches("\\d+")) {
            try {
                Orders order = new Orders(Integer.parseInt(orderNumber));
                if (order.getSubscriberId() != currentSubscriberId) {
                    lblError.setText("This order does not belong to you");
                    return;
                }
                
                if (order.getStatus() == 1) { // Active status
                    order.setStatus(-1); // Set to cancelled
                    order.UpdateDetails();
                    loadSubscriberOrders(); // Refresh the table
                    lblError.setText("Order cancelled successfully");
                    lblStatus.setText("Status: Cancelled");
                } else {
                    lblError.setText("Order cannot be cancelled - invalid status");
                }
            } catch (Exception e) {
                lblError.setText("Error cancelling order: " + e.getMessage());
            }
        } else {
            lblError.setText("Please enter a valid order number");
        }
    }

    /**
     * Converts numeric status to readable text.
     * 
     * @param status The numeric status code
     * @return String representation of the status
     */
    private String getStatusText(int status) {
        switch (status) {
            case -1: return "Cancelled";
            case 0: return "Pending";
            case 1: return "Fulfilled";
            default: return "Unknown";
        }
    }

    /**
     * Handles the back button action.
     * Returns to the menu screen.
     * 
     * @param event The action event
     * @throws IOException If there is an error loading the menu screen
     */
    public void Back(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/Menu.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/Menu.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Menu");
        stage.show();
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    /**
     * Handles the exit button action.
     * Disconnects from the server and exits the application.
     * 
     * @param event The action event
     * @throws Exception If there is an error during disconnect
     */
	public void getExitBtn(ActionEvent event) throws Exception {
		System.out.println("Disconnecting from the Server and ending the program.");
		HashMap<String, String> EndingConnections = new HashMap<String, String>();
		EndingConnections.put("Disconnect", "");
		ClientUI.chat.accept(EndingConnections);
		System.exit(0);
	}
}





    
    /*
     * 
			Subscriber chen = new Subscriber(Integer.parseInt(idnumber));
			
			txtName.setText(chen.getName());
			
			String newName = txtName.getText();
			
			chen.setName(newName);
			chen.setEmail("chen@gmail.com");
			
			chen.UpdateDetails();
			
			/*
     */
    
    

