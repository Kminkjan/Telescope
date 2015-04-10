package crawler;

public class ExecuteCrawlerString {
	public static String Checkin(String message, CrawlerCommunicationThread ct) {
		String[] input = message.split(" ");
		String login = "";
		
		boolean kanInloggen = true;
		if (input.length == 1) { // no argument, force error
			kanInloggen = false;
			login = CrawlerCommand.Checkin("", ct.getModel());
		} else if (input.length == 2) {
			kanInloggen = true;
			login = CrawlerCommand.Checkin(input[1], ct.getModel());
		}

		if (kanInloggen) {
			ct.setCrawlerName(input[1]);
		}

		return login;
	}
	
	public static String Searchpoll(String message, CrawlerCommunicationThread ct) {
		String[] input = message.split(" ");
		String output = "";
		if (input.length == 1) { // missing 4 arguments, force error
			output = CrawlerCommand.Searchpoll("", "", "", "", ct.getCrawlerName()); //.bidAuction("", "", u.getUsertoken());
		} else if (input.length == 2) { // same, force error
			output = CrawlerCommand.Searchpoll(input[1], "", "", "", ct.getCrawlerName());
		} else if (input.length == 3) { // same, force error
			output = CrawlerCommand.Searchpoll(input[1], input[2], "", "", ct.getCrawlerName());
		} else if (input.length == 4) { // same, force error
			output = CrawlerCommand.Searchpoll(input[1], input[2], input[3], "", ct.getCrawlerName());
		} else if (input.length == 5) { // all good
			output = CrawlerCommand.Searchpoll(input[1], input[2], input[3], input[4], ct.getCrawlerName());
		}
		return output;
	}
}
