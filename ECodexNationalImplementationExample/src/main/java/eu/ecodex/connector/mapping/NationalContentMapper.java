package eu.ecodex.connector.mapping;

import eu.ecodex.connector.common.exception.ImplementationMissingException;
import eu.ecodex.connector.common.message.MessageContent;
import eu.ecodex.connector.mapping.exception.ECodexConnectorContentMapperException;

public class NationalContentMapper implements ECodexConnectorContentMapper {

    @Override
    public void mapInternationalToNational(MessageContent messageContent) throws ECodexConnectorContentMapperException,
            ImplementationMissingException {
        System.out.println("Ich bin die nationale Implementierung des Content Mappers für Österreich!!!");

    }

    @Override
    public void mapNationalToInternational(MessageContent messageContent) throws ECodexConnectorContentMapperException,
            ImplementationMissingException {
        System.out.println("Ich bin die nationale Implementierung des Content Mappers für Österreich!!!");

    }

}
