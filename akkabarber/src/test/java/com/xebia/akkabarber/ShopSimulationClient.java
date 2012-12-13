package com.xebia.akkabarber;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;

import com.typesafe.config.ConfigFactory;
import com.xebia.akkabarber.model.Customer;
import com.xebia.akkabarber.model.WakeUp;

public class ShopSimulationClient {

	private Random random = new Random();

	@Test
	public void simulateCustomersToShop() throws Exception {
		ActorSystem system = ActorSystem.create("ClientApplication");

		final ActorRef barber = system.actorOf(new Props(Barber.class));
		final ActorRef shop = createShop(system, barber);
		startWakeUpThread(shop);
		for (int i = 0; i < 20; i++) {
			shop.tell(new Customer((i + 1) + ""));
			Thread.sleep(random.nextInt(450));
		}

		new CountDownLatch(1).await(10, TimeUnit.SECONDS);
		system.shutdown();
	}

	private ActorRef createShop(ActorSystem system, final ActorRef barber) {
		final ActorRef shop = system.actorOf(new Props(
				new UntypedActorFactory() {
					private static final long serialVersionUID = 1L;

					public UntypedActor create() {
						return new Shop(barber);
					}
				}), "shop");
		return shop;
	}

	private void startWakeUpThread(final ActorRef shop) {
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
				new Runnable() {
					public void run() {
						shop.tell(new WakeUp());
					}
				}, 100, 1000, TimeUnit.MILLISECONDS);
	}

}
