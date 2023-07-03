
package eu.domibus.connector.controller.service;

import eu.ecodex.dc5.message.model.DC5MessageId;

import jakarta.validation.constraints.NotNull;

/**
 * Service for generating a for the connector unique message id
 *  if multiple instances are running the generated id must be unique over all
 *  instances
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public interface DomibusConnectorMessageIdGenerator {
    
    /**
     * generates a uniquie Message Id for a received message
     * the maximum string length for this id is 255
     * @return the message id
     */
    @NotNull
    public DC5MessageId generateDomibusConnectorMessageId();

}
