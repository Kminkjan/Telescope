package model;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * All SQL queries in one place
 * 
 * @author Administrator
 *
 */
public class Queries {
	
	/**
	 * Static Queries
	 */
	public final static String 
		POST_SEARCH 	= "INSERT INTO search (`timestamp`) VALUES (UNIX_TIMESTAMP(NOW()));",
		MAX_SEARCH_ID 	= "SELECT MAX(`searchid`) FROM search;",
        DISTINCT_TAGS   = "SELECT tag, COUNT(*) AS amount\n" +
				"FROM url_data  \n" +
				"GROUP BY tag  \n" +
				"HAVING COUNT(*)>3;",//"SELECT DISTINCT `tag` from url_data",
        DELETE_URLS     = "DELETE FROM url WHERE `url` NOT IN(SELECT `url` FROM url_data);";

	/**
	 * Voorbeeld van een static query getter
	 * 
	 * @param string
	 *            String om in query te verwerken
	 * @param getal
	 *            Getal om in query te verwerken
	 * @return A SQL query
	 */
	public static String QUERY_DOE_IETS(String string, int getal) {
		return String.format("SELECT \"%s\" FROM tabel WHERE getal = %s;",
				string, getal);
	}

	/**
	 * Get URLS from given tags
	 * 
	 * @param tags
	 *            All the tags to get URLS from
	 * @return A SQL query
	 */
	public static String GET_URLS_FROM_TAG(String... tags) {
		String query = "SELECT * FROM url_data WHERE ";
		for (String tag : tags)
			query += tag.toLowerCase() + " IN tag OR ";
		return query.substring(0, query.length() - 4) + ";";
	}

	/**
	 * Insert URL into table
	 * 
	 * @param url
	 *            The URL to put
	 * @return A SQL query
	 * @deprecated Crawlers do this now
	 */
	public static String PUT_URL(String url) {
		return String
				.format("INSERT INTO url (`url`, `timestamp`) VALUES (\"%s\", CURDATE());",
						url);
	}

	/**
	 * Insert URL data in the database <b>Please use PUT_URL first!!!</b>
	 * 
	 * @param url
	 *            The URL to put
	 * @param tag
	 *            The tag of the site to put
	 * @param rating
	 *            The given rating of the site, compared to the tag
	 * @return A SQL query
	 * @deprecated Crawlers do this now
	 */
	public static String PUT_URL_DATA(String url, String tag, int rating) {
		return String
				.format("INSERT INTO url_data (`url`, `tag`, `rating`) VALUES (\"%s\", \"%s\", %s);",
						url, tag, rating);
	}

	public static String GET_RESULT_FROM_ID(int id) {
		return String.format("SELECT domain, tag, COUNT(completeurl) as rating FROM `search_result` WHERE searchid = %d GROUP BY domain, tag HAVING rating > 2;", id);
	}
	
	/**
	 * Check if the tag exists in our URL_Data table. If the result of this query is 0, please execute INSERT_URL and INSERT_URL_DATA
	 * @param tag The tag to check for
	 * @return A SQL query
	 */
	public static String CHECK_URL_DATA(String tag) {
		return String.format(
				"SELECT COUNT(*) FROM url_data WHERE tag=\"%s\";", tag);
	}
	
	/**
	 * Only needed to execute when tag does not exist yet <b>Check this by using CHECK_URL_DATA</b>
	 * @param tag The tag needed to add
	 * @return A SQL query
	 */
	public static String INSERT_URL(String tag) {
		return String.format(
				"INSERT INTO url (url, timestamp) VALUES (\"https://www.google.com/search?q=%s&num=3\", %s);", tag, getCurrentTimestamp());
	}
	
	/**
	 * Only needed to execute when tag does not exist yet. <b>Please use INSERT_URL first!</b>
	 * @param tag The tag needed to add
	 * @return A SQL query
	 */
	public static String INSERT_URL_DATA(String tag) {
		return String.format(
				"INSERT INTO url_data (url, tag, rating) VALUES (\"https://www.google.com/search?q=%s&num=3\", \"%s\", 1);", tag, tag);
	}

	public static String POST_SEARCH_TAGS(List<String> tags, int searchId) {
		String query = String
				.format("INSERT INTO tag (`tag`, `searchid`) VALUES ");
		for (String tag : tags) {
			query += String.format("(\"%s\", %s)", tag, searchId) + ",";
		}
		return query.substring(0, query.length() - 1) + ";";
	}
	
	
	public static int getCurrentTimestamp() {
		return (int) (System.currentTimeMillis() / 1000L);
	}

	public static String GET_HYPERLINKS(int id) {
		return String.format("SELECT * FROM hyperlink WHERE searchid = %d;", id);
	}

    public final static String GET_TAG_URLS(String tag, int topAmount) {
        return String.format("SELECT url, domain FROM `url_data` WHERE tag = \"%s\" ORDER BY rating LIMIT %s;",
                tag, topAmount);
    }

    public static String REFRESH_TAG(String tag) {
        return String.format("DELETE FROM url_data WHERE tag = \"%s\" AND rating < (SELECT MIN(rating) FROM (SELECT * " +
                "from url_data WHERE tag = \"%s\" ORDER BY rating DESC LIMIT 3) as u);", tag, tag);
    }

    public static String FILTER_URLS(String tag) {
        return String.format("DELETE FROM url_data WHERE tag = \"%s\" AND url NOT IN (SELECT url FROM (SELECT * FROM url_data WHERE tag = \"%s\" ORDER BY rating DESC) AS t1 GROUP BY domain);", tag, tag);
    }

	/**
	 * @deprecated Is experimental, use on own risk
	 * @param tag	The tat
	 * @return
	 */
    public static String EXP_REFRESH(String tag) {
        return String.format("DELETE FROM url_data WHERE tag = \"%s\" AND url NOT IN (SELECT url FROM (SELECT * FROM url_data WHERE tag = \"%s\" ORDER BY rating DESC) AS t1 GROUP BY domain) AND rating < (SELECT MIN(rating) FROM (SELECT * " +
                "from url_data WHERE tag = \"%s\" ORDER BY rating DESC LIMIT 3) as u);", tag, tag, tag);
    }

    public static String TIMOUT_SEARCH() {
        return String.format("DELETE FROM search WHERE %s - `timestamp` > %s;", getCurrentTimestamp(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
    }

	public static String GET_SUGGESTIONS(List<String> tags) {

		String query = "SELECT tag, rating FROM url_data WHERE domain IN (SELECT domain FROM url_data";

		for (String tag : tags) {
			query += String.format(" WHERE tag = \"%s\" OR", tag);
		}

		return query.substring(0, query.length() - 3) + ") ORDER BY rating DESC LIMIT 5;";
	}
}
