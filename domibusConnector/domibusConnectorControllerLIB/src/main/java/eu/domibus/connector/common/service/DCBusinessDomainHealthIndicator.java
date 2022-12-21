package eu.domibus.connector.common.service;

import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.ecodex.dc5.domain.DCBusinessDomainManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("businessDomains")
@RequiredArgsConstructor
public class DCBusinessDomainHealthIndicator implements HealthIndicator {

    private final DCBusinessDomainManagerImpl businessDomainManager;

    @Override
    public Health health() {
        Map<DomibusConnectorBusinessDomain, DCBusinessDomainManager.DomainValidResult> allBusinessDomainsValidations = businessDomainManager.getAllBusinessDomainsValidations();
        long overallDomains = allBusinessDomainsValidations.size();
        long validDomains = allBusinessDomainsValidations.values().stream().filter(DCBusinessDomainManager.DomainValidResult::isValid).count();
        long warningDomains = allBusinessDomainsValidations.values().stream().filter(r -> r.isValid() && !r.getWarnings().isEmpty()).count();
        long invalidDomains = overallDomains - validDomains;

        Health.Builder h;

        if (validDomains > 0) {
            h = Health.up();
        } else {
            h = Health.down();
        }
        h.withDetail("overallDomainsCount", overallDomains);
        h.withDetail("validDomainsCount", validDomains);
        h.withDetail("warningDomainsCount", warningDomains);
        h.withDetail("invalidDomains", invalidDomains);

        return h.build();
    }
}
