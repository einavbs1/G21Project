package entity;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import client.ChatClient;
import client.ClientUI;

public class Orders {

	public final static int DIDNT_PICKEDUP = -2;
	public final static int SUBSCRIBER_CANCELLD = -1;
	public final static int ORDER_COMPLETED = 0;
	public final static int CREATE_AN_ORDER = 1;
	public final static int BOOK_HAS_ARRIVED = 2;

	private int order_number;
	private int subscriber_id;
	private String book_barcode;
	private String book_title;
	private Date order_requestedDate;
	private int order_status;
	private Date order_bookArrivedDate;

	/**
	 * Author: chen Constructor that loads order from DB if exists.
	 * 
	 * @param orderNumber
	 */
	public Orders(int orderNumber) {
		String[] str = getOrderFromDB(orderNumber);
		loadOrder(str);
	}

	/**
	 * (chen tsafir) Constructor for creating a new order. This constructor sends a
	 * request to the server to create a new order for a specific subscriber and
	 * book, waits for the server's response, and loads the order details if
	 * successfully created.
	 *
	 * @param subscriberId the ID of the subscriber making the order.
	 * @param bookBarcode  the barcode of the book being ordered.
	 */
	public Orders(int subscriberId, String bookBarcode, String bookTitle) {
		HashMap<String, String> createOrderMap = new HashMap<>();
		String newOrderDetails = subscriberId + ", " + bookBarcode + ", " + bookTitle;
		createOrderMap.put("Order+CreateNewOrder", newOrderDetails);
		ClientUI.chat.accept(createOrderMap);

		String NewOrderString = ChatClient.getStringfromServer();

		if (NewOrderString.startsWith("OrderCreated:")) {
			order_number = Integer.parseInt(NewOrderString.split(":")[1]);
			String[] str = getOrderFromDB(order_number);
			loadOrder(str);
		}
	}

	/**
	 * (chen tsafir) Private method to load order details into this object. The
	 * array should contain order details in the following order: [0] - order number
	 * [1] - subscriber id [2] - book barcode [3] - requested date [4] - status [5]
	 * - book arrived date
	 *
	 * @param str array of order details from the database
	 */
	private void loadOrder(String[] str) {
		order_number = Integer.parseInt(str[0]);
		subscriber_id = Integer.parseInt(str[1]);
		book_barcode = str[2];
		book_title = str[3];
		order_requestedDate = Date.valueOf(str[4]);
		order_status = Integer.parseInt(str[5]);
		order_bookArrivedDate = str[6].equals("null") ? null : Date.valueOf(str[6]);
	}

	/**
	 * (chen tsafir) Retrieves order details from the database for a specific order
	 * number. This method sends a request to the server to fetch order information
	 * and processes the response. -use the LoadOrder case in EchoServer -use the
	 * LoadOrder method in mysqlConnection
	 * 
	 * @param orderNumber The unique identifier of the order to retrieve
	 * @return String array containing the order details split by comma
	 * @throws NoSuchElementException if the order is not found in the system
	 */
	private String[] getOrderFromDB(int orderNumber) throws NoSuchElementException {
		// create hashmap to send the server
		HashMap<String, String> getOrderMap = new HashMap<>();
		getOrderMap.put("Order+LoadOrder", String.valueOf(orderNumber));

		// send to server
		ClientUI.chat.accept(getOrderMap);
		String OrderfromDBString = ChatClient.getStringfromServer();
		// check if the order is exist
		if (OrderfromDBString.contains(",")) {
			String[] parts = OrderfromDBString.split(", ");
			return parts;
		} else {
			throw new NoSuchElementException("The order number: " + orderNumber + " is not registered in the system.");
		}
	}

	/**
	 * (chen tsafir) Updates the details of the current order in the database. This
	 * method sends a request to the server to update the order details using the
	 * string representation of the order (via `toString()`). It waits for the
	 * server's confirmation and reloads the updated order details if the update is
	 * successful.
	 *
	 * Behavior: - Sends an update request to the server with the current order
	 * details as a string. - Waits for the server's response indicating whether the
	 * update succeeded. - Reloads the updated order details from the database if
	 * the update is successful. - Resets the server's response string after
	 * handling the update.
	 */

	public boolean UpdateDetails() {
		HashMap<String, String> updateMap = new HashMap<>();
		updateMap.put("Order+UpdateOrderDetails", toString()); // Using toString to generate a string with all the
																// details

		ClientUI.chat.accept(updateMap);
		String UpdateStatusString = ChatClient.getStringfromServer();

		if (UpdateStatusString.equals("Updated")) {
			loadOrder(getOrderFromDB(order_number));
			return true;
		}
		return false;

	}

