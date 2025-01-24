package entity;

import java.util.HashMap;
import java.util.NoSuchElementException;

import client.ChatClient;
import client.ClientUI;
import common.Month;

public class BorrowsReturnReport {

	private int month;
	private int year;
	private int totalBorrows;
	private int returnedInTime;
	private int lateReturn;
	private int returnedBeforeTime;
	private int notReturnedYet;
	private int lostBooks;
	private Month month2;

	/**
	 * Author: Einav Constractor that load notification from DB if exist.
	 * 
	 * @param id
	 */
	public BorrowsReturnReport(int month, int year) {
		loadReport(getReportFromDB(month, year));
	}

	/**
	 * Author: Einav private method that loading the details in this notification
	 * instance.
	 * 
	 * @param str -notification's details string array (usually from the DB).
	 */
	private void loadReport(String[] str) {
		month = Integer.parseInt(str[0]);
		month2 = Month.getByNumber(month);
		year = Integer.parseInt(str[1]);
		totalBorrows = Integer.parseInt(str[2]);
		returnedInTime = Integer.parseInt(str[3]);
		lateReturn = Integer.parseInt(str[4]);
		returnedBeforeTime = Integer.parseInt(str[5]);
		notReturnedYet = Integer.parseInt(str[6]);
		lostBooks = Integer.parseInt(str[7]);
	}

	/**
	 * Author: Einav
	 * 
	 * @param serial
	 * @return String[] - array of notification's string with each field in array's
	 *         positions.
	 * @throws NoSuchElementException
	 */
	private String[] getReportFromDB(int month, int year) throws NoSuchElementException {
		String str = new String();
		HashMap<String, String> requestHashMap = new HashMap<String, String>();
		requestHashMap.put("GetBorrowsReport", String.valueOf(month) + ", " + String.valueOf(year));
		ClientUI.chat.accept(requestHashMap);
		/// send request to DB to get the string.
		str = ChatClient.getStringfromServer();
		if (str.contains(",")) {
			String[] parts = str.split(", ");
			return parts;
		} else {
			throw new NoSuchElementException("We can't give Report for this Date: " + getMonthString() + "." + year);
		}
	}

	@Override
	public String toString() {
		return month + ", " + year + ", " + totalBorrows + ", " + returnedInTime + ", " + lateReturn + ", "
				+ returnedBeforeTime + ", " + notReturnedYet + ", " + lostBooks;
	}

	public int getTotalBorrows() {
		return totalBorrows;
	}

	public int getReturnedInTime() {
		return returnedInTime;
	}

	public int getLateReturn() {
		return lateReturn;
	}

	public int getReturnedBeforeTime() {
		return returnedBeforeTime;
	}

	public int getNotReturnedYet() {
		return notReturnedYet;
	}

	public int getLostBooks() {
		return lostBooks;
	}

	public int getMonth() {
		return month;
	}

	public String getMonthString() {
		if (month2 == null) {
			return "Invalid Month";
		}
		return month2.toString();
	}

	public int getYear() {
		return year;
	}

	///////////////////////
	/// Getters
	/// No setters because we don't want to change notification.
	///////////////////////

	public void addTotalBorrows() {
		totalBorrows++;
	}

	public void addReturnedInTime() {
		returnedInTime++;
	}

	public void addLateReturn() {
		lateReturn++;
	}

	public void addReturnedBeforeTime() {
		returnedBeforeTime++;
	}

	public void addNotReturnedYet() {
		notReturnedYet++;
	}

	public void addLostBooks() {
		lostBooks++;
	}

	public void removeFromLostBooks() {
		lostBooks--;
	}

	public void removeFromNotReturnedYet() {
		notReturnedYet--;
	}

}
