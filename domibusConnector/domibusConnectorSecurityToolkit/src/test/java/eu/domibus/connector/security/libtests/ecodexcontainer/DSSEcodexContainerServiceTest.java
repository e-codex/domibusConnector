package eu.domibus.connector.security.libtests.ecodexcontainer;

import eu.domibus.connector.security.container.DomibusSecurityContainer;
import eu.ecodex.dss.model.BusinessContent;
import eu.ecodex.dss.model.CertificateStoreInfo;
import eu.ecodex.dss.model.ECodexContainer;
import eu.ecodex.dss.model.EnvironmentConfiguration;
import eu.ecodex.dss.model.ProxyData;
import eu.ecodex.dss.model.SignatureParameters;
import eu.ecodex.dss.model.token.AdvancedSystemType;
import eu.ecodex.dss.model.token.TokenIssuer;
import eu.ecodex.dss.service.ECodexLegalValidationService;
import eu.ecodex.dss.service.impl.dss.DSSECodexContainerService;
import eu.ecodex.dss.service.impl.dss.DSSECodexLegalValidationService;
import eu.ecodex.dss.service.impl.dss.DSSECodexTechnicalValidationService;
import eu.ecodex.dss.util.SignatureParametersFactory;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.EncryptionAlgorithm;
import eu.europa.esig.dss.InMemoryDocument;
import eu.europa.esig.dss.MimeType;
import eu.europa.esig.dss.client.http.proxy.ProxyConfig;
import eu.europa.esig.dss.client.http.proxy.ProxyProperties;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.StreamUtils;

