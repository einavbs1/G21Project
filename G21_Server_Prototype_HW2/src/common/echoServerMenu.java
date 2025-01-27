package common;
/*
 * This enum is defining the action select of the user
 */
public enum echoServerMenu {
		
	/////////////////////////
    Subscriber("Subscriber"),
    Book("Book"),
    BookCopy("BookCopy"),
    borrowedRecord("BorrowedRecord"),
    Librarian("Librarian"),
    LogActivity("LogActivity"),
    Notifications("Notifications"),
    order("Order"),
    Reminders("Reminders"),
	/////////////////////////
	Connected("Connected"),
	Disconnect("Disconnect");

	
    private final String displayName;

    /* 
     * Constructor to set the display name for each enum
     */
    echoServerMenu(String displayName) {
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
    public static echoServerMenu getSelectionFromEnumName(String enumName) {
        for (echoServerMenu action : echoServerMenu.values()) {
            if (action.name().equalsIgnoreCase(enumName)) {  // Compare with enum name (e.g., "ShowAllSubscribers")
                return action;
            }
        }
        return null;
    }
}
