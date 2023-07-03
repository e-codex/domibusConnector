package eu.domibus.connector.domain.model;

import eu.domibus.connector.domain.enums.ConfigurationSource;
import eu.domibus.connector.domain.enums.LinkMode;
import eu.domibus.connector.domain.enums.LinkType;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.style.ToStringCreator;

import javax.persistence.AttributeConverter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import javax.ws.rs.core.Link;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DomibusConnectorLinkPartner {

    @Valid
    private LinkPartnerName linkPartnerName;

    private String description;

    private boolean enabled;

    //allowed LinkMode.PASSIVE or LinkMode.PULL
    private LinkMode rcvLinkMode = LinkMode.PASSIVE;

    //allowed LinkMode.PASSIVE or LinkMode.PUSH
    private LinkMode sendLinkMode = LinkMode.PASSIVE;

    private LinkType linkType;

    private Duration pullInterval = Duration.ofMinutes(5l);

    private Map<String,String> properties = new HashMap<>();

    private DomibusConnectorLinkConfiguration linkConfiguration;

    private ConfigurationSource configurationSource;

    public LinkPartnerName getLinkPartnerName() {
        return linkPartnerName;
    }

    public void setLinkPartnerName(LinkPartnerName linkPartnerName) {
        this.linkPartnerName = linkPartnerName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LinkType getLinkType() {
        return linkType;
    }

    public void setLinkType(LinkType linkType) {
        this.linkType = linkType;
    }

    public DomibusConnectorLinkConfiguration getLinkConfiguration() {
        return linkConfiguration;
    }

    public ConfigurationSource getConfigurationSource() {
        return configurationSource;
    }

    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }

    public LinkMode getRcvLinkMode() {
        return rcvLinkMode;
    }

    public void setRcvLinkMode(LinkMode rcvLinkMode) {
        this.rcvLinkMode = rcvLinkMode;
    }

    public LinkMode getSendLinkMode() {
        return sendLinkMode;
    }

    public void setSendLinkMode(LinkMode sendLinkMode) {
        this.sendLinkMode = sendLinkMode;
    }

    public void setLinkConfiguration(DomibusConnectorLinkConfiguration linkConfiguration) {
        this.linkConfiguration = linkConfiguration;
    }

    public Map<String,String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String,String> properties) {
        this.properties = properties;
    }

    public static class LinkPartnerNameConverter implements AttributeConverter<LinkPartnerName, String> {

        @Override
        public String convertToDatabaseColumn(LinkPartnerName attribute) {
            if (attribute == null) {
                return null;
            }
            return attribute.getLinkName();
        }

        @Override
        public LinkPartnerName convertToEntityAttribute(String dbData) {
            if (dbData == null) {
                return null;
            }
            return LinkPartnerName.of(dbData);
        }
    }

    public static class LinkPartnerName {
        @NotBlank
        @NonNull
        @NotNull
        private String linkName;

        public static LinkPartnerName of(LinkPartnerName name) {
            return name;
        }
        public static LinkPartnerName of(String name) {
            Objects.requireNonNull(name, "Link partner name cannot be null!");
            if (StringUtils.isBlank(name)) {
                throw new IllegalArgumentException("Link partner name is not allowed to be empty!");
            }
            return new LinkPartnerName(name);
        }

        public LinkPartnerName(String linkName) {
            Objects.requireNonNull(linkName, "Link partner name cannot be null!");
            this.linkName = linkName;
        }

        public String getLinkName() {
            return linkName;
        }

        public void setLinkName(String linkName) {
            Objects.requireNonNull(linkName, "Link partner name cannot be null!");
            this.linkName = linkName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LinkPartnerName that = (LinkPartnerName) o;
            return Objects.equals(linkName, that.linkName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(linkName);
        }

        @Override
        public String toString() {
            return this.linkName;
        }
    }

    public Duration getPullInterval() {
        return pullInterval;
    }

    public void setPullInterval(Duration pullInterval) {
        this.pullInterval = pullInterval;
    }

    public String toString() {
        return new ToStringCreator(this)
                .append("linkName", this.linkPartnerName)
                .toString();
    }
}
