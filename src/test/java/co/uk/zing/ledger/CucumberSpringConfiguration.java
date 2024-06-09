package co.uk.zing.ledger;


import co.uk.zing.ledger.config.TestConfig;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ActiveProfiles("test")
@SpringBootTest
@CucumberContextConfiguration
@ContextConfiguration(classes = {LedgerApplication.class})
@Import(TestConfig.class) // Import the configuration
public class CucumberSpringConfiguration {
}

