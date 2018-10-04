package eu.domibus.connector.security.container;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import eu.domibus.connector.security.container.service.ECodexContainerFactoryService;
import eu.domibus.connector.security.container.service.TokenIssuerFactory;
import eu.domibus.connector.security.spring.SecurityToolkitConfigurationProperties;
import eu.domibus.connector.tools.logging.LoggingMarker;
import eu.ecodex.dss.model.token.LegalTrustLevel;
import eu.ecodex.dss.model.token.Token;
import eu.ecodex.dss.service.ECodexContainerService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import eu.domibus.connector.domain.model.DetachedSignatureMimeType;
import eu.domibus.connector.domain.model.DomibusConnectorBigDataReference;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageAttachment;
import eu.domibus.connector.domain.model.DomibusConnectorMessageContent;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDocument;
import eu.domibus.connector.domain.model.builder.DetachedSignatureBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageDocumentBuilder;
import eu.domibus.connector.persistence.service.DomibusConnectorBigDataPersistenceService;
import eu.domibus.connector.security.exception.DomibusConnectorSecurityException;
import eu.ecodex.dss.model.BusinessContent;
import eu.ecodex.dss.model.ECodexContainer;
import eu.ecodex.dss.model.SignatureParameters;
import eu.ecodex.dss.model.checks.CheckProblem;
import eu.ecodex.dss.model.checks.CheckResult;
import eu.ecodex.dss.model.token.AdvancedSystemType;
import eu.ecodex.dss.model.token.TokenIssuer;
import eu.ecodex.dss.service.ECodexException;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.InMemoryDocument;
import eu.europa.esig.dss.MimeType;
import java.io.OutputStream;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

/**
 * TODO: documentation needed
 * 
 */
@Component("domibusConnectorSecurityContainer")
public class DomibusSecurityContainer {

    public static final String RED_TOKEN_WARNING_MESSAGE = "A RedToken was generated!";

    public static final String MAIN_DOCUMENT_NAME = "mainDocument";
    
    public static final String ASICS_CONTAINER_IDENTIFIER = "ASIC-S";

    public static final String TOKEN_XML_IDENTIFIER = "tokenXML";

    public static final String TOKEN_PDF_IDENTIFIER = "tokenPDF";

    public static final String CONTENT_PDF_IDENTIFIER = "ContentPDF";

    public static final String CONTENT_XML_IDENTIFIER = "ContentXML";
    
    private static final String TOKEN_XML_FILE_NAME = "Token.xml";

    private static final String TOKEN_PDF_FILE_NAME = "Token.pdf";

    private static final String DETACHED_SIGNATURE_DOCUMENT_NAME = "detachedSignature";

    private static final Logger LOGGER = LoggerFactory.getLogger(DomibusSecurityContainer.class);

    @Autowired
    ECodexContainerFactoryService eCodexContainerFactoryService;
            
    @Autowired
    DomibusConnectorBigDataPersistenceService bigDataPersistenceService;

    @Autowired
    TokenIssuerFactory tokenIssuerFactory;

