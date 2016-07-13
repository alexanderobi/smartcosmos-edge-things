package net.smartcosmos.edge.things.resource;

import javax.inject.Inject;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import net.smartcosmos.edge.things.service.GetThingEdgeService;
import net.smartcosmos.security.user.SmartCosmosUser;
import net.smartcosmos.spring.SmartCosmosRdao;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@SmartCosmosRdao
@Slf4j
public class GetThingResource {

    GetThingEdgeService getThingService;

    @Inject
    public GetThingResource(GetThingEdgeService getThingService) {
        this.getThingService = getThingService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE, consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> findAll(
        @RequestParam(required = false, defaultValue = "1") Integer page,
        @RequestParam(required = false, defaultValue = "20") Integer size,
        @Valid @RequestParam(required = false, defaultValue = "asc") String sortOrder,
        @RequestParam(required = false, defaultValue = "created") String sortBy,
        SmartCosmosUser user) {

        return getThingService.getAll(user, page, size, sortOrder, sortBy);
    }
}
