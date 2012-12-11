package com.xebia.akkabarber.model;

import java.io.Serializable;

public class Customer implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String id;
	
	public Customer(String id){
		this.id=id;
	}

	public String getId() {
		return this.id;
	}

}
