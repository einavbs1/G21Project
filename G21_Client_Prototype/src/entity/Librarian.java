package entity;

import java.sql.Date;
import java.util.HashMap;
import java.util.NoSuchElementException;

import client.ChatClient;
import client.ClientUI;

public class Librarian {

	private static int librarian_id;
	private String librarian_name;
	private String librarian_password;
	private Date librarian_lastCheckedNotifications;

	/**
	 * Author: Yuval. Constructor that load librarian from DB if exist.
	 * 
	 * @param id
	 */
	public Librarian(int librarian_id) {
		String[] str = getLibrarianFromDB(librarian_id);
		loadLibrarian(str);
	}

	/**
	 * Author: Yuval. Constructor that creates a new librarian and adds it to the
	 * DB.
	 * 
	 * @param librarian_id       - Subscriber ID
	 * @param librarian_name     - Subscriber name
	 * @param librarian_password - Subscriber phone number
	 */
	public Librarian(int librarian_id, String librarian_name, String librarian_password) {
		String newlib = librarian_id + ", " + librarian_name + ", " + librarian_password;

		/// LibrarianToDB
		HashMap<String, String> addLibrarianMap = new HashMap<>();
		addLibrarianMap.put("Librarian+AddNewLibrarian", newlib);
		ClientUI.chat.accept(addLibrarianMap);

		// now load to this librarian
		String[] str = getLibrarianFromDB(librarian_id);
		loadLibrarian(str);
	}

	/**
	 * Author: Yuval. private method that loading the details in this id librarian
	 * instance.
	 * 
	 * @param str - librarian's details string array (usually from the DB).
	 */
	private void loadLibrarian(String[] str) {
		librarian_id = Integer.parseInt(str[0]);
		librarian_name = str[1];
		librarian_password = str[2];
		librarian_lastCheckedNotifications = Date.valueOf(str[3]);
	}

	/**
	 * Author: Yuval.
	 * 
	 * @param id - subscriber's id to get.
	 */
	private String[] getLibrarianFromDB(int librarian_id) throws NoSuchElementException {
		String response = new String();

		// send request to DB to get the string

		HashMap<String, String> request = new HashMap<>();
		request.put("Librarian+GetLibrarianDetails", String.valueOf(librarian_id));
		ClientUI.chat.accept(request);
		response = ChatClient.getStringfromServer();

		if (response.contains(",")) {
			String[] parts = response.split(", ");
			return parts;
		} else {
			throw new NoSuchElementException("The id: " + librarian_id + " is not registered to the system.");
		}
	}

	/**
	 * Author: Yuval. After we set the new information that we want to save we will
	 * send request to DB. see details in the setter section VVV.
	 */
	public void UpdateLibrarianDetails() {
		String newDetails = toString();

		/// Send the update request to the server

		HashMap<String, String> updateMap = new HashMap<>();
		updateMap.put("Librarian+UpdateLibrarian", newDetails);

		ClientUI.chat.accept(updateMap);

		// loading new information from DB. ------- was before update might delete.
		loadLibrarian(getLibrarianFromDB(librarian_id));
	}

	/**
	 * Author: Yuval. toString of librarian
	 */
	public String toString() {
		return librarian_id + ", " + librarian_name + ", " + librarian_password + ", " + librarian_lastCheckedNotifications;
	}

	/////////////////////////////////////
	/// Getters - Author: Yuval.
	/////////////////////////////////////
	public int getId() {
		return librarian_id;
	}

	public String getName() {
		return librarian_name;
	}

	public String getPassword() {
		return librarian_password;
	}
	
	public Date getLibrarian_lastCheckedNotifications() {
		return librarian_lastCheckedNotifications;
	}

	/////////////////////////////////////////////////
	/// Setters - Author: Yuval.
	///
	/// if we want to update the librarian details:
	/// 1. we will set the change.
	/// 2. generate toString
	/// 3. send String to save in DB.
	///
	/// ******* can't change the ID *******
	//////////////////////////////////////////////////

	
	public void setLibrarian_lastCheckedNotifications(Date librarian_lastCheckedNotifications) {
		this.librarian_lastCheckedNotifications = librarian_lastCheckedNotifications;
	}

	public void setName(String name) {
		this.librarian_name = name;
	}

	public void setPassword(String password) {
		this.librarian_password = password;
	}
}