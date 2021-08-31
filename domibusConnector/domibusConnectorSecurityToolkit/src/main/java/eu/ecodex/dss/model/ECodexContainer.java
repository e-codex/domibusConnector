/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/ECodexContainer.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */
package eu.ecodex.dss.model;

import eu.ecodex.dss.model.token.Token;
import eu.europa.esig.dss.model.DSSDocument;

import java.util.List;

/**
 * this holds the to be signed content plus the created asic document 
 * 
 * <p>DISCLAIMER: Project owner e-CODEX</p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
public class ECodexContainer {

    /**
     * the documents that are stored within the container
     */
    private BusinessContent businessContent;

    /**
     * the token about the trust as object structure
     */
    private Token token;

    /**
     * the signed XML representation of the {@link #token}
     */
    private DSSDocument tokenXML;
    
    /**
     * the signed PDF representation of the {@link #token}
     */
    private DSSDocument tokenPDF;
    
    /**
     * the generated ASiC-S "file"
     */
    private DSSDocument asicDocument;

    /**
     * the documents that are stored within the container
     * 
     * @return the value
     */
    public BusinessContent getBusinessContent() {
        return businessContent;
    }

    /**
     * convenience method to give access to the main document of the (signed) business content
     * 
     * @return the value
     */
    public DSSDocument getBusinessDocument() {
        return (businessContent == null) ? null : businessContent.getDocument();
    }
    
    /**
     * convenience method to give access to the detached signature of the main document of the (signed) business content
     *
     * @return the value
     */
    public DSSDocument getBusinessSignature() {
        return (businessContent == null) ? null : businessContent.getDetachedSignature();
    }

    /**
     * convenience method to give access to the attachments of the (signed) business content
     * 
     * @return the value
     */
    public List<DSSDocument> getBusinessAttachments() {
        return (businessContent == null) ? null : businessContent.getAttachments();
    }
    
    /**
     * the documents that are stored within the container
     * 
     * @param businessContent the value
     * @return this class' instance for chaining
     */
    public ECodexContainer setBusinessContent(final BusinessContent businessContent) {
        this.businessContent = businessContent;
        return this;
    }

    /**
     * the token about the trust as object structure
     * 
     * @return the value
     */
    public Token getToken() {
        return token;
    }

    /**
     * the token about the trust as object structure
     * 
     * @param token the value
     * @return this class' instance for chaining
     */
    public ECodexContainer setToken(final Token token) {
        this.token = token;
        return this;
    }

    /**
     * the signed XML representation of the {@link #token}
     * 
     * @return the value
     */
    public DSSDocument getTokenXML() {
        return tokenXML;
    }

    /**
     * the signed XML representation of the {@link #token}
     *
     * @param tokenXML the value
     * @return this class' instance for chaining
     */
    public ECodexContainer setTokenXML(final DSSDocument tokenXML) {
        this.tokenXML = tokenXML;
        return this;
    }

    /**
     * the signed PDF representation of the {@link #token}
     * 
     * @return the value
     */
    public DSSDocument getTokenPDF() {
        return tokenPDF;
    }

    /**
     * the signed PDF representation of the {@link #token}
     *
     * @param tokenPDF the value
     * @return this class' instance for chaining
     */
    public ECodexContainer setTokenPDF(final DSSDocument tokenPDF) {
        this.tokenPDF = tokenPDF;
        return this;
    }

    /**
     * the generated ASiC-S "file"
     * 
     * @return the value
     */
    public DSSDocument getAsicDocument() {
        return asicDocument;
    }

    /**
     * the generated ASiC-S "file"
     *
     * @param asicDocument the value
     * @return this class' instance for chaining
     */
    public ECodexContainer setAsicDocument(final DSSDocument asicDocument) {
        this.asicDocument = asicDocument;
        return this;
    }
}
