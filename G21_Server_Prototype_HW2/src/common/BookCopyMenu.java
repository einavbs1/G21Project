package common;

public enum BookCopyMenu {
	
    ///////-----Author: Einav.-----////////
	CreateBookCopy("Create Book Copy"),
    GetBookCopy("Get Book Copy"),
    UpdateBookCopyDetails("Update Book Copy Details");

    private final String displayName;

    /* 
     * Constructor to set the display name for each enum
     */
    private BookCopyMenu(String displayName) {
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
    public static BookCopyMenu getSelectionFromEnumName(String enumName) {
        for (BookCopyMenu action : BookCopyMenu.values()) {
            if (action.name().equalsIgnoreCase(enumName)) {  // Compare with enum name (e.g., "ShowAllSubscribers")
                return action;
            }
        }
        return null;
    }
	
}
