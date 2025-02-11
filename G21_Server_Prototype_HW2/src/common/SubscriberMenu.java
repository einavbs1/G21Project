package common;

public enum SubscriberMenu {

	AddNewSubscriber("Add New Subscriber"),
	GetSubscriberDetails("Get Subscriber Details"),
	UpdateSubscriber("Update Subscriber"),
	ShowAllSubscribers("Show All Subscribers"),
    GetSubscribersIDsAndNames("Get Subscribers IDs And Names"),
    GetFrozenReportForSubscriber("Get Frozen Report For Subscriber"),
    AddingNewRecordOfFrozen("Adding New Record Of Frozen"),
    UpdateRecordOfFrozen("Update Record Of Frozen"),
    GetSpecificFrozenRecord("Get Specific Frozen Record"),
    GetStatusReport("Get Status Report"),
    UpdateSubscribersStatusReport("Update Subscribers Status Report");

	private final String displayName;

	/*
	 * Constructor to set the display name for each enum
	 */
	private SubscriberMenu(String displayName) {
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
	public static SubscriberMenu getSelectionFromEnumName(String enumName) {
		for (SubscriberMenu action : SubscriberMenu.values()) {
			if (action.name().equalsIgnoreCase(enumName)) { // Compare with enum name (e.g., "ShowAllSubscribers")
				return action;
			}
		}
		return null;
	}
}
