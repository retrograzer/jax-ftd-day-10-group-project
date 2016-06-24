package com.cooksys.ftd.chat.server;
//good place to keep a queue of messages?
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server implements Runnable {

	Logger log = LoggerFactory.getLogger(Server.class);

	int port;
	private static Map<ClientHandler, Thread> handlerThreads;
	
	public static Map<ClientHandler, Thread> getHandlerThreads() {
		return handlerThreads;
	}

	public Server(int port) {
		super();
		this.port = port;
		this.handlerThreads = new ConcurrentHashMap<>();
	}
	
	@Override
	public void run() {
		log.info("Server started on port {}", this.port);
		try (ServerSocket server = new ServerSocket(this.port)) {
			while (true) {
				//get socket, waits till this passes to do client crap
				Socket client = server.accept();
				log.info("Client connected {}", client.getRemoteSocketAddress());
				ClientHandler clientHandler = new ClientHandler(client, handlerThreads);
				//put client in new thread
				Thread clientHandlerThread = new Thread(clientHandler);
				this.handlerThreads.put(clientHandler, clientHandlerThread);
				log.info("These are the threads: " + handlerThreads.toString());
				//handlerThreads.
				log.info("clientHandler: {} " + clientHandler.toString());
				clientHandlerThread.start();
			}
		} catch (IOException e) {
			log.error("Server fail! oh noes :(", e);
		} finally {
			for (ClientHandler clientHandler : this.handlerThreads.keySet()) {
				try {
					clientHandler.close();
					this.handlerThreads.get(clientHandler).join();
					this.handlerThreads.remove(clientHandler);
				} catch (IOException | InterruptedException e) {
					log.warn("Failed to close handler :/", e);
				}
			}
		}
	}

}
