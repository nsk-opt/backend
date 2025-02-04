FROM gradle:latest

WORKDIR /app

COPY . /app

VOLUME [ "/var/logs" ]

RUN chmod +x /app/misc/generate-ssl.sh

CMD ["sh", "-c", "/app/misc/generate-ssl.sh && ./gradlew bootRun"]