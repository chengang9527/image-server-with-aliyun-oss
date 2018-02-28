FROM mayan31370/openjdk-alpine-with-chinese-timezone:8-jre
RUN sed -i 's/dl\-cdn\.alpinelinux\.org/mirrors.aliyun.com/g' /etc/apk/repositories && apk add --no-cache imagemagick
ENV MAGICK_HOME=/usr
ENV IMAGE_SERVER=serverUrl
ENV IMAGE_OSS_ENDPOINT=endPoint
ENV IMAGE_OSS_ACCESS_KEY_ID=keyId
ENV IMAGE_OSS_ACCESS_KEY_SECRET=keySecret
ENV IMAGE_OSS_BUCKET_NAME=bucketName
ENV IMAGE_MAX_SIZE=1024x1024
ENV IMAGE_BASE_URL=baseUrl
EXPOSE 8080
ENTRYPOINT ["java","-Xms128m","-Xmx128m","-jar","-Dserver.port=8080","/app.jar"]
ADD target/*.jar app.jar
