FROM registry.cn-hangzhou.aliyuncs.com/counect_data/openjdk-8-jre-alpine-fixed-timezone
RUN apk add --no-cache imagemagick
ENV MAGICK_HOME=/usr
ENV IMAGE_SERVER=serverUrl
ENV IMAGE_OSS_ENDPOINT=endPoint
ENV IMAGE_OSS_ACCESS_KEY_ID=keyId
ENV IMAGE_OSS_ACCESS_KEY_SECRET=keySecret
ENV IMAGE_OSS_BUCKET_NAME=bucketName
ENV IMAGE_MAX_SIZE=1024x1024
ENV IMAGE_BASE_URL=baseUrl
EXPOSE 8080
ENTRYPOINT ["java","-jar","-Dserver.port=8080","/app.jar"]
ADD target/*.jar app.jar