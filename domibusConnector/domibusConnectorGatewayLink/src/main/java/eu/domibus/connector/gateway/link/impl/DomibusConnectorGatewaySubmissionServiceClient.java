package eu.domibus.connector.gateway.link.impl;

import javax.annotation.Resource;

import eu.domibus.connector.domain.transition.tools.PrintDomibusConnectorMessageType;
import org.apache.cxf.common.util.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import eu.domibus.connector.controller.exception.DomibusConnectorGatewaySubmissionException;
import eu.domibus.connector.controller.service.DomibusConnectorGatewaySubmissionService;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.transformer.DomibusConnectorDomainMessageTransformer;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.ws.gateway.submission.webservice.DomibusConnectorGatewaySubmissionWebService;

@Component
public class DomibusConnectorGatewaySubmissionServiceClient implements DomibusConnectorGatewaySubmissionService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DomibusConnectorGatewaySubmissionServiceClient.class);

	@Resource(name="gwSubmissionClient")
	DomibusConnectorGatewaySubmissionWebService submissionClient;

	@Override
	public void submitToGateway(DomibusConnectorMessage message) throws DomibusConnectorGatewaySubmissionException {
		DomibusConnectorMessageType request = DomibusConnectorDomainMessageTransformer.transformDomainToTransition(message);

		if (LOGGER.isTraceEnabled()) {
		    LOGGER.trace("Printing out request message:");
            LOGGER.trace("message=[{}]", PrintDomibusConnectorMessageType.messageToString(request));
        }

		LOGGER.debug("#submitToGateway: calling webservice to send request");
		DomibsConnectorAcknowledgementType ack = submissionClient.submitMessage(request);
		LOGGER.debug("#submitToGateway: received [{}] from gw", ack);

        if (ack != null && ack.isResult()) {
            String ebmsId = ack.getMessageId();
            LOGGER.info("GW accepted message and sent id [{}] back", ebmsId);
            message.getMessageDetails().setEbmsMessageId(ebmsId);
        } else {
            if (ack != null && !StringUtils.isEmpty(ack.getResultMessage()))
                throw new DomibusConnectorGatewaySubmissionException(ack.getResultMessage());
            else
                throw new DomibusConnectorGatewaySubmissionException("Undefined submission error!");
        }
	}

}
