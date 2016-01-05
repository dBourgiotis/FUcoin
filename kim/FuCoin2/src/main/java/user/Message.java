package user;

import java.util.UUID;

public class Message {

	private UUID ID;

	public Message() {
		ID = UUID.randomUUID();
	}

	public UUID getID() {
		return ID;
	}

}
