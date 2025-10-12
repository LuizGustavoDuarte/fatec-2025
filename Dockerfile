FROM maven:3.9.5-eclipse-temurin-21 AS builder
WORKDIR /app

RUN apt-get update && apt-get install -y libquadmath0 libgfortran5

COPY pom.xml ./
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

RUN apt-get update && \
    apt-get install -y --no-install-recommends libquadmath0 libgfortran5 curl && \
    rm -rf /var/lib/apt/lists/*

ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom"

COPY --from=builder /app/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]