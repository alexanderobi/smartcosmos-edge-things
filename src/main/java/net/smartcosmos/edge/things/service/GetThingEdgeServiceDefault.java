package net.smartcosmos.edge.things.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import net.smartcosmos.edge.things.domain.RestEdgePagedThingResponseDto;
import net.smartcosmos.edge.things.domain.things.RestPagedThingResponse;
import net.smartcosmos.edge.things.domain.things.RestThingResponse;
import net.smartcosmos.edge.things.exception.RestException;
import net.smartcosmos.edge.things.service.event.EventSendingService;
import net.smartcosmos.edge.things.service.metadata.GetMetadataRestService;
import net.smartcosmos.edge.things.service.things.GetThingRestService;
import net.smartcosmos.security.user.SmartCosmosUser;

import static net.smartcosmos.edge.things.util.ResponseBuilderUtility.buildForwardingResponse;

@Service
@Slf4j
public class GetThingEdgeServiceDefault implements GetThingEdgeService {

    private final EventSendingService eventSendingService;
    private final ConversionService conversionService;
    private final GetMetadataRestService getMetadataService;
    private final GetThingRestService getThingService;

    private final String[] THING_FIELDS = { "urn", "id", "active", "type", "created", "lastModified" };

    @Autowired
    public GetThingEdgeServiceDefault(
        EventSendingService eventSendingService,
        ConversionService conversionService,
        GetMetadataRestService getMetadataService,
        GetThingRestService getThingService) {

        this.eventSendingService = eventSendingService;
        this.conversionService = conversionService;
        this.getMetadataService = getMetadataService;
        this.getThingService = getThingService;
    }

    @Override
    public ResponseEntity<?> getByTypeAndUrn(String type, String urn, Set<String> metadataKeys, SmartCosmosUser user) {

        ResponseEntity thingResponse = getThingService.findByTypeAndUrn(type, urn, user);
        if (!thingResponse.getStatusCode()
            .is2xxSuccessful()) {
            log.warn(getByTypeAndUrnLogMessage(type, urn, user, thingResponse.toString()));
            return buildForwardingResponse(thingResponse);
        }

        Map<String, Object> resultMap = new LinkedHashMap<>();

        if (thingResponse.hasBody() && thingResponse.getBody() instanceof RestThingResponse) {
            Map<String, Object> thingResponseMap = conversionService.convert(thingResponse.getBody(), Map.class);
            resultMap.putAll(thingResponseMap);
        }

        try {
            Map<String, Object> metadaResponseMap = getMetadataForThing(type, urn, metadataKeys, user);
            resultMap.putAll(metadaResponseMap);
        } catch (RestException e) {
            String msg = getByTypeAndUrnLogMessage(type, urn, user, e.toString());
            log.error(msg);
            log.debug(msg, e);
            return e.getResponseEntity();
        }

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .body(resultMap);
    }

    @Override
    public ResponseEntity<?> getByType(
        String type, Set<String> metadataKeys, Integer page, Integer size, String sortOrder, String sortBy, SmartCosmosUser user) {

        if (StringUtils.isNotBlank(sortBy) && ArrayUtils.contains(THING_FIELDS, sortBy)) {
            // Look up Things and merge Metadata
            return getThingsMergeMetadata(type, metadataKeys, page, size, sortOrder, sortBy, user);
        } else {
            // Look up Metadata owners and merge Thing fields
            return getMetadataOwnerMergeThings(type, metadataKeys, page, size, sortOrder, sortBy, user);
        }
    }

    private ResponseEntity<?> getThingsMergeMetadata(
        String type,
        Set<String> metadataKeys,
        Integer page,
        Integer size,
        String sortOrder,
        String sortBy,
        SmartCosmosUser user) {

        ResponseEntity thingResponse = getThingService.findByType(type, page, size, sortOrder, sortBy, user);
        if (!thingResponse.getStatusCode()
            .is2xxSuccessful()) {
            log.warn(getByTypeLogMessage(type, user, thingResponse.toString()));
            return buildForwardingResponse(thingResponse);
        }

        if (thingResponse.hasBody() && thingResponse.getBody() instanceof RestPagedThingResponse) {

            RestPagedThingResponse thingPage = (RestPagedThingResponse) thingResponse.getBody();
            try {
                List<Map<String, Object>> data = collectFindByTypeData(thingPage.getData(), metadataKeys, user);

                RestEdgePagedThingResponseDto<Map<String, Object>> responsePage = RestEdgePagedThingResponseDto.<Map<String, Object>>builder()
                    .data(data)
                    .page(thingPage.getPage())
                    .build();

                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body(responsePage);
            } catch (RestException e) {
                String msg = getByTypeLogMessage(type, user, e.toString());
                log.error(msg);
                log.debug(msg, e);
                return e.getResponseEntity();
            }
        }

        return buildForwardingResponse(thingResponse);
    }

    private ResponseEntity<?> getMetadataOwnerMergeThings(
        String type,
        Set<String> metadataKeys,
        Integer page,
        Integer size,
        String sortOrder,
        String sortBy,
        SmartCosmosUser user) {
        /**
         * TODO: Add Sorting by Metadata keys (OBJECTS-910)
         * author: asiegel
         * date: 18 Jul 2016
         */

        throw new UnsupportedOperationException("Sorting by Metadata keys is currently not supported.");
    }

    private List<Map<String, Object>> collectFindByTypeData(
        Collection<RestThingResponse> responseList,
        Set<String> metadataKeys,
        SmartCosmosUser user) throws RestException {

        List<Map<String, Object>> data = new ArrayList<>();

        for (RestThingResponse thing : responseList) {
            Map<String, Object> thingMap = conversionService.convert(thing, Map.class);
            Map<String, Object> metadataMap = getMetadataForThing(thing.getType(), thing.getUrn(), metadataKeys, user);
            thingMap.putAll(metadataMap);

            data.add(thingMap);
        }

        return data;
    }

    private Map<String, Object> getMetadataForThing(String type, String urn, Set<String> metadataKeys, SmartCosmosUser user) throws RestException {

        Map<String, Object> resultMap = new LinkedHashMap<>();

        ResponseEntity metadataResponse = getMetadataService.findByOwner(type, urn, metadataKeys, user);
        HttpStatus metadataHttpStatus = metadataResponse.getStatusCode();
        if (!metadataHttpStatus.is2xxSuccessful() && !HttpStatus.NOT_FOUND.equals(metadataHttpStatus)) {
            throw new RestException(metadataResponse);
        }

        if (metadataHttpStatus.is2xxSuccessful()
            && metadataResponse.hasBody() && metadataResponse.getBody() instanceof Map) {
            Map<String, Object> metadaResponseMap = (Map<String, Object>) metadataResponse.getBody();
            resultMap.putAll(metadaResponseMap);
        }

        return resultMap;
    }

    private String getByTypeLogMessage(String type, SmartCosmosUser user, String message) {
        return String.format("Read request for Thing with type '%s' by user '%s' failed: %s",
                             type,
                             user,
                             message);
    }

    private String getByTypeAndUrnLogMessage(String type, String urn, SmartCosmosUser user, String message) {
        return String.format("Read request for Thing with type '%s' and urn '%s' by user '%s' failed: %s",
                             type,
                             urn,
                             user,
                             message);
    }

}
