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

public class GenerateReportsController {

    @FXML
    private Button btnBack;
    
    @FXML
    private Button btnExit;

    /**
     * Opens the Borrowed Books Report screen
     * @param event The action event
     * @throws IOException If there is an error loading the screen
     */
    @FXML
    public void showBorrowedBooksReport(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/BorrowedBookReport.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/BorrowedBookReport.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Borrowed Books Report");
        stage.show();
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    /**
     * Opens the Subscriber Status Report screen
     * @param event The action event
     * @throws IOException If there is an error loading the screen
     */
    @FXML
    public void showSubscriberStatusReport(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SubscriberStatusReport.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/SubscriberStatusReport.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Subscriber Status Report");
        stage.show();
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    /**
     * Returns to the previous menu screen
     * @param event The action event
     * @throws IOException If there is an error loading the screen
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
     * Exits the application and disconnects from the server
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