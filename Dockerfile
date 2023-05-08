## pull maven image from DockerHub to run mvn command and give that name as builder to use in next step, you can give any name you want (here, I used multi-stage build which is better way using docker)
FROM maven:3.9.0-eclipse-temurin-11-alpine as builder
## create folder (if not existed) and define working folder inside container
WORKDIR /opt/app
## copy our project into that directory
COPY . /opt/app
## generate jar file by using maven which we already add maven image in above, (you can use `mvn clean install -DskipTests`) (this step will take a few minutes)
RUN mvn clean package -DskipTests

## use eclipse temurin instead of openJDK for better performance and small size and eclipse temurin can provide java as well
## use JRE instead of JDK for small size and JRE is enough to run jar. Alpine image tag is a light weight image than other image tags
FROM eclipse-temurin:11-jre-alpine

## set working directory
WORKDIR /opt/app

## below copy command is use when you genereate jar file with maven in docker container.
## from `builder` is from above step which we gave the name to `builder`
## opt/app is the path where we generate jar file in above step
## you can define static jar file name without snapshot version with `<finalName>` tag inside `<build>` tag in `pom.xml` file.
COPY --from=builder /opt/app/target/job-interview-0.0.1-SNAPSHOT.jar  app.jar

ENTRYPOINT ["java","-jar","app.jar"]