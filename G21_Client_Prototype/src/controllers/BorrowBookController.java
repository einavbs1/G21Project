package controllers;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import client.ClientUI;
import entity.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;

public class BorrowBookController {

	public static final int AVAILABLE = 1;
	public static final int NotAVAILABLE = 0;

	@FXML
	private Label lblborrowPageTitle;
	@FXML
	private Label lblsubscriberInstruction;
	@FXML
	private Label lblsubscriberId;
	@FXML
	private Label lblsubscriberidmsg;
	@FXML
	private Label lblbookInstruction;
	@FXML
	private Label lblbookDetails;
	@FXML
	private Label lblbookDetailsmsg;
	@FXML
	private Label lblborrowmsg;

	@FXML
	private TextField txtSubscriberId;
	@FXML
	private TextField txtbookBarcode;

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

	private Subscriber subscriber;
	private Book bookToBorrow;

	/**
	 * This method is for the back button closing the current GUI and uploading the
	 * menu GUI.
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
	
	
	public void initialize() {
		subscriber = null;
		bookToBorrow = null;
		lblsubscriberidmsg.setText("");
		txtSubscriberId.setText("");
		txtbookBarcode.setText("");
		txtbookBarcode.setEditable(false);
		btnLoadBook.setDisable(true);
		btnBorrow.setDisable(true);
	}
	
	private void changeString(String s, String color, Label lbl) {
		Platform.runLater(() -> {
			lbl.setText(s);
			lbl.setTextFill(Paint.valueOf(color));
		});
		PauseTransition pause = new PauseTransition(Duration.seconds(10));
		pause.setOnFinished(event -> {
			lbl.setText(" ");
		});
		pause.play();
	}

	enum myEnum{
		VerifyID,VerifyBarcode, LoadedBook;
	}
	
	private boolean VerifyInput(myEnum x) {
		switch (x) {
		case VerifyID:
			if(txtSubscriberId.getText().isEmpty()) {
				changeString("You have to enter the id of the subscriber to load his status.","#bf3030",lblsubscriberidmsg);
				return false;
			}
			if(!txtSubscriberId.getText().matches("\\d+")) {
				changeString("ID number must contain only digits.","#bf3030",lblsubscriberidmsg);
				return false;
			}			
			break;
			
		case VerifyBarcode:
			if(txtbookBarcode.getText().isEmpty()) {
				changeString("You have to enter the Barcode of the book to load his data.","#bf3030",lblbookDetailsmsg);
				return false;
			}

			if(!txtbookBarcode.getText().matches("\\d+")) {
				changeString("Book barcode must contain only digits.","#bf3030",lblbookDetailsmsg);
				return false;
			}			
			break;
		case LoadedBook:
			if(bookToBorrow ==null) {
				changeString("Book not loaded yet.","#bf3030",lblborrowmsg);
				return false;
			}
			break;
			
			default:
				return false;
		}
		
		return true;
	}
	/**
	 * Author: Matan. load subscriber details and display its status. Displays a
	 * message depending on the input.
	 * 
	 * @param event - click on the display subscriber button.
	 */
	public void loadSubscriberBtn(ActionEvent event) {
		if (VerifyInput(myEnum.VerifyID)) {
			txtbookBarcode.setText("");
			try {
				subscriber = new Subscriber(Integer.parseInt(txtSubscriberId.getText()));				
				if (subscriber.getStatus().toLowerCase().equals("active")) {
					lblsubscriberidmsg.setTextFill(Paint.valueOf("#086f03"));
					lblsubscriberidmsg.setText("Can continue borrow book.\n The status of the subscriber: " + subscriber.getName() + " \nis: "+subscriber.getStatus());
					txtbookBarcode.setEditable(true);
					btnLoadBook.setDisable(false);

				} else {
					changeString("The subscription status is frozen","#bf3030",lblsubscriberidmsg);
					txtbookBarcode.setEditable(false);
					btnLoadBook.setDisable(true);
				}

			} catch (Exception e) {
				changeString(e.getMessage(),"#bf3030",lblsubscriberidmsg);
			}

			// The string contains more chars that not digits or empty.
		}
	}

	/**
	 * Author: Matan. load book details and display if its possible to borrow.
	 * Displays a message depending on the input
	 * 
	 * @param event - click on the display book button.
	 */
	public void loadCopyofBookBtn(ActionEvent event) {
		if(VerifyInput(myEnum.VerifyBarcode)) {
			String bookBarcode = txtbookBarcode.getText();
			try {
				bookToBorrow = new Book(bookBarcode);
				int avaliableCopy = bookToBorrow.getAvailableCopies();
	
				if (avaliableCopy > NotAVAILABLE) {
					changeString(bookToBorrow.getTitle() + " is available to borrow","#086f03",lblbookDetailsmsg);
					txtbookBarcode.setEditable(false);
					btnLoadBook.setDisable(true);
					btnBorrow.setDisable(false);
	
				} else {
					changeString(bookToBorrow.getTitle() + "is not available to borrow","#bf3030",lblbookDetailsmsg);
				}
	
			} catch (NoSuchElementException e) {
				changeString(bookBarcode + "is not exist in the library","#bf3030",lblbookDetailsmsg);
			}
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
		if(VerifyInput(myEnum.LoadedBook)) {

			List<String> listOfBookCopies = new ArrayList<String>();
			listOfBookCopies = Book.getAllmyCopies(bookToBorrow.getBarcode());
			
			BookCopy availabeCopyToBorrow = BookCopy.whoIsAvailable(listOfBookCopies);
			if (availabeCopyToBorrow != null) {

				// update details in the specific copy for borrowing
				Date currentDate = new Date(System.currentTimeMillis()); // Get the current date and time
				Calendar calendar = Calendar.getInstance(); // Get the current calendar instance
				calendar.setTime(currentDate); // Set the current time in the calendar
				calendar.add(Calendar.DAY_OF_MONTH, 14); // Add 14 days to the calendar
				Date returnDate = new Date(calendar.getTimeInMillis()); // Convert the updated calendar time back to a Date
																		
				//taking this book to borrow
				availabeCopyToBorrow.setisAvailableStatus(NotAVAILABLE);
				availabeCopyToBorrow.setReturnDate(returnDate);
				availabeCopyToBorrow.setSubscriberID(subscriber.getId());
				availabeCopyToBorrow.UpdateDetails();

				// update details in the Book for borrowing
				try {
					bookToBorrow.removeFromAvailableCopies();
					bookToBorrow.UpdateDetails();
					try {/*
					// create new record in the borrowed record table
					new BorrowedRecord(availabeCopyToBorrow.getSubscriberId(), bookToBorrow.getBarcode(),
							bookToBorrow.getTitle(), availabeCopyToBorrow.getCopyNo());
					new LogActivity(availabeCopyToBorrow.getSubscriberId(), "Borrow a Book", 
							bookToBorrow.getBarcode(), bookToBorrow.getTitle(), availabeCopyToBorrow.getCopyNo());*/
					initialize();
					changeString(
							"congratulations, The borrowing of " + availabeCopyToBorrow.getTitle() + " was successful.","#086f03",lblborrowmsg);
					}catch (Exception e) {
						changeString(e.getMessage(),"#bf3030",lblborrowmsg);
					}

				}catch (Exception e) {
					changeString(e.getMessage(),"#bf3030",lblborrowmsg);

				}
				
		} else {
			changeString("There is no avaiable copy of " + bookToBorrow.getTitle() + " in the library","#bf3030",lblborrowmsg);
		}
	}
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
