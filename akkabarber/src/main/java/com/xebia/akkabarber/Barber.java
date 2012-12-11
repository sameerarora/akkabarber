package com.xebia.akkabarber;

import java.util.Random;

import com.xebia.akkabarber.model.Customer;

import akka.actor.UntypedActor;

/**
 * Actor Class for Barber.
 */
public class Barber extends UntypedActor {

	Random random = new Random();

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof Customer) {
			Customer customer=(Customer)message;
			if (performHaircut()) {
				System.out.println("Customer " + customer.getId() + " Got a Haircut");
			}
		}
	}

	private boolean performHaircut() throws InterruptedException {
		Thread.sleep(random.nextInt(450));
		return true;
	}

}
