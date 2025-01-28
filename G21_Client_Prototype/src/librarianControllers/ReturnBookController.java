package librarianControllers;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import client.ChatClient;
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
import mainControllers.ConnectionSetupController;

public class ReturnBookController {
	public static final int AVAILABLE = 1;
	public static final int NotAVAILABLE = 0;

	@FXML
	private Label lblreturnPageTitle;
	@FXML
	private Label lblsubscriberInstruction;
	@FXML
	private Label lblsubscriberId;
	@FXML
	private Label lblsubscriberidmsg;
	@FXML
	private Label lblbookBarcode;
	@FXML
	private Label lblbookCopyNo;
	@FXML
	private Label lblbookDetailsmsg;
	@FXML
	private Label lblreturnmsg;

	@FXML
	private TextField txtSubscriberId;
	@FXML
	private TextField txtbookBarcode;
	@FXML
	private TextField txtbookCopyNo;

	@FXML
	private Button btnLoadSubscriber = null;
	@FXML
	private Button btnLoadBook = null;
	@FXML
	private Button btnReturn = null;
	@FXML
	private Button btnExit = null;
	@FXML
	private Button btnBack = null;

	// to prevent from borrow a book if the details of Subscriber and Book weren't
	// loaded
	private Subscriber subscriber;
	private BookCopy bookCopyToReturn;
	private BorrowedRecord mySubBorrowedRecord;
	private List<String> myBorrows;

	/**
	 * This method is for the back button closing the current GUI and uploading the
	 * menu GUI.
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
	
	/**
	 * initialize all the variables for the GUI
	 */
	public void initialize() {
		subscriber = null;
		bookCopyToReturn = null;
		mySubBorrowedRecord = null;
		myBorrows = null;
		lblsubscriberidmsg.setText("");
		txtSubscriberId.setText("");
		txtbookBarcode.setText("");
		txtbookCopyNo.setText("");
		txtbookCopyNo.setEditable(false);
		txtbookBarcode.setEditable(false);
		btnLoadBook.setDisable(true);
		btnReturn.setDisable(true);
	}
	
	/**
	 * presents a message on the gui for 10 seconds
	 * @param s
	 * @param color
	 * @param lbl
	 */
	private void changeString(String s, String color, Label lbl) {
		Platform.runLater(() -> {
			lbl.setText(s);
			lbl.setTextFill(Paint.valueOf(color));
		});
		PauseTransition pause = new PauseTransition(Duration.seconds(10));
		pause.setOnFinished(event -> {
			if (lbl.getText().equals(s)) {
				lbl.setText(" ");
			}
		});
		pause.play();
	}

	enum myEnum {
		VerifyID, VerifyBookDetails, LoadedBookToReturn;
	}
	
	/**
	 * switch case for convenient distribution of the actions of the user
	 * @param x
	 * @return
	 */
	private boolean VerifyInput(myEnum x) {

		switch (x) {
		case VerifyID: // if id button check if the input is valid
			if (txtSubscriberId.getText().isEmpty()) {
				changeString("You have to enter the id of the subscriber to load his status.", "#bf3030",
						lblsubscriberidmsg);
				return false;
			}
			if (!txtSubscriberId.getText().matches("\\d{9}")) {
				changeString("ID number must contain only 9 digits.", "#bf3030", lblsubscriberidmsg);
				return false;
			}
			try {
				subscriber = new Subscriber(Integer.parseInt(txtSubscriberId.getText()));
				return true;
			}catch (Exception e) {
				changeString(e.getMessage(), "#bf3030", lblsubscriberidmsg);
				return false;
			}

		case VerifyBookDetails: //if barcode button check if the input is valid
			if (txtbookBarcode.getText().isEmpty() || txtbookCopyNo.getText().isEmpty()) {
				changeString("You have to enter the book details load his data.", "#bf3030", lblbookDetailsmsg);
				return false;
			}

			if (!txtbookBarcode.getText().matches("\\d+") || !txtbookCopyNo.getText().matches("\\d+")) {
				changeString("Book barcode and copy No. can contains only digits.", "#bf3030", lblbookDetailsmsg);
				return false;
			}
			break;

		case LoadedBookToReturn: // check if the input is valid
			if (bookCopyToReturn == null || mySubBorrowedRecord == null) {
				changeString("You should insert first book that the subscriber borrowed.", "#bf3030", lblreturnmsg);
				return false;
			}
			break;

		default:
			return (false);

		}

		return true;
	}

