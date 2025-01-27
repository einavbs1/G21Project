package common;

public enum BookMenu {
	
    ///////-----Author: Einav.-----///////
    CreateBook("Create Book"),
    GetBook("Get Book"),
    UpdateBookDetails("Update Book Details"),
    SearchBookByName("Search Book By Name"),
    SearchBookBySubject("Search Book By Subject"),
    SearchBookByDescription("Search Book By Description"),
    GetAllMyCopies("Get All My Copies"),
    GetBookBarcodesAndTitles("Get Book Barcodes And Titles"),
    GetAllSubjects("Get All Subjects");

    private final String displayName;

    /* 
     * Constructor to set the display name for each enum
     */
    private BookMenu(String displayName) {
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
    public static BookMenu getSelectionFromEnumName(String enumName) {
        for (BookMenu action : BookMenu.values()) {
            if (action.name().equalsIgnoreCase(enumName)) {  // Compare with enum name (e.g., "ShowAllSubscribers")
                return action;
            }
        }
        return null;
    }
	
}
