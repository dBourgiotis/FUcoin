package user;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;

/**
 * A {@code UserActor} represents a user, who has a list of known users, with
 * whom they can make transactions. A user has a wallet, that stores the current
 * balance and performs the transactions.
 * 
 * @author Kim
 *
 */
public class UserActor extends UntypedActor {

	private final String userName;
	private int balance;
	private final Map<String, ActorRef> addressBook;
	private final Collection<UUID> seenMessages;
	private final Map<UUID, Integer> openRequests;
	private final Map<UUID, Integer> openReplies;
	private final UserControl userControl;
	private static final int MAX_SEEN_MESSAGES = 1000;

	private UserActor(String userName, UserControl userControl, int balance) {
		this.userName = userName;
		this.userControl = userControl;
		this.setBalance(balance);
		addressBook = new HashMap<String, ActorRef>();
		seenMessages = new CircularFifoQueue<UUID>(MAX_SEEN_MESSAGES);
		openRequests = new HashMap<UUID, Integer>();
		openReplies = new HashMap<UUID, Integer>();
	}

	private UserActor(String userName, UserControl userControl) {
		this(userName, userControl, 0);
	}

	public void startTransaction(String userName, int amount) {
		Message request = new TransactionRequest(userName, amount, getUserName());
		UUID requestID = request.getID();

		addRequest(requestID, amount);
		if (isNeighbour(userName)) {
			getAddress(userName).tell(request, getSelf());
		} else {
			gossip(request);
		}
	}

	@Override
	public void onReceive(Object message) throws Exception {
		System.out.println("==================");
		System.out.println("Username: " + getUserName());
		if (message instanceof JoinRequest) {
			handleJoinRequest((JoinRequest) message);
		} else if (message instanceof JoinReply) {
			System.out.println("JoinReply");
			handleJoinReply((JoinReply) message);
		} else if (message instanceof TransactionInit) {
			System.out.println("TransactionInit");
			handleTransactionInit((TransactionInit) message);
		} else if (message instanceof TransactionRequest) {
			System.out.println("TransactionRequest");
			handleTransactionRequest((TransactionRequest) message);
		} else if (message instanceof TransactionReply) {
			System.out.println("TransactionReply");
			handleTransactionReply((TransactionReply) message);
		} else if (message instanceof NeighbourRequest) {
			System.out.println("NeighbourRequest");
			handleNeighbourRequest((NeighbourRequest) message);
		} else if (message instanceof TransactionAccept) {
			System.out.println("TransactionAccept");
			handleTransactionAccept((TransactionAccept) message);
		} else {
			unhandled(message);
		}
	}

	// Message Handlers

	private void handleTransactionAccept(TransactionAccept message) {
		String userName = message.getUserName();
		UUID ID = message.getID();
		UUID requestID = message.getRequestID();
		Collection<String> neighbours = getNeigbours();
		Map<String, ActorRef> addressBook = getAddressBook();

		if (isMessageSeen(ID)) {
			// discard message
		} else if (getUserName().equals(userName)) {
			handleOwnTransactionAccept(requestID, userName);
		} else if (neighbours.contains(userName)) {
			ActorRef recipient = addressBook.get(userName);
			recipient.tell(message, getSelf());
		} else {
			gossip(message);
		}
		markMessageSeen(ID);

	}

	private void handleOwnTransactionAccept(UUID requestID, String userName) {
		if (isOpenReply(requestID)) {
			int amount = getReplyAmount(requestID);
			addToBalance(amount);
			removeReply(requestID);
		} else {
			System.out.println("Wasn't an open reply.");
		}
	}

	private void handleTransactionInit(TransactionInit init) {
		TransactionRequest request = new TransactionRequest(init);
		UUID requestID = request.getID();
		int amount = request.getAmount();
		if (amount <= getBalance()) {
			addRequest(requestID, amount * -1);
			handleTransactionRequest(request);
		} else {
			System.out.println("Not enough money.");
		}
	}

	private void handleNeighbourRequest(NeighbourRequest message) {
		ActorRef sender = getSender();
		Collection<String> neighbours = getNeigbours();
		NeighbourReply reply = new NeighbourReply(neighbours);

		sender.tell(reply, sender);
	}

