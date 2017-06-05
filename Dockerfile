FROM openjdk:8-jre-alpine
EXPOSE 8080
ADD target/*.jar app.jar
ENV MAGICK_HOME=/usr
RUN apk add --no-cache imagemagick
ENTRYPOINT ["java","-jar","/app.jar"]