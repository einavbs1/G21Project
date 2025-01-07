package entity;

import java.sql.Date;
import java.util.HashMap;
import java.util.NoSuchElementException;

import client.ChatClient;
import client.ClientUI;

import java.time.LocalDate;

public class Orders {
	private int order_number;
    private int subscriber_id;
    private String book_barcode;
    private Date order_requestedDate;
    private int order_status;
    private Date order_bookArrivedDate;
    
    /****Author: chen
     * Constructor that loads order from DB if exists.
     * @param orderNumber
     */
    public Orders(int orderNumber) {
        String[] str = getOrderFromDB(orderNumber);
        loadOrder(str);
    }
    
    
    
    /**(chen tsafir)
     * Constructor for creating a new order. 
     * This constructor sends a request to the server to create a new order 
     * for a specific subscriber and book, waits for the server's response, 
     * and loads the order details if successfully created.
     *
     * @param subscriberId the ID of the subscriber making the order.
     * @param bookBarcode  the barcode of the book being ordered.
     */
    public Orders( int subscriberId, String bookBarcode) {
        HashMap<String, String> createOrderMap = new HashMap<>();
        String newOrderDetails = subscriberId + ", " + bookBarcode;
        createOrderMap.put("CreateNewOrder", newOrderDetails);
        ClientUI.chat.accept(createOrderMap);
        
        while (ChatClient.fromserverString.equals(new String())) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        if (ChatClient.fromserverString.startsWith("OrderCreated:")) {
            order_number = Integer.parseInt(ChatClient.fromserverString.split(":")[1]);
            String[] str = getOrderFromDB(order_number);
            loadOrder(str);
        }
        ChatClient.ResetServerString();
    }
    
    
    
    /** (chen tsafir)
     * Private method to load order details into this object.
     * The array should contain order details in the following order:
     * [0] - order number
     * [1] - subscriber id
     * [2] - book barcode
     * [3] - requested date
     * [4] - status
     * [5] - book arrived date
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
        
    /** (chen tsafir)
     * Static method to load an order from the database.
     * This method creates and returns a new Orders object based on the order number.
     *
     * @param orderNumber the number of the order to load
     * @return Orders object if found in database, null if not found
     */
    public static Orders loadOrder(int orderNumber) {    // שינינו את השם ל-lowercase
        HashMap<String, String> loadMap = new HashMap<>();
        loadMap.put("LoadOrder", String.valueOf(orderNumber));
        
        ClientUI.chat.accept(loadMap);
        
        while (ChatClient.fromserverString.equals(new String())) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        if (!ChatClient.fromserverString.equals("Empty")) {
            return new Orders(orderNumber);  // משתמש בבנאי הקיים שטוען מה-DB
        }
        ChatClient.ResetServerString();
        return null;
    }
    
    
    
    /** (chen tsafir)
     * Retrieves order details from the database for a specific order number.
     * This method sends a request to the server to fetch order information and processes the response.
     * -use the LoadOrder case in EchoServer 
     * -use the LoadOrder method in mysqlConnection
     * 
     * @param orderNumber The unique identifier of the order to retrieve
     * @return String array containing the order details split by comma
     * @throws NoSuchElementException if the order is not found in the system
     */
    private String[] getOrderFromDB(int orderNumber) throws NoSuchElementException {
    	//create hashmap to send the server
        HashMap<String, String> getOrderMap = new HashMap<>();
        getOrderMap.put("LoadOrder", String.valueOf(orderNumber));
        
        // send to server
        ClientUI.chat.accept(getOrderMap);
        
        // waiting for server respond
        while (ChatClient.fromserverString.equals(new String())) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        String str = ChatClient.fromserverString;
        ChatClient.ResetServerString();
        
        // check if the order is exist
        if (str.contains(",")) {
            String[] parts = str.split(", ");
            return parts;
        } else {
            throw new NoSuchElementException("The order number: " + orderNumber + " is not registered in the system.");
        }
    }
    
    
    
    /** (chen tsafir)
     * Updates the details of the current order in the database.
     * This method sends a request to the server to update the order details using 
     * the string representation of the order (via `toString()`). It waits for the server's 
     * confirmation and reloads the updated order details if the update is successful.
     *
     * Behavior:
     * - Sends an update request to the server with the current order details as a string.
     * - Waits for the server's response indicating whether the update succeeded.
     * - Reloads the updated order details from the database if the update is successful.
     * - Resets the server's response string after handling the update.
     */

