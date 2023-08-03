package eu.domibus.connector.dss.configuration;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = BasicDssConfigurationProperties.PREFIX)
@Validated
public class BasicDssConfigurationProperties {

    public static final String PREFIX = "connector.dss";

    @NestedConfigurationProperty
    private ProxyProperties httpsProxy;

    @NestedConfigurationProperty
    private ProxyProperties httpProxy;

    private Map<String, @Valid Tsp> timeStampServers = new HashMap<>();

    private Map<String, @Valid TrustListSourceConfigurationProperties> trustListSources = new HashMap<>();

    @NotNull
    private Duration tlCacheExpiration = Duration.ofDays(1);

    @NotNull
    private Path tlCacheLocation = Paths.get("./tlcache");

    public ProxyProperties getHttpsProxy() {
        return httpsProxy;
    }

    public void setHttpsProxy(ProxyProperties httpsProxy) {
        this.httpsProxy = httpsProxy;
    }

    public ProxyProperties getHttpProxy() {
        return httpProxy;
    }

    public void setHttpProxy(ProxyProperties httpProxy) {
        this.httpProxy = httpProxy;
    }

    public Map<String, Tsp> getTimeStampServers() {
        return timeStampServers;
    }

    public void setTimeStampServers(Map<String, Tsp> timeStampServers) {
        this.timeStampServers = timeStampServers;
    }

    public Map<String, TrustListSourceConfigurationProperties> getTrustListSources() {
        return trustListSources;
    }

    public void setTrustListSources(Map<String, TrustListSourceConfigurationProperties> trustListSources) {
        this.trustListSources = trustListSources;
    }

    public Duration getTlCacheExpiration() {
        return tlCacheExpiration;
    }

    public void setTlCacheExpiration(Duration tlCacheExpiration) {
        this.tlCacheExpiration = tlCacheExpiration;
    }

    public Path getTlCacheLocation() {
        return tlCacheLocation;
    }

    public void setTlCacheLocation(Path tlCacheLocation) {
        this.tlCacheLocation = tlCacheLocation;
    }

    @Valid
    public static class Tsp {

        //@Pattern(regexp = "^(https|http):\\/\\/", message = "Only http or https urls are allowed")
        @NotBlank
        private String url;

        private String policyOid;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPolicyOid() {
            return policyOid;
        }

        public void setPolicyOid(String policyOid) {
            this.policyOid = policyOid;
        }
    }

    public static class ProxyProperties implements Serializable {

        private static final long serialVersionUID = 1570253159682776873L;

        /** The host to use */
        private String host;
        /** The port to use */
        private int port;
        /** The user to use */
        private String user;
        /** The password to use */
        private char[] password;
        /** The host connection scheme */
        private String scheme;
        /** Defines a list of hosts (URLs) to be excluded from the proxy configuration */
        private Collection<String> excludedHosts;

        /**
         * Default constructor with null values
         */
        public ProxyProperties() {
            // empty
        }

        /**
         * Returns the proxy host to use
         *
         * @return the proxy host
         */
        public String getHost() {
            return host;
        }

        /**
         * Set the proxy host
         *
         * @param host
         *            the host to use
         */
        public void setHost(String host) {
            this.host = host;
        }

        /**
         * Returns the port to use
         *
         * @return the proxy port
         */
        public int getPort() {
            return port;
        }

        /**
         * Set the proxy port
         *
         * @param port
         *            the port to use
         */
        public void setPort(int port) {
            this.port = port;
        }

        /**
         * Returns the user to use
         *
         * @return the proxy user
         */
        public String getUser() {
            return user;
        }

        /**
         * Set the proxy user
         *
         * @param user
         *            the user to use
         */
        public void setUser(String user) {
            this.user = user;
        }

        /**
         * Returns the password to use
         *
         * @return the proxy password
         */
        public char[] getPassword() {
            return password;
        }

        /**
         * Set the proxy password
         *
         * @param password
         *            the password to use
         * @deprecated since DSS 5.12. Use {@code #setPassword(char[] password)}
         */
        @Deprecated
        public void setPassword(String password) {
            this.password = password != null ? password.toCharArray() : null;
        }

        /**
         * Set the proxy password
         *
         * @param password
         *            the password to use
         */
        public void setPassword(char[] password) {
            this.password = password;
        }

        /**
         * Gets the host connection scheme
         *
         * @return {@link String}
         */
        public String getScheme() {
            return scheme;
        }

        /**
         * Sets the host connection scheme (e.g. "http", "https", etc.)
         *
         * @param scheme {@link String}
         */
        public void setScheme(String scheme) {
            this.scheme = scheme;
        }

        /**
         * Gets a collection of hosts to be excluded
         *
         * @return a collection of {@link String}s
         */
        public Collection<String> getExcludedHosts() {
            return excludedHosts;
        }

        /**
         * Sets a collection of hosts (URLs) to be excluded from the proxy configuration
         *
         * @param excludedHosts
         *            a collection of hosts URLs to exclude
         */
        public void setExcludedHosts(Collection<String> excludedHosts) {
            this.excludedHosts = excludedHosts;
        }

    }

}
