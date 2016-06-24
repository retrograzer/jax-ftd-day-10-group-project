package com.cooksys.ftd.chat.server;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Messages {
	
	Logger log = LoggerFactory.getLogger(Server.class);
	
	private Deque<String> messes = new ArrayDeque<String>();
	private static Map<ClientHandler, Thread> handlerThreads = new ConcurrentHashMap<>();
	
	public Messages(Map<ClientHandler, Thread> handlerThreads) {
		
	}
	
	public Deque<String> getMesses() {
		return messes;
	}
	
	public void setMesses(String echo) {
		messes.addFirst(echo);
	}
	
	public void insertMessage (String message) {
		messes.addFirst(message);
	}
	
	public void broadcast() {
		String message = messes.removeFirst();
		for(ClientHandler client : Server.getHandlerThreads().keySet()) {
			client.sendMessage(message);
			log.info("Broadcast message: {}", message);
		}
		log.info("Broadcast method");
	}
	
	
}