	private void handleTransactionReply(TransactionReply message) {
		String userName = message.getUserName();
		String senderName = message.getSenderName();
		UUID ID = message.getID();
		UUID requestID = message.getRequestID();
		Collection<String> neighbours = getNeigbours();
		Map<String, ActorRef> addressBook = getAddressBook();

		if (isMessageSeen(ID)) {
			// discard message
		} else if (getUserName().equals(userName)) {
			handleOwnTransactionReply(requestID, userName, senderName);
		} else if (neighbours.contains(userName)) {
			ActorRef recipient = addressBook.get(userName);
			recipient.tell(message, getSelf());
		} else {
			gossip(message);
		}
		markMessageSeen(ID);
	}

	private void handleOwnTransactionReply(UUID requestID, String userName, String senderName) {
		if (isOpenRequest(requestID)) {
			int amount = getRequestedAmount(requestID);
			addToBalance(amount);
			removeRequest(requestID);
			TransactionAccept accept = new TransactionAccept(senderName, requestID);
			getSender().tell(accept, getSelf());
		} else {
			System.out.println("Not requested transaction reply.");
		}
	}

	private void addToBalance(int amount) {
		setBalance(getBalance() + amount);
		userControl.updateBalance(getBalance());
	}

	private int getRequestedAmount(UUID requestID) {
		return openRequests.get(requestID);
	}

	private boolean isOpenRequest(UUID requestID) {
		return openRequests.containsKey(requestID);
	}

	private void addRequest(UUID requestID, int amount) {
		openRequests.put(requestID, new Integer(amount));
	}

	private void removeRequest(UUID requestID) {
		openRequests.remove(requestID);
	}

	private boolean isOpenReply(UUID requestId) {
		return openReplies.containsKey(requestId);
	}

	private void addReply(UUID requestID, int amount) {
		openReplies.put(requestID, new Integer(amount));
		System.out.println(amount);
	}

	private void removeReply(UUID requestID) {
		openReplies.remove(requestID);
	}

	private int getReplyAmount(UUID requestID) {
		return openReplies.get(requestID);
	}

	private void handleTransactionRequest(TransactionRequest message) {
		String userName = message.getUserName();
		UUID messageID = message.getID();

		if (isMessageSeen(messageID)) {
			// discard request
		} else if (getUserName().equals(userName)) {
			handleOwnRequest(message);
		} else if (getNeigbours().contains(userName)) {
			ActorRef recipient = addressBook.get(userName);
			recipient.tell(message, getSelf());
		} else {
			gossip(message);
		}
		markMessageSeen(messageID);
	}

	private void handleOwnRequest(TransactionRequest message) {
		int amount = message.getAmount();
		UUID requestID = message.getID();
		String sender = message.getSenderName();

		if (getBalance() >= -1 * amount) {
			addReply(requestID, amount);
			TransactionReply reply = new TransactionReply(sender, requestID, getUserName());
			getSelf().tell(reply, getSelf());
		} else {
			System.out.println("Not enough money to comply the request.");
		}


	}

	private void handleJoinRequest(JoinRequest message) {
		ActorRef sender = getSender();
		String userName = message.getUserName();
		UUID messageID = message.getID();

		if (isMessageSeen(messageID)) {
			// discard message
		} else if (isNeighbour(userName)) {
			ActorRef address = getAddress(userName);
			JoinReply reply = new JoinReply(userName, address);
			sender.tell(reply, getSelf());
		} else {
			gossip(message);
		}
		markMessageSeen(messageID);
	}

	private void markMessageSeen(UUID messageID) {
		seenMessages.add(messageID);
	}

	private boolean isMessageSeen(UUID messageID) {
		return seenMessages.contains(messageID);
	}

	private void handleJoinReply(JoinReply message) {
		String userName = message.getUserName();
		ActorRef address = message.getAddress();
		if (address != null) {
			addNeighbour(userName, address);
		} else {
			JoinRequest joinRequest = new JoinRequest(userName);
			gossip(joinRequest);
		}
	}

	private void gossip(Message message) {
		for (ActorRef neighbour : getAddresses()) {
			neighbour.tell(message, getSelf());
		}
		
	}

