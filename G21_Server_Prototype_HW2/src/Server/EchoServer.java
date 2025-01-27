package Server;

import java.util.HashMap;
import common.echoServerMenu;
import gui.ClientConnectionStatusController;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ocsf.server.*;
import serverForHandleEntitiesRequests.*;

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
		String[] menuSplitString = menuChoiceString.split("\\+");
		echoServerMenu x = echoServerMenu.getSelectionFromEnumName(menuSplitString[0]);
		// System.out.println("You selected: "+ x);
		switch (x) {

		case Subscriber:
			this.sendToAllClients(subscriberServer.handleSubscriberRequests(infoFromUser));
			flag++;
			break;
			
		case Book:
			this.sendToAllClients(bookServer.handleBookRequests(infoFromUser));
			flag++;
			break;
			
		case BookCopy:
			this.sendToAllClients(bookCopyServer.handleBookCopyRequests(infoFromUser));
			flag++;
			break;
			
		case borrowedRecord:
			this.sendToAllClients(borrowedRecordServer.handleBorrowedRecordRequests(infoFromUser));
			flag++;
			break;
			
		case Librarian:
			this.sendToAllClients(librarianServer.handleLibrarianRequests(infoFromUser));
			flag++;
			break;
			
		case LogActivity:
			this.sendToAllClients(logActivityServer.handleLogActivityRequests(infoFromUser));
			flag++;
			break;
			
		case Notifications:
			this.sendToAllClients(notificationsServer.handleNotificationsRequests(infoFromUser));
			flag++;
			break;
			
		case order:
			this.sendToAllClients(ordersServer.handleOrdersRequests(infoFromUser));
			flag++;
			break;
			
		case Reminders:
			this.sendToAllClients(remindersServer.handleRemindersRequests(infoFromUser));
			flag++;
			break;
		
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
