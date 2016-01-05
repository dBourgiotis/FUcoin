import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import user.Message;
import user.UserActor;
import user.UserControl;

public class UserActorTest {

	private ActorRef kim;
	private static ActorSystem system;
	private Inbox inbox;

	@BeforeClass
	public static void setUpAll() {
		system = ActorSystem.create("UserActorTestSystem");
	}

	@Before
	public void setUp() {
		UserControl userControl = new UserControl(system, "kim", 100);
		kim = system.actorOf(UserActor.props("Kim", userControl, 100));
		inbox = Inbox.create(system);
	}

	@After
	public void tearDown() {
		kim = null;
	}

	@Test
	public void test() throws InterruptedException {
		Message joinReply = new UserActor.JoinReply("Test", kim);
		Message joinRequest = new UserActor.JoinRequest("Test");
		inbox.send(kim, joinReply);
		Thread.sleep(500);
		inbox.send(kim, joinRequest);
		Thread.sleep(500);
	}

}
