FROM registry.cn-hangzhou.aliyuncs.com/counect_data/openjdk-8-jre-alpine-fixed-timezone
RUN apk add --no-cache imagemagick
ENV MAGICK_HOME=/usr
EXPOSE 8080
ENTRYPOINT ["java","-jar","-Dserver.port=8080","/app.jar"]
ADD target/*.jar app.jar