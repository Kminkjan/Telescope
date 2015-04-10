package crawler;

import model.Model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class CrawlerCommunicationEstablisher extends Thread {
	private static Model model;

	static ServerSocket serverSocket;
    private static List<CrawlerCommunicationThread> threads = new ArrayList<CrawlerCommunicationThread>();

	
	public CrawlerCommunicationEstablisher() {
	}
	
	public static void initialize(Model m) {
		try {
			model = m;
			System.out.println("CCS: Booting...");
			
			serverSocket = new ServerSocket(Model.CRAWLER_PORT);
			CrawlerCommunicationEstablisher cce = new CrawlerCommunicationEstablisher();
			
			cce.start();
			System.out.println("CCS: Now running.");
			
		} catch (IOException e) {
			System.out.println("CSS: Failed to boot!!");
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			
			while (true) {
				Socket socket = serverSocket.accept();
                threads.add(CrawlerCommunicationThread.initialize(socket, model));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public static List<CrawlerCommunicationThread> getThreads() {
        return threads;
    }
}
