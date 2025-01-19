package controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import client.ChatClient;
import client.ClientUI;
import entity.BorrowedRecord;
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

public class BorrowedBooksTableController {

    @FXML
    private TableView<String> borrowedBooksTable;
    
    @FXML
    private TableColumn<String, String> borrowNumberColumn;
    @FXML
    private TableColumn<String, String> subscriberIdColumn;
    @FXML
    private TableColumn<String, String> bookBarcodeColumn;
    @FXML
    private TableColumn<String, String> bookTitleColumn;
    @FXML
    private TableColumn<String, String> copyNumberColumn;
    @FXML
    private TableColumn<String, String> borrowDateColumn;
    @FXML
    private TableColumn<String, String> expectedReturnDateColumn;
    @FXML
    private TableColumn<String, String> librarianIdColumn;
    
    @FXML
    private Button btnBack;
    @FXML
    private Button btnExit;

    private static List<String> currentBorrowedBooks;

    @FXML
    public void initialize() {
        setupColumns();
        loadCurrentBorrowedBooks();
    }

    private void setupColumns() {
        borrowNumberColumn.setCellValueFactory(cellData -> {
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
        bookTitleColumn.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[3]);
        });
        copyNumberColumn.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[4]);
        });
        borrowDateColumn.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[5]);
        });
        expectedReturnDateColumn.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[6]);
        });
        librarianIdColumn.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[8]);
        });
    }

    private void loadCurrentBorrowedBooks() {
        try {
            currentBorrowedBooks = BorrowedRecord.getMonthlyBorrowedBooksStats();
            borrowedBooksTable.setItems(FXCollections.observableArrayList(currentBorrowedBooks));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void Back(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GenerateReports.fxml")); // או שם המסך הקודם שלך
        Parent root = loader.load();
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/GenerateReports.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Generate Reports");
        stage.show();
        ((Node) event.getSource()).getScene().getWindow().hide();
    }
    @FXML
    public void getExitBtn(ActionEvent event) throws Exception {
        System.out.println("Disconnecting from the Server and ending the program.");
        HashMap<String, String> EndingConnections = new HashMap<String, String>();
        EndingConnections.put("Disconnect", "");
        ClientUI.chat.accept(EndingConnections);
        System.exit(0);
    }
}