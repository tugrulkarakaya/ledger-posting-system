package co.uk.zing.ledger.integration;

import io.gatling.javaapi.core.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.time.Duration;
import java.util.UUID;

import static io.gatling.javaapi.http.HttpDsl.http;

public class ForexSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("*/*")
            .contentTypeHeader("application/json");

    // Function to increase posted credits
    ChainBuilder increasePostedCredits = exec(http("Increase Posted Credits")
            .post("/api/command/373982c9-41dc-4c79-8672-9c1d90327bb7/increasePostedCredits?amount=20000")
            .check(status().is(200))
    );

    // Function to create forex request
    ChainBuilder createForexRequest = exec(http("Create Forex Request")
            .post("/api/forex/create")
            .body(StringBody(session -> {
                String requestId = UUID.randomUUID().toString();
                return "{\n" +
                        "  \"requestId\": \"" + requestId + "\",\n" +
                        "  \"sourceAccountId\": \"373982c9-41dc-4c79-8672-9c1d90327bb7\",\n" +
                        "  \"destinationAccountId\": \"2bacbe86-134e-4311-8303-3761d28ae0a9\",\n" +
                        "  \"amount\": 50,\n" +
                        "  \"exchangeRate\": 1,\n" +
                        "  \"synchronize\": true\n" +
                        "}";
            })).asJson()
            .check(status().is(200))
    );

    // Define the scenario with the condition
    ScenarioBuilder scn = scenario("Forex Create Load Test")
            .repeat(300).on(
                    exec(createForexRequest)
                            .exitHereIfFailed()
                            .exec(session -> {
                                int requestCount = session.getInt("requestCount");
                                session = session.set("requestCount", requestCount + 1);
                                return session;
                            })
                            .doIf(session -> session.getInt("requestCount") % 100 == 0).then(
                                    exec(increasePostedCredits)
                            )
            );

    {
        setUp(scn.injectOpen(
                atOnceUsers(10), // Start with 10 users
                rampUsers(100).during(Duration.ofMinutes(1)) // Ramp up to 100 users over 1 minute
        ).protocols(httpProtocol));
    }
}