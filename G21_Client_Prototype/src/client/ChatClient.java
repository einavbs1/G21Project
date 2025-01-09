package client;

import ocsf.client.*;
import common.ChatIF;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is handling the messages from the client to the server and the other way.
 * another test. - einav
 */
public class ChatClient extends AbstractClient {

	ChatIF clientUI;
	private static List<String> listFromServer = new ArrayList<String>();
	private static String fromserverString = new String();
	public static boolean awaitResponse = false;
	

	public ChatClient(String host, int port, ChatIF clientUI) throws IOException {
		super(host, port); // Call the superclass constructor
		this.clientUI = clientUI;
		// openConnection();
	}
	
	/**Author: Einav
	 * getting the respond from the server and clear the message. (who needed to take it took).
	 * @return String - the respond from the server
	 */
	public static String getStringfromServer() {
		String toRet = fromserverString;
		fromserverString = new String();
		return toRet;
	}
	/**Author: Einav
	 * getting the respond from the server and clear the message. (who needed to take it took).
	 * @return List<String> - the respond from the server
	 */
	public static List<String> getListfromServer() {
		List<String> toRet = listFromServer;
		listFromServer = new ArrayList<String>();
		return toRet;
	}
	

	/**
	 * This method is handeling the msg from the server, we got 2 options to get the
	 * msg: 1- if we ask for all subscribers table we are expecting for a
	 * List<String> 2- if we asked to load one subscriber or waiting for feedback
	 * from the server we would expect one String.
	 * @param msg - the message from the server
	 */
	public void handleMessageFromServer(Object msg) {
		//extracting the msg from the server upon the class.
		if (msg.getClass().equals(listFromServer.getClass())) {
			listFromServer = (List<String>) msg;
		}
		else if (msg.getClass().equals(fromserverString.getClass())) {
			this.fromserverString = (String) msg;
		}
		else {
			System.out.println("The return from the server class is: " + msg.getClass());
		}
		//note that we got the result right now.
		awaitResponse = false;
	}

	
	/**This method is getting the message from the client and send it to the server.
	 * @param obj - object but the client sending hashmap to the server
	 */
	public void handleMessageFromClientUI(Object obj) {
		try {
			openConnection();// in order to send more than one message
			awaitResponse = true;
			sendToServer(obj);
			// wait for response
			while (awaitResponse) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			clientUI.display("Could not send message to server: Terminating client." + e);
			quit();
		}
	}

	/**
	 * This method terminates the client.
	 */
	public void quit() {
		try {
			closeConnection();
		} catch (IOException e) {
		}
		System.exit(0);
	}
}
