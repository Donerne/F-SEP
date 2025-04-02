package com.example.f_sep;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.opencsv.CSVReader;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class S3Utils {

    private AmazonS3 s3Client;
    private static final String bucketName = BuildConfig.BUCKET_NAME;

    public S3Utils(String accessKey, String secretKey) {
        // Initialize AWS credentials
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        this.s3Client = new AmazonS3Client(awsCredentials);
        this.s3Client.setRegion(Region.getRegion(Regions.US_EAST_1)); // Change to your region
    }

    public List<String[]> fetchCsvFromS3(String key) throws Exception {
        List<String[]> csvData = new ArrayList<>();

        try {
            boolean objectExists = s3Client.doesObjectExist(bucketName, key);
            if (!objectExists) {
                System.out.println("Error: S3 object does not exist at key: " + key);
                return null;
            }

            try (S3Object s3Object = s3Client.getObject(bucketName, key);
                 InputStreamReader reader = new InputStreamReader(s3Object.getObjectContent());
                 CSVReader csvReader = new CSVReader(reader)) {

                String[] line;
                while ((line = csvReader.readNext()) != null) {
                    if (key.contains("raw_news/")) {
                        if (line.length >= 5) {
                            String date = line[0];       // Keep date as String (or parse to LocalDate)
                            String headline = line[1];

                            try {
                                csvData.add(new String[]{date, headline});
                            } catch (NumberFormatException e) {
                                System.err.println("Skipping invalid row: " + Arrays.toString(line));
                            }
                        }
                    }
                    else if (!key.contains("raw_news/")) {
                        if (line.length >= 5) {
                            String date = line[0];       // Keep date as String (or parse to LocalDate)
                            String priceStr = line[1]; // Parse this to Double
                            String week1 = line[2];
                            String week2 = line[3];
                            String week3 = line[4];

                            try {
                                double price = Double.parseDouble(priceStr.trim());
                                csvData.add(new String[]{date, String.valueOf(price), week1, week2, week3});
                            } catch (NumberFormatException e) {
                                System.err.println("Skipping invalid row: " + Arrays.toString(line));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching CSV from S3: " + e.getMessage());
            throw e;
        }

        return csvData.isEmpty() ? null : csvData;
    }
}