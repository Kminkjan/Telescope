package model;

import java.util.List;

/**
 * The result of a search by the webclient, contains the data that is actively crawled
 *
 * Created by Kris on 2-4-2015.
 */
public class ResultData {
    private List<SearchResult2> results;
    private List<HyperLink> hyperLinks;

    public ResultData(List<SearchResult2> results, List<HyperLink> hyperLinks) {
        this.results = results;
        this.hyperLinks = hyperLinks;
    }

    public ResultData() {
    }

    public List<SearchResult2> getResults() {
        return results;
    }

    public void setResults(List<SearchResult2> results) {
        this.results = results;
    }

    public List<HyperLink> getHyperLinks() {
        return hyperLinks;
    }

    public void setHyperLinks(List<HyperLink> hyperLinks) {
        this.hyperLinks = hyperLinks;
    }

    public boolean isEmpty() {
        return hyperLinks.isEmpty() && results.isEmpty();
    }
}
