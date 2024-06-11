
# Ledger Posting System

This project implements a scalable ledger posting system using Spring Boot and microservices architecture, leveraging CQRS and Event Sourcing patterns. The system is designed to handle heavy write loads efficiently and provides APIs for asset transfers and account management.

## Youtube
**Youtube Video Link**: https://youtu.be/qy88zaSoIfQ


## How to Run the Project

To get the project up and running, follow these steps:

### Prerequisites

Ensure you have the following installed on your system:
- Java 17 or above
- Maven
- Docker
- Docker Compose

### Steps

1. **Build the Project**  
   Run the following Maven command to build the project while skipping tests:
   ```sh
   mvn clean package -DskipTests

2. **Start the Docker Containers**  
   Use Docker Compose to build and start all required services, including Kafka, PostgreSQL, Zookeeper, the ledger application, and Kafdrop for Kafka message monitoring:
   ```sh
   docker-compose up --build -d
### Accessing the Application

Once the Docker containers are up and running, the application can be accessed at: 

    http://localhost:8080/swagger-ui/index.html

### Notes

- Ensure that port `8080` is free on the host system to run the application.
- Docker Compose also opens port `5005` for debugging purposes.

### Included Services

The Docker Compose setup includes the following services:
- **Kafka**: For message brokering.
- **PostgreSQL**: As the database.
- **Zookeeper**: For Kafka coordination.
- **Ledger Application**: The main Spring Boot application.
- **Kafdrop**: A web UI for monitoring Kafka messages.

### Debugging

To debug the application, connect to port `5005` using your preferred IDE.

## Business Logic


The core business logic of this application revolves around maintaining a highly reliable, scalable, and efficient ledger system. The system adheres to principles of immutability, double-entry accounting, concurrency control, efficient aggregations, client-supplied timestamps, atomic transactions, balance caching, audit logging, high throughput, and scalability.

The design and implementation guidelines for this ledger system are based on the comprehensive principles outlined in the document [How to Scale a Ledger.pdf](./docs/How%20to%20Scale%20a%20Ledger.pdf).

### Summary of "How to Scale a Ledger.pdf"

The document "How to Scale a Ledger" provides a detailed explanation of the necessity and advantages of using a ledger database in financial services. It highlights the challenges faced by companies in tracking and managing money as they scale, such as the need for sophisticated financial reporting, maintaining a clean history of financial events, and ensuring performance and consistency under high loads.

Key features of a scalable ledger database include:

*Note: The features highlighted in bold have been implemented in the current project. The others are planned for future implementation.*

1. **Immutability**: Ensures every change is recorded and past states can always be retrieved.
2. **Double-entry Accounting**: Enforces that money cannot be moved without specifying both the source and destination.
3. **Concurrency Controls**: Prevents double-spending of money, even with parallel transactions.
4. **Efficient Aggregations**: Facilitates fast computation of financial event summaries.
5. **Client-supplied Timestamps and Account Balance Versions**: Maintains consistency and accuracy for historical transactions.
6. **Atomic Transactions**: Ensures groups of entries either all succeed or all fail.
7. Balance Caching: Optimizes for fast retrievals and efficient balance computations.
8. Audit Logs: Provides traceability and accountability for all financial events.
9. High Throughput: Supports thousands of writes per second for large-scale money movement.
10. Scalability: Designed to scale with business needs, potentially through sharding or using scalable databases.

These principles are critical in building a ledger system that not only meets current business needs but can also scale efficiently as the business grows.

## Project Features and Requirements

### Communication Capabilities

The application can process forex operations either synchronously or asynchronously. This behavior is determined by the payload of the REST request, specifically the `"synchronize": false` field.

- **Synchronous Requests**: If the request is synchronous, the application processes it directly.
- **Asynchronous Requests**: If the request is asynchronous (i.e., `"synchronize": false`), the application accepts the request and writes it to Kafka for later processing.

When a request arrives, the application makes a quick check to ensure the cached available balance is sufficient to accept the request. If the cached balance is adequate, the request is accepted. The real available balance, calculated from actual transactions, will be processed during the forex operation. This approach ensures efficient handling of requests while maintaining accuracy and consistency in balance management.

### API Documentation

The project's APIs can be reviewed using Swagger. Swagger provides an interactive interface for exploring and testing the APIs. This documentation includes detailed information about each endpoint, including request and response formats, parameters, and example payloads.

