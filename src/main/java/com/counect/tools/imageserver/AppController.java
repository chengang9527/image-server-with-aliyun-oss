package com.counect.tools.imageserver;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AppController {

  private static final String FILE_NAME_FORMAT = "%s-%s.%s";
  private static final String[] SHOULD_FORMATTED_IMAGE_EXTENSIONS = {"jpeg", "jpg", "png"};
  private static final Logger LOGGER = LoggerFactory.getLogger("image-server");

  @Value("${image-server}")
  private String imageServer;
  @Value("${oss-endpoint}")
  private String ossEndPoint;
  @Value("${oss-access-key-id}")
  private String ossAccessKeyId;
  @Value("${oss-access-key-secret}")
  private String ossAccessKeySecret;
  @Value("${oss-bucket-name}")
  private String ossBucketName;
  @Value("${image-max-size}")
  private String imageMaxSize;
  @Value("${base-url}")
  private String baseUrl;

  private void uploadFileToOSS(File file, String filename) {
    OSSClient client = new OSSClient(ossEndPoint, ossAccessKeyId, ossAccessKeySecret);
    try {
      ObjectMetadata om = new ObjectMetadata();
      om.setContentType(MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(filename));
      om.setLastModified(new Date());
      client.putObject(ossBucketName, filename, file, om);
    } catch (ClientException e) {
      LOGGER.error("OSS can not connected.", e);
    } catch (OSSException e) {
      LOGGER.error(String.format("Code:%s%nMessage:%s%nRequestId:%s%nHostId:%s", e.getErrorCode(),
          e.getErrorMessage(), e.getRequestId(), e.getHostId()), e);
    } finally {
      client.shutdown();
    }
  }

  @GetMapping("/**")
  public String image(HttpServletRequest request) {
    return "redirect:" + imageServer + request.getServletPath();
  }

  @ResponseBody
  @PostMapping("/")
  public String upload(MultipartFile file) throws IOException, InterruptedException {
    String filename = generateFilename(file);
    File local = saveFileToLocal(file, filename);
    if (FilenameUtils.isExtension(filename, SHOULD_FORMATTED_IMAGE_EXTENSIONS)) {
      convertImage(local);
    }
    File formattedFile = new File(local.getAbsolutePath());
    uploadFileToOSS(formattedFile, filename);
    FileUtils.deleteQuietly(formattedFile);
    return baseUrl + filename;
  }

  private void convertImage(File local) throws IOException, InterruptedException {
    Runtime.getRuntime().exec(String
        .format("convert -strip -resize '%s' %s %s", imageMaxSize, local.getAbsolutePath(),
            local.getAbsolutePath())).waitFor();
  }

  private File saveFileToLocal(MultipartFile file, String filename) throws IOException {
    File local = new File(filename);
    FileUtils.copyInputStreamToFile(file.getInputStream(), local);
    return local;
  }

  private String generateFilename(MultipartFile file) {
    String dateStr = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    String randomStr = RandomStringUtils.randomAlphanumeric(6);
    String extensionStr = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
    return String.format(FILE_NAME_FORMAT, dateStr, randomStr, extensionStr);
  }
}
