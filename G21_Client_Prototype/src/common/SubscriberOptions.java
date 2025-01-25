package common;

public enum SubscriberOptions {
	UpdateMyData("Update My Data"),
	ViewActionsHistory("View Actions History"),
	ExtendBorrowRequest("Extend Borrow Request"),
	SearchBooks("Search Books"),
	OrderBook("Order Book"),
	ShowMyOrders("Show My Orders"),
	ViewReminders("Show My Reminders");
	
    private final String displayName;


	SubscriberOptions(String displayName) {
        this.displayName = displayName;
    }
	
	public String getDisplayName() {
        return this.displayName;
    }

	 /** This method is geting the userselect if the string equals to the display name
     * @param select - string of the user select
     * @return UserSelect - that the user selected  / null if not found
     */
    public SubscriberOptions getSelection(String select) {
        for (SubscriberOptions action : SubscriberOptions.values()) {
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
    public static SubscriberOptions getSelectionFromEnumName(String enumName) {
        for (SubscriberOptions action : SubscriberOptions.values()) {
            if (action.name().equalsIgnoreCase(enumName)) {  // Compare with enum name (e.g., "ShowAllSubscribers")
                return action;
            }
        }
        return null;  // Return null if no match is found
    }
}
