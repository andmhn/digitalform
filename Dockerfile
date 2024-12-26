# Angular Build
#===================

FROM node:20-alpine AS development

WORKDIR /form-ui
COPY form-ui/package.json /form-ui/package.json
COPY form-ui/package-lock.json /form-ui/package-lock.json

RUN npm ci
COPY form-ui /form-ui

FROM development AS ui-build
RUN ["npm", "run", "build"]

# Spring Boot Build
#===================

FROM --platform=$BUILDPLATFORM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /form-api
COPY form-api/pom.xml /form-api/pom.xml
RUN mvn dependency:go-offline

COPY form-api/src /form-api/src
COPY --from=ui-build /form-ui/dist/browser /form-api/target/classes/static

RUN mvn package -DskipTests

# Spring Boot Run
#=================

FROM eclipse-temurin:21-jre-alpine

EXPOSE 8080
VOLUME /tmp
ARG DEPENDENCY=/form-api/target/**.jar
COPY --from=builder ${DEPENDENCY} /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
