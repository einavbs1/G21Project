package common;

public enum LibrarianMenu {

///////-----Author: Yuval.-----///////
	AddNewLibrarian("Add New Librarian"),
	GetLibrarianDetails("Get Librarian Details"),
	UpdateLibrarian("Update Librarian");

	private final String displayName;

	/*
	 * Constructor to set the display name for each enum
	 */
	private LibrarianMenu(String displayName) {
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
	public static LibrarianMenu getSelectionFromEnumName(String enumName) {
		for (LibrarianMenu action : LibrarianMenu.values()) {
			if (action.name().equalsIgnoreCase(enumName)) { // Compare with enum name (e.g., "ShowAllSubscribers")
				return action;
			}
		}
		return null;
	}
}
