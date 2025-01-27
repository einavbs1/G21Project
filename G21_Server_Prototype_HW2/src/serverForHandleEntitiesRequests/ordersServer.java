package serverForHandleEntitiesRequests;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;

import common.*;
import queries.queriesForOrders;

public class ordersServer {

	public static Object handleOrdersRequests(HashMap<String, String> infoFromUser) {

		String menuChoiceString = (infoFromUser.keySet().iterator().next());
		String[] menuSplitString = menuChoiceString.split("\\+");
		OrderMenu x = OrderMenu.getSelectionFromEnumName(menuSplitString[1]);

		switch (x) {

		case LoadOrder:
			String requestedOrder = queriesForOrders.LoadOrder(Integer.parseInt(infoFromUser.get(menuChoiceString)));
			return (requestedOrder); // send the string or "empty"

		// This case Creates a new order with the details provided by the user and sends
		// the order number to client. (chen tsafir)
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
