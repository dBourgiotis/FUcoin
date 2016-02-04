package main;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import user.UserControl;

public class Main {

	/**
	 * Launch the application.
	 * 
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		Config config = ConfigFactory.defaultApplication();
		ActorSystem system = ActorSystem.create("TestSystem", config);

		MainFrame main = new MainFrame(system);
		main.setVisible(true);

		UserControl kim = new UserControl(system, "Kim", 100);
		UserControl alex = new UserControl(system, "Alex", 1000);
		UserControl dimi = new UserControl(system, "Dimi", 400);
		UserControl berkay = new UserControl(system, "Berkay", 299);

		Thread.sleep(1000L);
		// kim.addNewNeighbour("Alex","akka://TestSystem/user/$b");
		kim.addNewNeighbour("Alex", "akka.tcp://TestSystem@127.0.0.1:2552/user/$b"); //
		// dimi.addNewNeighbour("Alex", "akka://TestSystem/user/$b");
		dimi.addNewNeighbour("Alex", "akka.tcp://TestSystem@192.168.43.153:2552/user/$b");
		kim.addNewNeighbour("Berkay", "akka://TestSystem/user/$d");
		dimi.addNewNeighbour("Berkay", "akka://TestSystem/user/$d");
		alex.addNewNeighbour("Dimi", "akka://TestSystem/user/$c");
		berkay.addNewNeighbour("Dimi", "akka://TestSystem/user/$c");
		alex.addNewNeighbour("Kim", "akka://TestSystem/user/$a");
		berkay.addNewNeighbour("Kim", "akka://TestSystem/user/$a");
	}

}
