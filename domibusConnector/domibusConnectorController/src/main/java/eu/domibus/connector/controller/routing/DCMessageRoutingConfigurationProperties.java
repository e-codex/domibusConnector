package eu.domibus.connector.controller.routing;

import eu.domibus.connector.common.DomibusConnectorDefaults;
import eu.domibus.connector.common.annotations.BusinessDomainScoped;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@BusinessDomainScoped
@Component
@ConfigurationProperties(prefix = DCMessageRoutingConfigurationProperties.ROUTING_CONFIG_PREFIX)
public class DCMessageRoutingConfigurationProperties {

    public static final String ROUTING_CONFIG_PREFIX = "connector.routing";

    private boolean enabled = true;

    private List<RoutingRule> backendRules = new ArrayList<>();

    private List<RoutingRule> gatewayRules = new ArrayList<>();

    @NotBlank
    private String defaultBackendName = DomibusConnectorDefaults.DEFAULT_BACKEND_NAME;

    @NotBlank
    private String defaultGatewayName = DomibusConnectorDefaults.DEFAULT_GATEWAY_NAME;

    /**
     * Backend name of the connector itself,
     * is used for connector2connector tests,
     * when the connector itself acts as a backend
     *
     */
    @NotBlank
    private String connectorBackendName = "connectorBackend";

    /**
     * Gateway name of the connector itself,
     * is used for backend2backend tests,
     * when the connector itself acts as a gateway
     *
     *
     *
     * NOT IMPLEMENTED YET!
     */
    @NotBlank
    private String connectorGatewayName = "connectorGateway";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<RoutingRule> getBackendRules() {
        return backendRules;
    }

    public void setBackendRules(List<RoutingRule> backendRules) {
        this.backendRules = backendRules;
    }

    public List<RoutingRule> getGatewayRules() {
        return gatewayRules;
    }

    public void setGatewayRules(List<RoutingRule> gatewayRules) {
        this.gatewayRules = gatewayRules;
    }

    public String getDefaultBackendName() {
        return defaultBackendName;
    }

    public void setDefaultBackendName(String defaultBackendName) {
        this.defaultBackendName = defaultBackendName;
    }

    public String getDefaultGatewayName() {
        return defaultGatewayName;
    }

    public void setDefaultGatewayName(String defaultGatewayName) {
        this.defaultGatewayName = defaultGatewayName;
    }

    public String getConnectorBackendName() {
        return connectorBackendName;
    }

    public void setConnectorBackendName(String connectorBackendName) {
        this.connectorBackendName = connectorBackendName;
    }

    public String getConnectorGatewayName() {
        return connectorGatewayName;
    }

    public void setConnectorGatewayName(String connectorGatewayName) {
        this.connectorGatewayName = connectorGatewayName;
    }

}
