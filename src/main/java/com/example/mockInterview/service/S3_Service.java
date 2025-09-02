package com.example.mockInterview.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class S3_Service {

    private final AmazonS3 s3Client; // Make it final to ensure it's initialized once

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3_Service(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(byte[] fileBytes, String originalFilename) throws IOException {

        // Use a temporary local file for the S3 upload
        File tempFile = convertBytesToFile(fileBytes, originalFilename);

        // Generate a unique file name
        String fileName = System.currentTimeMillis() + "_" + originalFilename.replace(" ", "_");

        // Upload the file to S3
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, tempFile));

        // Clean up the local file
        tempFile.delete();

        // Return the S3 path
        return s3Client.getUrl(bucketName, fileName).toString();
    }

    /**
     * Helper method to convert a byte array into a temporary local file.
     * @param fileBytes The byte array to convert.
     * @param originalFilename The original filename to use for the temp file.
     * @return A temporary File object.
     */
    private File convertBytesToFile(byte[] fileBytes, String originalFilename) throws IOException {
        // Create a temporary file
        File convertedFile = new File(originalFilename);
        
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            // Write the byte array directly to the file output stream
            fos.write(fileBytes);
        }
        return convertedFile;
    }
}