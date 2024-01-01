package eu.domibus.connector.controller.service;

import eu.ecodex.dc5.message.model.DC5Message;

public interface PutOnQueue {

    public void putOnQueue(DC5Message message);

    public jakarta.jms.Queue getQueue();

}
