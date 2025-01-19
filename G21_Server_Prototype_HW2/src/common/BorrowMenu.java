package common;

public enum BorrowMenu {

	///////-----Author: Matan.-----///////
	AddNewBorrow("Add New Borrow"),
	GetBorrowRecord("Get Borrow Record"),
	UpdateBorrowDetails("Update Borrow Details");

	private final String displayName;

	/*
	 * Constructor to set the display name for each enum
	 */
	private BorrowMenu(String displayName) {
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
	public static BorrowMenu getSelectionFromEnumName(String enumName) {
		for (BorrowMenu action : BorrowMenu.values()) {
			if (action.name().equalsIgnoreCase(enumName)) { // Compare with enum name (e.g., "ShowAllSubscribers")
				return action;
			}
		}
		return null;
	}
}
