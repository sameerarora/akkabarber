package com.xebia.akkabarber;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;

import com.xebia.akkabarber.model.Customer;
import com.xebia.akkabarber.model.WakeUp;

public class ShopSimulationClient {

	private Random random = new Random();
	private ActorRef shop;

	@Test
	public void simulateCustomersToShop() throws Exception {
		ActorSystem system = ActorSystem.create("ClientApplication");

		final ActorRef barber = system.actorOf(new Props(Barber.class));
		shop = createShop(system, barber);
		startWakeUpThread(shop);
		sendCustomers();

		new CountDownLatch(1).await(100, TimeUnit.SECONDS);
		system.shutdown();
	}

	private void sendCustomers() throws InterruptedException {
		final CyclicBarrier barrier = new CyclicBarrier(5);
		final AtomicInteger customerId=new AtomicInteger(0);
		for (int index = 0; index < 5; index++) {
			
			new Thread(new Runnable() {
				public void run() {
					try {
						int threadCount=customerId.getAndIncrement();
						barrier.await();
						
						for (int i = 0; i < 20; i++) {
							shop.tell(new Customer((threadCount * i + 1) + ""));
							Thread.sleep(random.nextInt(450));
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
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
