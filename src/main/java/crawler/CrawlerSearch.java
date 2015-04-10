package crawler;

public class CrawlerSearch {
	private int searchid, pagesfound, pagesscanned, resultspushed;
	
	public CrawlerSearch(int searchid) {
		this.searchid = searchid;
		this.pagesfound = 0;
		this.pagesscanned = 0;
		this.resultspushed = 0;
	}
	
	public void addSearchStatistics(int pagesfound, int pagesscanned, int resultspushed) {
		this.pagesfound += pagesfound;
		this.pagesscanned += pagesscanned;
		this.resultspushed += resultspushed;
	}
	
	public int getSearchId() {
		return searchid;
	}
	
	public int getPagesFound() {
		return pagesfound;
	}
	
	public int getPagesScanned() {
		return pagesscanned;
	}
	
	public int getResultsPushed() {
		return resultspushed;
	}
}
