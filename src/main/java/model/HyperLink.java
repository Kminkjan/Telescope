package model;

public class HyperLink {
	private String hyperlink, completeurl;
	private int amount;
	public HyperLink(String hyperlink, String completeurl, int amount){
		this.setCompleteurl(completeurl);
		this.setHyperlink(hyperlink);
		this.amount = amount;
	}
	public String getCompleteurl() {
		return completeurl;
	}
	public void setCompleteurl(String completeurl) {
		this.completeurl = completeurl;
	}
	public String getHyperlink() {
		return hyperlink;
	}
	public void setHyperlink(String hyperlink) {
		this.hyperlink = hyperlink;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
}
