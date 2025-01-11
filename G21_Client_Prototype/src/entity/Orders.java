package entity;

import java.sql.Date;
import java.util.HashMap;
import java.util.NoSuchElementException;

import client.ChatClient;
import client.ClientUI;

public class Orders {
	private int order_number;
	private int subscriber_id;
	private String book_barcode;
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
	public Orders(int subscriberId, String bookBarcode) {
		HashMap<String, String> createOrderMap = new HashMap<>();
		String newOrderDetails = subscriberId + ", " + bookBarcode;
		createOrderMap.put("CreateNewOrder", newOrderDetails);
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
		order_requestedDate = Date.valueOf(str[3]);
		order_status = Integer.parseInt(str[4]);
		order_bookArrivedDate = str[5].equals("null") ? null : Date.valueOf(str[5]);
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
		getOrderMap.put("LoadOrder", String.valueOf(orderNumber));

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

	public void UpdateDetails() {
		HashMap<String, String> updateMap = new HashMap<>();
		updateMap.put("UpdateOrderDetails", toString()); // Using toString to generate a string with all the details

		ClientUI.chat.accept(updateMap);
		String UpdateStatusString = ChatClient.getStringfromServer();

		if (UpdateStatusString.equals("Updated")) {
			loadOrder(getOrderFromDB(order_number));
		}

	}

	/**
	 * (chen tsafir) return string
	 */
	@Override
	public String toString() {
		return order_number + ", " + subscriber_id + ", " + book_barcode + ", " + order_requestedDate + ", "
				+ order_status + ", " + order_bookArrivedDate;
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

	/*public void cancelReservation() {
		this.order_status = -1;
		UpdateDetails();
	}

	public void completeReservation() {
		this.order_status = 1;
		this.order_bookArrivedDate = new Date(System.currentTimeMillis());
		UpdateDetails();
	}*/

	public void setBookArrivedDate(Date arrivedDate) {
		this.order_bookArrivedDate = arrivedDate;
	}
}