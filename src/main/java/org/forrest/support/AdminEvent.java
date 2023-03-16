package org.forrest.support;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AdminEvent {
    String resourceType;
    String operationType;
    String realmId;
    String clientId;
    String userId;
    String ipAddress;
    String resourcePath;
    String error;
}
