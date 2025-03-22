FROM eclipse-temurin:21 AS builder
WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src

RUN ./mvnw package -DskipTests

RUN jdeps --ignore-missing-deps -q \
    --recursive \
    --multi-release 21 \
    --print-module-deps \
    --class-path 'target/dependency/*' \
    target/room-booking-0.0.1-SNAPSHOT.jar > modules.txt

RUN $JAVA_HOME/bin/jlink \
    --module-path $JAVA_HOME/jmods \
    --add-modules $(cat modules.txt) \
    --output /custom-jre \
    --strip-debug --compress=2 --no-man-pages --no-header-files

FROM alpine:latest

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app
COPY --from=builder /app/target/room-booking-0.0.1-SNAPSHOT.jar app.jar
COPY --from=builder /custom-jre /custom-jre

ENV JAVA_HOME=/custom-jre
ENV PATH="$JAVA_HOME/bin:$PATH"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
