package com.rainbowrecorder.api.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AmazonClient {
    private AmazonS3 s3Client;

    @Value("${s3.endpointUrl}")
    private String endpointUrl;

    @Value("${s3.bucketName}")
    private String bucketName;

    @Value("${s3.accessKeyId}")
    private String accessKeyId;

    @Value("${s3.secretKey}")
    private String secretKey;

    @Value("${s3.region}")
    private String region;

    @PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials
                = new BasicAWSCredentials(this.accessKeyId, this.secretKey);

        this.s3Client  = AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    public String getContentType(String image) throws Exception {
        // Get content type of media using regex matcher
        Pattern contentPattern = Pattern.compile("^data:(.+?);base64,");
        Matcher contentMatcher = contentPattern.matcher(image);
        boolean matchFound = contentMatcher.find();
        if (!matchFound) { throw new Exception("Unidentified content type."); }
        return contentMatcher.group(1);
    }

    public String makeObjectKey(String userId, String timestamp, String contentType) {
        String fileExtension = contentType.substring(contentType.indexOf('/') + 1);
        return userId + "/" + timestamp + "." + fileExtension;
    }

    public byte[] getImageBuffer(String image) {
        return Base64.decodeBase64(image.substring(image.indexOf(",") + 1).getBytes());
    }

    public Optional<String> uploadImage(String userId, String timestamp, String image) throws IOException, Exception {
        try {
            // The image is stored in an S3 bucket as a buffer. To get the buffer, the "data:...;base64," prefix must
            // be removed from the image string, and the string must be decoded into a byte buffer.
            byte[] buffer = getImageBuffer(image);
            String contentType = getContentType(image);
            String objectKey = makeObjectKey(userId, timestamp, contentType);
            InputStream stream = new ByteArrayInputStream(buffer);
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(buffer.length);
            meta.setContentType(contentType);
            s3Client.putObject(
                    new PutObjectRequest(bucketName, objectKey, stream, meta)
                    .withCannedAcl(CannedAccessControlList.PublicRead)
            );
            stream.close();
            // Check to make sure that the object was uploaded successfully.
            URL location = s3Client.getUrl(bucketName, objectKey);
            // Returns application accessible url to object
            return Optional.ofNullable(location.toString());
        } catch(AmazonServiceException err) {
            err.printStackTrace();
            return Optional.empty();
        } catch(SdkClientException err) {
            err.printStackTrace();
            return Optional.empty();
        }
    }

    public void deleteFileFromS3Bucket(String fileUrl) {
        String decodedUrl = URLDecoder.decode(fileUrl, StandardCharsets.UTF_8);
        String fileName = decodedUrl.replace("https://post-photos.s3.us-east-2.amazonaws.com/", "");
        s3Client.deleteObject(bucketName, fileName);
    }

    private void uploadFileTos3bucket(String fileName, File file) {
        s3Client.putObject(bucketName, fileName, file);
    }

    private File convertMultiPartToFile(MultipartFile file)
            throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" +   multiPart.getOriginalFilename().replace(" ", "_");
    }
}
