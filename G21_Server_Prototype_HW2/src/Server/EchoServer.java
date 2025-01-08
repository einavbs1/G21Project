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
	

	/** Constructs an instance of the echo server.
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
		//This case is getting the table from the SQL and sending to the client
		case ShowAllSubscribers:
			List<String> TheTable = mysqlConnection.GetSubscriberTable();
			this.sendToAllClients(TheTable);
			flag++;
			break;
		//This case is getting the information to change from the user and saving in DB.
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
		//This case is getting the information to change from the user and saving in DB.
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
		//This case is loading the requested ID from the DB and sending to the client.
		case LoadSubscriber:
			String RequestedID = mysqlConnection.Load(Integer.parseInt(infoFromUser.get(menuChoiceString)));
			this.sendToAllClients(RequestedID);
			flag++;
			break;
		//This case is loading the requested borrowing records(by id) from the DB and sending to the client.
		case GetBorrowedRecord:
			String getBorrowedRecord[] = infoFromUser.get(menuChoiceString).split(", ");
			String borrowedRecordTableByid = mysqlConnection.getBorrowedRecordFromDB(Integer.parseInt(getBorrowedRecord[0]));
			this.sendToAllClients(borrowedRecordTableByid);
			flag++;
			break;
		//This case is updating the expected borrowing time in the record from the client and sending to the DB.
		case UpdateBorrowedReturnTime:
			String updateBorrowRecordsdetails[] = infoFromUser.get(menuChoiceString).split(", ");
			boolean borrowRecordUpdateSuccess = mysqlConnection.UpdateBorrowedRecordReturnTime(Integer.parseInt(updateBorrowRecordsdetails[0]),
					 java.sql.Date.valueOf(updateBorrowRecordsdetails[6]), java.sql.Date.valueOf(updateBorrowRecordsdetails[7]),
							 Integer.parseInt(updateBorrowRecordsdetails[10]));
			
			
			if (borrowRecordUpdateSuccess) {
				this.sendToAllClients("Borrow record has been updated");
			} else {
				this.sendToAllClients("Error");
			}
			flag++;
			break;
		//This case is getting new borrowing record from the client and saving in DB.
		case AddBorrowedRedocrd:
			String newBorrowRecordsdetails[] = infoFromUser.get(menuChoiceString).split(", ");
			boolean borrowRecordCreateSuccess = mysqlConnection.createNewBorrowedRecord(Integer.parseInt(newBorrowRecordsdetails[0]), Integer.parseInt(newBorrowRecordsdetails[1]),
					newBorrowRecordsdetails[2], newBorrowRecordsdetails[3], Integer.parseInt(newBorrowRecordsdetails[4]), java.sql.Date.valueOf(newBorrowRecordsdetails[5]),
					java.sql.Date.valueOf(newBorrowRecordsdetails[6]), java.sql.Date.valueOf(newBorrowRecordsdetails[7]), Integer.parseInt(newBorrowRecordsdetails[8]),
					newBorrowRecordsdetails[9],Integer.parseInt(newBorrowRecordsdetails[10]));
			
			if (borrowRecordCreateSuccess) {
				this.sendToAllClients("Borrow record has been added");
			} else {
				this.sendToAllClients("Error");
			}
			flag++;
			break;
		//This case is Showing the client that connect to the server and showing it on the table GUI and shows a message.
		case Connected:
			String onlineStatuString = (clientIp + ", " + clientPCName + ", Connected");
			popUpString = "Client from IP: " + clientIp + ", HostName: " + clientPCName + ", Status: Connected";
			//System.out.println("Client from IP: " + clientIp + ", HostName: " + clientPCName + ", Status: Connected");
			if(clientsstatusconnections.containsKey(clientIp)) {
				clientsstatusconnections.remove(clientIp);
			}
			clientsstatusconnections.put(clientIp, onlineStatuString);
			tableController.setconnection(popUpString);
			this.sendToAllClients("Connected");
			flag++;
			break;
		//This case is Showing the client that disconnected to the server and showing it on the table GUI and shows a message.
		case Disconnect:
			String offlineStatuString = (clientIp + ", " + clientPCName + ", Disconnected");
			popUpString = "Client from IP: " + clientIp + ", HostName: " + clientPCName + ", Status: Disconnected";
			//System.out.println("Client from IP: " + clientIp + ", HostName: " + clientPCName + ", Status: Disconnected");
			clientsstatusconnections.remove(clientIp);
			clientsstatusconnections.put(clientIp, offlineStatuString);
			tableController.setconnection(popUpString);
			this.sendToAllClients("Disconnected");
			flag++;
			break;
		//error with the userselect action.
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
