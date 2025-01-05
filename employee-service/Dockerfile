ARG IMAGE_TAG_JRE=bellsoft/liberica-openjre-alpine-musl:17.0.13-12
ARG IMAGE_TAG_MAVEN=maven:3.9.9-amazoncorretto-17

FROM ${IMAGE_TAG_MAVEN} AS build
WORKDIR /build/
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src/
RUN mvn clean package -DskipTests

FROM ${IMAGE_TAG_JRE}
WORKDIR /opt/cv/employees
ARG JAR_FILE=/build/target/*.jar
COPY --from=build ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

