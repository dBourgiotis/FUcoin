package Actors;

import java.util.ArrayList;
import java.util.List;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import main.UserFrame.User;

/**
 * 
 * @author Kim
 *
 */
public class UserActor extends UntypedActor {

	private final User user;
	private final List<String> knownNeighbours;

	/**
	 * 
	 * @return
	 */
	public static Props props(User user) {
		return Props.create(new UserCreator(user));
	}

	/**
	 * 
	 */
	public UserActor(User user) {
		this.user = user;
		knownNeighbours = new ArrayList<String>();
	}

	/**
	 * 
	 */
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof HelloMessage) {
			HelloMessage hello = (HelloMessage) message;
			String sender = getSender().path().name();
			user.getMessage(hello.getMessage(), sender);
		} else if (message instanceof JoinRequest) {
			String userName = getSelf().path().name();
			JoinAccept join = new JoinAccept(userName);
			getSender().tell(join, getSelf());
		} else if (message instanceof JoinAccept) {
			JoinAccept join = (JoinAccept) message;
			String userName = join.getName();
			user.addNeighbour(userName);
		}

	}

	public static class UserCreator implements Creator<UserActor> {
		private static final long serialVersionUID = 1L;
		private final User user;

		public UserCreator(User user) {
			this.user = user;
		}

		@Override
		public UserActor create() throws Exception {
			return new UserActor(user);
		}
	}
	
	public static class HelloMessage {
		private final String message;

		public HelloMessage(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}

	public static class JoinRequest {
	}

	public static class JoinAccept {
		private final String name;
		
		public JoinAccept(String userName) {
			this.name = userName;
		}
		
		public String getName() {
			return name;
		}
	}

}
