package eu.domibus.connector.lib.spring.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

public class StoreConfigurationProperties {

    private final static Logger LOGGER = LoggerFactory.getLogger(StoreConfigurationProperties.class);

    /**
     * Path to the Key/Truststore
     */
    @Nonnull
    Resource path;

    /**
     * Password to open the Store
     */
    String password;

    public StoreConfigurationProperties() {}

    public StoreConfigurationProperties(Resource path, String password) {
        this.path = path;
        this.password = password;
    }

    public Resource getPath() {
        return path;
    }

    public void setPath(Resource path) {
        this.path = path;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public @Nullable
    String getPathUrlAsString() {
        try {
            if (path == null) {
                LOGGER.debug("#getPathUrlAsString: resolved to null");
                return null;
            }
            LOGGER.trace("#getPathUrlAsString: get url from [{}] to [{}]", path, path.getURL().toString());
            return path.getURL().toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
