package common;

public enum BorrowedRecordMenu {

	///////-----Author: Matan.-----///////
	AddNewBorrow("Add New Borrow"),
	GetBorrowRecord("Get Borrow Record"),
	UpdateBorrowDetails("Update Borrow Details"),
	SubscriberActiveBorrows("Subscriber Active Borrows"),
    GetBorrowsOfBookInSpecificDate("Get Borrows Of Book In Specific Date"),
    GetBorrowsReport("Get Borrows Report");

	private final String displayName;

	/*
	 * Constructor to set the display name for each enum
	 */
	private BorrowedRecordMenu(String displayName) {
		this.displayName = displayName;
	}

	/*
	 * Getter to retrieve the display name
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	/*
	 * Getter to retrieve the enum itself if there is his name.
	 */
	public static BorrowedRecordMenu getSelectionFromEnumName(String enumName) {
		for (BorrowedRecordMenu action : BorrowedRecordMenu.values()) {
			if (action.name().equalsIgnoreCase(enumName)) { // Compare with enum name (e.g., "ShowAllSubscribers")
				return action;
			}
		}
		return null;
	}
}
