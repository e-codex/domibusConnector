package eu.domibus.connector.persistence.spring;

import eu.ecodex.dc5.util.repository.DC5ConfigItemDao;
import eu.ecodex.dc5.util.model.DC5ConfigItem;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DatabaseResourceLoader {

    public static final String DB_URL_PREFIX = "dbkeystore:";

    private final DC5ConfigItemDao keystoreDao;

    public DatabaseResourceLoader(
            DC5ConfigItemDao keystoreDao) {
        this.keystoreDao = keystoreDao;
    }

    public Resource getResource(String location) {
        if (location.startsWith(DB_URL_PREFIX)) {
//            DomibusConnectorKeystoreDao databaseResourceDao =
//                    this.keystoreDao.getBean(DomibusConnectorKeystoreDao.class);
            String resourceName = location.substring(DB_URL_PREFIX.length());
            Optional<DC5ConfigItem> byUuid = keystoreDao.findByUuid(resourceName);
            if (byUuid.isPresent()) {
                return new DatabaseResource(byUuid.get().getKeystore(), "Database Resource: [" + resourceName + "]", location);
            }
        }
        return null;
    }

    public static class DatabaseResource extends ByteArrayResource {

        private final String resourceString;

        private DatabaseResource(byte[] byteArray, String description, String resourceString) {
            super(byteArray, description);
            this.resourceString = resourceString;
        }

        public String getResourceString() {
            return resourceString;
        }

    }

}
