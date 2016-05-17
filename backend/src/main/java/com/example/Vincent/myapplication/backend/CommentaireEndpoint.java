package com.example.Vincent.myapplication.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "commentaireApi",
        version = "v1",
        resource = "commentaire",
        namespace = @ApiNamespace(
                ownerDomain = "backend.myapplication.Vincent.example.com",
                ownerName = "backend.myapplication.Vincent.example.com",
                packagePath = ""
        )
)
public class CommentaireEndpoint {

    private static final Logger logger = Logger.getLogger(CommentaireEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Commentaire.class);
    }

    /**
     * Returns the {@link Commentaire} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Commentaire} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "commentaire/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Commentaire get(@Named("id") int id) throws NotFoundException {
        logger.info("Getting Commentaire with ID: " + id);
        Commentaire commentaire = ofy().load().type(Commentaire.class).id(id).now();
        if (commentaire == null) {
            throw new NotFoundException("Could not find Commentaire with ID: " + id);
        }
        return commentaire;
    }

    /**
     * Inserts a new {@code Commentaire}.
     */
    @ApiMethod(
            name = "insert",
            path = "commentaire",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Commentaire insert(Commentaire commentaire) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that commentaire.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(commentaire).now();
        logger.info("Created Commentaire with ID: " + commentaire.getId());

        return ofy().load().entity(commentaire).now();
    }

    /**
     * Updates an existing {@code Commentaire}.
     *
     * @param id          the ID of the entity to be updated
     * @param commentaire the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Commentaire}
     */
    @ApiMethod(
            name = "update",
            path = "commentaire/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Commentaire update(@Named("id") int id, Commentaire commentaire) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(commentaire).now();
        logger.info("Updated Commentaire: " + commentaire);
        return ofy().load().entity(commentaire).now();
    }

    /**
     * Deletes the specified {@code Commentaire}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Commentaire}
     */
    @ApiMethod(
            name = "remove",
            path = "commentaire/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") int id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(Commentaire.class).id(id).now();
        logger.info("Deleted Commentaire with ID: " + id);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "commentaire",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Commentaire> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Commentaire> query = ofy().load().type(Commentaire.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Commentaire> queryIterator = query.iterator();
        List<Commentaire> commentaireList = new ArrayList<Commentaire>(limit);
        while (queryIterator.hasNext()) {
            commentaireList.add(queryIterator.next());
        }
        return CollectionResponse.<Commentaire>builder().setItems(commentaireList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(int id) throws NotFoundException {
        try {
            ofy().load().type(Commentaire.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Commentaire with ID: " + id);
        }
    }
}