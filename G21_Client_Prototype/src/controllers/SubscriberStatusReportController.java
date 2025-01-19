package controllers;

import java.io.IOException;
import java.util.HashMap;

import client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class SubscriberStatusReportController {
    
    @FXML
    private Button btnTableView;
    
    @FXML
    private Button btnChartView;
    
    @FXML
    private Button btnBack;
    
    @FXML
    private Button btnExit;
    
    /**
     * Shows the table view of subscribers status
     */
    @FXML
    public void showTableView(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SubscriberStatusTable.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/SubscriberStatusTable.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Subscribers Status - Table View");
        stage.show();
        ((Node) event.getSource()).getScene().getWindow().hide();
    }
    
    /**
     * Shows the bar chart view of subscribers status
     */
    @FXML
    public void showChartView(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SubscriberStatusChart.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/SubscriberStatusChart.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Subscribers Status - Chart View");
        stage.show();
        ((Node) event.getSource()).getScene().getWindow().hide();
    }
    
    /**
     * Returns to the previous reports menu screen
     */
    @FXML
    public void Back(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GenerateReports.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/GenerateReports.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Generate Reports");
        stage.show();
        ((Node) event.getSource()).getScene().getWindow().hide();
    }
    
    /**
     * Exits the application and disconnects from the server
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