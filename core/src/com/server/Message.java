package com.server;

public class Message {
	public String message;
	
	public Message () {
		
	}
	
	public Message(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return message;
	}
	
	
}
