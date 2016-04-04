package getaway;
import java.io.*;

public class Message implements Serializable {
	
	protected static final long serialVersionUID = 1112122200L;
	
	static final int
			WHOISIN = 0,
			CHATMESSAGE = 1,
			LOGOUT = 2,
			PRIVATE_MESSAGE = 3;
	private int type;
	private String message;
	private String [] messageArray;
	
	Message(int type, String message) {
		this.type = type;
		this.message = message;
	}

	Message (int type, String[] messageArray) {
		this.type = type;
		this.messageArray = messageArray;
	}
	
	int getType(){
		return type;
	}
	
	String getMessage() {
		return message;
	}
	String [] getMessageArray() { return messageArray; }

}