	/**
	 * Author: Matan. load subscriber details and display its details. Displays a
	 * message depending on the input
	 * 
	 * @param event - click on the display subscriber button.
	 */
	public void loadSubscriberBtn(ActionEvent event) {
		if (VerifyInput(myEnum.VerifyID)) {
			int idnumber = Integer.parseInt(txtSubscriberId.getText());
			try {
				subscriber = new Subscriber(idnumber);

				try {
					myBorrows = BorrowedRecord.getSubscriberActiveBorrowsFromDB(idnumber);

					lblsubscriberidmsg.setTextFill(Paint.valueOf("#086f03"));
					lblsubscriberidmsg.setText("The subscriber: " + subscriber.getName() + "\nhas books to return.\n"
							+ "Please continue and enter the book details to return.");
					txtbookCopyNo.setEditable(true);
					txtbookBarcode.setEditable(true);
					btnLoadBook.setDisable(false);

				} catch (NoSuchElementException e) {
					changeString(e.getMessage(), "#bf3030", lblsubscriberidmsg);
					txtbookCopyNo.setEditable(false);
					txtbookBarcode.setEditable(false);
					btnLoadBook.setDisable(true);
				}
			} catch (NoSuchElementException e) {
				changeString(e.getMessage(), "#bf3030", lblsubscriberidmsg);
			}
		}

	}

	/**
	 * Author: Matan. load borrowed copy book details and display its details.
	 * Displays a message depending on the input
	 * 
	 * @param event - click on the display book button.
	 */
	public void loadCopyofBookBtn(ActionEvent event) {

		if (VerifyInput(myEnum.VerifyBookDetails)) {
			int flag = 0;
			String bookBarcode = txtbookBarcode.getText();
			String bookCopyNo = txtbookCopyNo.getText();
			try {
				bookCopyToReturn = new BookCopy(bookBarcode, Integer.parseInt(bookCopyNo));

				for (String borrow : myBorrows) {
					String[] borrowSplitted = borrow.split(", ");
					if (borrowSplitted[1].equals(bookBarcode) && borrowSplitted[3].equals(bookCopyNo)) {
						mySubBorrowedRecord = new BorrowedRecord(Integer.parseInt(borrowSplitted[0]));
						flag++;
						break;
					}
				}
				if (flag > 0) {

					lblbookDetailsmsg.setTextFill(Paint.valueOf("#086f03"));
					lblbookDetailsmsg.setText("The book: " + bookCopyToReturn.getTitle() + " has been borrowed by: "
							+ subscriber.getName() + "\nYou can continue the return process.");
					btnReturn.setDisable(false);
					txtbookBarcode.setEditable(false);
					txtbookCopyNo.setEditable(false);
					btnLoadBook.setDisable(true);
					txtSubscriberId.setEditable(false);
					btnLoadSubscriber.setDisable(false);
				} else {
					changeString("The book isn't borrowed by: \"" + subscriber.getName() + "\" \nid ("
							+ subscriber.getId() + ").", "#bf3030", lblbookDetailsmsg);

				}
			} catch (NoSuchElementException e) {
				changeString(e.getMessage(), "#bf3030", lblbookDetailsmsg);
			}
		}

	}

