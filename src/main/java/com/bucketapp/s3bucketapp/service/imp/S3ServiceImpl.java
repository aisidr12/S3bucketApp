package com.bucketapp.s3bucketapp.service.imp;

import com.bucketapp.s3bucketapp.exception.S3BucketException;
import com.bucketapp.s3bucketapp.service.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class S3ServiceImpl implements S3Service {
    private final S3Client s3Client;
    @Value("${aws.bucket.name}")
    private String bucketName;

    @Value("${upload.s3.localPath}")
    private String localPath;


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

    @Override
    public String downloadFile(String fileName) throws IOException {

        if (!doesObjectExist(fileName)) {
            return "This filename does not exist";
        }
        GetObjectRequest objectRequest = GetObjectRequest.builder().bucket(bucketName)
                .key(fileName)
                .build();
        ResponseInputStream<GetObjectResponse> result = s3Client.getObject(objectRequest);
        try {
            FileOutputStream fos = new FileOutputStream(localPath + fileName);
            byte[] readBuff = new byte[1024];
            int read_len = 0;
            while ((read_len = result.read(readBuff)) > 0) {
                fos.write(readBuff, 0, read_len);
            }
            fos.close();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
        return "File downloaded success";
    }

    @Override
    public List<String> objectsInsideBuckets() {
        return s3Client.listBuckets().buckets().stream().map(Bucket::name).toList();
    }

    @Override
    public void deleteBucketWithObjectsInsideNonVersion(String bucketName) {
        List<S3Object> s3Objects = s3Client.listObjects(ListObjectsRequest.builder().bucket(bucketName).build()).contents();
        if (!s3Objects.isEmpty()) {
            for (S3Object s3Object : s3Objects) {
                s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(s3Object.key()).build());
                s3Client.deleteBucket(DeleteBucketRequest.builder().bucket(bucketName).build());
            }

        }
    }

    @Override
    public void deleteBucketWithControlVersion(String bucketName) {

    }

    @Override
    public void deleteEmptyBucket(String bucketName) throws S3BucketException {
        List<S3Object> s3Objects = s3Client.listObjects(ListObjectsRequest.builder().bucket(bucketName).build()).contents();
        if (s3Objects.isEmpty()) {
            s3Client.deleteBucket(DeleteBucketRequest
                    .builder()
                    .bucket(bucketName)
                    .build());
        } else {
            throw new S3BucketException("Bucket must be empty before delete");
        }
    }

    private boolean doesObjectExist(String objectkey) {
        try {
            s3Client.headObject(HeadObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(objectkey)
                    .build());
        } catch (S3Exception exception) {
            if (exception.statusCode() == 404) {
                return false;
            }
        }
        return true;
    }

    private PutObjectRequest buildObjectRequest(String fileName) {
        return PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
    }

}
