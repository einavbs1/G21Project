package serverForHandleEntitiesRequests;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;

import common.*;
import queries.queriesForOrders;

/**
 * This class is to handle the client requests about the orders Server table.
 */
public class ordersServer {

	
	/**
	 * This method is getting the request from the server (that got from the client).
	 * and handling the client request to the DB.
	 * 
	 * @param infoFromUser - hashmap that we get from the client.
	 **
	 ** we are getting the request as hashmap from the client.
	 ** Key = EntityServer + request.
	 ** Value = Values that need to query. 
	 *
	 * @return String (OR) List<String> - very query returning different object
	 */
	public static Object handleOrdersRequests(HashMap<String, String> infoFromUser) {

		String menuChoiceString = (infoFromUser.keySet().iterator().next());
		String[] menuSplitString = menuChoiceString.split("\\+");
		OrderMenu x = OrderMenu.getSelectionFromEnumName(menuSplitString[1]);

		switch (x) {

		// Loads an order based on the given order ID.
		case LoadOrder:
			String requestedOrder = queriesForOrders.LoadOrder(Integer.parseInt(infoFromUser.get(menuChoiceString)));
			return (requestedOrder); // send the string or "empty"

		// This case Creates a new order with the details provided by the user and sends the order number to client.
		case CreateNewOrder:
			String[] orderDetails = infoFromUser.get(menuChoiceString).split(", ");
			int newOrderNum = queriesForOrders.createOrder(Integer.parseInt(orderDetails[0]), orderDetails[1],
					orderDetails[2]);
			if (newOrderNum != -1) {
				return ("OrderCreated:" + newOrderNum);
			} else {
				return ("Error");
			}

		// This case Updating the order details (chen tsafir)
		case UpdateOrderDetails:
			String[] orderData = infoFromUser.get(menuChoiceString).split(", ");
			boolean success = queriesForOrders.updateOrderDetails(Integer.parseInt(orderData[0]),
					Integer.parseInt(orderData[1]), orderData[2], orderData[3], Date.valueOf(orderData[4]),
					Integer.parseInt(orderData[5]), orderData[6].equals("null") ? null : Date.valueOf(orderData[6]));
			if (success) {
				return ("Updated");
			} else {
				return ("Error");
			}

		// Get all existing orders for specific Book. - Matan
		case GetAllActiveOrdersofaBook:
			String bookBarcodeNeedsOrders = infoFromUser.get(menuChoiceString);
			List<String> allMyOrders = queriesForOrders.GetAllMyActiveOrders(bookBarcodeNeedsOrders);
			return (allMyOrders);

		// This case Sends all active orders for specific subscriber (chen tsafir)
		case ShowSubscriberActiveOrders:
			List<String> activeOrdersForSubscriber = queriesForOrders
					.GetOrdersBySubscriber(Integer.parseInt(infoFromUser.get(menuChoiceString)));
			return (activeOrdersForSubscriber);

		default:
			break;
		}

		return "Error";
	}
}
