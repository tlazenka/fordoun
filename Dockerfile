FROM gradle:6.6.1-jdk8

ENV APP_HOME /app
WORKDIR $APP_HOME

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

RUN ./gradlew build

COPY . .

CMD ["./gradlew", "allTest"]
