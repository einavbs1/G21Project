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

		UserSelect x = UserSelect.getSelectionFromEnumName(menuChoiceString);

		switch (x) {

		// This case is getting the table from the SQL and sending to the client
		case ShowAllSubscribers:
			List<String> TheTable = mysqlConnection.GetSubscriberTable();
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

		////////////////////////////////////////////////////////////////////
		////////////////////// start of Einavs adding ///////////////////////

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
			boolean UpdateBookSucc = queriesForBooks.updateBookDetails(UpdateBookDetailes[0], UpdateBookDetailes[1],
					UpdateBookDetailes[2], UpdateBookDetailes[3], Integer.parseInt(UpdateBookDetailes[4]),
					Integer.parseInt(UpdateBookDetailes[5]), Integer.parseInt(UpdateBookDetailes[6]),
					Integer.parseInt(UpdateBookDetailes[7]), UpdateBookDetailes[8]);
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
					Integer.parseInt(UpdateBookCopyDetailes[3]), java.sql.Date.valueOf(UpdateBookCopyDetailes[4]),
					Integer.parseInt(UpdateBookCopyDetailes[5]));
			if (UpdateBookCopySucc) {
				this.sendToAllClients("bookCopy has been updated");
			} else {
				this.sendToAllClients("Error");
			}
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
					Integer.parseInt(NewReminderDetails[1]), NewReminderDetails[2], NewReminderDetails[3], java.sql.Date.valueOf(NewReminderDetails[4]),
					Integer.parseInt(NewReminderDetails[5]));
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
					Integer.parseInt(idNinfo[2]), idNinfo[3], idNinfo[4], idNinfo[5], idNinfo[6]);
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
					Integer.parseInt(idNinfoNew[2]), idNinfoNew[3], idNinfoNew[4], idNinfoNew[5], idNinfoNew[6]);
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
			String RequestedIDToGet = queriesForSubscriber.getSubscriberDetails(Integer.parseInt(infoFromUser.get(menuChoiceString)));
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
			String RequestedLibToGet = queriesForLibrarian.getLibrarianDetails(Integer.parseInt(infoFromUser.get(menuChoiceString)));
			this.sendToAllClients(RequestedLibToGet);
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
			boolean borrowRecordUpdateSuccess = queriesForBorrows.UpdateBorrowedRecordReturnTime(
					Integer.parseInt(updateBorrowRecordsdetails[0]),
					java.sql.Date.valueOf(updateBorrowRecordsdetails[6]),
					java.sql.Date.valueOf(updateBorrowRecordsdetails[7]),
					Integer.parseInt(updateBorrowRecordsdetails[10]));

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
			int newBorrowNumber = queriesForBorrows.createNewBorrowedRecord(Integer.parseInt(newBorrowRecordsdetails[0]),
					newBorrowRecordsdetails[1], newBorrowRecordsdetails[2],
					Integer.parseInt(newBorrowRecordsdetails[3]), java.sql.Date.valueOf(newBorrowRecordsdetails[4]),
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
			String[] orderDetails = infoFromUser.get(menuChoiceString).split(" ");
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
			try {
				String[] orderData = infoFromUser.get(menuChoiceString).split(", ");
				if (orderData.length < 7) {
					this.sendToAllClients("Error: Missing data");
					break;
				}

				boolean success = queriesForOrders.updateOrderDetails(Integer.parseInt(orderData[0]),
						Integer.parseInt(orderData[1]), orderData[2], Integer.parseInt(orderData[3]),
						Date.valueOf(orderData[4]), Integer.parseInt(orderData[5]),
						orderData[6].equals("null") ? null : Date.valueOf(orderData[6]));

				if (success) {
					this.sendToAllClients("Updated");
				} else {
					this.sendToAllClients("Error");
				}
			} catch (NumberFormatException e) {
				this.sendToAllClients("Error: Invalid number format");
			} catch (IllegalArgumentException e) {
				this.sendToAllClients("Error: Invalid date format");
			}
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
		
			
		//added by amir 18.1	
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
