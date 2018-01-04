package eu.domibus.connector.security;


import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.domibus.connector.security.container.DomibusSecurityContainer;
import eu.domibus.connector.security.exception.DomibusConnectorSecurityException;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;

public class DomibusConnectorSecurityToolkitDefaultImpl implements DomibusConnectorSecurityToolkit {

    static Logger LOGGER = LoggerFactory.getLogger(DomibusConnectorSecurityToolkitDefaultImpl.class);

    @Resource(name="domibusConnectorSecurityContainer")
    DomibusSecurityContainer securityContainer;

    public void setSecurityContainer(DomibusSecurityContainer securityContainer) {
        this.securityContainer = securityContainer;
    }

    @Override
    public void buildContainer(DomibusConnectorMessage message) throws DomibusConnectorSecurityException {
        securityContainer.createContainer(message);
    }

    @Override
    public void validateContainer(DomibusConnectorMessage message) throws DomibusConnectorSecurityException {
        securityContainer.recieveContainerContents(message);
    }

}
