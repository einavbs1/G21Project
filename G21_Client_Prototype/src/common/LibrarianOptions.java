package common;


public enum LibrarianOptions {
	RegisterNewSubscriber("Register New Subscriber"),
	SearchBooks("Search Books"),
	BorrowBook("Borrow Book"),
	ReturnBook("Return Book"),
	CurrentBorrowBooks("Current Borrow Books"),
	ViewAllSubscribers("View All Subscribers"),
	UpdateSubscriberData("Update Subscriber Data"),
	GenerateReports("Generate Reports"),
	ViewNotifications("View Notifications");
	
    private final String displayName;


	LibrarianOptions(String displayName) {
        this.displayName = displayName;
    }
	
	public String getDisplayName() {
        return this.displayName;
    }

	 /** This method is geting the userselect if the string equals to the display name
     * @param select - string of the user select
     * @return UserSelect - that the user selected  / null if not found
     */
    public LibrarianOptions getSelection(String select) {
        for (LibrarianOptions action : LibrarianOptions.values()) {
            if (action.getDisplayName().equalsIgnoreCase(select)) {
                return action;
            }
        }
        return null;
    }
    
    /** This method is returning the UserSelect enum if we got enum as a string (but not the display message)
     * if there is not enum like this we returning null.
     * @param enumName - the string of the exact enum (not displayname)
     * @return UserSelect
     */
    public static LibrarianOptions getSelectionFromEnumName(String enumName) {
        for (LibrarianOptions action : LibrarianOptions.values()) {
            if (action.name().equalsIgnoreCase(enumName)) {  // Compare with enum name (e.g., "ShowAllSubscribers")
                return action;
            }
        }
        return null;  // Return null if no match is found
    }
}
