package eu.ecodex.dc5.domain.model;

import eu.domibus.connector.domain.model.DC5BusinessDomain;

import javax.annotation.Nullable;
import jakarta.persistence.AttributeConverter;


public class BusinessDomainIdConverter implements AttributeConverter<DC5BusinessDomain.BusinessDomainId, String> {

    @Override
    @Nullable
    public String convertToDatabaseColumn(DC5BusinessDomain.BusinessDomainId attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getBusinessDomainId();
    }

    @Override
    @Nullable
    public DC5BusinessDomain.BusinessDomainId convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return new DC5BusinessDomain.BusinessDomainId(dbData);
    }
}
