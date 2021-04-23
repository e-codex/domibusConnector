package eu.domibus.connector.controller.processor.steps;

import eu.domibus.connector.controller.routing.DCMessageRoutingConfigurationProperties;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.lib.logging.MDC;
import eu.domibus.connector.persistence.service.DCMessagePersistenceService;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import eu.domibus.connector.tools.logging.LoggingMarker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * This step looks up the correct backend name
 *
 */
@Component
public class LookupBackendNameStep implements MessageProcessStep {

    private static final Logger LOGGER = LogManager.getLogger(LookupBackendNameStep.class);

    private final DCMessageRoutingConfigurationProperties dcMessageRoutingConfigurationProperties;
    private final DCMessagePersistenceService dcMessagePersistenceService;

    public LookupBackendNameStep(DCMessageRoutingConfigurationProperties dcMessageRoutingConfigurationProperties, DCMessagePersistenceService dcMessagePersistenceService) {
        this.dcMessageRoutingConfigurationProperties = dcMessageRoutingConfigurationProperties;
        this.dcMessagePersistenceService = dcMessagePersistenceService;
    }

    @Override
    @MDC(name = LoggingMDCPropertyNames.MDC_DC_STEP_PROCESSOR_PROPERTY_NAME, value = "LookupBackendNameStep")
    public boolean executeStep(DomibusConnectorMessage domibusConnectorMessage) {
        if (!StringUtils.isEmpty(domibusConnectorMessage.getMessageDetails().getConnectorBackendClientName())) {
            //return when already set
            return true;
        }
        String backendName = null;

        //Lookup backend by conversation id
        String conversationId = domibusConnectorMessage.getMessageDetails().getConversationId();
        if (!StringUtils.isEmpty(conversationId)) {
            List<DomibusConnectorMessage> messagesByConversationId = dcMessagePersistenceService.findMessagesByConversationId(conversationId);
            backendName = messagesByConversationId.stream()
                    .map(m -> m.getMessageDetails()
                            .getConnectorBackendClientName())
                    .filter(s -> !StringUtils.isEmpty(s))
                    .findAny().orElse(null);

        }
        if (backendName != null) {
            LOGGER.info(LoggingMarker.Log4jMarker.BUSINESS_LOG, "ConversationId [{}] is used to set backend to [{}]", conversationId, backendName);
        }

        //lookup backend by rules and default backend
        String defaultBackendName = dcMessageRoutingConfigurationProperties.getDefaultBackendName();
        if (dcMessageRoutingConfigurationProperties.isEnabled()) {
            LOGGER.debug("Backend routing is enabled");
            Optional<String> first = dcMessageRoutingConfigurationProperties.getBackendRules().stream()
                    .filter(r -> r.getMatchClause().matches(domibusConnectorMessage))
                    .map(DCMessageRoutingConfigurationProperties.RoutingRule::getLinkName)
                    .findFirst();
            if (first.isPresent()) {
                backendName = first.get();
                LOGGER.info(LoggingMarker.Log4jMarker.BUSINESS_LOG, "Looked up backend name [{}] for message", backendName);
                domibusConnectorMessage.getMessageDetails().setConnectorBackendClientName(backendName);
            } else {
                LOGGER.warn(LoggingMarker.Log4jMarker.BUSINESS_LOG, "No backend rule pattern has matched! Applying default backend name [{}]!",
                        defaultBackendName);
                domibusConnectorMessage.getMessageDetails().setConnectorBackendClientName(defaultBackendName);
            }
        } else {
            LOGGER.debug("Backend routing is disabled, applying default backend name [{}]!", dcMessageRoutingConfigurationProperties.getDefaultBackendName());
            domibusConnectorMessage.getMessageDetails().setConnectorBackendClientName(defaultBackendName);
        }
        return true;
    }

}
