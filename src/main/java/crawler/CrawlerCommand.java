package crawler;

import model.Model;

public class CrawlerCommand {
	private static Model model;
	
	public static String Checkin(String crawlername, Model m) {
		model = m;

		if (crawlername.equals("")) {
			return JSONMaker
					.generateErrorMessage(
							"missing_parameters",
							"No name specified. Syntax: checkin <name>. Example: checkin rutger desktop");
		}

		// If crawler exists, throw error
		if (model.getActiveCrawler(crawlername) != null )
			return JSONMaker.generateErrorMessage("unknown_user",
					"Name is already checked-in.");
		
		// als die wel bestaat maar niet online is
		// TODO Crawlers moeten nog offline gaan indien we er een poosje niks van horen
		try {
			if (!model.getActiveCrawler(crawlername).getOnline()) {
				model.getActiveCrawler(crawlername).setOnline(true);
			} 
		} catch (NullPointerException e){
			
		}
		
		model.addActiveCrawler(new ActiveCrawler(crawlername));
		
		// create json output
		JSONMaker j = new JSONMaker();
		j.addObject("name", crawlername);
		j.addObject("timestamp", getCurrentTimestamp());
		j.trimComma();
		return j.getJSON();
	}
	
	public static String Searchpoll(String searchid, String pagesfound, String pagesscanned, String resultspushed, String crawlername) {	
		// Check if searchid is not null
		if (searchid.equals("")) {
			return JSONMaker
					.generateErrorMessage(
							"missing_parameters",
							"No searchid specified. Syntax: searchpoll <searchid> <pagesfound> <pagesscanned> <resultspushed>. Example: searchpoll 1 23 7 7");
		}
		
		// Check if pagesfound is not null
		if (pagesfound.equals("")) {
			return JSONMaker
					.generateErrorMessage(
							"missing_parameters",
							"No pagesfound specified. Syntax: searchpoll <searchid> <pagesfound> <pagesscanned> <resultspushed>. Example: searchpoll 1 23 7 7");
		}
		
		// Check if pagesscanned is not null
		if (pagesscanned.equals("")) {
			return JSONMaker
					.generateErrorMessage(
							"missing_parameters",
							"No pagesscanned specified. Syntax: searchpoll <searchid> <pagesfound> <pagesscanned> <resultspushed>. Example: searchpoll 1 23 7 7");
		}
		
		// Check if resultspushed is not null
		if (resultspushed.equals("")) {
			return JSONMaker
					.generateErrorMessage(
							"missing_parameters",
							"No resultspushed specified. Syntax: searchpoll <searchid> <pagesfound> <pagesscanned> <resultspushed>. Example: searchpoll 1 23 7 7");
		}
		
		int iSearchid;
		if (searchid.matches("\\d+")) {
			iSearchid = Integer.parseInt(searchid);
		} else
			return JSONMaker.generateErrorMessage("unprocessable_request",
					"No valid searchid given.");

		int iPagesfound;
		if (pagesfound.matches("\\d+")) {
			iPagesfound = Integer.parseInt(pagesfound);
		} else
			return JSONMaker.generateErrorMessage("unprocessable_request",
					"No valid pagesfound given.");
		
		int iPagesscanned;
		if (pagesscanned.matches("\\d+")) {
			iPagesscanned = Integer.parseInt(pagesscanned);
		} else
			return JSONMaker.generateErrorMessage("unprocessable_request",
					"No valid pagesscanned given.");

		int iResultsPushed;
		if (resultspushed.matches("\\d+")) {
			iResultsPushed = Integer.parseInt(resultspushed);
		} else
			return JSONMaker.generateErrorMessage("unprocessable_request",
					"No valid resultspushed given.");

		// Add data to model
		model.getActiveCrawler(crawlername).addSearchStatistics(iSearchid, iPagesfound, iPagesscanned, iResultsPushed);
		
		return "Added data huehuehue " + iSearchid + " - " + iPagesfound + ", " + iPagesscanned + ", " + iResultsPushed + " \r\n Current: PF " + model.getActiveCrawler(crawlername).getCrawlerSearch(1).getPagesFound() + " - PS " + model.getActiveCrawler(crawlername).getCrawlerSearch(1).getPagesScanned() + " - RP " + model.getActiveCrawler(crawlername).getCrawlerSearch(1).getResultsPushed();
	}
	
	public static int getCurrentTimestamp() {
		return (int) (System.currentTimeMillis() / 1000L);
	}
}
