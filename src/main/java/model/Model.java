package model;

import crawler.ActiveCrawler;
import crawler.CrawlerCommunicationEstablisher;
import crawler.CrawlerCommunicationThread;
import crawler.CrawlerSearch;
import org.codehaus.jackson.JsonNode;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * The model handles all the data inside the server.
 */
public class Model {
    private DatabaseWrapper dbWrapper;
    private Timer scheduledTask;
    @Context
    ServletContext context;

    private ArrayList<ActiveCrawler> activeCrawlers;

    /**
     * Declare port to use for talking with crawler
     */
    public final static int CRAWLER_PORT = 25678;

    /**
     * Creates the model and a DataBaseWrapper.
     */
    public Model() {
        dbWrapper = new DatabaseWrapper();

        /* Refresh the database every 30 minutes */
        scheduledTask = new Timer();
        scheduledTask.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    System.out.println("refresh in 10 seconds");
                    Thread.sleep(10000);
                } catch (InterruptedException ignored) {
                }
                dbWrapper.RefreshDatabase();
            }
        }, 0, TimeUnit.MILLISECONDS.convert(30, TimeUnit.MINUTES));

        CrawlerCommunicationEstablisher.initialize(this);
        activeCrawlers = new ArrayList<ActiveCrawler>();
    }

    public ResultData getResultById(int id) {
        return dbWrapper.getResult(id);
    }

    /**
     * Post search to the database. Converts the given {@link JsonNode} to a List with Strings and hands them over to
     * the {@link DatabaseWrapper}. Then distributes the tags over the available crawlers.
     *
     * @param node The node that contains the tags
     * @return The assigned search id and suggestions, or an error code (see {@link model.Strings}).
     */
    public SearchEntity postSearch(JsonNode node) {
        if (node != null && node.has("tags")) {
            List<String> tagList = new ArrayList<String>();
            for (JsonNode jsonNode : node.get("tags")) {
                tagList.add(jsonNode.asText());
            }
            if (!tagList.isEmpty()) {
                int searchId = dbWrapper.postSearch(tagList);
                List<String> urlList;

                if (CrawlerCommunicationEstablisher.getThreads().isEmpty()) {
                    return new SearchEntity(searchId, null);
                }

                // Distribute searchtags over the crawlers
                for (String tag : tagList) {
                    System.out.println(tag + ":\n");
                    urlList = dbWrapper.getUrlsByTag(tag);

                    for (CrawlerCommunicationThread cct : CrawlerCommunicationEstablisher.getThreads()) {
                        System.out.printf("checking %s\n", cct.getCrawlerName());
                        while (cct.getCapacity() > 0 && !urlList.isEmpty()) {
                            String url = urlList.remove(0);
                            cct.updateCapacity(-1);
                            cct.SendOutput("activecrawl " + searchId + " " + url + " " + tag);
                        }

                        // All urls are processed
                        if (urlList.isEmpty()) {
                            System.out.println("All urls processed");
                            break;
                        }
                    }

                    for (String url : urlList) {
                        System.out.println("FALLBACK");
                        // Shouldn't come here but if there are NO crawlers available just order the first crawler
                        CrawlerCommunicationThread cct = CrawlerCommunicationEstablisher.getThreads().get(0);
                        cct.SendOutput("activecrawl " + searchId + " " + url + " " + tag);
                    }
                }

                return new SearchEntity(searchId, dbWrapper.getSuggestion(tagList));
            } else {
                return new SearchEntity(-2, null); // No tags given
            }
        }
        return new SearchEntity(-1, null); // Bad JSON
    }

    public void addActiveCrawler(ActiveCrawler ac) {
        System.out.println("Crawler checked in! " + ac.getCrawlerName());
        activeCrawlers.add(ac);
    }

    public ActiveCrawler getActiveCrawler(String crawlername) {
        System.out.println(activeCrawlers.size() + " aantal crawlers");
        for (ActiveCrawler ac : activeCrawlers) {
            if (ac.getCrawlerName().equals(crawlername)) {
                return ac;
            }
        }
        return null;
    }

    public CrawlerSearch getCrawlerSearchResults(String crawlername,
                                                 int searchid) {
        return getActiveCrawler(crawlername).getCrawlerSearch(searchid);
    }

    /**
     * Called when the server shuts down.
     */
    public void destroy() {
        System.out.println("Server closing...");
        scheduledTask.cancel();
    }

    public void removeCrawler(String name) {
        Iterator<CrawlerCommunicationThread> i = CrawlerCommunicationEstablisher.getThreads().iterator();
        while (i.hasNext()) {
            if (i.next().getCrawlerName().equals(name)) {
                i.remove();
                System.out.println("Crawler " + name + " removed");
            }
        }
    }
}
