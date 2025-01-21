package controllers;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import client.ChatClient;
import client.ClientUI;
import javafx.animation.PauseTransition;
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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import entity.*;

/**
 * This class is showing us the GUI with table of all active borrows of
 * subscriber by id, and option to extend borrow request.
 */
public class ExtendBorrowRequestController {

	@FXML
	private Label lblTitle;
	@FXML
	private Label lblBorrowNum;
	@FXML
	private Label lblMessageStatus;
	@FXML
	private Label lblErrMessage;

	@FXML
	private Button btnExit = null;
	@FXML
	private Button btnBack = null;
	@FXML
	private Button btnExtendBorrow = null;

	@FXML
	private TextField txtBorrowNumber;

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

	private Subscriber me = ChatClient.getCurrectSubscriber();

	public void start(Stage primaryStage) {
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("/gui/ExtendBorrowRequest.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/gui/ExtendBorrowRequest.css").toExternalForm());
			primaryStage.setTitle("Extend Borrow Request");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * Author: Yuval. initialize the GUI with the data to the table view.
	 */
	public void initialize() {
		try {
			List<String> res = BorrowedRecord.getSubscriberActiveBorrowsFromDB(me.getId());
			loadactiveBorrowRecords(res);
			initTheTable();
		} catch (Exception e) {
			changeString(e.getMessage(),lblErrMessage);
		}
	}

	/**
	 * This method is changing the message on the String to 10 seconds
	 * 
	 * @param s - the message we want to see on the GUI
	 */
	private void changeString(String s, Label lbl) {
		Platform.runLater(() -> {
			lbl.setText(s);
		});
		PauseTransition pause = new PauseTransition(Duration.seconds(10));
		pause.setOnFinished(event -> {
			lbl.setText("");
		});
		pause.play();
	}

	public void initTheTable() {
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

	/**
	 * Author: Yuval. This method is getting list of the active Borrow Record and
	 * uploading it to the table view
	 * 
	 * @param activeBorrowRecords - list of the active borrow records.
	 */
	public void loadactiveBorrowRecords(List<String> activeBorrowRecords) {
		// Convert the list to an observable list and set it to the table
		tableBorrows.setItems(FXCollections.observableArrayList(activeBorrowRecords));
	}

	private boolean VerifyInput() {

		if (txtBorrowNumber.getText().isEmpty()) {
			changeString("You must enter the borrow number that you want to extend",lblErrMessage);
			return false;
		}

		for (int i = 0; i < tableBorrows.getItems().size(); i++) {
			String borrowNumber = tableBorrows.getColumns().get(0).getCellData(i).toString();
			if (borrowNumber.equals(txtBorrowNumber.getText())) {
				return true;
			}
		}
		changeString("You must enter borrow id from the shown table.",lblErrMessage);
		return false;

	}

	/**
	 * Author: Yuval. This method is for the extend borrow button, checking if
	 * extend can be done. sending the information to the server about which borrow
	 * to extend and which date to. what fields to change.
	 * 
	 * @param event - the click on the ExtendBorrow button.
	 */
	public void ExtendTheRequestedBorrowbtn(ActionEvent event) {
		if (VerifyInput()) {
			try {
				BorrowedRecord chosenBorrow = new BorrowedRecord(Integer.parseInt(txtBorrowNumber.getText()));
				Book book = new Book(chosenBorrow.getBookBarcode());
				if (book.getOrdersNumber() > 0) {
					changeString("There are waiting orders for this book. Cannot extend borrow.",lblErrMessage);
				} else {
					
					LocalDate currentDate = LocalDate.now();
					LocalDate expectReturnDate = chosenBorrow.getBorrowExpectReturnDate().toLocalDate(); // Assuming `chosenBorrow.getBorrowExpectReturnDate()` returns java.sql.Date

					// Check if the extension is requested within one week from the expected return date
					if (ChronoUnit.DAYS.between(currentDate, expectReturnDate) > 7) {
					    changeString("Reject!! - You can only extend the borrow within one week of the expected return date.",lblErrMessage);
					} else {
					    LocalDate newExpectReturnDate = expectReturnDate.plusDays(7);
					    chosenBorrow.setBorrowExpectReturnDate(Date.valueOf(newExpectReturnDate)); // Convert LocalDate to java.sql.Date
						if(chosenBorrow.UpdateBorrowDetails()) {
							changeString("Borrow extended successfully.",lblMessageStatus);
							initialize();
						}
						else {
							changeString("Error while update the extend Date",lblErrMessage);
						}
						
					}

				}
			} catch (NoSuchElementException e) {
				changeString(e.getMessage(),lblErrMessage);
			}
		}
	}

	/*
	 * Author: Yuval. This method is for the exit button sending a message to the
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

	/**
	 * Author: Yuval. This method is for the back button closing the current GUI and
	 * uploading the menu GUI.
	 * 
	 * @param event - click on the back button.
	 * @throws IOException
	 */
	public void Back(ActionEvent event) throws IOException {
		FXMLLoader Loader = new FXMLLoader(getClass().getResource("/gui/SubscriberMenu.fxml"));
		Parent Root = Loader.load();
		Stage Stage = new Stage();
		Scene Scene = new Scene(Root);
		Scene.getStylesheets().add(getClass().getResource("/gui/SubscriberMenu.css").toExternalForm());
		Stage.setScene(Scene);
		Stage.setTitle("Subscriber Menu");
		Stage.show();
		((Node) event.getSource()).getScene().getWindow().hide();
	}

}
