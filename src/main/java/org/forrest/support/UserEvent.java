package org.forrest.support;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public class UserEvent {
    String type;
    String realmId;
    String clientId;
    String userId;
    String ipAddress;
    String error;
    Map<String, String> details;
}
