package controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import client.ChatClient;
import client.ClientUI;
import entity.Subscriber;
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

public class SubscriberStatusTableController {

    @FXML
    private TableView<String> subscriberTable;
    
    @FXML
    private TableColumn<String, String> subscriberIdColumn;
    @FXML
    private TableColumn<String, String> subscriberNameColumn;
    @FXML
    private TableColumn<String, String> statusColumn;
    
    @FXML
    private Button btnBack;
    @FXML
    private Button btnExit;

    private static List<String> currentSubscribers;

    @FXML
    public void initialize() {
        setupColumns();
        loadSubscriberData();
    }

    private void setupColumns() {
        subscriberIdColumn.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[0]);
        });
        subscriberNameColumn.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[1]);
        });
        statusColumn.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[2]);
        });
    }

    private void loadSubscriberData() {
        try {
            currentSubscribers = Subscriber.getMonthlySubscriberStats();
            subscriberTable.setItems(FXCollections.observableArrayList(currentSubscribers));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void Back(ActionEvent event) throws IOException {
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

    @FXML
    public void getExitBtn(ActionEvent event) throws Exception {
        System.out.println("Disconnecting from the Server and ending the program.");
        HashMap<String, String> EndingConnections = new HashMap<>();
        EndingConnections.put("Disconnect", "");
        ClientUI.chat.accept(EndingConnections);
        System.exit(0);
    }
}