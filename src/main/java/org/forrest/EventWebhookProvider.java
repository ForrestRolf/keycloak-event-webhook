package org.forrest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.forrest.support.Config;
import org.forrest.support.UserEvent;
import org.jetbrains.annotations.NotNull;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmProvider;

import java.io.IOException;
import java.util.Base64;

public class EventWebhookProvider implements EventListenerProvider {
    private static final Logger log = Logger.getLogger(EventWebhookProvider.class);
    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final Config config;

    private final KeycloakSession session;
    private final RealmProvider model;

    public EventWebhookProvider(KeycloakSession keycloakSession, Config config) {
        this.session = keycloakSession;
        this.model = keycloakSession.realms();
        this.config = config;
    }

    @Override
    public void onEvent(Event event) {
        if (!config.isUserEventEnabled()) {
            return;
        }
        if (!config.getIncludeUserEvents().isEmpty() && !config.getIncludeUserEvents().contains(event.getType())) {
            return;
        }

        try {
            UserEvent userEvent = UserEvent.builder()
                    .userId(event.getUserId())
                    .type(event.getType().name())
                    .realmId(event.getRealmId())
                    .clientId(event.getClientId())
                    .ipAddress(event.getIpAddress())
                    .error(event.getError())
                    .details(event.getDetails())
                    .build();
            String evt = objectMapper.writeValueAsString(userEvent);

            for (String url : config.getUrl()) {
                sendRequest(url, evt);
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        if (!config.isAdminEventEnabled()) {
            return;
        }
        if (!config.getIncludeAdminEvents().isEmpty() && !config.getIncludeAdminEvents().contains(event.getResourceType())) {
            return;
        }

        try {
            org.forrest.support.AdminEvent adminEvent = org.forrest.support.AdminEvent.builder()
                    .resourceType(event.getResourceType().name())
                    .operationType(event.getOperationType().name())
                    .realmId(event.getAuthDetails().getRealmId())
                    .clientId(event.getAuthDetails().getClientId())
                    .userId(event.getAuthDetails().getUserId())
                    .ipAddress(event.getAuthDetails().getIpAddress())
                    .resourcePath(event.getResourcePath())
                    .error(event.getError())
                    .build();
            String evt = objectMapper.writeValueAsString(adminEvent);
            for (String url : config.getUrl()) {
                sendRequest(url, evt);
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void close() {

    }

    private void sendRequest(String url, String body) {
        if (url == null) {
            log.error("The service url of webhook is not configured");
            return;
        }
        try {
            log.info("Call webhook: " + url);
            RequestBody formBody = RequestBody.create(body, JSON);
            log.debug("body: " + body);

            okhttp3.Request.Builder builder = new Request.Builder()
                    .url(url)
                    .addHeader("User-Agent", "Keycloak Event Webhook");

            if (config.getUsername() != null && config.getPassword() != null) {
                String sourceString = config.getUsername() + ":" + config.getPassword();
	            String encodedString = Base64.getEncoder().encodeToString(sourceString.getBytes());
                builder.addHeader("Authorization", "Basic " + encodedString);
            }

            Request request = builder.post(formBody)
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    log.error(e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    log.debug(response);
                    log.info("Sent successfully");
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
