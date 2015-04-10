package model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Contains information about a SearchResult that can be send to the webpages
 */
@XmlRootElement
public class SearchResult2 {
    private String domain, tag;
    private int rating;


    public SearchResult2(String domain, String tag, int rating) {
        this.domain = domain;
        this.tag = tag;
        this.rating = rating;
    }

    public String getDomain() {
        return domain;
    }

    public String getTag() {
        return tag;
    }

    public int getRating() {
        return rating;
    }
}