    public void UpdateDetails() {
    	HashMap<String, String> updateMap = new HashMap<>();
        updateMap.put("UpdateOrder", toString());  // Using toString to generate a string with all the details
        
        ClientUI.chat.accept(updateMap);
        
        while (ChatClient.fromserverString.equals(new String())) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        if (ChatClient.fromserverString.equals("Updated")) {
            loadOrder(getOrderFromDB(order_number));
        }
        ChatClient.ResetServerString();
    }
    
    
    
    
    /** (chen tsafir)
     * Cancels the current reservation.
     * This method sends a request to the server to cancel the reservation 
     * associated with the current order number. It waits for the server's response 
     * and, if the cancellation is successful, updates the order status accordingly.
     *
     * Behavior:
     * - Sends a cancellation request to the server with the order number.
     * - Waits for the server's response indicating whether the cancellation succeeded.
     * - Updates the `order_status` to -1 if the cancellation is successful.
     * - Resets the server's response string after handling the cancellation.
     * **** The update itself happen in mysqlConnection in method-updateOrderStatus-
     */
    public void cancelReservation() {
    	HashMap<String, String> cancelMap = new HashMap<>();
        cancelMap.put("CancelOrder", String.valueOf(order_number));
        
        ClientUI.chat.accept(cancelMap);
        
        while (ChatClient.fromserverString.equals(new String())) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        if (ChatClient.fromserverString.equals("Cancelled")) {
            this.order_status = -1;
        }
        ChatClient.ResetServerString();
    }
    
    
    
    
    /** (chen tsafir)
     * Marks the current order as fulfilled.
     * This method sends a request to the server to update the order's status 
     * to "complete" (represented by the status value `1`). It waits for the server's 
     * confirmation and updates the local order status and arrival date upon success.
     *
     * Behavior:
     * - Sends an update request to the server with the order number and status `1` (complete).
     * - Waits for the server's response indicating whether the update succeeded.
     * - Updates the `order_status` to `1` and sets the `order_bookArrivedDate` to the current date if the update is successful.
     * - Resets the server's response string after handling the update.
     * **** The update itself happen in mysqlConnection in method-updateOrderStatus-
     */
    public void fullfil() {
        HashMap<String, String> fulfillMap = new HashMap<>();
        fulfillMap.put("UpdateOrderStatus", order_number + " " + "1");  // 1 represent status complete 
        
        ClientUI.chat.accept(fulfillMap);
        
        while (ChatClient.fromserverString.equals(new String())) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        if (ChatClient.fromserverString.equals("Updated")) {
            this.order_status = 1;
            this.order_bookArrivedDate = new Date(System.currentTimeMillis());
        }
        ChatClient.ResetServerString();
    }
    
    
    
    /** (chen tsafir)
     * return string
     */
    @Override
    public String toString() {
        return order_number + ", " + subscriber_id + ", " + book_barcode + ", " + order_requestedDate + ", " + order_status + ", " + 
               order_bookArrivedDate;
    }
    
    ///////////////////////
    /// Getters(chen tsafir)
    ///////////////////////
    
    /** (chen tsafir)
     * Gets the order number.
     * @return the order's unique identifier
     */
    public int getOrderNumber() {
        return order_number;
    }
    
    
    /**(chen tsafir)
     * Gets the subscriber ID associated with this order.
     * @return the subscriber's ID
     */
    public int getSubscriberId() {
        return subscriber_id;
    }
    
    
    /**(chen tsafir)
     * Gets the book barcode for this order.
     * @return the book's barcode
     */
    public String getBookBarcode() {
        return book_barcode;
    }
    
  
    /**(chen tsafir)
     * Gets the order's request date.
     * @return the date when the order was requested
     */
    public Date getRequestedDate() {
        return order_requestedDate;
    }
    
    
    /**(chen tsafir)
     * Gets the order's current status.
     * @return the status code (-1: cancelled, 0: pending, 1: fulfilled)
     */
    public int getStatus() {
        return order_status;
    }
    
    
    /**(chen tsafir)
     * Gets the date when the book arrived.
     * @return the arrival date of the book, or null if not arrived yet
     */
    public Date getBookArrivedDate() {
        return order_bookArrivedDate;
    }
    
    /////////////////////////////////////////////////
    /// Setters
    ///(chen tsafir)
    /// if we want to update the order details:
    /// 1. we will set the change
    /// 2. generate toString
    /// 3. send String to save in DB
    ///
    /// ******* can't change the order number *******
    //////////////////////////////////////////////////
    
   
    /**(chen tsafir)
     * Sets the order status.
     * @param status the new status (-1: cancelled, 0: waiting, 1: fulfilled)
     */
    public void setStatus(int status) {
        this.order_status = status;
    }
    
    /**(chen tsafir)
     * Sets the book arrival date.
     * @param arrivedDate the date when the book arrived
     */
    public void setBookArrivedDate(Date arrivedDate) {
        this.order_bookArrivedDate = arrivedDate;
    }
}