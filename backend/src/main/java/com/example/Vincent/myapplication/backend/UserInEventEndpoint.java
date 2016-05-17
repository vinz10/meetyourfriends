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
        name = "userInEventApi",
        version = "v1",
        resource = "userInEvent",
        namespace = @ApiNamespace(
                ownerDomain = "backend.myapplication.Vincent.example.com",
                ownerName = "backend.myapplication.Vincent.example.com",
                packagePath = ""
        )
)
public class UserInEventEndpoint {

    private static final Logger logger = Logger.getLogger(UserInEventEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(UserInEvent.class);
    }

    /**
     * Returns the {@link UserInEvent} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code UserInEvent} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "userInEvent/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public UserInEvent get(@Named("id") int id) throws NotFoundException {
        logger.info("Getting UserInEvent with ID: " + id);
        UserInEvent userInEvent = ofy().load().type(UserInEvent.class).id(id).now();
        if (userInEvent == null) {
            throw new NotFoundException("Could not find UserInEvent with ID: " + id);
        }
        return userInEvent;
    }

    /**
     * Inserts a new {@code UserInEvent}.
     */
    @ApiMethod(
            name = "insert",
            path = "userInEvent",
            httpMethod = ApiMethod.HttpMethod.POST)
    public UserInEvent insert(UserInEvent userInEvent) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that userInEvent.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(userInEvent).now();
        logger.info("Created UserInEvent with ID: " + userInEvent.getId());

        return ofy().load().entity(userInEvent).now();
    }

    /**
     * Updates an existing {@code UserInEvent}.
     *
     * @param id          the ID of the entity to be updated
     * @param userInEvent the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code UserInEvent}
     */
    @ApiMethod(
            name = "update",
            path = "userInEvent/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public UserInEvent update(@Named("id") int id, UserInEvent userInEvent) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(userInEvent).now();
        logger.info("Updated UserInEvent: " + userInEvent);
        return ofy().load().entity(userInEvent).now();
    }

    /**
     * Deletes the specified {@code UserInEvent}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code UserInEvent}
     */
    @ApiMethod(
            name = "remove",
            path = "userInEvent/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") int id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(UserInEvent.class).id(id).now();
        logger.info("Deleted UserInEvent with ID: " + id);
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
            path = "userInEvent",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<UserInEvent> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<UserInEvent> query = ofy().load().type(UserInEvent.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<UserInEvent> queryIterator = query.iterator();
        List<UserInEvent> userInEventList = new ArrayList<UserInEvent>(limit);
        while (queryIterator.hasNext()) {
            userInEventList.add(queryIterator.next());
        }
        return CollectionResponse.<UserInEvent>builder().setItems(userInEventList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(int id) throws NotFoundException {
        try {
            ofy().load().type(UserInEvent.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find UserInEvent with ID: " + id);
        }
    }
}