package eu.domibus.connector.firststartup;

import eu.ecodex.dc5.util.repository.DC5UserDao;
import eu.ecodex.dc5.util.repository.DC5UserPasswordDao;
import eu.ecodex.dc5.util.model.DC5User;
import eu.ecodex.dc5.util.model.DC5UserPassword;
import eu.domibus.connector.persistence.model.enums.UserRole;
import eu.domibus.connector.tools.logging.LoggingMarker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.transaction.Transactional;
import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * This bean will be run on first startup (for details see FirstStartupCondition)
 *
 *  It requires a already initialized database
 *  It creates an initial admin user with a random password. Password
 *  will be logged to console
 *
 *
 *
 */
@Configuration
@ConditionalOnProperty(prefix = InitializeAdminUserProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(InitializeAdminUserProperties.class)
public class InitializeAdminUser {

    private static final Logger LOGGER = LogManager.getLogger(InitializeAdminUser.class);

    private final InitializeAdminUserProperties initializeAdminUserProperties;

    //instead of the DAOs the service (WebUserPersistenceService) should be used here.
    //But this requires some refactoring, because the service is deeply integrated in
    //the webLib Module
    private final DC5UserDao userDao;
    private final DC5UserPasswordDao userPasswordDao;


    public InitializeAdminUser(InitializeAdminUserProperties initializeAdminUserProperties,
                               DC5UserDao userDao,
                               DC5UserPasswordDao userPasswordDao) {
        this.initializeAdminUserProperties = initializeAdminUserProperties;
        this.userDao = userDao;
        this.userPasswordDao = userPasswordDao;
    }

    /**
     * check if the admin user exists in DB
     *  if not - create it
     */
    @EventListener
    @Transactional
    public void checkAdminUser(ContextRefreshedEvent contextRefreshedEvent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String adminUserName = initializeAdminUserProperties.getInitialUserName();

        DC5User adminUser = userDao.findOneByUsernameIgnoreCase(adminUserName);
        if (adminUser != null) {
            LOGGER.info(LoggingMarker.Log4jMarker.CONFIG, "Admin user [{}] already exists in DB", adminUser.getUsername());
            return;
        }
        String newUserPassword = initializeAdminUserProperties.getInitialUserPassword();

        DC5User newAdminUser = new DC5User();
        newAdminUser.setUsername(adminUserName);
        newAdminUser.setLocked(false);
        newAdminUser.setRole(UserRole.ADMIN);

        String salt = getHexSalt();

        String dbPassword = generatePasswordHashWithSaltOnlyPW(newUserPassword, salt);

        DC5UserPassword userPassword = new DC5UserPassword();
        userPassword.setInitialPassword(initializeAdminUserProperties.isInitialChangeRequired());
        userPassword.setCurrentPassword(true);
        userPassword.setPassword(dbPassword);
        userPassword.setSalt(salt);
        userPassword.setUser(newAdminUser);

        newAdminUser.getPasswords().add(userPassword);

        if (initializeAdminUserProperties.isLogInitialToConsole()) {
            LOGGER.info(LoggingMarker.Log4jMarker.CONFIG,
                    "\n###############################" +
                            "\nSuccessfully created initial admin user [{}] with pw [{}]" +
                            "\n###############################\n", adminUserName, newUserPassword);
        }

        userDao.save(newAdminUser);
        userPasswordDao.save(userPassword);

    }


    //should be done by user service!
    private static String generatePasswordHashWithSaltOnlyPW(String password, String saltParam)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = DatatypeConverter.parseHexBinary(saltParam);
        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return toHex(hash);
    }

    //should be done by user service!
    private static String getHexSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return toHex(salt);
    }

    //should be done by user service!
    private static String toHex(byte[] array) throws NoSuchAlgorithmException {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

}
