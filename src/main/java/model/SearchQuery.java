package model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
/**
 * @deprecated
 */
public class SearchQuery {
    private String query;

    public SearchQuery() {
    }

    public SearchQuery(String query) {
        this.query = query;
    }

    @XmlElement
    public String getSearchQuery() {
        return query;
    }

    public void setSearchQuery(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "SearchQuery = " + query;
    }
}