	/**
	 * Author: Matan.
	 * 
	 * @param barcode
	 * @return list<String> of orders of the same book
	 */
	private static List<String> getAllActiveOrdersofaBook(String barcode) {

		HashMap<String, String> requestHashMap = new HashMap<String, String>();
		requestHashMap.put("Order+GetAllActiveOrdersofaBook", barcode);
		ClientUI.chat.accept(requestHashMap);
		List<String> myOrders = ChatClient.getListfromServer();

		return myOrders;
	}
	
	
	public static List<String> checkMyActiveOrders(String bookBarcode) {
	    int countActiveOrders;
	    Book bookToCheck = new Book(bookBarcode);
	    int ordersOfBook = bookToCheck.getOrdersNumber();

	    List<String> myActiveOrders = getAllActiveOrdersofaBook(bookBarcode);
	    
	    boolean updated = true;

	    while (updated) {
	        updated = false;
	        countActiveOrders = 0;

	        if (myActiveOrders != null) {
	            for (String activeOrder : myActiveOrders) {
	                String[] parts = activeOrder.split(", ");

	                if (!parts[6].equals("null")) {
	                    LocalDate arrivedDate = Date.valueOf(parts[6]).toLocalDate();

	                    if (arrivedDate.plusDays(2).isBefore(LocalDate.now())) {
	                    	
	                        Orders orderToCancel = new Orders(Integer.valueOf(parts[0]));
	                        orderToCancel.setStatus(Orders.DIDNT_PICKEDUP);
	                        orderToCancel.UpdateDetails();

	                        try {
	                            bookToCheck.removeFromOrdersNumber();
	                            bookToCheck.UpdateDetails();
	                        } catch (Exception e) {
	                            return null;
	                        }
	                    } else {
	                        countActiveOrders++;
	                    }
	                } else {
	                    countActiveOrders++;
	                }
	            }

	            if (ordersOfBook != countActiveOrders) {
	                myActiveOrders = Orders.getAllActiveOrdersofaBook(bookBarcode);
	                updated = true;
	        	    ordersOfBook = bookToCheck.getOrdersNumber();
	        	    countActiveOrders = 0;
	            }
	        }
	    }
	    if(ordersOfBook == 0) {
	    	return new ArrayList<String>();
	    }

	    return myActiveOrders;
	}


	/**
	 * Author: Matan.
	 * 
	 * @param barcode
	 * @return list<String> of orders of the same book
	 */
	public static List<String> getMyActiveOrdersSubscriber(int subID) {

		HashMap<String, String> showOrdersMap = new HashMap<>();
		showOrdersMap.put("Order+ShowSubscriberActiveOrders", String.valueOf(subID));
		ClientUI.chat.accept(showOrdersMap);
		List<String> ordersList = ChatClient.getListfromServer();

		return ordersList;
	}

	/**
	 * Author: Matan. find the first order that from data that receive from DB
	 * 
	 * @param listOfBookCopies
	 * @return BookCopy
	 */
	public static String theFirstOrderToNotifyArrivalOfBook(List<String> listOfBookCopyOrders) {

		for (String orderOfaBook : listOfBookCopyOrders) {
			String[] orderArray = orderOfaBook.split(", ");

			if ((orderArray[6].equals("null")) && (Integer.parseInt(orderArray[5]) == 1)) {
				return orderOfaBook;
			}
		}
		return null;
	}

	/**
	 * Converts numeric status to readable text.
	 * 
	 * @param status The numeric status code
	 * @return String representation of the status
	 */
	public static String getStatusString(int status) {
		switch (status) {
		case -2:
			return "Order cancelled (Didn't picked up the book)";
		case -1:
			return "Order cancelled (User request)";
		case 0:
			return "Order completed";
		case 1:
			return "Order in progress";
		case 2:
			return "Book is arrived - needs to Pickup the book";
		default:
			return "Unknown status";
		}
	}

	/**
	 * (chen tsafir) return string
	 */
	@Override
	public String toString() {
		return order_number + ", " + subscriber_id + ", " + book_barcode + ", " + book_title + ", "
				+ order_requestedDate + ", " + order_status + ", "
				+ ((order_bookArrivedDate == null) ? "null" : order_bookArrivedDate);
	}

	///////////////////////
	/// Getters(chen tsafir)
	///////////////////////

	public int getOrderNumber() {
		return order_number;
	}

	public int getSubscriberId() {
		return subscriber_id;
	}

	public String getBookBarcode() {
		return book_barcode;
	}

	public String getBook_title() {
		return book_title;
	}

	public Date getRequestedDate() {
		return order_requestedDate;
	}

	public int getStatus() {
		return order_status;
	}

	public Date getBookArrivedDate() {
		return order_bookArrivedDate;
	}

	/////////////////////////////////////////////////
	/// Setters
	/// (chen tsafir)
	/// if we want to update the order details:
	/// 1. we will set the change
	/// 2. generate toString
	/// 3. send String to save in DB
	///
	//////////////////////////////////////////////////

	public void setStatus(int status) {
		this.order_status = status;
	}

	/*
	 * public void cancelReservation() { this.order_status = -1; UpdateDetails(); }
	 * 
	 * public void completeReservation() { this.order_status = 1;
	 * this.order_bookArrivedDate = new Date(System.currentTimeMillis());
	 * UpdateDetails(); }
	 */

	public void setBookArrivedDate(Date arrivedDate) {
		this.order_bookArrivedDate = arrivedDate;
	}
}