package common;

public enum OrderMenu {

	///////-----Author: Chen.-----///////
    CreateNewOrder("Create New Order"),
    LoadOrder("Load Order"),
    UpdateOrderDetails("Update Order Details"),
	GetAllActiveOrdersofaBook("Get All Active Orders for a Book"),
    ShowSubscriberActiveOrders("Show Subscriber Active Orders");

	private final String displayName;

	/*
	 * Constructor to set the display name for each enum
	 */
	private OrderMenu(String displayName) {
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
	public static OrderMenu getSelectionFromEnumName(String enumName) {
		for (OrderMenu action : OrderMenu.values()) {
			if (action.name().equalsIgnoreCase(enumName)) { // Compare with enum name (e.g., "ShowAllSubscribers")
				return action;
			}
		}
		return null;
	}
}
