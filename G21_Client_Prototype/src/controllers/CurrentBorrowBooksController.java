package controllers;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;
import entity.*;

/**
 * This class is showing us the GUI with table of all active borrows of
 * subscriber by id, and option to extend borrow by the librarian.
 */
public class CurrentBorrowBooksController {

	@FXML
	private Label lblTitle;
	@FXML
	private Label lblInstructions;
	@FXML
	private Label lblInstructions2;
	@FXML
	private Label lblId;
	@FXML
	private Label lblBorrowNum;
	@FXML
	private Label lblDaysExtend;
	@FXML
	private Label lblMessage;
	@FXML
	private Label lblSelectedBorrowDetails;

	@FXML
	private Button btnExit = null;
	@FXML
	private Button btnBack = null;
	@FXML
	private Button btnShowAllMyActiveBorrows = null;
	@FXML
	private Button btnLoadOneBorrow = null;
	@FXML
	private Button btnExtendSelectedBorrow = null;

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

	public static Subscriber mySubscriber;

	private BorrowedRecord chosenRecord;

	/*
	 * Author: Yuval. initialize the GUI with the data to the table view.
	 */
	public void initialize() {
		if (mySubscriber != null) {
			txtSubscriberId.setText(String.valueOf(mySubscriber.getId()));
			try {
				List<String> myActiveBorrows = BorrowedRecord.getSubscriberActiveBorrowsFromDB(mySubscriber.getId());
				loadactiveBorrowRecords(myActiveBorrows);
				txtBorrowNumber.setEditable(true);
				btnLoadOneBorrow.setDisable(false);
			} catch (Exception e) {
				changeString(e.getMessage(), "#bf3030");
				loadactiveBorrowRecords(new ArrayList<String>());
				txtBorrowNumber.setEditable(false);
				btnLoadOneBorrow.setDisable(true);
			}
		}else {
			loadactiveBorrowRecords(new ArrayList<String>());
			txtBorrowNumber.setEditable(false);
			btnLoadOneBorrow.setDisable(true);
		}
		txtDaysToExtend.setEditable(false);
		btnExtendSelectedBorrow.setDisable(true);
		chosenRecord = null;
		lblSelectedBorrowDetails.setText("");
		if (!txtBorrowNumber.getText().isEmpty()) {
			txtBorrowNumber.setText("");
		}
		if (!txtDaysToExtend.getText().isEmpty()) {
			txtDaysToExtend.setText("");
		}
		initTheTable();

	}