	/**
	 * Author: Matan. Return copy of book(after all the checks). Displays a message
	 * depending on the input.
	 * 
	 * @param event - click on the borrow now button.
	 */
	public void returnBtn(ActionEvent event) {
		if (VerifyInput(myEnum.LoadedBookToReturn)) {

			Book bookToReturn = new Book(bookCopyToReturn.getBarcode());

			try {
				if (mySubBorrowedRecord.getBorrowLostBook() > 0) {
					bookToReturn.removeFromLostNumber();
					mySubBorrowedRecord.lostThisBook(BorrowedRecord.FOUNDBOOK);
					bookCopyToReturn.lostThisCopy(BorrowedRecord.FOUNDBOOK);
				}
				bookToReturn.addToAvailableCopies();
				bookToReturn.UpdateDetails();

				bookCopyToReturn.setisAvailableStatus(AVAILABLE);
				bookCopyToReturn.setReturnDate(null);
				bookCopyToReturn.setSubscriberID(null);
				bookCopyToReturn.UpdateDetails();
				LocalDate currentDate = LocalDate.now();

				mySubBorrowedRecord.setBorrowActualReturnDate(Date.valueOf(currentDate));
				mySubBorrowedRecord.UpdateBorrowDetails();
				LocalDate expectReturnDate = mySubBorrowedRecord.getBorrowExpectReturnDate().toLocalDate();
				
				long timeDifferenceDays = ChronoUnit.DAYS.between(expectReturnDate, currentDate);
				String activityMsg = "You return the book: " + bookCopyToReturn.getTitle();
				if (timeDifferenceDays > 6) {
					
					SubscribersStatusReport reportToUpdate = new SubscribersStatusReport(LocalDate.now().getMonthValue(), LocalDate.now().getYear());
					reportToUpdate.addGotFroze();
					reportToUpdate.UpdateDetails();
					
					String msgActivityFrozen = "You account has been frozen for 30 days by the librarian: "
							+ ChatClient.getCurrectLibrarian().getName() + ". Because of the late return.";
					new LogActivity(subscriber.getId(),msgActivityFrozen, bookCopyToReturn.getBarcode(), bookCopyToReturn.getTitle(), bookCopyToReturn.getCopyNo());
					
					Subscriber.addingNewRecordOfFrozen(subscriber.getId(), Date.valueOf(currentDate),
							Date.valueOf(currentDate.plusDays(30)));

					subscriber.setStatus(Subscriber.FROZEN);
					subscriber.setFrozenUntil(Date.valueOf(currentDate.plusDays(30)));
					subscriber.UpdateDetails();
					lblreturnmsg.setText("The book is returned late by " + timeDifferenceDays + " days.\n"
							+ subscriber.getName() + "'s subscription has been frozen for a month.\n"
							+ "The returning of " + bookCopyToReturn.getTitle() + " was successful.");

				} else {
					lblreturnmsg.setTextFill(Paint.valueOf("#086f03"));
					lblreturnmsg.setText(
							"congratulations, The returning of " + bookCopyToReturn.getTitle() + " was successful");

				}
				if (timeDifferenceDays > 0) {
					activityMsg = "You return the book: " + bookCopyToReturn.getTitle() + "late by "
							+ timeDifferenceDays + " days.";
				}
				if (timeDifferenceDays > 6) {
					activityMsg += " This late return was the cause of freezing your subscriptions.";
				}

				new LogActivity(subscriber.getId(), activityMsg,
						bookCopyToReturn.getBarcode(), bookCopyToReturn.getTitle(), bookCopyToReturn.getCopyNo());

				String ordertoNotifyString = Orders
						.theFirstOrderToNotifyArrivalOfBook(Orders.checkMyActiveOrders(bookCopyToReturn.getBarcode()));

				if (ordertoNotifyString != null && ordertoNotifyString.contains(",")) {
					String[] partStrings = ordertoNotifyString.split(", ");
					Orders notifyThisOrder = new Orders(Integer.parseInt(partStrings[0]));
					notifyThisOrder.setBookArrivedDate(Date.valueOf(currentDate));
					notifyThisOrder.UpdateDetails();

					Subscriber subtoNotify = new Subscriber(notifyThisOrder.getSubscriberId());
					String msgToReminder = "The book \"" + bookToReturn.getTitle() + "\" that you order ("
							+ notifyThisOrder.getOrderNumber() + ") has arrived. " + " You have 2 days (until: "
							+ currentDate.plusDays(2) + ") to collect it otherwise the order will be cancel.";

					new Reminders(msgToReminder, subtoNotify.getId(), subtoNotify.getPhoneNumber(),
							subtoNotify.getEmail(), Date.valueOf(currentDate));

				}
				initialize();
			} catch (Exception e) {
				changeString(e.getMessage(), "#bf3030", lblbookDetailsmsg);

			}

		}
	}

	/*
	 * This method is for the exit button sending a message to the server that now
	 * we are disconnecting, closing the GUI and the connection for the server.
	 */
	public void getExitBtn(ActionEvent event) throws Exception {
		ConnectionSetupController.stopConnectionToServer();
		System.exit(0);
	}
}
