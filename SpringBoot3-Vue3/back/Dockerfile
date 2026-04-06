FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml ./
COPY common/pom.xml ./common/
COPY modules/pom.xml ./modules/
COPY modules/system/pom.xml ./modules/system/
COPY modules/system/audit/pom.xml ./modules/system/audit/
COPY modules/system/auth/pom.xml ./modules/system/auth/
COPY modules/system/dept/pom.xml ./modules/system/dept/
COPY modules/system/menu/pom.xml ./modules/system/menu/
COPY modules/system/permission/pom.xml ./modules/system/permission/
COPY modules/system/role/pom.xml ./modules/system/role/
COPY modules/system/user/pom.xml ./modules/system/user/
COPY gateway/pom.xml ./gateway/

RUN mvn dependency:go-offline -B

COPY common ./common
COPY modules ./modules
COPY gateway ./gateway

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/common/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
