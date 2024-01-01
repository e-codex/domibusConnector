package eu.ecodex.utils.configuration.annotation.processor;

import org.springframework.boot.configurationprocessor.ConfigurationMetadataAnnotationProcessor;

import javax.annotation.processing.SupportedAnnotationTypes;


//TODO: extend this annotation processor to add ConfigurationDescripion and ConfigurationLabel annotations
//to the metadata .json file
//@SupportedAnnotationTypes({ "*" })
public class AnnotatationMetadataProcessor extends ConfigurationMetadataAnnotationProcessor {
}
