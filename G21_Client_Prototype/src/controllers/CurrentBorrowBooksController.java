package controllers;

import java.awt.TextField;

import java.awt.desktop.AboutHandler;
import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator.OfDouble;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javax.naming.AuthenticationException;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import client.ChatClient;
import client.ClientUI;
import javafx.application.Platform;
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
 * This class is showing us the GUI with table of all active borrows of subscriber by id, and option to extend borrow by the librarian.
 */
public class CurrentBorrowBooksController {

	@FXML
    private Label lblId;
    @FXML
    private Label lblBorrowId;
    @FXML
    private Label lblIdError; 
    @FXML
    private Label lblMessageStatus;
    
    @FXML
    private Button btnExit = null;
    @FXML
    private Button btnBack = null;
    @FXML
    private Button btnShowBorrows = null;
    @FXML
    private Button btnExtendBorrow = null;   
    @FXML
    private Button btnLoadBorrow = null;  
    
    @FXML
    private TextField txtSubscriberId;
    @FXML
    private TextField txtBorrowNumber;
    @FXML
    private TextField txtDaysToExtend;
      
    @FXML
    private TableView<String> tableBorrows;
    @FXML
    private TableColumn<String, String> colBorrowNumber;
    @FXML
    private TableColumn<String, String> colBookBarcode;
    @FXML
    private TableColumn<String, String> colBookTitle;
    @FXML
    private TableColumn<String, String> colBookCopyNo;
    @FXML
    private TableColumn<String, String> colBorrowDate;
    @FXML
    private TableColumn<String, String> colExpectReturnDate;
    
    private boolean isBorrowsLoaded = false; // flag to track if table borrows are loaded
    private boolean isBorrowBookLoaded = false; // flag to track if borrow book is loaded

