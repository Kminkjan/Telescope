package model;

import java.util.List;

/**
 * Contains data about the search, like the id and suggestions
 * Created by Kris on 6-4-2015.
 */
public class SearchEntity {
    private final int searchid;
    private final List<String> suggestions;

    public SearchEntity(int searchid, List<String> suggestions) {
        this.searchid = searchid;
        this.suggestions = suggestions;
    }

    public int getSearchid() {
        return searchid;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    @Override
    public String toString() {
        return "SearchEntity{" +
                "searchid=" + searchid +
                ", suggestions=" + suggestions +
                '}';
    }
}
