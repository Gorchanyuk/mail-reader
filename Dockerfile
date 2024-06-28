FROM maven:3.8.4-openjdk-17 as build
WORKDIR /build
COPY pom.xml .
RUN mvn --fail-never verify
COPY src src
RUN mvn -T 10C -Dmaven.test.skip -DskipTests -o package

FROM eclipse-temurin:17-jre-alpine
COPY --from=build /build/target/mail-reader-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]