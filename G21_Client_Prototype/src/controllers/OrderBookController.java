package controllers;

import java.io.IOException;
import java.util.HashMap;
import client.ClientUI;
import client.ChatClient;
import entity.Subscriber;
import entity.Book;
import entity.Orders;
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
 * This class handles the GUI for ordering books.
 * It allows subscribers to order books by entering their barcode.
 * 
 * @author Chen
 */
public class OrderBookController {
    
    @FXML
    private TextField txtBarcode;
    
    @FXML
    private Button btnOrder;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnExit;
    
    @FXML
    private Label lblStatus;
    
    private int currentSubscriberId;

    /**
     * Sets the current subscriber ID for making orders.
     * 
     * @param subscriberId The ID of the subscriber making the order
     */
    public void setSubscriber(int subscriberId) {
        this.currentSubscriberId = subscriberId;
    }

    /**
     * Checks if the subscriber's status is active.
     * 
     * @return boolean True if subscriber is active, false otherwise
     */
    private boolean isSubscriberActive() {
        try {
            Subscriber subscriber = new Subscriber(currentSubscriberId);
            return subscriber.getStatus().equals("Active");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if a book is available for ordering.
     * 
     * @param barcode The barcode of the book to check
     * @return boolean True if book is available, false otherwise
     */
    private boolean isBookAvailable(String barcode) {
        try {
            Book book = new Book(barcode);
            int totalCopies = book.getAllCopies();
            
            // Get active orders count
            HashMap<String, String> activeOrdersMap = new HashMap<>();
            activeOrdersMap.put("GetActiveOrdersCount", barcode);
            ClientUI.chat.accept(activeOrdersMap);
            int activeOrders = Integer.parseInt(ChatClient.getStringfromServer());
            
            return activeOrders < totalCopies;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Handles the order button click event.
     * Validates subscriber status and book availability before making order.
     * 
     * @param event The action event
     */
    @FXML
    public void handleOrderButton(ActionEvent event) {
        String barcode = txtBarcode.getText().trim();
        
        if (barcode.isEmpty()) {
            lblStatus.setText("Please enter book barcode");
            lblStatus.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }
        
        if (!isSubscriberActive()) {
            lblStatus.setText("Cannot make order - Subscriber account is frozen");
            lblStatus.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }
        
        try {
            Book book = new Book(barcode); // Verify book exists
            if (!isBookAvailable(barcode)) {
                lblStatus.setText("Cannot make order - No copies available");
                lblStatus.setTextFill(javafx.scene.paint.Color.RED);
                return;
            }
            
            new Orders(currentSubscriberId, barcode);
            lblStatus.setText("Order created successfully");
            lblStatus.setTextFill(javafx.scene.paint.Color.GREEN);
            txtBarcode.clear();
            
        } catch (Exception e) {
            lblStatus.setText("Error: Book not found or invalid barcode");
            lblStatus.setTextFill(javafx.scene.paint.Color.RED);
        }
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
    @FXML
    public void getExitBtn(ActionEvent event) throws Exception {
        System.out.println("Disconnecting from the Server and ending the program.");
        HashMap<String, String> EndingConnections = new HashMap<String, String>();
        EndingConnections.put("Disconnect", "");
        ClientUI.chat.accept(EndingConnections);
        System.exit(0);
    }
}