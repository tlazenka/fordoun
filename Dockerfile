FROM gradle:9.7.1-jdk17

ENV APP_HOME /app
WORKDIR $APP_HOME

COPY build.gradle.kts settings.gradle gradlew ./
COPY gradle ./gradle

RUN ./gradlew build

COPY . .

CMD ["./gradlew", "allTest"]
