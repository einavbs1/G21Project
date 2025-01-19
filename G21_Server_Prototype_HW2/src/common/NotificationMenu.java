package common;

public enum NotificationMenu {

///////-----Author: Einav.-----///////
	CreateNotification("Create Notification"),
	GetNotification("Get Notification");

	private final String displayName;

	/*
	 * Constructor to set the display name for each enum
	 */
	private NotificationMenu(String displayName) {
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
	public static NotificationMenu getSelectionFromEnumName(String enumName) {
		for (NotificationMenu action : NotificationMenu.values()) {
			if (action.name().equalsIgnoreCase(enumName)) { // Compare with enum name (e.g., "ShowAllSubscribers")
				return action;
			}
		}
		return null;
	}
}
