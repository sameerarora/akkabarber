package com.xebia.akkabarber;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.kernel.Bootable;

public class BarberShopServer implements Bootable {

	private ActorSystem system;
	private ActorRef shop;
	private ActorRef barber;

	public void shutdown() {
		System.out.println("Shutting Down shop for the day");
	}

	@SuppressWarnings("serial")
	public void startup() {
		System.out.println("Opening up Shop for the Day");
		system = ActorSystem.create("BarberShopApp", ConfigFactory.load()
				.getConfig("BarberShopApp"));
		barber = system.actorOf(new Props(Barber.class));
		System.out.println("Barber Walks in ....");
		shop=system.actorOf(new Props(new UntypedActorFactory() {

			public UntypedActor create() {
				return new Shop(barber);
			}
		}), "shop");
		System.out.println("Shop is open...");
	}


}
