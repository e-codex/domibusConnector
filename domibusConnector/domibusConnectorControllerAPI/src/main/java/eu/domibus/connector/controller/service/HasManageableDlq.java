package eu.domibus.connector.controller.service;

import javax.jms.Message;
import javax.jms.Queue;
import java.util.List;

public interface HasManageableDlq extends PutOnQueue {
    Queue getDlq();
    List<Message> listAllMessages();
    List<Message> listAllMessagesInDlq();
    void moveMsgFromDlqToQueue(String jmsId);
    void deleteMsgFromDlq(String jmsId);
    void deleteMsgFromQueue(String jmsId);
}