To access the Swagger UI, navigate to the following URL:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

This interface allows you to interact with the APIs directly, making it easier to understand and test their functionality.


### Load Testing


The forex transaction process is used in the load test. You can view the load test code in the following internal link:
   ```sh
   mvn gatling:test
   ```

[ForexSimulation.java](src/test/java/co/uk/zing/ledger/loadtest/ForexSimulation.java)

This load test simulates various scenarios to ensure the system can handle high transaction volumes efficiently.

**Note**:
- The current test uses static account IDs. To improve this, the process should be modified to create accounts first, increase the system balance, and then start the test. Currently, the balance is increased every 100 requests.

  To run this load test, please follow these steps:
   1. Create two accounts.
   2. Call the endpoint to increase the posted credit for these accounts.
   3. Update the account IDs in the `ForexSimulation.java` code with the newly created account IDs.
   4. Run the load test.

  This ensures the load test operates with dynamically created accounts and accurately simulates real-world usage.

- Roughly 8-10% of the load test requests fail. The reason for these failures is that all records are versioned, so any database change during the operation prevents the current operation from continuing. This issue needs to be handled by retrying with new values. This has not been implemented yet, but it is a known issue. (**`ObjectOptimisticLockingFailureException`**)

### Integration and Unit Test Cases

Tests are placed under [Integration and Unit Test Cases](https://github.com/tugrulkarakaya/ledger-posting-system/tree/13-readme-file/src/test/resources/features). As this is a demo app, I have implemented a limited set of tests, including forex command handler unit tests, account creation, and forex operation REST API requests.

In my recent job, I use Gherkin language with Robot Framework for integration tests and Cucumber (again with Gherkin) for component tests.

Although I did not develop this project with TDD (I have previously made a presentation and a demo KATA for TDD; if I can find the video, I will add the URL), BDD-driven tests can be found to demonstrate my skill.

**Note**: Integration tests currently require Docker Compose to be up and running. This part can also be improved.

**Sample Test Files**:
- [Forex Command Handler Feature](src/test/resources/features/unit_tests/forex_command_handler.feature)
- [Forex Command Handler Steps](src/test/java/co/uk/zing/ledger/steps/ForexCommandHandlerSteps.java)

### Exception Management

Exception handling is a crucial aspect of a ledger posting system. For this demo application, I have created sample exceptions including `AccountNotFound`, `InsufficientFunds`, and `MissingAccountName`. A Global Exception Handler has been added to manage these exceptions at the REST level.

In a system like ledger posting, it is essential to have comprehensive exception management at the controller level, service level, and even for logging purposes. Proper handling of exceptions ensures robustness and reliability.

I have implemented three sample exceptions and included a unit test scenario for the `InsufficientFunds` exception. However, it is important to note that this area is critical for the overall stability and reliability of a ledger system and should be extensively developed and tested.


### Additional Information for Reviewers

This project includes Swagger for API documentation and Docker Compose to facilitate easy setup and running of the application. Although primarily a demonstration, it incorporates CQRS and Event Sourcing patterns with Kafka for event messaging to handle both synchronous and asynchronous communication effectively.

While not developed with a strict Test-Driven Development (TDD) approach, the project reflects a strong understanding of Behavior-Driven Development (BDD) principles. Future improvements could include more comprehensive testing using tools like Gherkin with Qtember or the Robot Framework to enhance component and integration testing.
### Opportunities for Improvement

- **Query Service in Another Docker** Currently, there is just one application running in Docker Compose, but the query and command paths should run in two different applications. This requires adding a new Docker application YAML file for configuration and another URL, not on the 8080 port. This separation would better adhere to the CQRS pattern.

- **Testing** Enhance the testing framework by incorporating more comprehensive tests using tools like Gherkin with Qtember or the Robot Framework to improve component and integration testing.

- **Demonstration of BDD**: I only wanted to demonstrate how BDD works. However, applications can be much more comprehensive or complex.

- **Testing**: Integration, unit, and possibly component tests have been mentioned and can be further developed.

- **Redis Cache Implementation**: I planned to implement Redis Cache to enhance performance. This isn't a rocket science project, but it took a lot of time.
  Performance Optimization: At NatWest, I was able to implement a system between microservice infrastructures, reducing the latency from 90 milliseconds to 9-10 milliseconds. I planned to do this in the current project but couldn't find enough time.



