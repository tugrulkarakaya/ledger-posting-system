package co.uk.zing.ledger.config;

import co.uk.zing.ledger.command.service.ForexEventConsumerService;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class TestRestTemplateConfig {

    @Bean
    public TestRestTemplate testRestTemplate() {
        return new TestRestTemplate();
    }

    @Bean
    public ForexEventConsumerService forexEventConsumerService() {
        return mock(ForexEventConsumerService.class);
    }
}
