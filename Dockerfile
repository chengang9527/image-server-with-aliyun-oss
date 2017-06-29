FROM openjdk:8-jre-alpine
RUN apk add --no-cache imagemagick
ENV MAGICK_HOME=/usr
EXPOSE 8080
ENTRYPOINT ["java","-jar","-Dserver.port=8080","/app.jar"]
ADD target/*.jar app.jar