    BusinessContent buildBusinessContent(@Nonnull DomibusConnectorMessage message) {
        if (message.getMessageContent() == null) {
            throw new IllegalArgumentException("messageContent is null!");
        }
        @Nonnull DomibusConnectorMessageContent messageContent = message.getMessageContent();
        
        BusinessContent businessContent = new BusinessContent();

        DSSDocument dssDocument = null;

        
        
        if (messageContent.getDocument() != null) {
            //
            DomibusConnectorMessageDocument document = messageContent.getDocument();
            
            
            //we are still assuming that the bussiness document is always a pdf!
            String pdfName = StringUtils.isEmpty(document.getDocumentName()) ? MAIN_DOCUMENT_NAME
                    + ".pdf"
                    : messageContent.getDocument().getDocumentName();            
            dssDocument = createInMemoryDocument(document.getDocument(), pdfName, MimeType.PDF);
        
        // message action does not require a document, make xml to main document
        } else if (!message.getMessageDetails().getAction().isDocumentRequired() && message.getMessageContent().getXmlContent() != null) {
            byte[] content = message.getMessageContent().getXmlContent();
            dssDocument = new InMemoryDocument(content, MAIN_DOCUMENT_NAME + ".xml", MimeType.XML);
        } else {            
            LOGGER.error("No content found for container!");
            throw new RuntimeException("not valid without document!");
        }
            
        businessContent.setDocument(dssDocument);

        DomibusConnectorMessageDocument msgDocument = messageContent.getDocument();
        
        if (msgDocument != null && 
                msgDocument.getDetachedSignature() != null
                && msgDocument.getDetachedSignature().getMimeType() != null) {
            
            String detachedSignatureName = msgDocument.getDetachedSignature().getDetachedSignatureName() != null ? msgDocument
                    .getDetachedSignature().getDetachedSignatureName() : DETACHED_SIGNATURE_DOCUMENT_NAME;
            
                    
            
                    DSSDocument detachedSignature = new InMemoryDocument(
                            msgDocument.getDetachedSignature().getDetachedSignature(),
                            detachedSignatureName,
                            MimeType.fromMimeTypeString(msgDocument.getDetachedSignature().getMimeType().getCode()));
            businessContent.setDetachedSignature(detachedSignature);
        }

        for (DomibusConnectorMessageAttachment attachment : message.getMessageAttachments()) {
            
            
            MimeType mimeType = MimeType.fromMimeTypeString(attachment.getMimeType());
            LOGGER.debug("buildBusinessContent: detected mimeType [{}] in attachment [{}]", mimeType.getMimeTypeString(), attachment);
            
            DSSDocument dssInMemoryDoc = createInMemoryDocument(attachment.getAttachment(), attachment.getName(), mimeType);
            
            businessContent.addAttachment(dssInMemoryDoc);
        }

        return businessContent;
    }

    
    /**
     * Takes the messageContent (xmlDocument + document)
     * and all messageAttachments and wraps them into a asic container
     * the generated token xml and the asic container
     * are attached as messageAttachments again
     * all other attachments are removed from the message!
     * 
     * the messageContent of the message must not be null!
     * 
     * @param message the message to process
     * @return - the processed message (same object as passed by param message)
     */
    public DomibusConnectorMessage createContainer(@Nonnull DomibusConnectorMessage message) {

        ECodexContainerService containerService = eCodexContainerFactoryService.createECodexContainerService(message);

        try {
            LOGGER.trace("createContainer: for message [{}]", message);
            BusinessContent businessContent = buildBusinessContent(message);

            message.getMessageAttachments().clear();
        
            ECodexContainer container = containerService.create(businessContent, tokenIssuerFactory.getTokenIssuer(message));


            Token token = container.getToken();
            if (LegalTrustLevel.NOT_SUCCESSFUL.equals(token.getValidation().getLegalResult().getTrustLevel())) {
                LOGGER.warn(LoggingMarker.BUSINESS_CERT_LOG, "a RedToken was generated!");
            }


            // KlarA: Added check of the container and the respective
            // error-handling
            CheckResult results = containerService.check(container);



            if (results.isSuccessful()) {
                if (container != null) {                    
                	DSSDocument asicDocument = container.getAsicDocument();
                    if (asicDocument != null) {
                        LOGGER.trace("converting asicDocument [{}] to asic message attachment and appending it to message", asicDocument);                        
                        DomibusConnectorMessageAttachment asicAttachment = convertDocumentToMessageAttachment(message, asicDocument, ASICS_CONTAINER_IDENTIFIER);
                        message.addAttachment(asicAttachment);

                    }
                    DSSDocument tokenXML = container.getTokenXML();
                    
                    if (tokenXML != null) {
                        LOGGER.trace("converting tokenXml {[{}] to message attachment and appending it to message", tokenXML);
                        tokenXML.setName(TOKEN_XML_FILE_NAME);
                        tokenXML.setMimeType(MimeType.XML);
                        DomibusConnectorMessageAttachment tokenAttachment = convertDocumentToMessageAttachment(message, tokenXML, TOKEN_XML_IDENTIFIER);
                        message.addAttachment(tokenAttachment);
                    }
                }
                return message;
            } else {
                String errormessage = "\nSeveral problems prevented the container from being created:";
                List<CheckProblem> problems = results.getProblems();
                for (CheckProblem curProblem : problems) {
                    errormessage += "\n-" + curProblem.getMessage();
                }
                throw new DomibusConnectorSecurityException(errormessage);
            }            
        } catch (ECodexException e) {
            throw new DomibusConnectorSecurityException("ECodex exception occured while creating container", e);
        } catch (IOException ioe) {
            throw new DomibusConnectorSecurityException("IOException occured while creating container", ioe);
        }
    }

