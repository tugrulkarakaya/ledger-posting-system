# Ledger Posting System

This project implements a scalable ledger posting system using Spring Boot and microservices architecture, leveraging CQRS and Event Sourcing patterns. The system is designed to handle heavy write loads efficiently and provides APIs for asset transfers and account management.

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

The design and implementation guidelines for this ledger system are based on the comprehensive principles outlined in the document [How to Scale a Ledger.pdf](docs/How to Scale a Ledger.pdf).

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
