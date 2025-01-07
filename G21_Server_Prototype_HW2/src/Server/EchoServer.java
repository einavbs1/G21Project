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
		//This case Sending all existing orders from the orders table to client. (chen tsafir)
		case ShowAllOrders:
		    List<String> ordersTable = mysqlConnection.GetOrdersTable();
		    this.sendToAllClients(ordersTable);
		    flag++;
		    break;
		//This case Retrieves details of a specific order by order number and sends the information to the client. (chen tsafir)
		case LoadOrder:
		    String requestedOrder = mysqlConnection.LoadOrder(
		        Integer.parseInt(infoFromUser.get(menuChoiceString))
		    );
		    this.sendToAllClients(requestedOrder);  // send the string or "empty"
		    flag++;
		    break;
		//This case Creates a new order with the details provided by the user and sends the order number to client. (chen tsafir)
		case CreateNewOrder:
		    String[] orderDetails = infoFromUser.get(menuChoiceString).split(" ");
		    int newOrderNum = mysqlConnection.createOrder(
		        Integer.parseInt(orderDetails[0]), orderDetails[1]);
		    if (newOrderNum != -1) {
		        this.sendToAllClients("OrderCreated:" + newOrderNum);
		    } else {
		        this.sendToAllClients("Error");
		    }
		    flag++;
		    break;
		//This case Updating the status of an existing order and sending a message to the client about success or failure (chen tsafir)
		case UpdateOrderStatus:
		    String[] statusUpdate = infoFromUser.get(menuChoiceString).split(" ");
		    boolean updateSuccess = mysqlConnection.updateOrderStatus(
		        Integer.parseInt(statusUpdate[0]),  // order number
		        Integer.parseInt(statusUpdate[1])   // new status
		    );
		    if (updateSuccess) {
		        this.sendToAllClients("Updated");
		    } else {
		        this.sendToAllClients("Error");
		    }
		    flag++;
		    break;
		  //This case Updating the order details (chen tsafir)
		case UpdateOrder:
		    try {
		        String[] orderData = infoFromUser.get(menuChoiceString).split(", ");
		        if (orderData.length < 7) {
		            this.sendToAllClients("Error: Missing data");
		            break;
		        }
		        
		        boolean success = mysqlConnection.updateOrder(
		            Integer.parseInt(orderData[0]),    
		            Integer.parseInt(orderData[1]),    
		            orderData[2],                      
		            Integer.parseInt(orderData[3]),    
		            Date.valueOf(orderData[4]),        
		            Integer.parseInt(orderData[5]),    
		            orderData[6].equals("null") ? null : Date.valueOf(orderData[6])
		        );
		        
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
		//This case Set the order as canceled (chen tsafir)
		case CancelOrder:
		    String orderToCancel = infoFromUser.get(menuChoiceString);
		    boolean cancelSuccess = mysqlConnection.updateOrderStatus(
		        Integer.parseInt(orderToCancel), 
		        -1  //represent canceled status
		    );
		    
		    if (cancelSuccess) {
		        this.sendToAllClients("Cancelled");
		    } else {
		        this.sendToAllClients("Error");
		    }
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
