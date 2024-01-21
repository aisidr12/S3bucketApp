package com.bucketapp.s3bucketapp.controller;

import com.bucketapp.s3bucketapp.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        return s3Service.uploadFile(file);
    }

    @GetMapping("/download/{filename}")
    public String downloadFile(@PathVariable String filename) throws IOException {
        return s3Service.downloadFile(filename);
    }

    @GetMapping("/buckets")
    public ResponseEntity<List<String>> listBuckets() {
        return ResponseEntity.ok(s3Service.objectsInsideBuckets());
    }

    @DeleteMapping("/delete/{bucketName}")
    public ResponseEntity<String> deleteObjects(@PathVariable String bucketName) {
        s3Service.deleteBucketWithObjectsInsideNonVersion(bucketName);
        return ResponseEntity.ok("Deleted Successfully");

    }
}
