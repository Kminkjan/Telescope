package crawler;

import model.Model;

import java.io.*;
import java.net.Socket;

public class CrawlerCommunicationThread extends Thread {
	private Socket socket;
	
	private PrintWriter pw;
	private String crawlerName = "";
	private static Model model;

	private int capacity = 0;

	public CrawlerCommunicationThread(Socket socket) {
		this.socket = socket;
	}
	
	public static CrawlerCommunicationThread initialize(Socket s, Model m) {
		model = m;
		CrawlerCommunicationThread ct = new CrawlerCommunicationThread(s);

		ct.start();

		System.out.println("CCS: New client connected at socket " + s.getPort());

        return ct;
	}

	public boolean isAvailable() {
		return socket.isConnected();
	}

	public void run() {
		// wacht op berichten van client
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			pw = new PrintWriter(new BufferedOutputStream(
					socket.getOutputStream()), true);

			boolean active = true;

			while (active) {
				String message = br.readLine().trim();
				
				String[] input = message.split(" ");
				
				if (input[0].toLowerCase().equals("checkin")) {
					SendOutput(ExecuteCrawlerString.Checkin(message, this));
				} else if (!crawlerName.isEmpty()) {// we zijn geauthenticeerd
					if (input[0].toLowerCase().equals("searchpoll")) {
						SendOutput(ExecuteCrawlerString.Searchpoll(message, this));
					} else if (input[0].toLowerCase().equals("shutdown")) {
						// Shutdown crawler
						System.out.println("shutting down: " + crawlerName);
						SendOutput("shutdown");
						active = false;
					} else if (input[0].toLowerCase().equals("addthread")) {
						// This crawler has one more thread avalaible\
						updateCapacity(1);
					}
				}
			}
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/* Crawler has been shut down */
		model.removeCrawler(crawlerName);
	}

	public void SendOutput(String output) {
		System.out.println("Printing output: " +output);
		pw.println(output);
		pw.flush();
	}
	
	public void setCrawlerName(String name) {
		crawlerName = name;		
	}
	
	public String getCrawlerName() {
		return crawlerName;
	}
	
	public Model getModel() {
		return model;
	}

	public synchronized int getCapacity() {
		return this.capacity;
	}

	public synchronized void updateCapacity(int amount) {
		if (capacity < 0) {
			System.out.println("ERROR ! UNEXPECTED AMOUNT ! ERROR");
		}
		this.capacity += amount;
		System.out.println("cap: " + capacity);
	}
}