    /**
     * Unpacks the asic container from the message
     * and puts the businessDocument, xmlContent into MessageContent
     * and other attachments are added to the messageAttachments
     * the asicAttachment and xmlToken attachment are removed from the message
     * @param message - the message to process
     */
    public void recieveContainerContents(DomibusConnectorMessage message) {
        ECodexContainerService containerService = eCodexContainerFactoryService.createECodexContainerService(message);


        if (message.getMessageAttachments() != null && !message.getMessageAttachments().isEmpty()) {
            DomibusConnectorMessageAttachment asicsAttachment = null;
            DomibusConnectorMessageAttachment tokenXMLAttachment = null;
            for (DomibusConnectorMessageAttachment attachment : message.getMessageAttachments()) {
            	
                if (attachment.getIdentifier().equals(ASICS_CONTAINER_IDENTIFIER) || attachment.getIdentifier().endsWith(".asics")) {
                    asicsAttachment = attachment;
                } else if (attachment.getIdentifier().equals(TOKEN_XML_IDENTIFIER) || attachment.getIdentifier().equals(TOKEN_XML_FILE_NAME)) {
                    tokenXMLAttachment = attachment;
                }
            }
            if (asicsAttachment == null) {
                throw new DomibusConnectorSecurityException("Could not find ASICS container in message attachments!");
            }
            if (tokenXMLAttachment == null) {
                throw new DomibusConnectorSecurityException("Could not find token XML in message attachments!");
            }
            message.getMessageAttachments().remove(asicsAttachment);
            message.getMessageAttachments().remove(tokenXMLAttachment);

            
            DomibusConnectorBigDataReference asicContainerDataRef = bigDataPersistenceService.getReadableDataSource(asicsAttachment.getAttachment());
            InputStream asicInputStream;
            try {                
                asicInputStream = asicContainerDataRef.getInputStream();
            } catch (IOException ioe) {                
                throw new RuntimeException(String.format("error while initializing asicInputStream from big data reference %s", 
                        asicContainerDataRef.getStorageIdReference()));
            }
            
            DomibusConnectorBigDataReference xmlTokenDataRef = bigDataPersistenceService.getReadableDataSource(tokenXMLAttachment.getAttachment());
            InputStream tokenStream; /// = xmlTokenDataRef.getInputStream();
            try {
                tokenStream = xmlTokenDataRef.getInputStream();
            } catch (IOException ioe) {
                throw new RuntimeException(String.format("error while initializing xmlTokenDataRef get input stream from %s",
                        xmlTokenDataRef.getStorageIdReference()));
            }


            try {
                if (LOGGER.isTraceEnabled()) {
                    asicInputStream = logTraceStream(asicInputStream, "asicInputStream", false);
                    tokenStream = logTraceStream(tokenStream, "tokenStream", true);
                }


                ECodexContainer container = containerService.receive(asicInputStream, tokenStream);

                // KlarA: Added check of the container and the respective
                // error-handling
                CheckResult results = containerService.check(container);

                DomibusConnectorMessageDocumentBuilder documentBuilder = DomibusConnectorMessageDocumentBuilder.createBuilder();
                DetachedSignatureBuilder detachedSignatureBuilder = DetachedSignatureBuilder.createBuilder();
                if (results.isSuccessful()) {
                    if (container != null) {
                        
                        LOGGER.trace("recieveContainerContents: check if businessContent contains detachedSignature [{}]", 
                                container.getBusinessContent().getDetachedSignature() != null);
                        if (container.getBusinessContent().getDetachedSignature() != null) {
                            try {
                                InputStream is = container.getBusinessContent().getDetachedSignature().openStream();
                                byte[] docAsBytes = new byte[is.available()];
                                is.read(docAsBytes);                                
                                detachedSignatureBuilder.setSignature(docAsBytes);
                                LOGGER.trace("recieveContainerContents: Writing detachedSignature [{}]", IOUtils.toString(docAsBytes, "UTF8"));
                            } catch (IOException e) {
                                throw new DomibusConnectorSecurityException("Could not read detached signature!");
                            }
                            if (!StringUtils.isEmpty(container.getBusinessContent().getDetachedSignature().getName())) {                                
                                detachedSignatureBuilder.setName(container.getBusinessContent().getDetachedSignature().getName());
                                LOGGER.trace("recieveContainerContents: detachedSignature has name [{}]", container.getBusinessContent().getDetachedSignature().getName());
                            }
                            try {
                                LOGGER.trace("recieveContainerContents: detachedSignature has mimeType [{}]", 
                                        container.getBusinessContent().getDetachedSignature().getMimeType().getMimeTypeString());
                                
                                detachedSignatureBuilder.setMimeType(DetachedSignatureMimeType.valueOf(container.getBusinessContent()
                                                .getDetachedSignature().getMimeType().getMimeTypeString()));
                                
                            } catch (IllegalArgumentException e) {
                                LOGGER.error("recieveContainerContents: No DetachedSignatureMimeType could be resolved of MimeType [{}], using default MimeType [{}]", 
                                        container.getBusinessContent().getDetachedSignature().getMimeType().getMimeTypeString(),
                                        DetachedSignatureMimeType.BINARY.getCode());
                                detachedSignatureBuilder.setMimeType(DetachedSignatureMimeType.BINARY);
                            }
                            //set detached signature                            
                            documentBuilder.withDetachedSignature(detachedSignatureBuilder.build());
                        }
                        

                        if (container.getBusinessDocument() != null) {
                            LOGGER.debug("The business document received from the container is of Mime Type {}",
                                    container.getBusinessDocument().getMimeType().getMimeTypeString());
                            try {
                            	
                                //LOGGER.trace("recieveContainerContents: Read following byte content [{}]", IOUtils.toString(docAsBytes, "UTF8"));
                                DomibusConnectorBigDataReference bigDataRef = this.bigDataPersistenceService.createDomibusConnectorBigDataReference(container.getBusinessDocument().openStream(), message.getConnectorMessageId(), container.getBusinessDocument().getName(), container.getBusinessDocument().getMimeType().getMimeTypeString());
                                
                                LOGGER.trace("copying businessDocument input stream to bigDataReference output Stream");
                                try (InputStream inputStream = container.getBusinessDocument().openStream();
                                OutputStream outputStream = bigDataRef.getOutputStream() ) {
                                    StreamUtils.copy(inputStream, outputStream);
                                } catch (IOException ioe) {
                                    throw new DomibusConnectorSecurityException("Could not read business document!", ioe);
                                }
                                
                                documentBuilder.setContent(bigDataRef);
                                
                                LOGGER.trace("recieveContainerContents: check if MimeType.PDF [{}] equals to [{}]", 
                                        MimeType.PDF.getMimeTypeString(), container.getToken().getDocumentType());
                                                                
                                if (MimeType.PDF.getMimeTypeString().equals(container.getToken().getDocumentType())) {                                                                        
                                    String docName = MAIN_DOCUMENT_NAME;
                                    if (!StringUtils.isEmpty(container.getBusinessDocument().getName())) {
                                        docName = container.getBusinessDocument().getName();
                                    }
                                    documentBuilder.setName(docName);
                                    message.getMessageContent().setDocument(documentBuilder.build());
                                }
                                
                                LOGGER.trace("recieveContainerContents: check if MimeType.XML [{}] equals to [{}]", 
                                        MimeType.XML.getMimeTypeString(), container.getToken().getDocumentType());
                                
                                if (MimeType.XML.getMimeTypeString().equals(container.getToken().getDocumentType())
                                        && !message.getMessageDetails().getAction().isDocumentRequired()) {
                                    LOGGER.trace("recieveContainerContents: Writing byteContent into MessageContent.setXmlContent");
                                    InputStream businessContent = container.getBusinessDocument().openStream();
                                    message.getMessageContent().setXmlContent(IOUtils.toByteArray(businessContent));
                                }
                            } catch (IOException e) {
                                throw new DomibusConnectorSecurityException("Could not read business document!");
                            }

                        } else {
                            LOGGER.debug("The business document received from the container is null!");
                        }
                        
                        

                        if (container.getBusinessAttachments() != null && !container.getBusinessAttachments().isEmpty()) {
                            for (DSSDocument businessAttachment : container.getBusinessAttachments()) {
                                try {
                                    DomibusConnectorMessageAttachment attachment = convertDocumentToMessageAttachment(
                                            message, 
                                            businessAttachment, 
                                            businessAttachment.getName());

                                    message.addAttachment(attachment);
                                } catch (IOException e) {
                                    LOGGER.error("Could not read attachment!", e);
                                    continue;
                                }
                            }
                        }
                        DSSDocument tokenPDF = container.getTokenPDF();
                        if (tokenPDF != null) {
                            try {
                                tokenPDF.setMimeType(MimeType.PDF);
                                tokenPDF.setName(TOKEN_PDF_FILE_NAME);
                                DomibusConnectorMessageAttachment attachment = convertDocumentToMessageAttachment(message, tokenPDF, TOKEN_PDF_IDENTIFIER);
                                message.addAttachment(attachment);
                            } catch (IOException e) {
                                LOGGER.error("Could not read Token PDF!", e);
                            }
                        }

                        DSSDocument tokenXML = container.getTokenXML();
                        if (tokenXML != null) {
                            try {
                                tokenXML.setMimeType(MimeType.XML);
                                tokenXML.setName(TOKEN_XML_FILE_NAME);
                                DomibusConnectorMessageAttachment attachment = convertDocumentToMessageAttachment(message, tokenXML, TOKEN_XML_IDENTIFIER); 
                                message.addAttachment(attachment);
                            } catch (IOException e) {
                                LOGGER.error("Could not read Token XML!", e);
                            }
                        }
                    } else {
                        throw new DomibusConnectorSecurityException("The resolved business container is null!");
                    }
                } else {
                    String errormessage = "\nSeveral problems prevented the container from being created:";
                    List<CheckProblem> problems = results.getProblems();
                    for (CheckProblem curProblem : problems) {
                        errormessage += "\n-" + curProblem.getMessage();
                    }
                    throw new DomibusConnectorSecurityException(errormessage);
                }
            } catch (ECodexException e) {
                throw new DomibusConnectorSecurityException(e);
            }
        }
    }

