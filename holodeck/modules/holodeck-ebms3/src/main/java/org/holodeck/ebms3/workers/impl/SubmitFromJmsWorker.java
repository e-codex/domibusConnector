package org.holodeck.ebms3.workers.impl;

import java.util.Map;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;
import org.holodeck.ebms3.workers.Task;

/**
 *  This implementation of this class is not finished yet. This class is supposed
 *  to register itself as a JMS listener for a given queue or topic, and then
 *  consume the messages arriving to that queue or topic by submitting them to
 *  holodeck for either a push (to be pushed by holodeck) or a pull (stored by
 *  holodeck so that they can be pulled at a later time by another MSH). Once
 *  the implementation of this class is finished, it can be declared in the file
 *  "holodeck/config/workers.xml" with two parameters: destination and brokerURL
 *
 * @author Hamid Ben Malek
 */
public class SubmitFromJmsWorker implements Task, MessageListener
{
//  private static final Log log = LogFactory.getLog(SubmitFromJmsWorker.class.getName());
  private static final Logger log = Logger.getLogger(SubmitFromJmsWorker.class.getName());

  protected String destination;
  protected String brokerURL;
  protected String isTopic = "false";

  protected Connection connection;
  protected Session session;
  protected ActiveMQConnectionFactory factory;
  protected MessageConsumer consumer;

  public void setParameters(Map<String, String> parameters)
  {
    if ( parameters == null ) return;
    destination = parameters.get("destination");
    brokerURL = parameters.get("brokerURL");
    isTopic = parameters.get("isTopic");
    log.info("destination=" + destination + "  and brokerURL=" + brokerURL);
    boolean topic = ( isTopic != null && isTopic.equalsIgnoreCase("true") );
    if ( destination != null && brokerURL != null )
    {
      try
      {
        factory = new ActiveMQConnectionFactory(brokerURL);
        connection = factory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination dest;
        if ( topic ) dest = session.createTopic(destination);
        else dest = session.createQueue(destination);
        consumer = session.createConsumer(dest);
        log.debug("Created jms consumer to fetch messages from destination " + destination);
        consumer.setMessageListener(this);
        connection.start();
      }
      catch(Exception ex) { ex.printStackTrace(); }
    }

  }

  public void run()
  {
  }

  public void onMessage(Message message) 
  {
    // get the properties from the message (such as pmode name, etc...)
    // then submit the message to holodeck
  }
}