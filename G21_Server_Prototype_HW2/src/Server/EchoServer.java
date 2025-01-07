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
			
		//Author: Avishag.
		//This case is getting the information to change from the user and saving in DB.
		case UpdateSubscriber:
			String idNinfo[] = infoFromUser.get(menuChoiceString).split(", ");
			boolean succ3 = mysqlConnection.updateSubscriverDetails(Integer.parseInt(idNinfo[0]), idNinfo[1], Integer.parseInt(idNinfo[2]), idNinfo[3], idNinfo[4], idNinfo[5], idNinfo[6]);
			if (succ3) {
				this.sendToAllClients("Updated");
			} else {
				this.sendToAllClients("Error");
			}
			flag++;
			break;
			
			//Author: Avishag.
			//This case is getting the information to add new user and saving in DB.
		case AddNewSubscriber:
			String idNinfoNew[] = infoFromUser.get(menuChoiceString).split(", ");
			boolean succ4 = mysqlConnection.addNewSubscriber(Integer.parseInt(idNinfoNew[0]), idNinfoNew[1], Integer.parseInt(idNinfoNew[2]), idNinfoNew[3], idNinfoNew[4], idNinfoNew[5], idNinfoNew[6]);
			if (succ4) {
				this.sendToAllClients("Added");
			} else {
				this.sendToAllClients("Error");
			}
			flag++;
			break;
			
			//Author: Avishag.
			//This case is loading the requested ID from the DB and sending to the client.
		case GetSubscriberDetails:
			String RequestedIDToGet = mysqlConnection.getSubscriberDetails(Integer.parseInt(infoFromUser.get(menuChoiceString)));
			this.sendToAllClients(RequestedIDToGet);
			flag++;
			break;
			
			//Author: Yuval.
			//This case is getting the information to change from the user and saving in DB.
		case UpdateLibrarian:
			String idNlibinfo[] = infoFromUser.get(menuChoiceString).split(", ");
			boolean succ5 = mysqlConnection.updateLibrarianDetails(Integer.parseInt(idNlibinfo[0]), idNlibinfo[1], idNlibinfo[2]);
			if (succ5) {
				this.sendToAllClients("Updated");
			} else {
				this.sendToAllClients("Error");
			}
			flag++;
			break;
			
			//Author: Yuval.
			//This case is getting the information to add new Librarian and saving in DB.
		case AddNewLibrarian:
			String idNinfoNewlib[] = infoFromUser.get(menuChoiceString).split(", ");
			boolean succ6 = mysqlConnection.addNewLibrarian(Integer.parseInt(idNinfoNewlib[0]), idNinfoNewlib[1], idNinfoNewlib[2]);
			if (succ6) {
				this.sendToAllClients("Added");
			} else {
				this.sendToAllClients("Error");
			}
			flag++;
			break;
				
			//Author: Yuval.
			//This case is loading the requested Librarian ID from the DB and sending to the client.
		case GetLibrarianDetails:
			String RequestedLibToGet = mysqlConnection.getLibrarianDetails(Integer.parseInt(infoFromUser.get(menuChoiceString)));
			this.sendToAllClients(RequestedLibToGet);
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
