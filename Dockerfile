FROM eclipse-temurin:24-jdk AS builder

WORKDIR /app

COPY . .

RUN chmod +x gradlew
RUN ./gradlew :server:shadowJar --no-daemon

FROM eclipse-temurin:24-jre

WORKDIR /app

COPY --from=builder /app/server/build/libs/server-all.jar app.jar

# Optional build args (supaya gak error walau gak dipakai)
ARG DB_USER
ARG DB_PASSWORD
ARG DB_NAME
ARG DOMAIN
ARG JWT_SECRET

ENV DB_USER=$DB_USER \
    DB_PASSWORD=$DB_PASSWORD \
    DB_NAME=$DB_NAME \
    DOMAIN=$DOMAIN \
    JWT_SECRET=$JWT_SECRET

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]