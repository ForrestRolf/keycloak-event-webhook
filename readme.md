# Keycloak event webhook

## Configuration

| Item                                                | Required | Default | Example                             |
|:----------------------------------------------------|:---------|:--------|:------------------------------------|
| KC_SPI_EVENTS_LISTENER_WEBHOOK_SERVER_URI           | true     |         | http://localhost,http://example.com |
| KC_SPI_EVENTS_LISTENER_WEBHOOK_USERNAME             | false    | null    | username                            |
| KC_SPI_EVENTS_LISTENER_WEBHOOK_PASSWORD             | false    | null    | password                            |
| KC_SPI_EVENTS_LISTENER_WEBHOOK_USER_EVENT_ENABLE    | false    | true    | false                               |
| KC_SPI_EVENTS_LISTENER_WEBHOOK_ADMIN_EVENT_ENABLE   | false    | true    | true                                |
| KC_SPI_EVENTS_LISTENER_WEBHOOK_INCLUDE_USER_EVENTS  | false    |         | LOGIN,CODE_TO_TOKEN                 |
| KC_SPI_EVENTS_LISTENER_WEBHOOK_INCLUDE_ADMIN_EVENTS | false    |         | AUTHORIZATION_RESOURCE              |

More info:
* [EventType](https://www.keycloak.org/docs-api/21.0.1/javadocs/org/keycloak/events/EventType.html)
* [ResourceType](https://www.keycloak.org/docs-api/21.0.1/javadocs/org/keycloak/events/admin/ResourceType.html)

> Note: If `KC_SPI_EVENTS_LISTENER_WEBHOOK_INCLUDE_USER_EVENTS` and `KC_SPI_EVENTS_LISTENER_WEBHOOK_INCLUDE_ADMIN_EVENTS` are not configured, it means that all events will be sent.

## Develop

### Prerequisites
* Docker
* Docker Compose

### Test

```shell
mvn clean package
docker compose up -d
```

## Deploy

* Copy target/keycloak-event-webhook.jar to /opt/keycloak/providers/keycloak-event-webhook.jar
* [Configuring Keycloak](https://www.keycloak.org/server/configuration)

