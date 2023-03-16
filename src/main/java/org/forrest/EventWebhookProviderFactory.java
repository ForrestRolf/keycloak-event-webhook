package org.forrest;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import java.util.Arrays;
import java.util.HashSet;

public class EventWebhookProviderFactory implements EventListenerProviderFactory {
    private static final Logger log = Logger.getLogger(EventWebhookProvider.class);
    private org.forrest.support.Config config = null;

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        return new EventWebhookProvider(keycloakSession, config);
    }

    @Override
    public void init(Config.Scope scope) {
        String[] includeUserEvents = scope.getArray("include-user-events");
        HashSet<EventType> userEventSet = new HashSet<>();
        if (includeUserEvents != null) {
            for (String e : includeUserEvents) {
                userEventSet.add(EventType.valueOf(e));
            }
        }

        String[] includeAdminEvents = scope.getArray("include-admin-events");
        HashSet<ResourceType> adminEventSet = new HashSet<>();
        if (includeAdminEvents != null) {
            for (String e : includeAdminEvents) {
                adminEventSet.add(ResourceType.valueOf(e));
            }
        }

        String[] urls = scope.getArray("server-uri");
        HashSet<String> urlSet = new HashSet<>(Arrays.asList(urls));

        this.config = org.forrest.support.Config.builder()
                .url(urlSet)
                .username(scope.get("username", null))
                .password(scope.get("password", null))
                .userEventEnabled(scope.getBoolean("user-event-enable", true))
                .adminEventEnabled(scope.getBoolean("admin-event-enable", true))
                .includeUserEvents(userEventSet)
                .includeAdminEvents(adminEventSet)
                .build();
        log.debug(this.config);
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return "webhook";
    }
}
