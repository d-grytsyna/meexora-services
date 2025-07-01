# meexora-services
A microservice-based backend for a mobile application that enables users to create, manage, and attend local events. The system supports dynamic ticket pricing, payments, and ticket monitoring. The app allows organizers and attendees to create events, register, and manage ticket sales.

Tech Stack

Java 21

Spring Boot

Spring Cloud (Eureka, Gateway)

Apache Kafka

PostgreSQL

Redis

Docker & Docker Compose

Maven

System Architecture Diagram (C4 level 2)


![C4-lvl2 drawio](https://github.com/user-attachments/assets/14fc61a1-b3a2-4d3b-ac83-40fe9d6a623b)



The system is based on a microservice architecture, where each service runs in its own isolated Docker container. In addition to core functional services, the environment includes supporting services for data storage and asynchronous communication:

PostgreSQL: primary relational database;

Redis: for caching and temporary data;

Apache Kafka with Zookeeper: for asynchronous message-based communication between services.

Each microservice has its own Dockerfile, defining how the service is built and runs inside a container.
The project includes a shared module meexora-common, which contains common DTOs, Kafka event models, error handling, and utilities. Before running Docker Compose, it's required to build all project modules using:

mvn clean install

For persistence, the PostgreSQL container runs with an initialization directory that contains SQL scripts to automatically create separate databases for each microservice on the first run. All data from PostgreSQL is persisted in a Docker volume pgdata to survive container restarts.
To start the entire infrastructure, simply run:

docker-compose up --build





