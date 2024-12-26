FROM maven:3.8-openjdk-17-slim AS build

WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN mvn clean package

FROM tomcat:10.1.31

RUN rm -rf /usr/local/tomcat/webapps/ROOT

COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]