package controllers;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import client.ChatClient;
import client.ClientUI;
import entity.*;
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

public class BorrowBookController {

	public static final int AVAILABLE = 1;
	public static final int NotAVAILABLE = 0;
	public static int BARCODE = 0;
	public static int COPY_NUMBER = 1;

	@FXML
	private Label borrowPageTitle;
	@FXML
	private Label subscriberInstruction;
	@FXML
	private Label subscriberId;
	@FXML
	private Label subscriberidmsg;
	@FXML
	private Label subscriberName;
	@FXML
	private Label subscriberPhoneNum;
	@FXML
	private Label bookInstruction;
	@FXML
	private Label bookDetails;
	@FXML
	private Label bookDetailsmsg;
	@FXML
	private Label borrowmsg;

	@FXML
	private TextField txtSubscriberId;
	@FXML
	private TextField txtSubName;
	@FXML
	private TextField txtSubPhoneNum;
	@FXML
	private TextField txtEmail;
	@FXML
	private TextField txtbookDetails;

	@FXML
	private Button btnLoadSubscriber = null;
	@FXML
	private Button btnLoadBook = null;
	@FXML
	private Button btnBorrow = null;
	@FXML
	private Button btnExit = null;
	@FXML
	private Button btnBack = null;

	// to prevent from borrow a book if the details of Subscriber and Book weren't
	// loaded
	private boolean method1Called = false;
	private boolean method2Called = false;
	private Subscriber subscriber;
	private Book bookToBorrow;
	Librarian curr_lib = new Librarian(12345);

	/**
	 * This method is for the back button closing the current GUI and uploading the
	 * menu GUI.
	 * 
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

	/**
	 * Author: Matan. load subscriber details and display its status. Displays a
	 * message depending on the input.
	 * 
	 * @param event - click on the display subscriber button.
	 */
	public void loadSubscriberBtn(ActionEvent event) {
		String idnumber = txtSubscriberId.getText();

		// The string contains only numbers.
		if (idnumber.matches("\\d+")) {

			try {
				subscriber = new Subscriber(Integer.parseInt(idnumber));
				String status = subscriber.getStatus();

				if (status.equals("Active")) {
					subscriberidmsg.setText("The subscription status is Active");
					txtSubName.setText(subscriber.getName());
					txtSubPhoneNum.setText(subscriber.getName());
					method1Called = true;

				} else {
					subscriberidmsg.setText("The subscription status is frozen");
				}

			} catch (NoSuchElementException e) {
				subscriberidmsg.setText("The subscription does not exist in the system");
			}

			// The string contains more chars that not digits or empty.
		} else {
			subscriberidmsg.setText("Please enter id that contains ONLY digits");
		}
	}

	/**
	 * Author: Matan. load book details and display if its possible to borrow.
	 * Displays a message depending on the input
	 * 
	 * @param event - click on the display book button.
	 */
	public void loadCopyofBookBtn(ActionEvent event) {
		String bookBarcode = txtbookDetails.getText();

		try {
			bookToBorrow = new Book(bookBarcode);
			int avaliableCopy = bookToBorrow.getAvailableCopies();

			if (avaliableCopy > NotAVAILABLE) {
				bookDetailsmsg.setText(bookToBorrow.getTitle() + "is available to borrow");
				method2Called = true;

			} else {
				bookDetailsmsg.setText(bookToBorrow.getTitle() + "is not available to borrow");
			}

		} catch (NoSuchElementException e) {
			bookDetailsmsg.setText(bookBarcode + "is not exist in the library");
		}
	}

	/**
	 * Author: Matan.
	 * Borrow copy of book(after all the checks). Displays a message
	 * depending on the input.
	 * update each table of DB - Book, Bookcopy, BoorowedRecords and activityLog
	 * @param event - click on the borrow now button.
	 */
	public void borrowBtn(ActionEvent event) {
		if ((method1Called && method2Called)) {

			List<String> listOfBookCopies = new ArrayList<String>();
			listOfBookCopies = Book.getAllmyCopies(bookToBorrow.getBarcode());

			BookCopy availabeCopyToBorrow = BorrowBookController.whoIsAvailable(listOfBookCopies);

			if (!availabeCopyToBorrow.equals(null)) {

				// update details in the specific copy for borrowing
				Date currentDate = new Date(System.currentTimeMillis()); // Get the current date and time
				Calendar calendar = Calendar.getInstance(); // Get the current calendar instance
				calendar.setTime(currentDate); // Set the current time in the calendar
				calendar.add(Calendar.DAY_OF_MONTH, 14); // Add 14 days to the calendar
				Date returnDate = new Date(calendar.getTimeInMillis()); // Convert the updated calendar time back to a Date
																		
				availabeCopyToBorrow.setReturnDate(returnDate);

				availabeCopyToBorrow.setSubscriberID(subscriber.getId());

				availabeCopyToBorrow.UpdateDetails();

				// update details in the Book for borrowing
				bookToBorrow.removeFromAvailableCopies();
				bookToBorrow.UpdateDetails();
				
				// create new record in the borrowed record table
				BorrowedRecord newBorrowedRecord = new BorrowedRecord(availabeCopyToBorrow.getSubscriberId(), bookToBorrow.getBarcode(),
						bookToBorrow.getTitle(), availabeCopyToBorrow.getCopyNo(), curr_lib.getId(), curr_lib.getName());
				borrowmsg.setText(
						"congratulations, The borrowing of " + availabeCopyToBorrow.getTitle() + " was successful");
				
				LogActivity borrowLogActivity = new LogActivity(availabeCopyToBorrow.getSubscriberId(), "Borrow a Book", 
						bookToBorrow.getBarcode(), bookToBorrow.getTitle(), availabeCopyToBorrow.getCopyNo());
				

			} else {
				borrowmsg.setText("Copy of " + availabeCopyToBorrow.getTitle() + "is not exist in the library");
			}
		} else {
			borrowmsg.setText("fill the details of Subscriber and Book");
		}
	}

	/**
	 * Author: Matan.
	 * find the first copy that available for borrow from data that receive from DB
	 * @param listOfBookCopies
	 * @return BookCopy
	 */
	public static BookCopy whoIsAvailable(List<String> listOfBookCopies) {

		for (String bookCopy : listOfBookCopies) {
			String[] fullBookCopy = bookCopy.split(", ");
			if (Integer.parseInt(fullBookCopy[COPY_NUMBER]) == AVAILABLE) {
				BookCopy copyToBorrow = new BookCopy(fullBookCopy[BARCODE],
						Integer.parseInt(fullBookCopy[COPY_NUMBER]));
				return copyToBorrow;
			}
		}
		return null;
	}

	/*
	 * This method is for the exit button sending a message to the server that now
	 * we are disconnecting, closing the GUI and the connection for the server.
	 */
	public void getExitBtn(ActionEvent event) throws Exception {
		System.out.println("Disconnecting from the Server and ending the program.");
		HashMap<String, String> EndingConnections = new HashMap<String, String>();
		EndingConnections.put("Disconnect", "");
		ClientUI.chat.accept(EndingConnections);
		System.exit(0);
	}
}