/**
 * Exploration tests for DSSEcodexContainerService
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public class DSSEcodexContainerServiceTest {

    private Properties properties;
    
    @Before
    public void setUp() {
        this.properties = loadTestProperties();
    }
    
    
    private DSSECodexContainerService initContainerService() throws Exception {
        DSSECodexContainerService containerService = new DSSECodexContainerService();
        
        
        EnvironmentConfiguration environmentConfiguration = initEnvironmentConfiguration();
        
        
        ECodexLegalValidationService ecodexLegalValidationService = new DSSECodexLegalValidationService();
        ecodexLegalValidationService.setEnvironmentConfiguration(environmentConfiguration);             
        containerService.setLegalValidationService(ecodexLegalValidationService);
        
        
        DSSECodexTechnicalValidationService technicalValidationService = new DSSECodexTechnicalValidationService();
        technicalValidationService.setEnvironmentConfiguration(environmentConfiguration);
        technicalValidationService.setProxyPreferenceManager(initProxyConfig());
        
        technicalValidationService.initAuthenticationCertificateVerification();
        
        containerService.setTechnicalValidationService(technicalValidationService);
        

        CertificateStoreInfo certStore = new CertificateStoreInfo();
        certStore.setLocation("file:src/test/resources/keys/connector-keystore.jks");
        certStore.setPassword("connector");
        String keyAlias = "domibusConnector";
        String keyPassword = "connector";
        EncryptionAlgorithm encryptionAlgorithm = EncryptionAlgorithm.RSA;
        DigestAlgorithm digestAlgorithm = DigestAlgorithm.SHA1;    
        
        SignatureParameters signingParameters = SignatureParametersFactory.create(certStore, keyAlias, keyPassword, encryptionAlgorithm, digestAlgorithm);
        assertThat(signingParameters).isNotNull();
        containerService.setContainerSignatureParameters(signingParameters);
        
        
//        CertificateVerifier certificateVerifier = Mockito.mock(CertificateVerifier.class);        
//        containerService.setCertificateVerifier(certificateVerifier);
                
        return containerService;
    }
    
    private EnvironmentConfiguration initEnvironmentConfiguration() {
        EnvironmentConfiguration envConfig = new EnvironmentConfiguration();         
        envConfig.setProxyHTTPS(initHttpsProxyData());                
        envConfig.setProxyHTTP(initHttpProxyData());
        return envConfig;
    }
    
    //ecodex container proxy config
    private ProxyConfig initProxyConfig() {
        ProxyConfig proxyConfig = new ProxyConfig();
        
        ProxyProperties httpsProxyProperties = new ProxyProperties();
        httpsProxyProperties.setHost(properties.getProperty("https.proxy.host"));
        httpsProxyProperties.setPort(Integer.valueOf(properties.getProperty("https.proxy.port")));
        httpsProxyProperties.setPassword(properties.getProperty("https.proxy.password"));
        httpsProxyProperties.setUser(properties.getProperty("https.proxy.user"));
        proxyConfig.setHttpsProperties(httpsProxyProperties);
        
        ProxyProperties httpProxyProperties = new ProxyProperties();
        httpProxyProperties.setHost(properties.getProperty("http.proxy.host"));
        httpProxyProperties.setPort(Integer.valueOf(properties.getProperty("http.proxy.port")));
        httpProxyProperties.setPassword(properties.getProperty("http.proxy.password"));
        httpProxyProperties.setUser(properties.getProperty("http.proxy.user"));
        proxyConfig.setHttpProperties(httpProxyProperties);
        
        return proxyConfig;
    }
    
    private ProxyData initHttpsProxyData() {
        ProxyData httpProxy = new ProxyData();
        httpProxy.setPort(Integer.valueOf(properties.getProperty("https.proxy.port")));
        httpProxy.setHost(properties.getProperty("https.proxy.host"));
        httpProxy.setAuthName(properties.getProperty("https.proxy.user"));
        httpProxy.setAuthPass(properties.getProperty("https.proxy.password"));
        return httpProxy;
    }
        
    private ProxyData initHttpProxyData() {
        ProxyData httpProxy = new ProxyData();
        httpProxy.setPort(Integer.valueOf(properties.getProperty("http.proxy.port")));
        httpProxy.setHost(properties.getProperty("http.proxy.host"));
        httpProxy.setAuthName(properties.getProperty("http.proxy.user"));
        httpProxy.setAuthPass(properties.getProperty("http.proxy.password"));
        return httpProxy;
    }
    
    private Properties loadTestProperties() {
        try {
            Properties loadedTestProperties = new Properties();
            InputStream testPropertiesInputStream = this.getClass().getResourceAsStream("/test.properties");
            if (testPropertiesInputStream == null) {
                throw new RuntimeException("test.properties could not be load as resource!");
            }        
            loadedTestProperties.load(testPropertiesInputStream);
            return loadedTestProperties;
        } catch (IOException ioe) {
            throw new RuntimeException("test.properties could not be read!");
        }
    }
    
    
    @Test
    public void testCreateBigContainer() throws Exception {
        
    }
    
    
    @Test
    public void testCreateContainer() throws Exception {
        DSSECodexContainerService containerService = initContainerService(); 
                        
        BusinessContent businessContent = new BusinessContent();
        
        DSSDocument xmlDocument = new InMemoryDocument(
            loadByteArrayFromClassPathRessource("/examples/Form_A.xml"),
            DomibusSecurityContainer.CONTENT_XML_IDENTIFIER + ".xml", 
            MimeType.PDF);        
   
        businessContent.setDocument(xmlDocument);
        
        
        DSSDocument formAPdf = new InMemoryDocument(
                            loadByteArrayFromClassPathRessource("/examples/Form_A.pdf"),
                            DomibusSecurityContainer.MAIN_DOCUMENT_NAME + ".pdf", 
                            MimeType.PDF);        
        businessContent.addAttachment(formAPdf);
        
        //big dss document (4GB random bytes)
//        DSSDocument doc = new MyBigDssFile();
//        businessContent.addAttachment(doc);
        
        DSSDocument attachmentOne = new InMemoryDocument(
                loadByteArrayFromClassPathRessource("/examples/supercool.pdf"),
                "supercool.pdf",
                MimeType.PDF);
        
        businessContent.addAttachment(attachmentOne);
        
        TokenIssuer tokenIssuer = createTokenIssuer();
        
        ECodexContainer container = containerService.create(businessContent, tokenIssuer);
        DSSDocument asicDocument = container.getAsicDocument();
        assertThat(asicDocument).isNotNull();
        
    }
    
    private TokenIssuer createTokenIssuer() {
        TokenIssuer tokenIssuer = new TokenIssuer();
        tokenIssuer.setAdvancedElectronicSystem(AdvancedSystemType.SIGNATURE_BASED);
        tokenIssuer.setCountry("AT");
        tokenIssuer.setServiceProvider("BRZ");
        return tokenIssuer;
    }
    
    private byte[] loadByteArrayFromClassPathRessource(String ressource) {
        try {
            InputStream inputStream = getClass().getResourceAsStream(ressource);
            return StreamUtils.copyToByteArray(inputStream);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } 
    }
    

}
