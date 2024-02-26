package eu.domibus.connector.controller.queues;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Session;

@Component
public class JmsDestinationResolver implements DestinationResolver {

    private final ApplicationContext ctx;

    public JmsDestinationResolver(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public Destination resolveDestinationName(@Nullable Session session, String destinationName, boolean pubSubDomain) throws JMSException {
        Destination bean = ctx.getBean(destinationName, Destination.class);
        return bean;
    }

}
