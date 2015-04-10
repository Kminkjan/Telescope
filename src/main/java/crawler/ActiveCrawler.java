package crawler;

import java.util.ArrayList;

public class ActiveCrawler {
	private String crawlername;
	private ArrayList<CrawlerSearch> searches;
	private boolean online = false;
	
	public ActiveCrawler(String crawlername){
		this.crawlername = crawlername;
		searches = new ArrayList<CrawlerSearch>();
		online = true;
	}
	
	public void addCrawlerSearch(int searchid) {
		searches.add(new CrawlerSearch(searchid));
	}
	
	public CrawlerSearch getCrawlerSearch(int searchid) {
		for (CrawlerSearch cs : searches) {
			if (cs.getSearchId() == searchid) {
				return cs;
			}
		}
		return null;
	}
	
	public void addSearchStatistics(int searchid, int pagesfound, int pagesscanned, int resultspushed) {
		if (getCrawlerSearch(searchid) == null) {
			searches.add(new CrawlerSearch(searchid));
		}
		
		getCrawlerSearch(searchid).addSearchStatistics(pagesfound, pagesscanned, resultspushed);
	}
	
	public String getCrawlerName() {
		return crawlername;
	}
	
	public boolean getOnline() {
		return online;
	}
	
	public void setOnline(boolean online) {
		this.online = online;
	}
}