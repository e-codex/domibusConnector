package eu.domibus.connector.backend.ws.link.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static eu.domibus.connector.backend.ws.link.spring.WSBackendLinkContextConfiguration.WS_BACKEND_LINK_PROFILE;

@Component("backendLinkInternalWaitQueueProperties")
@Profile(WS_BACKEND_LINK_PROFILE)
@ConfigurationProperties(prefix = "connector.backend.internal.wait-queue")
public class BackendLinkInternalWaitQueueProperties {

    /**
     * This property configures the name of the internal waiting queue for messages which are transported
     * from the connector to the backend client
     */
    private String name;

    /**
     * Configures how long we should wait (milliseconds) while polling the backend wait queue for messages.
     * <p>
     * This property configures the receiveTimeout of the internal backend wait queue, this timeout is used
     * when the pull client is asking for messages. The jms queue is asked by browsing if it contains any messages
     * for the client. The receive timeout specifies how long the we should wait for messages on the queue.
     * <p>
     * The unit is milliseconds.
     */
    private Integer receiveTimeout = 10;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getReceiveTimeout() {
        return receiveTimeout;
    }

    public void setReceiveTimeout(Integer waitQueueReceiveTimeout) {
        this.receiveTimeout = waitQueueReceiveTimeout;
    }
}
