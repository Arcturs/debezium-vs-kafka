FROM maven:3.8.3-openjdk-17-slim as BUILDER
WORKDIR /opt/build/analyzing
COPY pom.xml /opt/build/analyzing/
COPY src /opt/build/analyzing/src/
RUN mvn -f /opt/build/analyzing/pom.xml clean package -B -DskipTests


FROM openjdk:17-alpine
WORKDIR /opt/app/analyzing
COPY --from=BUILDER /opt/build/analyzing/target/*.jar /opt/app/analyzing/analyzing.jar

RUN apk --no-cache add curl

EXPOSE 8080 8081

ENTRYPOINT ["java","-jar", "/opt/app/analyzing/analyzing.jar"]