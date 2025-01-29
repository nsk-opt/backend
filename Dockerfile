FROM gradle:latest

WORKDIR /app

COPY . /app

VOLUME [ "/var/logs" ]

CMD ["./gradlew", "bootRun"]