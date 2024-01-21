package com.bucketapp.s3bucketapp.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface S3Service {

    String uploadFile(MultipartFile multipartFile) throws IOException;

    String downloadFile(String fileName) throws IOException;

    List<String> objectsInsideBuckets();

    void deleteBucketWithObjectsInsideNonVersion(String bucketName);

    void deleteBucketWithControlVersion(String bucketName);

    void deleteEmptyBucket(String bucketName);

}
