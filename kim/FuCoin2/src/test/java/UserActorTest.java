import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import akka.actor.ActorSystem;
import user.UserControl;

public class UserActorTest {

	private static final int THREAD_WAIT = 80;
	private static UserControl kim;
	private static UserControl alex;
	private static UserControl dimi;
	private static UserControl berkay;
	private static ActorSystem system;

	@BeforeClass
	public static void setUpAll() {
		system = ActorSystem.create("TestSystem");
	}

	@BeforeClass
	public static void setUp() throws InterruptedException {
		kim = new UserControl(system, "Kim", 100);
		alex = new UserControl(system, "Alex", 1000);
		dimi = new UserControl(system, "Dimi", 400);
		berkay = new UserControl(system, "Berkay", 299);

		Thread.sleep(1000L);
		kim.addNewNeighbour("Alex", "akka://TestSystem/user/$b");
		dimi.addNewNeighbour("Alex", "akka://TestSystem/user/$b");
		kim.addNewNeighbour("Berkay", "akka://TestSystem/user/$d");
		dimi.addNewNeighbour("Berkay", "akka://TestSystem/user/$d");
		alex.addNewNeighbour("Dimi", "akka://TestSystem/user/$c");
		berkay.addNewNeighbour("Dimi", "akka://TestSystem/user/$c");
		alex.addNewNeighbour("Kim", "akka://TestSystem/user/$a");
		berkay.addNewNeighbour("Kim", "akka://TestSystem/user/$a");
	}

	@AfterClass
	public static void tearDown() {
		kim = null;
		berkay = null;
		dimi = null;
		alex = null;
	}

	@Test
	public void testPositiveTransaction() throws InterruptedException {
		int preAmountKim = kim.getBalance();
		int preAmountBerkay = berkay.getBalance();
		kim.makeTransaction("Berkay", 20);
		Thread.sleep(THREAD_WAIT);
		int afterAmountKim = kim.getBalance();
		int afterAmountBerkay = berkay.getBalance();
		assertEquals(20, preAmountKim - afterAmountKim);
		assertEquals(-20, preAmountBerkay - afterAmountBerkay);
	}

	@Test
	public void testNegativeTransaction() throws InterruptedException {
		int preAmountKim = kim.getBalance();
		int preAmountBerkay = berkay.getBalance();
		kim.makeTransaction("Berkay", -20);
		Thread.sleep(THREAD_WAIT);
		int afterAmountKim = kim.getBalance();
		int afterAmountBerkay = berkay.getBalance();
		assertEquals(-20, preAmountKim - afterAmountKim);
		assertEquals(20, preAmountBerkay - afterAmountBerkay);
	}

	@Test
	public void testNegativeTransactionLimit() throws InterruptedException {
		int preAmountKim = kim.getBalance();
		int preAmountBerkay = berkay.getBalance();
		kim.makeTransaction("Berkay", -100000);
		Thread.sleep(THREAD_WAIT);
		int afterAmountKim = kim.getBalance();
		int afterAmountBerkay = berkay.getBalance();
		assertEquals(0, preAmountKim - afterAmountKim);
		assertEquals(0, preAmountBerkay - afterAmountBerkay);
	}

	@Test
	public void testPositiveTransactionLimit() throws InterruptedException {
		int preAmountKim = kim.getBalance();
		int preAmountBerkay = berkay.getBalance();
		kim.makeTransaction("Berkay", 100000);
		Thread.sleep(THREAD_WAIT);
		int afterAmountKim = kim.getBalance();
		int afterAmountBerkay = berkay.getBalance();
		assertEquals(0, preAmountKim - afterAmountKim);
		assertEquals(0, preAmountBerkay - afterAmountBerkay);
	}
}