    private InputStream logTraceStream(InputStream inputStream, String name, boolean logStreamContent) {
        LOGGER.debug("#logTraceStream [{}]: into byte[]", name);
        try {
            byte[] byteArray = StreamUtils.copyToByteArray(inputStream);
            LOGGER.debug("#logTraceStream [{}]: read [{}] bytes", name, byteArray.length);
            if (logStreamContent) {
                LOGGER.debug("#logTraceStream [{}]: content: [{}]", name, new String(byteArray));
            }
            return new ByteArrayInputStream(byteArray);
        } catch (IOException ioe) {
            LOGGER.debug("#logTraceStream [{}]: Error while reading from stream!", name);
            throw new RuntimeException(ioe);
        }
    }


    private DomibusConnectorMessageAttachment convertDocumentToMessageAttachment(DomibusConnectorMessage message, DSSDocument document, String identifier) //, String name, String mimeType)
            throws IOException {
        LOGGER.trace("convertDocumentToMessageAttachment: called with message [{}], document [{}], identifier [{}]", message, document, identifier);
        
        DomibusConnectorBigDataReference bigDataRef = bigDataPersistenceService.createDomibusConnectorBigDataReference(document.openStream(), message.getConnectorMessageId(), document.getName(), document.getMimeType().getMimeTypeString());
        
        String documentName = document.getName();
        String mimeTypeString = MimeType.BINARY.getMimeTypeString();
        if (document.getMimeType() != null) {
            mimeTypeString = document.getMimeType().getMimeTypeString();
        }
        
        bigDataRef.setName(documentName);
        bigDataRef.setMimetype(mimeTypeString);
        
//        LOGGER.trace("copied: [{}]", IOUtils.toString(document.openStream()));
//        
        LOGGER.debug("Copy input stream from dss document to output stream of big data reference");
        try (InputStream inputStream = document.openStream();
            OutputStream outputStream = bigDataRef.getOutputStream()) {
            int bytesCopied = StreamUtils.copy(inputStream, outputStream);
            if (bytesCopied == 0) {
                throw new DomibusConnectorSecurityException("Cannot create attachment with empty content!");
                //TODO: delete bigDataRef from database!
            }
        } catch (IOException ioe) {
            throw new DomibusConnectorSecurityException("Error while writing attachment to storage!", ioe);
        }



    	if(StringUtils.isEmpty(identifier)){
    		throw new DomibusConnectorSecurityException("Cannot create attachment without identifier!");
    	}
    	
        DomibusConnectorMessageAttachment attachment = new DomibusConnectorMessageAttachment(bigDataRef, identifier);
        
		attachment.setName(documentName);
        attachment.setMimeType(mimeTypeString);

        LOGGER.trace("attachment created with bigDataRef [{}], and identifier [{}], name [{}], mimeTypeString [{}]", bigDataRef, identifier, documentName, mimeTypeString);
        
        return attachment;
    }

    
    /**
     * in later versions this method should return a streaming based DSSDocument
     * so no conversion to an byte[] is necessary
     * @param dataRef - the reference to the data, the reference id must be set!
     * @param name name of the dssDocument
     * @param mimeType mimeType of the dssDocument
     * @return the created InMemoryDocument
     */
    DSSDocument createInMemoryDocument(DomibusConnectorBigDataReference dataRef, String name, MimeType mimeType) {
        try {
            DomibusConnectorBigDataReference readableDataSource = bigDataPersistenceService.getReadableDataSource(dataRef);
            InputStream inputStream = readableDataSource.getInputStream();
            byte[] content = StreamUtils.copyToByteArray(inputStream);
            InMemoryDocument dssDocument = new InMemoryDocument(content, name, MimeType.PDF);
            return dssDocument;
        } catch (IOException ioe) {
            throw new RuntimeException("error while loading data from bigDataPersistenceService", ioe);
        }
    }
    
    
}
