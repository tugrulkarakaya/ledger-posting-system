package co.uk.zing.ledger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "co.uk.zing.ledger")
//@EnableJpaRepositories(basePackages = "co.uk.zing.ledger.command.repository")
//@EntityScan(basePackages = "co.uk.zing.ledger.command.model")
public class LedgerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LedgerApplication.class, args);
	}

}
