package librarianControllers;

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
import mainControllers.ConnectionSetupController;
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
	private Button btnLostBook = null;
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
	@FXML
	private TableColumn<String, String> colLostBook;

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
		} else {
			loadactiveBorrowRecords(new ArrayList<String>());
			txtBorrowNumber.setEditable(false);
			btnLoadOneBorrow.setDisable(true);
		}
		txtDaysToExtend.setEditable(false);
		btnExtendSelectedBorrow.setDisable(true);
		btnLostBook.setDisable(true);
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


	/**
	 * plit each row of data (string) and display it in separate columns
	 */
	private void initTheTable() {
		
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

		colLostBook.setCellValueFactory(cellData -> {
			String[] parts = cellData.getValue().split(", ");
			if (Integer.parseInt(parts[6]) > 0) {

				return new javafx.beans.property.SimpleStringProperty("Book Lost");
			}
			return new javafx.beans.property.SimpleStringProperty("");
		});

		colLostBook.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
					setStyle(""); // Clear style
				} else {
					setText(item);
					if (item.equalsIgnoreCase("Book Lost")) {
						setTextFill(javafx.scene.paint.Color.RED);
					}
				}
			}
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
		checkid, checkBorrow, checkDays, LostBook;
	}
	
	/*
	 * switch case for convenient distribution of the actions of the user
	 * in each case it check if the input is valid.
	 * present message in the GUI in any case
	 * @param x
	 * @return
	 */
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
			
		case LostBook:
			if(VerifyInput(myEnum.checkBorrow)) {
				for (int i = 0; i < tableBorrows.getItems().size(); i++) {
					String borrowNumber = tableBorrows.getColumns().get(0).getCellData(i).toString();
					String lostStatus = tableBorrows.getColumns().get(6).getCellData(i).toString();
					if (borrowNumber.equals(String.valueOf(chosenRecord.getBorrowNumber())) && !lostStatus.equals("Lost Book")) {
						return true;
					}
				}
				return false;
			}
			return false;

		default:
			throw new IllegalArgumentException("Unexpected value: " + x);
		}
		return true;

	}

	/**
	 * This method is changing the message on the String to 10 seconds
	 * 
	 * @param s - the message we want to see on the GUI
	 * @param color
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

	/**
	 * load one borrow record to edit(lost book or extend borrow)
	 * @param event
	 */
	public void btnLoadOneBorrowToEdit(ActionEvent event) {
		if (VerifyInput(myEnum.checkBorrow)) {
			btnLostBook.setDisable(false);
			txtDaysToExtend.setEditable(true);
			btnExtendSelectedBorrow.setDisable(false);
			chosenRecord = new BorrowedRecord(Integer.parseInt(txtBorrowNumber.getText()));
			lblSelectedBorrowDetails.setText("You will extend the borrow id = " + chosenRecord.getBorrowNumber()
					+ " for book: \"" + chosenRecord.getBookTitle() + "\" \nCurrent expected return date: "
					+ chosenRecord.getBorrowExpectReturnDate());
		}
	}

	/*
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

			Reminders currReminder = new Reminders(chosenRecord.getReminderSerial());

			Date newDateReminder = Date.valueOf(expectReturnDate_Date.plusDays(Integer.parseInt(daysToExtend) - 1));
			String newMSG = "Reminder to return the book: \"" + chosenRecord.getBookTitle()
					+ "\" tommorow on the date: " + newExpectReturnDate + ".";

			if (!Date.valueOf(LocalDate.now()).before(currReminder.getDate())) {
				currReminder = new Reminders(newMSG, mySubscriber.getId(), mySubscriber.getPhoneNumber(),
						mySubscriber.getEmail(), newDateReminder);
			} else {
				currReminder.setMessage(newMSG);
				currReminder.setSubscriberPhone(mySubscriber.getPhoneNumber());
				currReminder.setSubscriberEmail(mySubscriber.getEmail());
				currReminder.setDate(newDateReminder);
				currReminder.UpdateDetails();
			}
			if (!Date.valueOf(LocalDate.now()).before(currReminder.getDate())) {
				chosenRecord.setReminderSerial(currReminder.getSerial());
			}
			BookCopy bookCopyToUpdate = new BookCopy(chosenRecord.getBookBarcode(), chosenRecord.getBookcopyNo());
			bookCopyToUpdate.setReturnDate(Date.valueOf(newExpectReturnDate));
			bookCopyToUpdate.UpdateDetails();
			chosenRecord.setBorrowExpectReturnDate(Date.valueOf(newExpectReturnDate));
			chosenRecord.setChangedBylibrarianId(ChatClient.getCurrectLibrarian().getId());
			chosenRecord.setChangedBylibrarianName(ChatClient.getCurrectLibrarian().getName());
			chosenRecord.setLastChange(Date.valueOf(LocalDate.now()));
			String activityMsg = "The librarian: " + ChatClient.getCurrectLibrarian().getName()+" has extend your borrow: "
					+chosenRecord.getBorrowNumber()+" with "+daysToExtend+" days more. (You need to return the book in the date: "+newExpectReturnDate+").";
			new LogActivity(chosenRecord.getSubscriberId(), activityMsg,
					chosenRecord.getBookBarcode(), chosenRecord.getBookTitle(), chosenRecord.getBookcopyNo());
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
	 * update that certain sbscriber lost certain book
	 * @param event
	 */
	public void btnLostTheBook(ActionEvent event) {
		if (VerifyInput(myEnum.LostBook)) {
			String borrowNumberInput = txtBorrowNumber.getText();
			Book booktoUpdate = new Book(chosenRecord.getBookBarcode());
			BookCopy bookCopyToUpdate = new BookCopy(chosenRecord.getBookBarcode(), chosenRecord.getBookcopyNo());
			try {
				booktoUpdate.addToLostNumber();
				booktoUpdate.UpdateDetails();

				bookCopyToUpdate.lostThisCopy(1);
				bookCopyToUpdate.setReturnDate(null);
				bookCopyToUpdate.UpdateDetails();

				chosenRecord.setBorrowLostBook(BorrowedRecord.LOSTBOOK);
				String msgLogActivity = "Lost the book: \"" + chosenRecord.getBookTitle() + "\" on the Date: "
						+ LocalDate.now();
				new LogActivity(chosenRecord.getSubscriberId(), msgLogActivity, chosenRecord.getBookBarcode(),
						chosenRecord.getBookTitle(), chosenRecord.getBookcopyNo());
				if (chosenRecord.UpdateBorrowDetails()) {
					initialize();
					changeString("Borrow num: " + borrowNumberInput
							+ " has been updated that the subscriber lost the book: " + chosenRecord.getBookTitle(),
							"#086f03");
				} else {
					changeString("Error while update the book to be lost.", "#bf3030");
				}

			} catch (Exception e) {
				changeString(e.getMessage(), "#bf3030");
			}

		}
	}

	/*
	 * Author: Yuval. This method is for the exit button sending a message to the
	 * server that now we are disconnecting, closing the GUI and the connection for
	 * the server.
	 */
	public void getExitBtn(ActionEvent event) throws Exception {
		ConnectionSetupController.stopConnectionToServer();
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
		FXMLLoader Loader = new FXMLLoader(getClass().getResource("/librarianGui/LibrarianMenu.fxml"));
		Parent Root = Loader.load();
		Stage Stage = new Stage();
		Scene Scene = new Scene(Root);
		Scene.getStylesheets().add(getClass().getResource("/librarianGui/LibrarianMenu.css").toExternalForm());
		Stage.setScene(Scene);
		Stage.setTitle("Librarian Menu");
		Stage.show();
		((Node) event.getSource()).getScene().getWindow().hide();
	}

}
