package common;
/*
 * This enum is defining the action select of the user
 */
public enum UserSelect {
    ShowAllSubscribers("Show All Subscribers"),
    UpdatePhoneNumber("Update Phone Number"),
    UpdateEmailAddress("Update Email Address"),
	LoadSubscriber("Load Subscriber"),
	Connected("Connected"),
	Disconnect("Disconnect"),
    ///////-----Author: Einav.-----///////
    CreateBook("Create Book"),
    GetBook("Get Book"),
    UpdateBookDetails("Update Book Details"),
    CreateBookCopy("Create Book Copy"),
    GetBookCopy("Get Book Copy"),
    UpdateBookCopyDetails("Update Book Copy Details"),
    CreateNotification("Create Notification"),
    GetNotification("Get Notification"),
    CreateReminder("Create Reminder"),
    GetReminder("Get Reminder"),
	///////-----Author: Avishag.-----///////
	AddNewSubscriber("Add New Subscriber"),
	GetSubscriberDetails("Get Subscriber Details"),
	UpdateSubscriber("Update Subscriber"),
	///////-----Author: Yuval.-----///////
	AddNewLibrarian("Add New Librarian"),
	GetLibrarianDetails("Get Librarian Details"),
	UpdateLibrarian("Update Librarian"),
	///////-----Author: Matan.-----///////
	AddNewBorrow("Add New Borrow"),
	GetBorrowRecord("Get Borrow Record"),
	UpdateBorrowDetails("Update Borrow Details"),
	///////-----Author: Amir.-----///////
	AddNewLogActivity("Add New Log Activity"),
	LoadActivityById("Load Activities By ID"), 
    LoadActivityBySerial("Load Activity By Serial"),
	///////-----Author: Chen.-----///////
    ShowAllOrders("Show All Orders"),
    CreateNewOrder("Create New Order"),
    LoadOrder("Load Order"),
    UpdateOrderDetails("Update Order Details"),
    ///////-----Author: Yuval New.-----///////
	SubscriberActiveBorrows("Subscriber Active Borrows");
	
	
	
	
	
    private final String displayName;

    /* 
     * Constructor to set the display name for each enum
     */
    UserSelect(String displayName) {
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
    public static UserSelect getSelectionFromEnumName(String enumName) {
        for (UserSelect action : UserSelect.values()) {
            if (action.name().equalsIgnoreCase(enumName)) {  // Compare with enum name (e.g., "ShowAllSubscribers")
                return action;
            }
        }
        return null;
    }
}
