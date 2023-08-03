package eu.domibus.connector.lib.spring.configuration;

import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.core.style.ToStringCreator;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Configuration properties for referencing a
 *  key in a key store
 *   a alias and a optional password
 */
@Log4j2
public class KeyConfigurationProperties {

    public KeyConfigurationProperties() {}

    public KeyConfigurationProperties(String alias, String password) {
        this.alias = alias;
        this.password = password;
    }

    /**
     * The alias of the Certificate/Key
     */
    @ConfigurationLabel("Alias of certificate or key")
    @NotNull(message = "an alias must be provided!")
    @Size(min = 1, message = "Alias must have at least one character!")
    String alias;

    /**
     * The password of the Certificate/Key
     */
    @NotNull
    @ConfigurationLabel("Password of key")
    String password = "";

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public @Nullable String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String toString() {
        ToStringCreator append = new ToStringCreator(this)
                .append("alias", this.alias);
        if (log.getLevel().isMoreSpecificThan(Level.TRACE)) {
            append.append("password", "****");
        } else {
            append.append("password", this.password);
        }
        return append.toString();
    }

}
