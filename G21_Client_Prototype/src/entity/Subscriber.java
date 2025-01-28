package entity;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import client.ChatClient;
import client.ClientUI;

public class Subscriber {

	public static String ACTIVE = "Active";
	public static String FROZEN = "Frozen";

	private static int id;
	private String name;
	private String phoneNumber;
	private String email;
	private String password;
	private String status;
	private Date registeredDate;
	private Date frozenUntil;
	private Date lastCheckedReminders;

	/**
	 * Author: Avishag. Constructor that load id from DB if exist.
	 * 
	 * @param id
	 */
	public Subscriber(int id) throws NoSuchElementException {
		String[] str = getSubscriberFromDB(id);
		loadSubscriber(str);
	}

	/**
	 * Author: Avishag. Constructor that creates a new subscriber and adds it to the
	 * DB.
	 * 
	 * @param id          - Subscriber ID
	 * @param name        - Subscriber name
	 * @param phoneNumber - Subscriber phone number
	 * @param email       - Subscriber email address
	 * @param password    - Subscriber password
	 * @throws Exception
	 */
	public Subscriber(int id, String name, String phoneNumber, String email, String password) throws Exception {
		String newsub = id + ", " + name + ", " + phoneNumber + ", " + email + ", " + password + ", " + ACTIVE;

		/// addSubscriberToDB
		HashMap<String, String> addSubscriberMap = new HashMap<>();
		addSubscriberMap.put("Subscriber+AddNewSubscriber", newsub);
		ClientUI.chat.accept(addSubscriberMap);
		String res = ChatClient.getStringfromServer();
		if (res.equals("Error")) {
			throw new Exception("Couldn't add the subscriber = " + name);
		}
		// now load to this subscriber
		String[] str = getSubscriberFromDB(id);
		loadSubscriber(str);
	}

	/**
	 * Author: Avishag. private method that loading the details in this id
	 * subscriber instance.
	 * 
	 * @param str - subscriber's details string array (usually from the DB).
	 */
	private void loadSubscriber(String[] str) {
		id = Integer.parseInt(str[0]);
		name = str[1];
		phoneNumber = str[2];
		email = str[3];
		password = str[4];
		status = str[5];
		registeredDate = Date.valueOf(str[6]);
		frozenUntil = (str[7].equals("null")) ? null : Date.valueOf(str[7]);
		lastCheckedReminders = (str[8].equals("null")) ? null : Date.valueOf(str[8]);

		if (needToChangeFrozenStatus(str[7])) {
			SubscribersStatusReport reportToUpdate = new SubscribersStatusReport(LocalDate.now().getMonthValue(), LocalDate.now().getYear());
			reportToUpdate.addUnfrozed();
			reportToUpdate.UpdateDetails();
			frozenUntil = null;
			status = "Active";
			UpdateDetails();
		}

	}


	/**
	 * check if status of subscriber need to be change to frozen
	 * @param date
	 * @return
	 */
	private boolean needToChangeFrozenStatus(String date) {
		if (date == null || date.equals("null")) {
			return false;
		}
		LocalDate inputDate = LocalDate.parse(date);
		LocalDate today = LocalDate.now();

		if (inputDate.isBefore(today)) {

			return true;
		}

		return false;

	}

	/**
	 * Author: Avishag.
	 * return specific subscriber of borrowing from DB 
	 * @param id - subscriber's id to get.
	 */
	private String[] getSubscriberFromDB(int id) throws NoSuchElementException {
		String response = new String();

		/// send request to DB to get the string

		HashMap<String, String> request = new HashMap<>();
		request.put("Subscriber+GetSubscriberDetails", String.valueOf(id));
		ClientUI.chat.accept(request);
		response = ChatClient.getStringfromServer();
		if (response.contains(",")) {
			String[] parts = response.split(", ");
			return parts;
		} else {
			throw new NoSuchElementException("The id: " + id + " is not registered to the system.");
		}
	}

	/**
	 * Author: Avishag. After we set the new information that we want to save we
	 * will send request to DB. see details in the setter section VVV.
	 */
	public boolean UpdateDetails() {
		String newDetails = toString();
		/// Send the update request to the server

		HashMap<String, String> updateMap = new HashMap<>();
		updateMap.put("Subscriber+UpdateSubscriber", newDetails);

		ClientUI.chat.accept(updateMap);
		String str = ChatClient.getStringfromServer();

		// loading new information from DB. ------- was before update might delete.
		loadSubscriber(getSubscriberFromDB(id));

		if (!str.equals("Updated")) {
			return false;
		}

		return true;

	}


