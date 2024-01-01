package eu.ecodex.utils.configuration.ui.vaadin.tools.configfield;

import eu.ecodex.utils.configuration.ui.vaadin.tools.UiConfigurationConversationService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.Resource;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = DurationFieldFactoryTest.TestContext.class)
@Disabled("repair test on CI server")
public class DurationFieldFactoryTest {

    @SpringBootApplication(scanBasePackages = {"eu.ecodex.utils.configuration.ui.vaadin.tools", "eu.ecodex.configuration.spring"})
    public static class TestContext {

    }

    @Autowired
    @UiConfigurationConversationService
    ConversionService conversionService;

    @Test
    void testConversionServiceDuration() {
        String duration = "10s";

        Duration convert = conversionService.convert(duration, Duration.class);

        assertThat(convert).isEqualTo(Duration.ofSeconds(10));
    }

    @Test
    void testConversionServiceResource() {
        String duration = "classpath:/META-INF/spring.factories";

        Resource convert = conversionService.convert(duration, Resource.class);

//        assertThat(convert).isEqualTo(Duration.ofSeconds(10));
    }

}