package eu.domibus.connector.lib.spring.configuration;

import javax.net.ssl.SSLContext;
import java.util.ArrayList;
import java.util.List;

public class TLSConnectionProperties extends KeyAndKeyStoreAndTrustStoreConfigurationProperties {

    /**
     * Minimum TLS version for this
     * connection eg. TLSv1, TLSv1.1, TLSv1.2, TLSv1.3
     *  must be supported by java:
     *  also see {@link SSLContext}
     */
    private String minTls;

    /**
     * A list of TLS proxy servers
     *  note: currently only the FIRST
     *  proxy server will be used!
     */
    private List<String> proxy;

    public String getMinTls() {
        return minTls;
    }

    public void setMinTls(String minTls) {
        this.minTls = minTls;
    }

    public List<String> getProxy() {
        return proxy;
    }

    public void setProxy(List<String> proxy) {
        this.proxy = proxy;
    }
}
