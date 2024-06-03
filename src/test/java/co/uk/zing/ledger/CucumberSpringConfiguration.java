package co.uk.zing.ledger;


import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@CucumberContextConfiguration
@ContextConfiguration(classes = {LedgerApplication.class})
public class CucumberSpringConfiguration {
}