	private Collection<ActorRef> getAddresses() {
		return getAddressBook().values();
	}

	public Collection<String> getNeigbours() {
		return getAddressBook().keySet();
	}

	private ActorRef getAddress(String userName) {
		return getAddressBook().get(userName);
	}

	private boolean isNeighbour(String userName) {
		return getAddressBook().containsKey(userName);
	}

	public void addNeighbour(String userName, ActorRef address) {
		getAddressBook().put(userName, address);
		userControl.updateNeighbours(getNeigbours());
	}

	// Messages

	public static class TransactionInit extends TransactionRequest {

		public TransactionInit(String userName, int amount, String senderName) {
			super(userName, amount, senderName);
		}

	}

	public static class BalanceReply extends Message {
		private final int balance;

		public BalanceReply(int balance) {
			this.balance = balance;
		}

		public int getBalance() {
			return balance;
		}
	}

	public static class NeighbourRequest extends Message {

	}

	public static class NeighbourReply extends Message {
		private final Collection<String> neighbours;

		public NeighbourReply(Collection<String> neighbours) {
			this.neighbours = neighbours;
		}

		public Collection<String> getNeighbours() {
			return neighbours;
		}
	}

	public static class JoinRequest extends Message {
		private final String userName;

		public JoinRequest(String userName) {
			this.userName = userName;
		}

		public String getUserName() {
			return userName;
		}
	}

	public static class JoinReply extends Message {
		private final String userName;
		private final ActorRef address;

		public JoinReply(String userName, ActorRef address) {
			this.userName = userName;
			this.address = address;
		}

		public String getUserName() {
			return userName;
		}

		public ActorRef getAddress() {
			return address;
		}
	}

	public static class TransactionRequest extends Message {
		private final String userName;
		private final String senderName;
		private final int amount;

		public TransactionRequest(String userName, int amount, String senderName) {
			this.userName = userName;
			this.amount = amount;
			this.senderName = senderName;
		}

		public TransactionRequest(TransactionInit init) {
			this.userName = init.getUserName();
			this.amount = init.getAmount();
			this.senderName = init.getSenderName();
		}

		public int getAmount() {
			return amount;
		}

		public String getUserName() {
			return userName;
		}

		public String getSenderName() {
			return senderName;
		}
	}

	public static class TransactionReply extends Message {
		private final String userName;
		private final String senderName;
		private final UUID requestID;

		public TransactionReply(String userName, UUID requestID, String senderName) {
			this.userName = userName;
			this.requestID = requestID;
			this.senderName = senderName;
		}

		public String getUserName() {
			return userName;
		}

		public UUID getRequestID() {
			return requestID;
		}

		public String getSenderName() {
			return senderName;
		}
	}

	public static class TransactionAccept extends Message {
		private final String userName;
		private final UUID requestID;

		public TransactionAccept(String userName, UUID requestID) {
			this.userName = userName;
			this.requestID = requestID;
		}

		public String getUserName() {
			return userName;
		}

		public UUID getRequestID() {
			return requestID;
		}
	}

	// Creator

	public static Props props(String userName, UserControl userControl) {
		return Props.create(new UserCreator(userName, userControl));
	}

	public static Props props(String userName, UserControl userControl, int initialBalance) {
		return Props.create(new UserCreator(userName, userControl, initialBalance));
	}

	public String getUserName() {
		return userName;
	}

	private Map<String, ActorRef> getAddressBook() {
		return addressBook;
	}

	public int getBalance() {
		return balance;
	}

	private void setBalance(int balance) {
		this.balance = balance;
	}

	public static class UserCreator implements Creator<UserActor> {

		private static final long serialVersionUID = 1L;
		private final String userName;
		private final int balance;
		private final UserControl userControl;


		public UserCreator(String userName, UserControl userControl, int balance) {
			this.userName = userName;
			this.userControl = userControl;
			this.balance = balance;
		}

		public UserCreator(String userName, UserControl userControl) {
			this(userName, userControl, 0);
		}

		@Override
		public UserActor create() throws Exception {
			return new UserActor(userName, userControl, balance);
		}
	}


}
