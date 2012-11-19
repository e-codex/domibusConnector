package eu.ecodex.connector.controller;

import eu.ecodex.connector.controller.exception.ECodexConnectorControllerException;

/**
 * Controller interface which handles the whole lifecycle of the connector.
 * 
 * @author riederb
 * 
 */
public interface ECodexConnectorController {

    /**
     * Method which handles all the way through the connector. Triggered by a
     * timer job executed every configured time-period it checks (depending on
     * the implementation) pending messages on one side and handles all messages
     * way through to the other side.
     * 
     * @throws ECodexConnectorControllerException
     */
    public void handleMessages() throws ECodexConnectorControllerException;

}
