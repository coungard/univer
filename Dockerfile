# Сборка: зависимости отдельным слоем, чтобы правки в src не перекачивали весь Maven-репозиторий
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml lombok.config ./
RUN mvn -B dependency:go-offline
COPY src ./src
# Тесты требуют Docker (Testcontainers) и гоняются в CI, в образе их не запускаем
RUN mvn -B package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/univer.jar app.jar
EXPOSE 8023
# JAVA_OPTS — для -Xmx и прочих флагов JVM без пересборки образа
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
