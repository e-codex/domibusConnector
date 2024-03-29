/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/token/LegalTrustLevel.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.model.token;

import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * This class holds the type of the trust level.
 * 
 * <p>DISCLAIMER: Project owner e-CODEX</p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@XmlType(name = "LegalTrustLevelEnum")
@XmlEnum
public enum LegalTrustLevel {

    /** aka GREEN */
     @XmlEnumValue("SUCCESSFUL")
    SUCCESSFUL("SUCCESSFUL", "Successful"),
    
    @XmlEnumValue("UNDETERMINED")
     UNDETERMINED("UNDETERMINED", "Undetermined"),
    
    /** aka RED */
     @XmlEnumValue("NOT_SUCCESSFUL")
    NOT_SUCCESSFUL("NOT_SUCCESSFUL", "Not Successful");
    
    private final String value;
    private final String text;

    /**
     * constructor
     * @param value the value "SUCCESSFUL" or "NOT_SUCCESSFUL"
     * @param text the textual representation "Successful" or "Not Successful"
     */
    LegalTrustLevel(final String value, final String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * the underlying string value
     * @return "SUCCESSFUL" or "NOT_SUCCESSFUL"
     */
    public String value() {
        return getValue();
    }

    /**
     * the underlying string value
     * @return "SUCCESSFUL" or "NOT_SUCCESSFUL"
     */
    public String getValue() {
        return value;
    }

    /**
     * the underlying string value
     * @return "SUCCESSFUL" or "NOT_SUCCESSFUL"
     */
    public String getText() {
        return text;
    }

    /**
     * checks whether the parameter is {@link #SUCCESSFUL}
     *
     * @param level the optional value
     * @return true, if {@link #SUCCESSFUL}
     */
    public static boolean isSuccessful(final LegalTrustLevel level) {
        return level == SUCCESSFUL;
    }

    /**
     * checks whether the parameter is {@link #NOT_SUCCESSFUL}
     *
     * @param level the optional value
     * @return true, if {@link #NOT_SUCCESSFUL}
     */
    public static boolean isNotSuccessful(final LegalTrustLevel level) {
        return level == NOT_SUCCESSFUL;
    }

    /**
     * detects the worst level in the provided array of levels:
     * the precedence order is: {@link #NOT_SUCCESSFUL}, {@link #SUCCESSFUL}
     *
     * @param levels the values (with null supported)
     * @return null, if levels are null or empty; otherwise the detected worst level
     */
    public static LegalTrustLevel worst(final LegalTrustLevel... levels) {
        if ( levels == null || levels.length == 0) {
            return null;
        }

        LegalTrustLevel result = null;

        for ( final LegalTrustLevel level : levels ) {
            if ( level == null ) {
                // ignore null values
                continue;
            }
            if ( result == null ) {
                // initialise the result
                result = level;
            } else if ( isNotSuccessful(level) ) {
                // the worst case
                result = level;
            } else if ( isSuccessful(level) ) {
                // check if can apply the level (that is not overwrite a worse value)
                if ( !isNotSuccessful(result) ) {
                    result = level;
                }
            }
            if ( isNotSuccessful(result) ) {
                return result; // this is the worst case, so we can ignore all others
            }
        }

        return result;
    }

    /**
     * factory retrieval method; if the instance is not found, then an IllegalArgumentException is thrown.
     *
     * @param v either "SUCCESSFUL" or "NOT_SUCCESSFUL"
     * @return the enum instance
     */
    public static LegalTrustLevel fromValue(final String v) {
        if (StringUtils.isEmpty(v)) {
            throw new IllegalArgumentException("value must not be empty");
        }
        for (LegalTrustLevel c: LegalTrustLevel.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
