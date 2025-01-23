package controllers;

import java.io.IOException;
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

/**
* This class handles the GUI for viewing notifications.
* It displays all notifications in the system in a table format.
* 
* @author chen
*/
public class ViewNotificationsController {
   
   @FXML
   private Button btnExit = null;
   @FXML
   private Button btnBack = null;

   @FXML
   private TableView<String> notificationsTable;
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
   

   /**
    * Initializes the controller and sets up the table columns.
    * This method is automatically called after the FXML file has been loaded.
    */
   public void initialize() {
       // Setup columns mapping
       serialColumn.setCellValueFactory(cellData -> {
           String[] parts = cellData.getValue().split(", ");
           return new javafx.beans.property.SimpleStringProperty(parts[0]);
       });
       
       messageColumn.setCellValueFactory(cellData -> {
           String[] parts = cellData.getValue().split(", ");
           return new javafx.beans.property.SimpleStringProperty(parts[1]);
       });
       
       subscriberIdColumn.setCellValueFactory(cellData -> {
           String[] parts = cellData.getValue().split(", ");
           return new javafx.beans.property.SimpleStringProperty(parts[2]);
       });
       
       dateColumn.setCellValueFactory(cellData -> {
           String[] parts = cellData.getValue().split(", ");
           return new javafx.beans.property.SimpleStringProperty(parts[3]);
       });
       
       borrowNumberColumn.setCellValueFactory(cellData -> {
           String[] parts = cellData.getValue().split(", ");
           return new javafx.beans.property.SimpleStringProperty(parts[4]);
       });

       // Load notifications when screen initializes
       loadNotifications();
   }
   
   
   private void loadNotifications() {
	   List<String> notificationsList = Notifications.getAllNotificationsFromDB();
       notificationsTable.setItems(FXCollections.observableArrayList(notificationsList));

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
       HashMap<String, String> EndingConnections = new HashMap<>();
       EndingConnections.put("Disconnect", "");
       ClientUI.chat.accept(EndingConnections);
       System.exit(0);
   }
}