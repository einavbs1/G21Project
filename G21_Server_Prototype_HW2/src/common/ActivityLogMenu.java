package common;

public enum ActivityLogMenu {

	///////-----Author: Amir.-----///////
	AddNewLogActivity("Add New Log Activity"),
	LoadActivityById("Load Activities By ID"), 
    LoadActivityBySerial("Load Activity By Serial");

	private final String displayName;

	/*
	 * Constructor to set the display name for each enum
	 */
	private ActivityLogMenu(String displayName) {
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
	public static ActivityLogMenu getSelectionFromEnumName(String enumName) {
		for (ActivityLogMenu action : ActivityLogMenu.values()) {
			if (action.name().equalsIgnoreCase(enumName)) { // Compare with enum name (e.g., "ShowAllSubscribers")
				return action;
			}
		}
		return null;
	}
}
