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
	public static void main(String[] args) {
		Config config = ConfigFactory.defaultApplication();
		ActorSystem system = ActorSystem.create("TestSystem", config);

		MainFrame main = new MainFrame(system);
		main.setVisible(true);

		// UserControl kim = new UserControl(system, "Kim", 100);
		UserControl alex = new UserControl(system, "Alex", 1000);
		UserControl dimi = new UserControl(system, "Dimi", 5);
		UserControl berkay = new UserControl(system, "Berkay", 10);
		try {
			Thread.sleep(1000L);
		} catch (Exception E) {
			System.out.println("Interruption Exception.");
		}
		// kim.addNewNeighbour("Alex", "akka://TestSystem/user/$b");
		// kim.addNewNeighbour("Alex", "akka.tcp://TestSystem@127.0.0.1:2552/user/$b");
		dimi.addNewNeighbour("Alex", "akka://TestSystem/user/$a");
		berkay.addNewNeighbour("Alex", "akka://TestSystem/user/$a");
		// dimi.addNewNeighbour("Alex", "akka.tcp://TestSystem@192.168.43.153:2552/user/$b");
		// kim.addNewNeighbour("Berkay", "akka://TestSystem/user/$d");
		// dimi.addNewNeighbour("Berkay", "akka://TestSystem/user/$d");
		alex.addNewNeighbour("Berkay", "akka://TestSystem/user/$c");
		alex.addNewNeighbour("Dimi", "akka://TestSystem/user/$b");
		// berkay.addNewNeighbour("Dimi", "akka://TestSystem/user/$c");
		// alex.addNewNeighbour("Kim", "akka://TestSystem/user/$a");
		// berkay.addNewNeighbour("Kim", "akka://TestSystem/user/$a");
	}

}
