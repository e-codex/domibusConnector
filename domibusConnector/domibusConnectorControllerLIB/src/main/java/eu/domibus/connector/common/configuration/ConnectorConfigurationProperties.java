package eu.domibus.connector.common.configuration;

import eu.domibus.connector.domain.enums.ConfigurationSource;
import eu.domibus.connector.domain.model.DC5BusinessDomain;
import eu.ecodex.dc5.domain.DCBusinessDomainManager;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Validated
@Valid
@ConfigurationProperties(prefix = ConnectorConfigurationProperties.PREFIX)
public class ConnectorConfigurationProperties {

    public static final String PREFIX = "connector";

    public ConnectorConfigurationProperties() {
    }

    @NotNull
    private ConfigurationSource[] allowedConfigurationSources = { ConfigurationSource.DB, ConfigurationSource.ENV };

    /**
     * a random instance name is the default
     */
    @NotNull
    @NotBlank
    private String instanceName = UUID.randomUUID().toString().substring(0,6);

    @NotNull
    @Valid
    private StageConfigurationProperties stage = new StageConfigurationProperties();

    @NotNull
    DC5BusinessDomain.BusinessDomainId defaultBusinessDomainId = DC5BusinessDomain.getDefaultBusinessDomainId();

    /**
     * should the business domains be loaded from
     * Database
     * @see {@link DCBusinessDomainManager}
     */
    boolean loadBusinessDomainsFromDb = true;

    @NotNull
    Map<DC5BusinessDomain.BusinessDomainId, @Valid BusinessDomainConfig> businessDomain = new HashMap<>();

    @Validated
    public static class BusinessDomainConfig {
        @NotNull
        private boolean enabled = true;

        private String description;

        private Map<String, String> properties = new HashMap<>();

        public Map<String, String> getProperties() {
            return properties;
        }

        public void setProperties(Map<String, String> properties) {
            this.properties = properties;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public DC5BusinessDomain.BusinessDomainId getDefaultBusinessDomainId() {
        return defaultBusinessDomainId;
    }

    public void setDefaultBusinessDomainId(DC5BusinessDomain.BusinessDomainId defaultBusinessDomainId) {
        this.defaultBusinessDomainId = defaultBusinessDomainId;
    }

    public Map<DC5BusinessDomain.BusinessDomainId, BusinessDomainConfig> getBusinessDomain() {
        return businessDomain;
    }

    public void setBusinessDomain(Map<DC5BusinessDomain.BusinessDomainId, BusinessDomainConfig> businessDomain) {
        this.businessDomain = businessDomain;
    }

    public boolean isLoadBusinessDomainsFromDb() {
        return loadBusinessDomainsFromDb;
    }

    public void setLoadBusinessDomainsFromDb(boolean loadBusinessDomainsFromDb) {
        this.loadBusinessDomainsFromDb = loadBusinessDomainsFromDb;
    }

    public StageConfigurationProperties getStage() {
        return stage;
    }

    public void setStage(StageConfigurationProperties stage) {
        this.stage = stage;
    }

    public ConfigurationSource[] getAllowedConfigurationSources() {
        return allowedConfigurationSources;
    }

    public void setAllowedConfigurationSources(ConfigurationSource[] allowedConfigurationSources) {
        this.allowedConfigurationSources = allowedConfigurationSources;
    }
}
