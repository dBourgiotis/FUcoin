package user;

import java.awt.EventQueue;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import scala.concurrent.duration.FiniteDuration;
import user.UserActor.JoinReply;
import user.UserActor.TransactionInit;

public class UserControl {
	private final ActorRef userActor;
	private final Inbox inbox;
	private UserFrame userFrame;

	private Collection<String> neighbours;
	private int balance;
	private final String userName;

	private final FiniteDuration DURATION = new FiniteDuration(10000L, TimeUnit.MILLISECONDS);
	private ActorSystem system;

	public UserControl(ActorSystem system, String userName, int initBalance) {
		this.userName = userName;
		userActor = system.actorOf(UserActor.props(userName, this, initBalance));
		neighbours = new HashSet<>();
		setBalance(initBalance);
		inbox = Inbox.create(system);
		this.system = system;
		createUserFrame();
	}


	private void createUserFrame() {
		UserStaticContext context = new UserStaticContext(this, userName, getBalance(), userActor);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UserFrame frame = new UserFrame(context);
					frame.setVisible(true);
					context.getUserControl().userFrame = frame;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	public void makeTransaction(String userName, int amount) {
		TransactionInit request = new TransactionInit(userName, amount, this.userName);
		inbox.send(userActor, request);
	}

	public void addNewNeighbour(String userName, String address) {
		ActorRef actor = system.actorFor(address);
		Inbox inbox = Inbox.create(system);
		JoinReply reply = new JoinReply(userName, actor);

		inbox.send(userActor, reply);
	}

	public void updateBalance(int balance) {
		userFrame.updateBalance(balance);
		setBalance(balance);
	}

	public void updateNeighbours(Collection<String> neighbours) {
		userFrame.updateNeighbours(neighbours);
	}
	public int getBalance() {
		return balance;
	}


	public void setBalance(int balance) {
		this.balance = balance;
	}
	public class UserStaticContext {
		private final UserControl userControl;
		private final String userName;
		private final int balance;
		private final ActorRef address;
		
		public UserStaticContext(UserControl userControl, String userName, int balance, ActorRef address) {
			this.userControl = userControl;
			this.userName = userName;
			this.balance = balance;
			this.address = address;
			
		}

		public UserControl getUserControl() {
			return userControl;
		}

		public String getUserName() {
			return userName;
		}

		public int getBalance() {
			return balance;
		}

		public ActorRef getAddress() {
			return address;
		}
	}
}
