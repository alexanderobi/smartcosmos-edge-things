package net.smartcosmos.edge.things.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.security.user.SmartCosmosUser;

/**
 * Methods for creating things with embedded metadata.
 */
public interface CreateThingEdgeService {

    /**
     * Create the Thing with metadata.
     *
     * @param response the {@link DeferredResult} response that should be asynchronous
     * @param type the programmer/user defined type of the thing to create/update
     * @param metadataMap a Map of the metadata to save for the thing
     * @param force are we acting as upsert (true)
     * @param user the Spring embedded {@link SmartCosmosUser}
     */
    void create(
        DeferredResult<ResponseEntity> response, String type, Map<String, Object> metadataMap, Boolean force, SmartCosmosUser user);
}
