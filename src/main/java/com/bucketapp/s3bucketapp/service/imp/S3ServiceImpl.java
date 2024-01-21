package com.bucketapp.s3bucketapp.service.imp;

import com.bucketapp.s3bucketapp.service.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;

@Service
public class S3ServiceImpl implements S3Service {
    private final S3Client s3Client;
    @Value("${aws.bucket.name}")
    private String bucketName;

    public S3ServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        try {
            String fileName = multipartFile.getOriginalFilename();
            PutObjectRequest objectRequest = buildObjectRequest(fileName);
            s3Client.putObject(objectRequest, RequestBody.fromBytes(multipartFile.getBytes()));
            return "File Successfully uploaded";
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    private PutObjectRequest buildObjectRequest(String fileName) {
        return PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
    }

}
