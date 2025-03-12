FROM eclipse-temurin:21 AS builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src
RUN ./mvnw package -DskipTests

# ---- Run Stage ----
FROM eclipse-temurin:21
RUN addgroup --system spring && adduser --system --group spring
USER spring:spring
WORKDIR /app
COPY --from=builder /app/target/room-booking-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
