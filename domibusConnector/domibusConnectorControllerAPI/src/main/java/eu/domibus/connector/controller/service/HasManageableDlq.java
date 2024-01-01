package eu.domibus.connector.controller.service;

import jakarta.jms.Message;
import jakarta.jms.Queue;
import java.util.List;

public interface HasManageableDlq extends PutOnQueue {
    Queue getDlq();
    List<Message> listAllMessages();
    List<Message> listAllMessagesInDlq();
    void moveMsgFromDlqToQueue(Message msg);
    void deleteMsg(Message msg);
    public String getName();

    String getMessageAsText(Message msg);

    String getDlqName();
}
