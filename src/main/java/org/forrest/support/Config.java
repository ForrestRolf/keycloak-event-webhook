package org.forrest.support;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.ResourceType;


import java.util.Set;

@Getter
@Setter
@Builder
public class Config {

    Set<String> url;
    String username;
    String password;
    boolean userEventEnabled;
    boolean adminEventEnabled;

    Set<EventType> includeUserEvents;
    Set<ResourceType> includeAdminEvents;
}
