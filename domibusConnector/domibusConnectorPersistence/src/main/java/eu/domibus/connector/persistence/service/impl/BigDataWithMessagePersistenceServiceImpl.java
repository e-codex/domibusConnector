
package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.model.DomibusConnectorBigDataReference;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageAttachment;
import eu.domibus.connector.domain.model.DomibusConnectorMessageContent;
import eu.domibus.connector.domain.model.helper.DomainModelHelper;
import eu.domibus.connector.persistence.service.DomibusConnectorBigDataPersistenceService;
import eu.domibus.connector.persistence.service.exceptions.PersistenceException;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import eu.domibus.connector.persistence.service.DomibusConnectorPersistAllBigDataOfMessageService;

import javax.annotation.Nonnull;

/**
 * Facade Service to make it easier to persist all big data of 
 * a message
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Service
public class BigDataWithMessagePersistenceServiceImpl implements DomibusConnectorPersistAllBigDataOfMessageService {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(BigDataWithMessagePersistenceServiceImpl.class);
    
    @Autowired
    private DomibusConnectorBigDataPersistenceService bigDataPersistenceServiceImpl;


    
    // START GETTER / SETTER //
    public void setBigDataPersistenceServiceImpl(DomibusConnectorBigDataPersistenceService bigDataPersistenceServiceImpl) {
        this.bigDataPersistenceServiceImpl = bigDataPersistenceServiceImpl;
    }
    // ENDE GETTER / SETTER //
    
    
    @Override
    //@Transactional
    public DomibusConnectorMessage persistAllBigFilesFromMessage(DomibusConnectorMessage message) {
        if (DomainModelHelper.isEvidenceMessage(message)) {
            LOGGER.debug("#persistAllBigFilesFromMessage: is evidence message doing nothing...");
            return message;
        }
        LOGGER.debug("persistAllBigFilesFromMessage: message [{}]", message);
//        try {
            for (DomibusConnectorMessageAttachment attachment : message.getMessageAttachments()) {                   
                DomibusConnectorBigDataReference readFrom = attachment.getAttachment();
                DomibusConnectorBigDataReference writeTo = null;
				try {
					writeTo = bigDataPersistenceServiceImpl.createDomibusConnectorBigDataReference(readFrom.getInputStream(), message.getConnectorMessageId(), attachment.getName(), attachment.getMimeType());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//                OutputStream outStream = writeTo.getOutputStream();
//                StreamUtils.copy(readFrom.getInputStream(), outStream);
//                outStream.flush();
//                outStream.close();
//                writeTo = bigDataPersistenceServiceImpl.copyInputToOutputAndPersist(readFrom, writeTo);
                attachment.setAttachment(writeTo);
            }
            DomibusConnectorMessageContent messageContent = message.getMessageContent();
            if (hasMainDocument(messageContent)) {
                DomibusConnectorBigDataReference docReadFrom = messageContent.getDocument().getDocument();
                DomibusConnectorBigDataReference docWriteTo = null;
				try {
					docWriteTo = bigDataPersistenceServiceImpl.createDomibusConnectorBigDataReference(docReadFrom.getInputStream(), message.getConnectorMessageId(), messageContent.getDocument().getDocumentName(), messageContent.getDocument().getDocument().getContentType());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//                OutputStream outStream = docWriteTo.getOutputStream();
//                StreamUtils.copy(docReadFrom.getInputStream(), outStream);
//                outStream.flush();
//                outStream.close();
//                docWriteTo = bigDataPersistenceServiceImpl.copyInputToOutputAndPersist(docReadFrom, docWriteTo);
                messageContent.getDocument().setDocument(docWriteTo);
            }
            LOGGER.debug("persistAllBigFilesFromMessage: SUCCESS - message [{}]", message);
            return message;
//        } catch (IOException ioe) {
//            throw new PersistenceException("A exception occured during copying big data into storage", ioe);
//        }
    }

    @Override
    @Transactional
    public void cleanForMessage(DomibusConnectorMessage message) {
        bigDataPersistenceServiceImpl.deleteDomibusConnectorBigDataReference(message);
    }

    @Override
    @Transactional
    public DomibusConnectorMessage loadAllBigFilesFromMessage(@Nonnull DomibusConnectorMessage message) {
        if (DomainModelHelper.isEvidenceMessage(message)) {
            LOGGER.debug("#persistAllBigFilesFromMessage: is evidence message doing nothing...");
            return message;
        }
        LOGGER.trace("#loadAllBigFilesFromMessage: message [{}]", message);
        for (DomibusConnectorMessageAttachment attachment : message.getMessageAttachments()) {
            DomibusConnectorBigDataReference activeRead = attachment.getAttachment();
            LOGGER.trace("#loadAllBigFilesFromMessage: loading attachment [{}]", activeRead);
            DomibusConnectorBigDataReference activatedRead = bigDataPersistenceServiceImpl.getReadableDataSource(activeRead);
            attachment.setAttachment(activatedRead);
        }
        DomibusConnectorMessageContent messageContent = message.getMessageContent();
        if (hasMainDocument(messageContent)) {
            DomibusConnectorBigDataReference docRefresRead = messageContent.getDocument().getDocument();
            LOGGER.trace("#loadAllBigFilesFromMessage: loading content document [{}]", docRefresRead);
            DomibusConnectorBigDataReference docActivatedRead = bigDataPersistenceServiceImpl.getReadableDataSource(docRefresRead);
            messageContent.getDocument().setDocument(docActivatedRead);
        }
        
        return message;
    }
        
    private boolean hasMainDocument(DomibusConnectorMessageContent content) {
        return content != null && content.getDocument() != null && content.getDocument().getDocument() != null;
    }
 

}
