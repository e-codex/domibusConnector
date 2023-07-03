package eu.ecodex.dc5.payload;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import java.nio.file.Path;
import java.nio.file.Paths;


@ConfigurationProperties(prefix = "connector.persistence.filesystem")
@Validated
public class DomibusConnectorFilesystemPersistenceProperties {


    /**
     * Property to configure the storage location on filesystem
     *  under this folder the LargeFilePersistenceServiceFilesystemImpl
     *  is managing the data
     */
    @NotNull
    private Path storagePath = Paths.get("./data/fsstorage");

    /**
     * Should the written files be encrypted?
     * Default is yes
     */
    private boolean encryptionActive = true;

    /**
     * Should the directory be created if it does not exist?
     */
    private boolean createDir = true;

    public Path getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(Path storagePath) {
        this.storagePath = storagePath;
    }

    public boolean isEncryptionActive() {
        return encryptionActive;
    }

    public void setEncryptionActive(boolean encryptionActive) {
        this.encryptionActive = encryptionActive;
    }

    public boolean isCreateDir() {
        return createDir;
    }

    public void setCreateDir(boolean createDir) {
        this.createDir = createDir;
    }
}
