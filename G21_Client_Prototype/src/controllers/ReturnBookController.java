package controllers;

import java.io.IOException;
import java.sql.Date;
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

public class ReturnBookController {
	public static final int AVAILABLE = 1;
	public static final int NotAVAILABLE = 0;
	public static int ORDERNUMBER = 0;
	public static int STATUS = 5;
	public static int ORDERBOOKARRIVEDATE = 5;
	public static int BORROWNUMBER = 123;
	 

	@FXML
	private Label returnPageTitle;
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
	private Label bookBarcode;
	@FXML
	private Label bookCopyNo;
	@FXML
	private Label bookDetailsmsg;
	@FXML
	private Label returnmsg;

	@FXML
	private TextField txtSubscriberId;
	@FXML
	private TextField txtSubName;
	@FXML
	private TextField txtSubPhoneNum;
	@FXML
	private TextField txtEmail;
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
	private boolean method1Called = false;
	private boolean method2Called = false;
	private Subscriber subscriber;
	private BookCopy bookCopyToReturn;
	BorrowedRecord newBorrowedRecord;
	
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
	 * Author: Matan. load subscriber details and display its details. Displays a
	 * message depending on the input
	 * 
	 * @param event - click on the display subscriber button.
	 */
	public void loadSubscriberBtn(ActionEvent event) {
		String idnumber = txtSubscriberId.getText();

		// The string contains only numbers.
		if (idnumber.matches("\\d+")) {

			try {
				subscriber = new Subscriber(Integer.parseInt(idnumber));

				txtSubName.setText(subscriber.getName());
				txtSubPhoneNum.setText(subscriber.getName());

			} catch (NoSuchElementException e) {
				subscriberidmsg.setText("The subscription does not exist in the system");
			}

			// The string contains more chars that not digits or empty.
		} else {
			subscriberidmsg.setText("Please enter id that contains ONLY digits");
		}
	}

	/**
	 * Author: Matan. load borrowed copy book details and display its details. Displays a message
	 * depending on the input
	 * 
	 * @param event - click on the display book button.
	 */
	public void loadCopyofBookBtn(ActionEvent event) {
		String bookBarcode = txtbookBarcode.getText();
		String bookCopyNo = txtbookCopyNo.getText();

		try {
			bookCopyToReturn = new BookCopy(bookBarcode, Integer.parseInt(bookCopyNo));
			int isBorrowed = bookCopyToReturn.getisAvailableStatus();
			
			if (isBorrowed == NotAVAILABLE) { //check the status of the book
				bookDetailsmsg.setText(bookCopyToReturn.getTitle() + "can be to returned");

			} else {
				bookDetailsmsg.setText(bookCopyToReturn.getTitle() + "can't be to returned");
			}

		} catch (NoSuchElementException e) {
			bookDetailsmsg.setText(bookBarcode + "is not exist in the library");
		}

	}

	/**
	 * Author: Matan.
	 * Return copy of book(after all the checks).
	 * Displays a message depending on the input.
	 * @param event - click on the borrow now button.
	 */
	public void returnBtn(ActionEvent event) {
		if ((method1Called && method2Called)){
			
			Book bookToReturn = new Book(bookCopyToReturn.getBarcode());
			
			bookToReturn.addToAvailableCopies();
			bookToReturn.removeFromOrdersNumber();
			bookToReturn.UpdateDetails();
			
			bookCopyToReturn.setisAvailableStatus(AVAILABLE);
			bookCopyToReturn.setReturnDate(null);
			bookCopyToReturn.setSubscriberID(null);
			bookCopyToReturn.UpdateDetails();
			
			// Returns the days difference between 2 dates: returnDate and expectRerurnDate
			long timeDifferenceDays = ReturnBookController.updateReturnTimeandlates(); 

			// Check if the difference is greater than 14 days
			if (timeDifferenceDays > 7) {
			    System.out.println("The book is returned late by " + timeDifferenceDays + " days.");
			    subscriber.setStatus("Frozen");
				subscriber.UpdateDetails();
				returnmsg.setText("congratulations, The returning of " + bookCopyToReturn.getTitle() + 
						" was successful. Your subscription is frozen for a month");
				
			} else {
			    System.out.println("The book was returned on time or within the 7-day window.");
			    returnmsg.setText("congratulations, The returning of " + bookCopyToReturn.getTitle() + " was successful");
			    
			}
							
			LogActivity returnLogActivity = new LogActivity(bookCopyToReturn.getSubscriberId(), "Return a Book", 
					bookCopyToReturn.getBarcode(), bookCopyToReturn.getTitle(), bookCopyToReturn.getCopyNo());
			
			List<String> listOfBookOrders = new ArrayList<String>(); 
			listOfBookOrders = Orders.GetAllOrdersofaBook(bookCopyToReturn.getBarcode());
		
			Orders OrderToUpdate = ReturnBookController.whoIsFirstOrder(listOfBookOrders);
			
			if (!OrderToUpdate.equals(null)) {
				System.out.println(("Book " + bookCopyToReturn.getTitle() + "have orders"));
				Date currentDate = newBorrowedRecord.getBorrowActualReturnDate();
				OrderToUpdate.setBookArrivedDate(currentDate);
				OrderToUpdate.UpdateDetails();
			} else {
				System.out.println(("Book " + bookCopyToReturn.getTitle() + "not have orders"));
			}
		
		} else {
			returnmsg.setText("fill the details of Subscriber and a Book");
		}
	}
	
	/**
	 * Author: Matan.
	 * find the first order that  from data that receive from DB
	 * @param listOfBookCopies
	 * @return BookCopy
	 */
	public static Orders whoIsFirstOrder(List<String> listOfBookCopyOrders) {

		for (String orderOfaBook : listOfBookCopyOrders) {
			String[] order = orderOfaBook.split(", ");
			
			if ((Date.valueOf(order[ORDERBOOKARRIVEDATE]) == null) && (Integer.parseInt(order[STATUS]) == AVAILABLE)) {
				Orders orderToUpdate = new Orders(Integer.parseInt(order[ORDERNUMBER]));
				return orderToUpdate;
			}
		}
		return null;
	}
	
	/** Author Matan
	 * check the difference days between the accutal return date and the expect return date
	 * also convert the result to number of days
	 * @return long
	 */
	public static long updateReturnTimeandlates() {
		BorrowedRecord newBorrowedRecord = new BorrowedRecord(BORROWNUMBER);
		//to get current date
		Date currentDate = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, 14);
        Date ReturnDate = new Date(calendar.getTimeInMillis());
        
		newBorrowedRecord.setBorrowActualReturnDate(ReturnDate);
		newBorrowedRecord.UpdateBorrowDetails();
		
		Date expectReturnDate = newBorrowedRecord.getBorrowExpectReturnDate();
		Date returnDate = newBorrowedRecord.getBorrowActualReturnDate();
		
		// Get the difference in milliseconds
		long timeDifferenceMillis = returnDate.getTime() - expectReturnDate.getTime();

		// Convert milliseconds to days
		long timeDifferenceDays = timeDifferenceMillis / (1000 * 60 * 60 * 24);
		
		return timeDifferenceDays;
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