	private void initTheTable() {
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

	private enum myEnum {
		checkid, checkBorrow, checkDays;
	}

	private boolean VerifyInput(myEnum x) {

		switch (x) {
		case checkid:
			if (txtSubscriberId.getText().isEmpty()) {
				changeString("Please fill id before trying to load subscriber data.", "#bf3030");
				return false;
			}

			if (!txtSubscriberId.getText().matches("\\d{9}")) {
				changeString("ID must be 9 digits.", "#bf3030");
				return false;
			}
			break;

		case checkBorrow:
			lblSelectedBorrowDetails.setText("");
			if (txtBorrowNumber.getText().isEmpty()) {
				changeString("Please fill the borrow number before trying load it.", "#bf3030");
				return false;
			} else if (!txtBorrowNumber.getText().matches("\\d+")) {
				changeString("Borrow number must contain only digits.", "#bf3030");
				return false;
			} else {
				for (int i = 0; i < tableBorrows.getItems().size(); i++) {
					String borrowNumber = tableBorrows.getColumns().get(0).getCellData(i).toString();
					if (borrowNumber.equals(txtBorrowNumber.getText())) {
						return true;
					}
				}
				changeString("You must enter borrow id from the shown table.", "#bf3030");
				return false;
			}
		case checkDays:
			if (txtDaysToExtend.getText().isEmpty()) {
				changeString("Please fill the days before trying extend the borrow.", "#bf3030");
				return false;
			} else if (!txtDaysToExtend.getText().matches("\\d+")) {
				changeString("Days to extend can contains only digits.", "#bf3030");
				return false;
			}
			break;

		default:
			throw new IllegalArgumentException("Unexpected value: " + x);
		}
		return true;

	}

	/**
	 * This method is changing the message on the String to 10 seconds
	 * 
	 * @param s - the message we want to see on the GUI
	 */
	private void changeString(String s, String color) {
		Platform.runLater(() -> {
			lblMessage.setText(s);
			lblMessage.setTextFill(Paint.valueOf(color));
		});
		PauseTransition pause = new PauseTransition(Duration.seconds(10));
		pause.setOnFinished(event -> {
			lblMessage.setText(" ");
		});
		pause.play();
	}

	/**
	 * Author: Yuval. This method is for the Load borrow button, checking if extend
	 * can be done (before asking in how many days).
	 * 
	 * @param event - the click on the LoadBorrow button.
	 */
	public void btnLoadAllMyActiveBorrows(ActionEvent event) {

		if (VerifyInput(myEnum.checkid)) {
			Subscriber newIDSub = null;
			try {
				newIDSub = new Subscriber(Integer.parseInt(txtSubscriberId.getText()));
				mySubscriber = newIDSub;
				initialize();

			} catch (Exception e) {
				changeString(e.getMessage(), "#bf3030");
			}
		}

	}

	public void btnLoadOneBorrowToEdit(ActionEvent event) {
		if (VerifyInput(myEnum.checkBorrow)) {
			txtDaysToExtend.setEditable(true);
			btnExtendSelectedBorrow.setDisable(false);
			chosenRecord = new BorrowedRecord(Integer.parseInt(txtBorrowNumber.getText()));
			lblSelectedBorrowDetails.setText("You will extend the borrow id = " + chosenRecord.getBorrowNumber()
					+ " for book: " + chosenRecord.getBookTitle() + " Current expected return date: "
					+ chosenRecord.getBorrowExpectReturnDate());
		}
	}

	/**
	 * Author: Yuval. This method is for the extend borrow button. sending the
	 * information to the server about which borrow to extend and which date to.
	 * 
	 * @param event - the click on the ExtendBorrow button.
	 */
	public void btnExtendBorrow(ActionEvent event) {
		if (VerifyInput(myEnum.checkDays)) {
			String borrowNumberInput = txtBorrowNumber.getText();
			String expectReturnDate = String.valueOf(chosenRecord.getBorrowExpectReturnDate());

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate expectReturnDate_Date = LocalDate.parse(expectReturnDate, formatter);

			// adding extend days to the expect return date.
			String daysToExtend = txtDaysToExtend.getText();
			LocalDate newExpectReturnDate = expectReturnDate_Date.plusDays(Integer.parseInt(daysToExtend));

			chosenRecord.setBorrowExpectReturnDate(Date.valueOf(newExpectReturnDate));
			chosenRecord.setLibrarianId(ChatClient.getCurrectLibrarian().getId());
			chosenRecord.setLibrarianName(ChatClient.getCurrectLibrarian().getName());
			if (chosenRecord.UpdateBorrowDetails()) {
				initialize();
				changeString("Borrow num: " + borrowNumberInput + " extended successfully with " + daysToExtend
						+ " days to the date: " + Date.valueOf(newExpectReturnDate), "#086f03");
			} else {
				changeString("Error while update the extend Date", "#bf3030");
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
		FXMLLoader Loader = new FXMLLoader(getClass().getResource("/gui/LibrarianMenu.fxml"));
		Parent Root = Loader.load();
		Stage Stage = new Stage();
		Scene Scene = new Scene(Root);
		Scene.getStylesheets().add(getClass().getResource("/gui/LibrarianMenu.css").toExternalForm());
		Stage.setScene(Scene);
		Stage.setTitle("Librarian Menu");
		Stage.show();
		((Node) event.getSource()).getScene().getWindow().hide();
	}

}
