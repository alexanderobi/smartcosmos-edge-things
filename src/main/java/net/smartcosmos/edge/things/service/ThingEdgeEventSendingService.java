package net.smartcosmos.edge.things.service;

import javax.inject.Inject;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import net.smartcosmos.edge.things.utility.ThingEdgeEventType;
import net.smartcosmos.events.SmartCosmosEventException;
import net.smartcosmos.events.SmartCosmosEventTemplate;
import net.smartcosmos.security.user.SmartCosmosUser;

/**
 * Send events for metadata interactions to the event service.
 */
@Slf4j
@Service
public class ThingEdgeEventSendingService implements EventSendingService {
    private final SmartCosmosEventTemplate smartCosmosEventTemplate;

    @Inject
    public ThingEdgeEventSendingService(SmartCosmosEventTemplate smartCosmosEventTemplate) {
        this.smartCosmosEventTemplate = smartCosmosEventTemplate;

        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Override
    public void sendEvent(SmartCosmosUser user, ThingEdgeEventType eventType, Object entity) {
        sendEvent(user, eventType.getEventName(), entity);
    }

    @Override
    public void sendEvent(SmartCosmosUser user, String eventType, Object entity) {
        try {
            smartCosmosEventTemplate.sendEvent(entity, eventType, user);
        } catch (SmartCosmosEventException e) {
            String msg = String.format("Exception processing metadata event '%s', entity: '%s', cause: '%s'.", eventType, entity, e.toString());
            log.error(msg);
            log.debug(msg, e);
        }
    }

}
