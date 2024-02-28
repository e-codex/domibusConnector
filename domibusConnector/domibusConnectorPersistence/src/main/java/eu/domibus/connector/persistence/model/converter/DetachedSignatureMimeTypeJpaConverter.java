package eu.domibus.connector.persistence.model.converter;

import eu.domibus.connector.domain.model.DetachedSignatureMimeType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DetachedSignatureMimeTypeJpaConverter implements AttributeConverter<DetachedSignatureMimeType, String> {


    @Override
    public String convertToDatabaseColumn(DetachedSignatureMimeType attribute) {
        return attribute.getCode();
    }

    @Override
    public DetachedSignatureMimeType convertToEntityAttribute(String dbData) {
        return DetachedSignatureMimeType.fromCode(dbData);
    }


}
