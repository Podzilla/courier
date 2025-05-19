mvn clean package -DskipTests
docker compose down
docker compose up -d --build