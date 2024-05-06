FROM maven:3.8.3-openjdk-17-slim as BUILDER
WORKDIR /opt/build/processing
COPY pom.xml /opt/build/processing/
COPY src /opt/build/processing/src/
RUN mvn -f /opt/build/processing/pom.xml clean package -B -DskipTests


FROM openjdk:17-alpine
WORKDIR /opt/app/processing
COPY --from=BUILDER /opt/build/processing/target/*.jar /opt/app/processing/processing.jar

RUN apk --no-cache add curl

EXPOSE 8080

ENTRYPOINT ["java","-jar", "/opt/app/processing/processing.jar"]