ARG OVERWRITE_VERSION

### Stage 1: build UI
FROM node:12 AS BUILD_UI_STAGE

WORKDIR /agr_curation
COPY src/main/cliapp ./cliapp
COPY src/main/resources ./resources

WORKDIR /agr_curation/cliapp
RUN make all build



### Stage 2: build API (and include UI components)
FROM maven:3.8-openjdk-11 as BUILD_API_STAGE
ARG OVERWRITE_VERSION

# copy the pom and src code to the container
COPY ./ ./
COPY --from=BUILD_UI_STAGE /agr_curation/resources ./src/main/resources

# Install make
RUN apt-get update
RUN apt-get install -y build-essential

# Optionally overwrite the application version stored in the pom.xml
RUN if [ "${OVERWRITE_VERSION}" != "" ]; then \
        mvn versions:set -ntp -DnewVersion=$OVERWRITE_VERSION; \
    fi;
# build the api jar
RUN make api



### Stage 3: build final application image
FROM openjdk:11-jre-slim

WORKDIR /agr_curation

# copy only the artifacts we need from the first stage and discard the rest
COPY --from=BUILD_API_STAGE /target/agr_curation_api-runner.jar .

# Expose necessary ports
EXPOSE 8080

# Set default env variables for local docker application execution
ENV QUARKUS_DATASOURCE_JDBC_URL jdbc:postgresql://postgres:5432/curation
ENV QUARKUS_DATASOURCE_USERNAME postgres
ENV QUARKUS_DATASOURCE_PASSWORD postgres

ENV QUARKUS_HIBERNATE_SEARCH_ORM_ELASTICSEARCH_HOSTS elasticsearch:9200
ENV QUARKUS_HIBERNATE_SEARCH_ORM_ELASTICSEARCH_PROTOCOL http

ENV QUARKUS_QPID_JMS_URL amqp://activemq:5672
ENV QUARKUS_QPID_JMS_USERNAME quarkus
ENV QUARKUS_QPID_JMS_PASSWORD quarkus

# Start the application
CMD ["java", "-jar", "agr_curation_api-runner.jar"]