	/**
	 * return all records of subscriber from DB
	 * @return
	 */
	public static List<String> showAllSubscribers() {
		HashMap<String, String> requestMap = new HashMap<>();
		requestMap.put("Subscriber+ShowAllSubscribers", "");

		ClientUI.chat.accept(requestMap);
		List<String> res = ChatClient.getListfromServer();

		return res;

	}

	/**
	 * return all records of subscriber only by id and name respectively from DB
	 * @return
	 */
	public static List<String> getIDsAndNames() {
		HashMap<String, String> requestMap = new HashMap<>();
		requestMap.put("Subscriber+GetSubscribersIDsAndNames", "");
		ClientUI.chat.accept(requestMap);
		return ChatClient.getListfromServer();
	}

	/**
	 * add new record to DB of change status to frozen
	 * @param SubscriberID
	 * @param start
	 * @param end
	 * @return
	 */
	public static String addingNewRecordOfFrozen(int SubscriberID, Date start, Date end) {
		HashMap<String, String> requestMap = new HashMap<>();
		requestMap.put("Subscriber+AddingNewRecordOfFrozen", SubscriberID + ", " + start + ", " + end);
		ClientUI.chat.accept(requestMap);
		return ChatClient.getStringfromServer();
	}
	

	/*
	 * return certain frozen record of subscriber from DB
	 * @param SubscriberID
	 * @param end
	 * @return
	 */
	public static String getSpecificFrozenRecord(int SubscriberID, Date end) {
		HashMap<String, String> requestMap = new HashMap<>();
		requestMap.put("Subscriber+GetSpecificFrozenRecord", SubscriberID + ", " + end);
		ClientUI.chat.accept(requestMap);
		return ChatClient.getStringfromServer();
	}
	

	/**
	 * update record in DB, frozen record of subscriber
	 * @param SubscriberID
	 * @param start
	 * @param end
	 * @return
	 */
	public static String updateRecordOfFrozen(int serial, Date start, Date end) {
		HashMap<String, String> requestMap = new HashMap<>();
		requestMap.put("Subscriber+UpdateRecordOfFrozen", serial + ", " + start + ", " + end);
		ClientUI.chat.accept(requestMap);
		return ChatClient.getStringfromServer();
	}

	/**
	 * return certain frozen report of subscriber from DB by certain times
	 * @param SubscriberID
	 * @param month1
	 * @param year1
	 * @return
	 */
	public static List<String> GetFrozenReportForSubscriber(int SubscriberID, int month1, int year1) {
		HashMap<String, String> requestMap = new HashMap<>();
		requestMap.put("Subscriber+GetFrozenReportForSubscriber", SubscriberID + ", " + month1 + ", " + year1);
		ClientUI.chat.accept(requestMap);
		return ChatClient.getListfromServer();
	}

	/**
	 * Author: Avishag. toString of subscriber
	 */
	public String toString() {
		return id + ", " + name + ", " + phoneNumber + ", " + email + ", " + password + ", " + status + ", "
				+ registeredDate + ", " + frozenUntil + ", " + lastCheckedReminders;
	}

	/////////////////////////////////
	/// Getters - Author: Avishag.
	/////////////////////////////////
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getStatus() {
		return status;
	}

	public Date getRegisteredDate() {
		return registeredDate;
	}

	public Date getFrozenUntil() {
		return frozenUntil;
	}

	public Date getLastCheckedReminders() {
		return lastCheckedReminders;
	}

	/////////////////////////////////////////////////
	/// Setters - Author: Avishag.
	///
	/// if we want to update the subscriber details:
	/// 1. we will set the change.
	/// 2. generate toString
	/// 3. send String to save in DB.
	///
	/// ******* can't change the ID *******
	//////////////////////////////////////////////////

	public void setFrozenUntil(Date frozenUntil) {
		this.frozenUntil = frozenUntil;
	}

	public void setLastCheckedReminders(Date lastCheckedReminders) {
		this.lastCheckedReminders = lastCheckedReminders;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
