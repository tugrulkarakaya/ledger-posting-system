package co.uk.zing.ledger;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest;

@ActiveProfiles("test")
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"co.uk.zing.ledger.steps", "co.uk.zing.ledger"},
        plugin = {"pretty", "html:target/cucumber-reports"}
)
public class CucumberTest {
}
