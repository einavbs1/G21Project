package Server;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import common.UserSelect;
import gui.ClientConnectionStatusController;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ocsf.server.*;
import queries.*;

/*
 * This class is defining as a server
 */
public class EchoServer extends AbstractServer {
	int connectionPort;
	public static HashMap<String, String> clientsstatusconnections = new HashMap<String, String>();
	public static String popUpString;
	public static Scene tableScene;
	public static Stage tableStage;
	public static ClientConnectionStatusController tableController;

	/**
	 * Constructs an instance of the echo server.
	 * 
	 * @param port - The port number to connect on.
	 */
	public EchoServer(int port) {
		super(port);
		connectionPort = port;
	}

	/**
	 * This method handles any messages received from the client.
	 *
	 * @param msg    The message received from the client.
	 * @param client The connection from which the message originated.
	 */
	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		int flag = 0;
		String clientIp = client.getInetAddress().getHostAddress();
		String clientPCName = client.getInetAddress().getHostName();

		HashMap<String, String> infoFromUser = (HashMap<String, String>) msg;
		String menuChoiceString = (infoFromUser.keySet().iterator().next());
		//String[] menuSplitString = menuChoiceString.split("+");
		UserSelect x = UserSelect.getSelectionFromEnumName(menuChoiceString);
		// System.out.println("You selected: "+ x);
		switch (x) {

		// This case is getting the table from the SQL and sending to the client
		case ShowAllSubscribers:
			List<String> TheTable = queriesForSubscriber.GetSubscriberTable();
			this.sendToAllClients(TheTable);
			flag++;
			break;

		// This case is getting the information to change from the user and saving in
		// DB.
		// This case is getting the information to change from the user and saving in
		// DB.
		case UpdatePhoneNumber:
			String idNphone[] = infoFromUser.get(menuChoiceString).split(" ");
			boolean succ1 = mysqlConnection.updatephone(Integer.parseInt(idNphone[0]), idNphone[1]);
			if (succ1) {
				this.sendToAllClients("Updated");
			} else {
				this.sendToAllClients("Error");
			}

			flag++;
			break;

		// This case is getting the information to change from the user and saving in
		// DB.

		// This case is getting the information to change from the user and saving in
		// DB.
/*
		case UpdateEmailAddress:
			String idNemail[] = infoFromUser.get(menuChoiceString).split(" ");
			boolean succ2 = mysqlConnection.updatemail(Integer.parseInt(idNemail[0]), idNemail[1]);
			if (succ2) {
				this.sendToAllClients("Updated");
			} else {
				this.sendToAllClients("Error");
			}
			flag++;
			break;

		// This case is loading the requested ID from the DB and sending to the client.
		// This case is loading the requested ID from the DB and sending to the client.
		case LoadSubscriber:
			String RequestedID = mysqlConnection.Load(Integer.parseInt(infoFromUser.get(menuChoiceString)));
			this.sendToAllClients(RequestedID);
			flag++;
			break;
*/
		////////////////////////////////////////////////////////////////////
		////////////////////// start of Einavs adding ///////////////////////

		case GetStatusReport:
			String statusSubscriberReport[] = infoFromUser.get(menuChoiceString).split(", ");
			String statusReport = queriesForStatusReport.GetStatusReport(
					Integer.parseInt(statusSubscriberReport[0]), Integer.parseInt(statusSubscriberReport[1]));

			if (statusReport.contains(",")) {
				this.sendToAllClients(statusReport);
			} else {
				this.sendToAllClients(queriesForStatusReport.addNewReportToDB(
						Integer.parseInt(statusSubscriberReport[0]), Integer.parseInt(statusSubscriberReport[1])));
			}
			flag++;
			break;
			
		case GetBorrowsReport:
			String borrowsReportdata[] = infoFromUser.get(menuChoiceString).split(", ");
			String borrowReport = queriesForBorrowsReport.GetStatusReport(
					Integer.parseInt(borrowsReportdata[0]), Integer.parseInt(borrowsReportdata[1]));
			if (borrowReport.contains(",")) {
				this.sendToAllClients(borrowReport);
			} else {
				this.sendToAllClients(queriesForBorrowsReport.addNewReportToDB(
						Integer.parseInt(borrowsReportdata[0]), Integer.parseInt(borrowsReportdata[1])));
			}
			flag++;
			break;
			
		case GetBookBarcodesAndTitles:
			List<String> AllBooksinfoFromDB = queriesForBooks.getBookBarcodesAndTitles();
			this.sendToAllClients(AllBooksinfoFromDB);
			flag++;
			break;
			
		case GetBorrowsOfBookInSpecificDate:
			String specificBookDateBorrows[] = infoFromUser.get(menuChoiceString).split(", ");
			String specificBookDateBorrowsReport = queriesForBorrows.getBorrowsOfBookInSpecificDate(specificBookDateBorrows[0],
					Integer.parseInt(specificBookDateBorrows[1]), Integer.parseInt(specificBookDateBorrows[2]));
			this.sendToAllClients(specificBookDateBorrowsReport);
			flag++;
			break;

		// This case is adding the book to the DB.
		case CreateBook:
			String NewBookDetails[] = infoFromUser.get(menuChoiceString).split(", ");
			boolean BookCreatesucc = queriesForBooks.addNewBookToDB(NewBookDetails[0], NewBookDetails[1],
					NewBookDetails[2], NewBookDetails[3], Integer.parseInt(NewBookDetails[4]),
					Integer.parseInt(NewBookDetails[5]), Integer.parseInt(NewBookDetails[6]),
					Integer.parseInt(NewBookDetails[7]), NewBookDetails[8]);
			if (BookCreatesucc) {
				this.sendToAllClients("book has been added");
			} else {
				this.sendToAllClients("Error");
			}
			flag++;
			break;

		// This case is getting book from DB with barcode and returning to client.
		case GetBook:
			String RequestedBookFromDB = queriesForBooks.GetBookFromDB(infoFromUser.get(menuChoiceString));
			this.sendToAllClients(RequestedBookFromDB);
			flag++;
			break;

		// This case is updating the book details in the DB.
		case UpdateBookDetails:
			String UpdateBookDetailes[] = infoFromUser.get(menuChoiceString).split(", ");
			int len = UpdateBookDetailes.length;
			StringBuilder descriptionBuilder = new StringBuilder();
			for (int i = 3; i < len - 5; i++) {
				descriptionBuilder.append(UpdateBookDetailes[i]);
				if (i != len - 6) {
					descriptionBuilder.append(", ");
				}
			}

			boolean UpdateBookSucc = queriesForBooks.updateBookDetails(UpdateBookDetailes[0], UpdateBookDetailes[1],
					UpdateBookDetailes[2], descriptionBuilder.toString(), Integer.parseInt(UpdateBookDetailes[len - 5]),
					Integer.parseInt(UpdateBookDetailes[len - 4]), Integer.parseInt(UpdateBookDetailes[len - 3]),
					Integer.parseInt(UpdateBookDetailes[len - 2]), UpdateBookDetailes[len - 1]);
			if (UpdateBookSucc) {
				this.sendToAllClients("book has been updated");
			} else {
				this.sendToAllClients("Error");
			}
			flag++;
			break;

		// This case is adding the bookCopy to the DB.
		case CreateBookCopy:
			String NewBookCopyDetails[] = infoFromUser.get(menuChoiceString).split(", ");
			boolean BookCopyCreatesucc = queriesForBookCopy.addNewBookCopyToDB(NewBookCopyDetails[0],
					Integer.parseInt(NewBookCopyDetails[1]), Integer.parseInt(NewBookCopyDetails[2]),
					Integer.parseInt(NewBookCopyDetails[3]), java.sql.Date.valueOf(NewBookCopyDetails[4]),
					Integer.parseInt(NewBookCopyDetails[5]));
			if (BookCopyCreatesucc) {
				this.sendToAllClients("book has been added");
			} else {
				this.sendToAllClients("Error");
			}
			flag++;
			break;

		// This case is getting book from DB with barcode and copyNo and returning to
		// client.
		case GetBookCopy:
			String GetBookCopyDetails[] = infoFromUser.get(menuChoiceString).split(", ");
			String RequestedBookCopyFromDB = queriesForBookCopy.GetBookCopyFromDB(GetBookCopyDetails[0],
					Integer.parseInt(GetBookCopyDetails[1]));
			this.sendToAllClients(RequestedBookCopyFromDB);
			flag++;
			break;

		// This case is updating the bookCopy details in the DB.
		case UpdateBookCopyDetails:
			String UpdateBookCopyDetailes[] = infoFromUser.get(menuChoiceString).split(", ");
			boolean UpdateBookCopySucc = queriesForBookCopy.updateBookCopyDetails(UpdateBookCopyDetailes[0],
					Integer.parseInt(UpdateBookCopyDetailes[1]), Integer.parseInt(UpdateBookCopyDetailes[2]),
					Integer.parseInt(UpdateBookCopyDetailes[3]),
					UpdateBookCopyDetailes[4].equals("null") ? null : java.sql.Date.valueOf(UpdateBookCopyDetailes[4]),
					UpdateBookCopyDetailes[5].equals("null") ? null : Integer.parseInt(UpdateBookCopyDetailes[5]));
			if (UpdateBookCopySucc) {
				this.sendToAllClients("bookCopy has been updated");
			} else {
				this.sendToAllClients("Error");
			}
			flag++;
			break;

		//
		case GetAllMyCopies:
			String bookBarcodeNeedsCopies = infoFromUser.get(menuChoiceString);
			List<String> allMycopies = queriesForBooks.GetAllMyCopies(bookBarcodeNeedsCopies);
			this.sendToAllClients(allMycopies);
			flag++;
			break;

		case CreateNotification:
			String NewNotificationDetails[] = infoFromUser.get(menuChoiceString).split(", ");
			int NotificationCreateNumber = queriesForNotifications.addNewNotificationToDB(NewNotificationDetails[0],
					Integer.parseInt(NewNotificationDetails[1]), java.sql.Date.valueOf(NewNotificationDetails[2]),
					Integer.parseInt(NewNotificationDetails[3]));
			if (NotificationCreateNumber > 0) {
				this.sendToAllClients(String.valueOf(NotificationCreateNumber));
			} else {
				this.sendToAllClients("Error");
			}
			flag++;
			break;

		case GetNotification:
			String GetNotiDetails = infoFromUser.get(menuChoiceString);
			String MyNotification = queriesForNotifications.GetNotification(Integer.valueOf(GetNotiDetails));
			this.sendToAllClients(MyNotification);
			flag++;
			break;

		case CreateReminder:
			String NewReminderDetails[] = infoFromUser.get(menuChoiceString).split(", ");
			int ReminderCreateNumber = queriesForReminders.addNewReminderToDB(NewReminderDetails[0],
					Integer.parseInt(NewReminderDetails[1]), NewReminderDetails[2], NewReminderDetails[3],
					java.sql.Date.valueOf(NewReminderDetails[4]), Integer.parseInt(NewReminderDetails[5]));
			if (ReminderCreateNumber > 0) {
				this.sendToAllClients(String.valueOf(ReminderCreateNumber));
			} else {
				this.sendToAllClients("Error");
			}
			flag++;
			break;

		case GetReminder:
			String GetReminderDetails = infoFromUser.get(menuChoiceString);
			String MyReminder = queriesForReminders.GetReminder(Integer.valueOf(GetReminderDetails));
			this.sendToAllClients(MyReminder);
			flag++;
			break;

		case GetAllSubjects:
			List<String> allSubjectsList = queriesForSubjects.getAllSubjects();
			this.sendToAllClients(allSubjectsList);
			flag++;
			break;

		case SearchBookByName:
			String bookNameToSearch = infoFromUser.get(menuChoiceString);
			List<String> allBooksByName = queriesForBooks.SearchBooksByName(bookNameToSearch);
			this.sendToAllClients(allBooksByName);
			flag++;
			break;

		case SearchBookBySubject:
			String subjectToSearch = infoFromUser.get(menuChoiceString);
			List<String> allBooksBySubject = queriesForBooks.SearchBooksBySubject(subjectToSearch);
			this.sendToAllClients(allBooksBySubject);
			flag++;
			break;

		case SearchBookByDescription:
			String DescriptionToSearch = infoFromUser.get(menuChoiceString);
			List<String> allBooksByDescription = queriesForBooks.SearchBooksByDescription(DescriptionToSearch);
			this.sendToAllClients(allBooksByDescription);
			flag++;
			break;

		////////////////////// END of Einavs adding ///////////////////////
		////////////////////////////////////////////////////////////////////

		////////////////////////////////////////////////////////////////////
		////////////////////// start of Avishag adding ///////////////////////

		// Author: Avishag.
		// This case is getting the information to change from the user and saving in
		// DB.
		case UpdateSubscriber:
			String idNinfo[] = infoFromUser.get(menuChoiceString).split(", ");
			boolean succ3 = queriesForSubscriber.updateSubscriverDetails(Integer.parseInt(idNinfo[0]), idNinfo[1],
					idNinfo[2], idNinfo[3], idNinfo[4], idNinfo[5], Date.valueOf(idNinfo[6]), idNinfo[7].equals("null") ? null : Date.valueOf(idNinfo[7]),
							Date.valueOf(idNinfo[8]));
			if (succ3) {
				this.sendToAllClients("Updated");
			} else {
				this.sendToAllClients("Error");
			}
			flag++;
			break;

		// Author: Avishag.
		// This case is getting the information to add new user and saving in DB.
		case AddNewSubscriber:
			String idNinfoNew[] = infoFromUser.get(menuChoiceString).split(", ");
			boolean succ4 = queriesForSubscriber.addNewSubscriber(Integer.parseInt(idNinfoNew[0]), idNinfoNew[1],
					idNinfoNew[2], idNinfoNew[3], idNinfoNew[4], idNinfoNew[5]);
			if (succ4) {
				this.sendToAllClients("Added");
			} else {
				this.sendToAllClients("Error");
			}
			flag++;
			break;

		// Author: Avishag.
		// This case is loading the requested ID from the DB and sending to the client.
		case GetSubscriberDetails:
			String RequestedIDToGet = queriesForSubscriber
					.getSubscriberDetails(Integer.parseInt(infoFromUser.get(menuChoiceString)));
			this.sendToAllClients(RequestedIDToGet);
			flag++;
			break;

		////////////////////// END of Avishag adding ///////////////////////
		////////////////////////////////////////////////////////////////////

		////////////////////////////////////////////////////////////////////
		////////////////////// start of Yuval adding ///////////////////////

		// Author: Yuval.
		// This case is getting the information to change from the user and saving in
		// DB.
		case UpdateLibrarian:
			String idNlibinfo[] = infoFromUser.get(menuChoiceString).split(", ");
			boolean succ5 = queriesForLibrarian.updateLibrarianDetails(Integer.parseInt(idNlibinfo[0]), idNlibinfo[1],
					idNlibinfo[2]);
			if (succ5) {
				this.sendToAllClients("Updated");
			} else {
				this.sendToAllClients("Error");
			}
			flag++;
			break;

		// Author: Yuval.
		// This case is getting the information to add new Librarian and saving in DB.
		case AddNewLibrarian:
			String idNinfoNewlib[] = infoFromUser.get(menuChoiceString).split(", ");
			boolean succ6 = queriesForLibrarian.addNewLibrarian(Integer.parseInt(idNinfoNewlib[0]), idNinfoNewlib[1],
					idNinfoNewlib[2]);
			if (succ6) {
				this.sendToAllClients("Added");
			} else {
				this.sendToAllClients("Error");
			}
			flag++;
			break;

		// Author: Yuval.
		// This case is loading the requested Librarian ID from the DB and sending to
		// the client.
		case GetLibrarianDetails:
			String RequestedLibToGet = queriesForLibrarian
					.getLibrarianDetails(Integer.parseInt(infoFromUser.get(menuChoiceString)));
			this.sendToAllClients(RequestedLibToGet);
			flag++;
			break;

		// Author: Yuval.
		// This case is getting the table of subscriber active borrows from the SQL and
		// sending to the client.
		// the client.
		case SubscriberActiveBorrows:
			String getSubscriberId = infoFromUser.get(menuChoiceString);
			List<String> ActiveBorrowListToTable = queriesForBorrows
					.getAllSubscriberActiveBorrowRecordsFromDB(Integer.parseInt(getSubscriberId));
			this.sendToAllClients(ActiveBorrowListToTable);
			flag++;
			break;

		////////////////////// END of Yuval adding ///////////////////////
		////////////////////////////////////////////////////////////////////

		////////////////////////////////////////////////////////////////////
		////////////////////// start of Matan adding ///////////////////////

		// This case is loading the requested borrowing records(by id) from the DB and
		// sending to the client.
		case GetBorrowRecord:
			String getBorrowedRecord = infoFromUser.get(menuChoiceString);
			String borrowedRecordTableByid = queriesForBorrows
					.getBorrowedRecordFromDB(Integer.parseInt(getBorrowedRecord));
			this.sendToAllClients(borrowedRecordTableByid);
			flag++;
			break;

		// This case is updating the expected borrowing time in the record from the
		// client and sending to the DB.
		case UpdateBorrowDetails:
			String updateBorrowRecordsdetails[] = infoFromUser.get(menuChoiceString).split(", ");
			Date actualReturnDate = updateBorrowRecordsdetails[7].equals("null") ? null
					: Date.valueOf(updateBorrowRecordsdetails[7]);
			boolean borrowRecordUpdateSuccess = queriesForBorrows.UpdateBorrowedRecord(
					Integer.parseInt(updateBorrowRecordsdetails[0]), Date.valueOf(updateBorrowRecordsdetails[6]),
					actualReturnDate, Integer.parseInt(updateBorrowRecordsdetails[10]),
					Integer.parseInt(updateBorrowRecordsdetails[11]));

			if (borrowRecordUpdateSuccess) {
				this.sendToAllClients("Borrow record has been updated");
			} else {
				this.sendToAllClients("Error");
			}
			flag++;
			break;
		// This case is getting new borrowing record from the client and saving in DB.
		case AddNewBorrow:
			String newBorrowRecordsdetails[] = infoFromUser.get(menuChoiceString).split(", ");
			int newBorrowNumber = queriesForBorrows.createNewBorrowedRecord(
					Integer.parseInt(newBorrowRecordsdetails[0]), newBorrowRecordsdetails[1],
					newBorrowRecordsdetails[2], Integer.parseInt(newBorrowRecordsdetails[3]),
					java.sql.Date.valueOf(newBorrowRecordsdetails[4]),
					java.sql.Date.valueOf(newBorrowRecordsdetails[5]),
					java.sql.Date.valueOf(newBorrowRecordsdetails[6]), Integer.parseInt(newBorrowRecordsdetails[7]),
					newBorrowRecordsdetails[8], Integer.parseInt(newBorrowRecordsdetails[9]));

			if (newBorrowNumber > 0) {
				this.sendToAllClients(String.valueOf(newBorrowNumber));
			} else {
				this.sendToAllClients("Error");
			}
			flag++;
			break;

		////////////////////// END of Matan adding ///////////////////////
		////////////////////////////////////////////////////////////////////

		////////////////////////////////////////////////////////////////////
		////////////////////// start of Amir adding ///////////////////////

		case AddNewLogActivity:
			String[] activityData = infoFromUser.get(menuChoiceString).split(", ");
			int NewActivityNumber = queriesForActivityLogs.AddNewLogActivity(Integer.parseInt(activityData[0]), // subscriberId
					activityData[1], // activityAction
					activityData[2], // bookBarcode
					activityData[3], // bookTitle
					activityData[4].equals("null") ? null : Integer.parseInt(activityData[4]), // bookcopyCopyNo
					Date.valueOf(activityData[5]) // activityDate
			);

			if (NewActivityNumber > 0) {
				this.sendToAllClients(String.valueOf(NewActivityNumber));
			} else {
				this.sendToAllClients("Error");
			}
			flag++;
			break;

		case LoadActivityById:
			List<String> activities = queriesForActivityLogs
					.LoadLogActivitybyid(Integer.parseInt(infoFromUser.get(menuChoiceString)));
			this.sendToAllClients(activities);
			flag++;
			break;

		case LoadActivityBySerial:
			String activity = queriesForActivityLogs
					.LoadLogActivityBySerialId(Integer.parseInt(infoFromUser.get(menuChoiceString)));
			this.sendToAllClients(activity);
			flag++;
			break;

		////////////////////// END of Amir adding ///////////////////////
		////////////////////////////////////////////////////////////////////

		////////////////////////////////////////////////////////////////////
		////////////////////// start of Chen adding ///////////////////////

		// This case Sending all existing orders from the orders table to client. (chen
		// tsafir)
		case ShowAllOrders:
			List<String> ordersTable = queriesForOrders.GetOrdersTable();
			this.sendToAllClients(ordersTable);
			flag++;
			break;
		// This case Retrieves details of a specific order by order number and sends the
		// information to the client. (chen tsafir)
		case LoadOrder:
			String requestedOrder = queriesForOrders.LoadOrder(Integer.parseInt(infoFromUser.get(menuChoiceString)));
			this.sendToAllClients(requestedOrder); // send the string or "empty"
			flag++;
			break;
		// This case Creates a new order with the details provided by the user and sends
		// the order number to client. (chen tsafir)
		case CreateNewOrder:
			String[] orderDetails = infoFromUser.get(menuChoiceString).split(", ");
			int newOrderNum = queriesForOrders.createOrder(Integer.parseInt(orderDetails[0]), orderDetails[1]);
			if (newOrderNum != -1) {
				this.sendToAllClients("OrderCreated:" + newOrderNum);
			} else {
				this.sendToAllClients("Error");
			}
			flag++;
			break;

		// This case Updating the order details (chen tsafir)
		case UpdateOrderDetails:
			String[] orderData = infoFromUser.get(menuChoiceString).split(", ");
			boolean success = queriesForOrders.updateOrderDetails(Integer.parseInt(orderData[0]),
					Integer.parseInt(orderData[1]), orderData[2], Date.valueOf(orderData[3]),
					Integer.parseInt(orderData[4]), orderData[5].equals("null") ? null : Date.valueOf(orderData[5]));
			if (success) {
				this.sendToAllClients("Updated");
			} else {
				this.sendToAllClients("Error");
			}
			flag++;
			break;

		// Get all existing orders for specific Book. - Matan
		case GetAllOrdersofaBook:
			String bookBarcodeNeedsOrders = infoFromUser.get(menuChoiceString);
			List<String> allMyOrders = queriesForOrders.GetAllMyOrders(bookBarcodeNeedsOrders);
			this.sendToAllClients(allMyOrders);
			flag++;
			break;

		// This case Sends all active orders for specific subscriber (chen tsafir)
		case ShowSubscriberActiveOrders:
			List<String> activeOrdersForSubscriber = queriesForOrders
					.GetOrdersBySubscriber(Integer.parseInt(infoFromUser.get(menuChoiceString)));
			this.sendToAllClients(activeOrdersForSubscriber);
			flag++;
			break;

		// This case ask the number of active order for specific book by barcode book
		// (chen tsafir)
		case GetActiveOrdersCount:
			String bookBarcodeForCount = infoFromUser.get(menuChoiceString);
			int orderCount = queriesForOrders.getActiveOrdersCountForBook(bookBarcodeForCount);
			this.sendToAllClients(String.valueOf(orderCount));
			flag++;
			break;

		// This case retrieves all notifications from the database (chen tsafir)
		case GetAllNotifications:
			List<String> notificationsTable = queriesForNotifications.GetAllNotifications();
			this.sendToAllClients(notificationsTable);
			flag++;
			break;
		////////////////////// END of Chen adding ///////////////////////
		////////////////////////////////////////////////////////////////////

		// error with the userselect action.
		// This case is Showing the client that connect to the server and showing it on
		// the table GUI and shows a message.
		case Connected:
			String onlineStatuString = (clientIp + ", " + clientPCName + ", Connected");
			popUpString = "Client from IP: " + clientIp + ", HostName: " + clientPCName + ", Status: Connected";
			// System.out.println("Client from IP: " + clientIp + ", HostName: " +
			// clientPCName + ", Status: Connected");
			if (clientsstatusconnections.containsKey(clientIp)) {
				clientsstatusconnections.remove(clientIp);
			}
			clientsstatusconnections.put(clientIp, onlineStatuString);
			tableController.setconnection(popUpString);
			this.sendToAllClients("Connected");
			flag++;
			break;

		// This case is Showing the client that disconnected to the server and showing
		// it on the table GUI and shows a message.
		case Disconnect:
			String offlineStatuString = (clientIp + ", " + clientPCName + ", Disconnected");
			popUpString = "Client from IP: " + clientIp + ", HostName: " + clientPCName + ", Status: Disconnected";
			// System.out.println("Client from IP: " + clientIp + ", HostName: " +
			// clientPCName + ", Status: Disconnected");
			clientsstatusconnections.remove(clientIp);
			clientsstatusconnections.put(clientIp, offlineStatuString);
			tableController.setconnection(popUpString);
			this.sendToAllClients("Disconnected");
			flag++;
			break;

		// added by amir 18.1
		// This case is Showing the borrowed books right now.
		case GetMonthlyBorrowedStats:
			List<String> currentlyBorrowed = queriesForBorrows.getMonthlyBorrowedBooksStats();
			this.sendToAllClients(currentlyBorrowed);
			flag++;
			break;

		// error with the userselect action. - probably didn't sent the key good from
		// the client.
		// error with the userselect action.
		default:
			System.out.println("Error with the choise? = " + menuChoiceString);
			break;

		}
		if (flag != 1) {
			this.sendToAllClients("Error");
		}

	}

	/**
	 * This method overrides the one in the superclass. Called when the server stops
	 * listening for connections.
	 */
	protected void serverStopped() {
		System.out.println("Server has stopped listening for connections.");
	}

}
//End of EchoServer class
