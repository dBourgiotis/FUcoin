package main;

import akka.actor.ActorSystem;
import user.UserControl;

public class Main {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		ActorSystem system = ActorSystem.create("TestSystem");
		UserControl kim = new UserControl(system, "Kim", 100);
		UserControl alex = new UserControl(system, "Alex", 1000);
		UserControl dimi = new UserControl(system, "Dimi", 400);
		UserControl berkay = new UserControl(system, "Berkay", 299);

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
