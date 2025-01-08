package Server;

import java.util.HashMap;
import java.util.List;
import common.UserSelect;
import gui.ClientConnectionStatusController;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ocsf.server.*;

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
			boolean BookCreatesucc = mysqlConnection.addNewBookToDB(NewBookDetails[0], NewBookDetails[1],
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
			String RequestedBookFromDB = mysqlConnection.GetBookFromDB(infoFromUser.get(menuChoiceString));
			this.sendToAllClients(RequestedBookFromDB);
			flag++;
			break;

		// This case is updating the book details in the DB.
		case UpdateBookDetails:
			String UpdateBookDetailes[] = infoFromUser.get(menuChoiceString).split(", ");
			boolean UpdateBookSucc = mysqlConnection.updateBookDetails(UpdateBookDetailes[0], UpdateBookDetailes[1],
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
			boolean BookCopyCreatesucc = mysqlConnection.addNewBookCopyToDB(NewBookCopyDetails[0],
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
			String RequestedBookCopyFromDB = mysqlConnection.GetBookCopyFromDB(GetBookCopyDetails[0],
					Integer.parseInt(GetBookCopyDetails[1]));
			this.sendToAllClients(RequestedBookCopyFromDB);
			flag++;
			break;

		// This case is updating the bookCopy details in the DB.
		case UpdateBookCopyDetails:
			String UpdateBookCopyDetailes[] = infoFromUser.get(menuChoiceString).split(", ");
			boolean UpdateBookCopySucc = mysqlConnection.updateBookCopyDetails(UpdateBookCopyDetailes[0],
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
		////////////////////// END of Einavs adding ///////////////////////
		////////////////////////////////////////////////////////////////////

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

		// error with the userselect action. - probably didn't sent the key good from
		// the client.
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
