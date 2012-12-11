package com.xebia.akkabarber;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.xebia.akkabarber.model.Customer;
import com.xebia.akkabarber.model.WakeUp;

public class Shop extends UntypedActor {

	Queue<Customer> customers = new LinkedBlockingQueue<Customer>();

	ActorRef barber;

	public Shop(ActorRef barber) {
		this.barber = barber;
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof Customer) {
			processCustomer(message);
		} else if (message instanceof WakeUp) {
			barber.tell(customers.poll());
		}
	}

	private void processCustomer(Object message) {
		if (customers.size() == 3) {
			Customer customer = (Customer) message;
			System.out.println("Customer " + customer.getId() + " is leaving");
			return;
		}
		customers.add((Customer) message);
	}

}
