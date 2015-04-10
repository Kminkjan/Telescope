package resources;

import model.Model;
import model.ResultData;
import model.SearchEntity;
import model.Strings;
import org.codehaus.jackson.JsonNode;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;

@Path("/backend")
public class BackendServlet {
    @Context
    ServletContext context;

    private Model model;

    public BackendServlet() {
    }

    /**
     * Post a new search, with a list of tags to search for.
     *
     * @param node A JsonNode containing the tags
     * @return An int with the assigned searchid, used for later processing
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response postSearch(JsonNode node) {
        System.out.println("POST Search called");
        long startTime = System.nanoTime();

        model = (Model) context.getAttribute("model");
        SearchEntity response = model.postSearch(node);

        System.out.printf("postSearch:\nResult: %s\nTime: %sms", response,
                TimeUnit.MILLISECONDS.convert((System.nanoTime() - startTime),
                        TimeUnit.NANOSECONDS));

		/* Check for error codes / if something went wrong */
        switch (response.getSearchid()) {
            case Strings.BAD_JSON:
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Strings.INVALID_JSON).build();
            case Strings.NO_TAGS_CODE:
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Strings.NO_TAGS).build();
            case Strings.DATABASE_ERROR_CODE:
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(Strings.BASIC_ERROR_MESSAGE).build();
        }

        return Response.status(Response.Status.CREATED).entity(response)
                .build();
    }

    /**
     * Retrieve the search results by searchid, which is assigned in
     * {@link #postSearch(JsonNode)}
     *
     * @param id The search's id
     * @return A List<SearchResult>
     */
    @Path("/result/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getSearch(@PathParam("id") int id) {
        System.out.println("GET Search called");
        long startTime = System.nanoTime();

        if (id < 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Strings.LOW_ID).build();
        }

        System.out.println(context);
        model = (Model) context.getAttribute("model");
        System.out.println(model);
        ResultData result = model.getResultById(id);

        System.out.printf("getSearch:\nResult: fine\nTime: %sms", TimeUnit.MILLISECONDS.convert(
                (System.nanoTime() - startTime), TimeUnit.NANOSECONDS));

        if (result.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(result).build();
    }
}
