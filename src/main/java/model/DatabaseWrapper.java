package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Class that handles all the communication with the database. All the queries
 * to the database should be fired from here. All the Queries are located in the
 * the {@link Queries} class and can be accessed statically.<br>
 * <br>
 * A good example of a query method would be {@link #putUrl}
 *
 * @author ServerTeam
 */
public class DatabaseWrapper {

	/**
	 * String constants for the database connection
	 */
	private final static String
			HOST = "jdbc:mysql://178.21.117.113:3306/",
			DATABASE = "telescope_db4",
			USERNAME = "rooter",
			PASSWORD = "haeshah3";

	public DatabaseWrapper() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Searches the database for tags.
	 *
	 * @param id The id to search for
	 */
	public ResultData getResult(int id) {
		long startTime = System.nanoTime();
		List<SearchResult2> resultList = new ArrayList<SearchResult2>();
		List<HyperLink> hyperLinkList = new ArrayList<>();

		Connection connection = null;
		Statement statement = null;
		ResultSet resultset = null;
		try {
			connection = getDBConnetion();
			statement = connection.createStatement();
			System.out.println();
			resultset = statement.executeQuery(Queries.GET_RESULT_FROM_ID(id));

			/* Fill the resultList */
			while (resultset.next()) {
				resultList.add(resultFactory(resultset));
			}
			hyperLinkList = getHyperlinks(id);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {if (resultset != null)resultset.close();} catch (Exception ignored) {}
			try {if (statement != null)statement.close();} catch (Exception ignored) {}
			try {if (connection != null)connection.close();} catch (Exception ignored) {}
		}
		System.out.println("Update:\nQuery: "
				+ Queries.GET_RESULT_FROM_ID(id)
				+ "\nTime: "
				+ TimeUnit.MILLISECONDS.convert(
						(System.nanoTime() - startTime), TimeUnit.NANOSECONDS));

		return new ResultData(resultList, hyperLinkList);
	}

	/**
	 * Searches the database for tags.
	 *
	 * @param id the id to search for
	 */
	public List<HyperLink> getHyperlinks(int id) {
		long startTime = System.nanoTime();
		List<HyperLink> resultList = new ArrayList<HyperLink>();

		Connection connection = null;
		Statement statement = null;
		ResultSet resultset = null;
		try {
			connection = getDBConnetion();
			statement = connection.createStatement();
			System.out.println();
			resultset = statement.executeQuery(Queries.GET_HYPERLINKS(id));

			/* Fill the resultList */
			while (resultset.next()) {
				resultList.add(hyperlinkFactory(resultset));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {if (resultset != null)resultset.close();} catch (Exception ignored) {}
			try {if (statement != null)statement.close();} catch (Exception ignored) {}
			try {if (connection != null)connection.close();} catch (Exception ignored) {}
		}
		System.out.println("Update:\nQuery: "
				+ Queries.GET_HYPERLINKS(id)
				+ "\nTime: "
				+ TimeUnit.MILLISECONDS.convert(
						(System.nanoTime() - startTime), TimeUnit.NANOSECONDS));

		return resultList;
	}

	private SearchResult2 resultFactory(ResultSet rs) throws SQLException {
		String domain = rs.getString("domain");
		String tag = rs.getString("tag");
		int rating = rs.getInt("rating");
		return new SearchResult2(domain, tag, rating);
	}

	private HyperLink hyperlinkFactory(ResultSet rs) throws SQLException {
		String domain = rs.getString("domain");
		String hyperlink = rs.getString("hyperlink");
		int amount = rs.getInt("amount");
		return new HyperLink(hyperlink, domain, amount);
	}

	/**
	 * Gets the SQL database connection
	 *
	 * @return The SQL database connection
	 * @throws SQLException
	 */
	private Connection getDBConnetion() throws SQLException {
		return DriverManager.getConnection(HOST + DATABASE,
				USERNAME, PASSWORD);
	}

	/**
     * Creates a search entry in the database, which can be processed by the
     * active crawlers.
     *
     * @param tagList
     *            The tags to search for
     * @return The search's id, this is returned to the client and used to
     *         retrieve data later on
     */
    public int postSearch(List<String> tagList) {
        long startTime = System.nanoTime();

        Connection connection = null;
        Statement statement = null;
        ResultSet resultset = null;
        int searchId = Strings.DATABASE_ERROR_CODE; // Fallback error code
        try {
            connection = getDBConnetion();
            statement = connection.createStatement();
			/* Create a search */
            statement.executeUpdate(Queries.POST_SEARCH);

			/* Retrieve the search's assigned id */
            resultset = statement.executeQuery(Queries.MAX_SEARCH_ID);

            if (resultset.next()) {
                searchId = resultset.getInt(1);
				/* Post the search tags */
                for(String tag : tagList) {
                    resultset = statement.executeQuery(Queries.CHECK_URL_DATA(tag));
                    if (resultset.next() && resultset.getInt(1) == 0)  {
						System.out.println(Queries.INSERT_URL(tag));
						statement.executeUpdate(Queries.INSERT_URL(tag));
						System.out.println(Queries.INSERT_URL_DATA(tag));
						statement.executeUpdate(Queries.INSERT_URL_DATA(tag));
                    }
                }
                System.out.println(Queries.POST_SEARCH_TAGS(tagList, searchId));
                statement.executeUpdate(Queries.POST_SEARCH_TAGS(tagList,
                        searchId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {if (resultset != null)resultset.close();} catch (Exception ignored) {}
            try {if (statement != null)statement.close();} catch (Exception ignored) {}
            try {if (connection != null)connection.close();} catch (Exception ignored) {}
        }
        System.out.println("Update:\nQuery: PostSearch\nTime: "
                + TimeUnit.MILLISECONDS.convert(
                (System.nanoTime() - startTime), TimeUnit.NANOSECONDS));
        return searchId;
    }



    public List<String> getUrlsByTag(String tag) {
        long startTime = System.nanoTime();

        List<String> urlList = new ArrayList<String>();


        Connection connection = null;
        Statement statement = null;
        ResultSet resultset = null;
        try {
            connection = getDBConnetion();
            statement = connection.createStatement();

			/* Retrieve the search's assigned id */
//            for(String tag : tagList) {
                resultset = statement.executeQuery(Queries.GET_TAG_URLS(tag, 3));

                while (resultset.next()) {
                    urlList.add(resultset.getString(1));
                }
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {if (resultset != null)resultset.close();} catch (Exception ignored) {}
            try {if (statement != null)statement.close();} catch (Exception ignored) {}
            try {if (connection != null)connection.close();} catch (Exception ignored) {}
        }
        System.out.println("Update:\nQuery: UrlsByTag\nTime: "
                + TimeUnit.MILLISECONDS.convert(
                (System.nanoTime() - startTime), TimeUnit.NANOSECONDS));
        return urlList;
    }

    public void RefreshDatabase() {
        System.out.println("REFRESHING...");
        long startTime = System.nanoTime();
        List<String> tags = new ArrayList<String>();

        Connection connection = null;
        Statement statement = null;
        ResultSet resultset = null;
        try {
            connection = getDBConnetion();
            statement = connection.createStatement();
            System.out.println();
            resultset = statement.executeQuery(Queries.DISTINCT_TAGS);

			/* Fill the resultList */
            while (resultset.next()) {
                tags.add(resultset.getString(1));
            }

            for(String tag : tags) {
//                System.out.println("refreshing: " + tag);
                statement.executeUpdate(Queries.FILTER_URLS(tag));
                statement.executeUpdate(Queries.REFRESH_TAG(tag));
//                statement.executeUpdate(Queries.EXP_REFRESH(tag));
            }

            System.out.println("done!\nDeleting redundant urls\n");
            statement.executeUpdate(Queries.DELETE_URLS);

            System.out.println("done!\nDeleting searches\n");
            statement.executeUpdate(Queries.TIMOUT_SEARCH());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {if (resultset != null)resultset.close();} catch (Exception ignored) {}
            try {if (statement != null)statement.close();} catch (Exception ignored) {}
            try {if (connection != null)connection.close();} catch (Exception ignored) {}
        }
        System.out.println("REFRESH done:"
                + "\nTime: "
                + TimeUnit.MILLISECONDS.convert(
                (System.nanoTime() - startTime), TimeUnit.NANOSECONDS));
    }

	public List<String> getSuggestion(List<String> tagList) {
		long startTime = System.nanoTime();
		List<String> suggestions = new ArrayList<>();

		Connection connection = null;
		Statement statement = null;
		ResultSet resultset = null;
		try {
			connection = getDBConnetion();
			statement = connection.createStatement();
			System.out.println(Queries.GET_SUGGESTIONS(tagList));
			resultset = statement.executeQuery(Queries.GET_SUGGESTIONS(tagList));

			/* Fill the resultList */
			while (resultset.next()) {
				suggestions.add(resultset.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {if (resultset != null)resultset.close();} catch (Exception ignored) {}
			try {if (statement != null)statement.close();} catch (Exception ignored) {}
			try {if (connection != null)connection.close();} catch (Exception ignored) {}
		}
		System.out.println("REFRESH done:"
				+ "\nTime: "
				+ TimeUnit.MILLISECONDS.convert(
				(System.nanoTime() - startTime), TimeUnit.NANOSECONDS));

		return suggestions;
	}
}
