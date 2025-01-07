package Server;

import java.sql.Date;
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
			
			
			
			/* ADDED BY AMIR*/
		case LoadActivityById:
		    List<String> activities = mysqlConnection.LoadLogActivitybyid(Integer.parseInt(infoFromUser.get(menuChoiceString)));
		    this.sendToAllClients(activities);
		    flag++;
		    break;
		    /* ADDED BY AMIR*/
		case LoadActivityBySerial:
		    String activity = mysqlConnection.LoadLogActivityBySerialId(Integer.parseInt(infoFromUser.get(menuChoiceString)));
		    this.sendToAllClients(activity);
		    flag++;
		    break;
		    /* ADDED BY AMIR*/
		case SaveLogActivity:
		    String[] activityData = infoFromUser.get(menuChoiceString).split(", ");
		    boolean success = mysqlConnection.saveLogActivity(
		        Integer.parseInt(activityData[0]),     // subscriberId
		        activityData[1],                       // activityAction
		        activityData[2],                       // bookBarcode
		        activityData[3],                       // bookTitle
		        activityData[4].equals("null") ? null : Integer.parseInt(activityData[4]),  // bookcopyCopyNo
		        Date.valueOf(activityData[5])          // activityDate
		    );
		    
		    if (success) {
		        this.sendToAllClients("Updated");
		    } else {
		        this.sendToAllClients("Error");
		    }
		    flag++;
		    break;
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