    /*Author: Yuval.
	 * initialize the GUI with the data to the table view.
	 */
    public void initialize() {
        // Split each row of data (string) and display it in separate columns
        colBorrowNumber.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[0]);
        });

        colBookBarcode.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[1]);
        });

        colBookTitle.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[2]);
        });

        colBookCopyNo.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[3]);
        });

        colBorrowDate.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[4]);
        });

        colExpectReturnDate.setCellValueFactory(cellData -> {
            String[] parts = cellData.getValue().split(", ");
            return new javafx.beans.property.SimpleStringProperty(parts[5]);
        });
    }
    
    
    /**Author: Yuval.
     * This method is getting list of the active Borrow Record and uploading it to the table view
     * @param activeBorrowRecords - list of the active borrow records.
     */
    public void loadactiveBorrowRecords(List<String> activeBorrowRecords) {
        // Convert the list to an observable list and set it to the table
    	tableBorrows.setItems(FXCollections.observableArrayList(activeBorrowRecords));
    	isBorrowsLoaded = true;
    }
    
	/**Author: Yuval.
	 * This method is for the Show Borrows button. getting the string from the
	 * server and calling other method "loadactiveBorrowRecords()" to handle it and load into the table.
	 * 
	 * @param event - the click on the show borrows button.
	 */
	public void ShowBorrowsbtn(ActionEvent event) {
		String idnumber = txtSubscriberId.getText();
		// The string contains only numbers.
		if (idnumber.matches("\\d{9}")) {
			HashMap<String, String> loadthisid = new HashMap<String, String>();
			loadthisid.put("Subscriber Active Borrows", idnumber);
			ClientUI.chat.accept(loadthisid);
			List<String> listBorrows = ChatClient.getListfromServer();
			loadactiveBorrowRecords(listBorrows);
			// The string contains chars that not digits or empty.
		} else {
			lblIdError.setText("Please enter id that contains ONLY 9 digits");
		}
	}

	
	/**Author: Yuval.
	 * This method is for the Load borrow button, checking if extend can be done (before asking in how many days).  
	 * 
	 * @param event - the click on the LoadBorrow button.
	 */
	public void btnLoadBorrow(ActionEvent event) {
		
		if (!isBorrowsLoaded) {
            lblMessageStatus.setText("You must load your active borrows first by entering your id first.");
            return; 
        }
		
		String borrowNumberInput = txtBorrowNumber.getText();
	
		// Try to retrieve the borrow record using the borrow number
	    BorrowedRecord borrowedRecord = null;
	    try {
	        // Create BorrowedRecord object using the borrow number
	        borrowedRecord = new BorrowedRecord(Integer.parseInt(borrowNumberInput));

	    } catch (NoSuchElementException e) {
	        // If the borrow record is not found, show an error message
	        lblMessageStatus.setText("Borrow number not found.");
	        return;
	    }

	    // Check if there are any waiting orders for the book
	    Book book = new Book(borrowedRecord.getBookBarcode());
	    if (book.getOrdersNumber() > 0) {
	        lblMessageStatus.setText("There are waiting orders for this book. Cannot extend borrow.");
	        return;
	    }
		
		isBorrowBookLoaded = true;
	
	}
	
	
    
	/**Author: Yuval.
	 * This method is for the extend borrow button.  
	 * sending the information to the server about which borrow to extend and which date to.
	 * 
	 * @param event - the click on the ExtendBorrow button.
	 */
	public void btnExtendBorrow(ActionEvent event) {
		
		if (!isBorrowBookLoaded) {
            lblMessageStatus.setText("You must load specific book borrow first.");
            return; 
        }
		
		HashMap<String, String> updateHashMap = new HashMap<String, String>();
		String newinfo = new String();
		String expectReturnDate = new String();
		String borrowNumberInput = txtBorrowNumber.getText();
		
		// Try to retrieve the borrow record using the borrow number
	    BorrowedRecord borrowedRecord = null;
		
	    // Create BorrowedRecord object using the borrow number
        borrowedRecord = new BorrowedRecord(Integer.parseInt(borrowNumberInput));
		
        // Get expected return date from BorrowedRecord
        expectReturnDate = borrowedRecord.getBorrowExpectReturnDate().toString();
	    
	    // Parse the expected return date
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	    LocalDate expectReturnDate_Date = LocalDate.parse(expectReturnDate, formatter);
	    
	    //adding extend days to the expect return date.
	    String daysToExtend = txtDaysToExtend.getText();
	    LocalDate newExpectReturnDate = expectReturnDate_Date.plusDays(Integer.parseInt(daysToExtend));
	    String newExpectReturnDateStr = newExpectReturnDate.toString();
		
	    //send to server.
	    newinfo = borrowNumberInput + " " + newExpectReturnDateStr + " " + borrowedRecord.getBorrowActualReturnDate().toString() + " " + '0';
        updateHashMap.put("Update Borrow Details", newinfo);

		// first we want to see on the GUI message to send the data and just then send it.
		lblMessageStatus.setText("Request sent to the server.\n Please wait...");
		Callable<Void> task = () -> {
			Platform.runLater(() -> {
				ClientUI.chat.accept(updateHashMap);
				String str = ChatClient.getStringfromServer();
				if (str.equals("Borrow record has been updated")) {
					lblMessageStatus.setText("Your extension request was approved");
				} else {
					lblMessageStatus.setText("Cant get your extension request.");
				}
			});
			return null;
		};
		
		/////////////////////////////worker librarian to einav////////////////////////////////////
		Librarian worker = new Librarian(123);
		String myWorkerName = worker.getName();
		int myWorkerId = worker.getId();
        /////////////////////////////worker librarian to einav////////////////////////////////////
		
		FutureTask<Void> futureTask = new FutureTask<>(task);
		new Thread(futureTask).start();
		try {
			futureTask.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

    
	/*Author: Yuval.
	 * This method is for the exit button sending a message to the server that now we are disconnecting,
	 * closing the GUI and the connection for the server.
	 */
	public void getExitBtn(ActionEvent event) throws Exception {
		System.out.println("Disconnecting from the Server and ending the program.");
		HashMap<String, String> EndingConnections = new HashMap<String, String>();
		EndingConnections.put("Disconnect", "");
		ClientUI.chat.accept(EndingConnections);
		System.exit(0);
	}
     
    
    /**Author: Yuval.
     * This method is for the back button closing the current GUI and uploading the menu GUI.
     * @param event - click on the back button.
     * @throws IOException
     */
    public void Back(ActionEvent event) throws IOException {
        FXMLLoader studentLoader = new FXMLLoader(getClass().getResource("/gui/Menu.fxml"));
        Parent studentRoot = studentLoader.load();
        Stage studentStage = new Stage();
        Scene studentScene = new Scene(studentRoot);
        studentScene.getStylesheets().add(getClass().getResource("/gui/Menu.css").toExternalForm());
        studentStage.setScene(studentScene);
        studentStage.setTitle("Menu");
        studentStage.show();
        ((Node) event.getSource()).getScene().getWindow().hide();
    }
 
}
