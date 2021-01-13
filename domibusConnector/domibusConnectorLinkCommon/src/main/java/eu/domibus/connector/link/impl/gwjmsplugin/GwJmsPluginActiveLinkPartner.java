package eu.domibus.connector.link.impl.gwjmsplugin;

import eu.domibus.connector.controller.service.SubmitToLink;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.api.ActiveLinkPartnerManager;
import eu.domibus.connector.link.api.ActiveLinkManager;
import org.springframework.beans.factory.annotation.Autowired;

//@Component
//@Profile(GwJmsPluginConfiguration.GW_JMS_PLUGIN_PROFILE)
public class GwJmsPluginActiveLinkPartner implements ActiveLinkPartnerManager {


    @Autowired
    private SubmitToGwJmsPlugin submitToGwJmsPlugin;

    @Autowired
    private GwJmsPluginActiveLink activeLink;

    @Autowired
    private DomibusConnectorLinkPartner linkPartner;

    @Override
    public ActiveLinkManager getActiveLink() {
//        return activeLink;
        return null;
    }


    @Override
    public void shutdown() {
        activeLink.shutdownLinkPartner(linkPartner.getLinkPartnerName());
    }

    @Override
    public SubmitToLink getSubmitToLinkBean() {
        return submitToGwJmsPlugin;
    }

    public void setLinkPartner(DomibusConnectorLinkPartner linkPartner) {
        this.linkPartner = linkPartner;
    }

    public DomibusConnectorLinkPartner getLinkPartner() {
        return linkPartner;
    }
}


