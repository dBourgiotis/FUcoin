package main;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.ConfigFactory;
import user.UserControl;
import akka.actor.Address;
import akka.actor.AddressFromURIString;
import akka.actor.Deploy;
import akka.remote.RemoteScope;

public class Main {

	/**
	 * Launch the application.
	 * 
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		ActorSystem system = ActorSystem.create("TestSystem");
		//remote actor init
		ConfigFactory.parseString("akka.remote.netty.hostname=\"1.2.3.4\"").withFallback(ConfigFactory.load());
		ActorRef actor = system.actorOf(new Props(SampleActor.class), "sampleActor");//SampleActor?
		actor.tell("Remote actors", null);

		UserControl kim = new UserControl(system, "Kim", 100);
		UserControl alex = new UserControl(system, "Alex", 1000);
		UserControl dimi = new UserControl(system, "Dimi", 400);
		UserControl berkay = new UserControl(system, "Berkay", 299);

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

}
