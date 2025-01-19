package common;

public enum RemindersMenu {

///////-----Author: Einav.-----///////
	CreateReminder("Create Reminder"),
    GetReminder("Get Reminder");

	private final String displayName;

	/*
	 * Constructor to set the display name for each enum
	 */
	private RemindersMenu(String displayName) {
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
	public static RemindersMenu getSelectionFromEnumName(String enumName) {
		for (RemindersMenu action : RemindersMenu.values()) {
			if (action.name().equalsIgnoreCase(enumName)) { // Compare with enum name (e.g., "ShowAllSubscribers")
				return action;
			}
		}
		return null;
	}
}
