/*
 *
 *
 *  Copyright (C) 2018 IHS Markit.
 *  All Rights Reserved
 *
 *
 *  NOTICE:  All information contained herein is, and remains
 *  the property of IHS Markit and its suppliers,
 *  if any.  The intellectual and technical concepts contained
 *  herein are proprietary to IHS Markit and its suppliers
 *  and may be covered by U.S. and Foreign Patents, patents in
 *  process, and are protected by trade secret or copyright law.
 *  Dissemination of this information or reproduction of this material
 *  is strictly forbidden unless prior written permission is obtained
 *  from IHS Markit.
 */

package org.fdc3.appd.poc.util;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import org.fdc3.appd.poc.config.ConfigId;
import org.fdc3.appd.poc.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * Handles all AWS S3 communications
 * <p>
 * This requires authentication access keys to access buckets for vote-bot
 *
 * @author Frank Tarsillo 3/26/17.
 */
public class AwsS3Client {

    private AmazonS3 s3Client;
    private Logger logger = LoggerFactory.getLogger(AwsS3Client.class);
    private Configuration config = Configuration.get();

    public AwsS3Client() {

        AWSCredentials credentials = new BasicAWSCredentials(config.get(ConfigId.S3_KEY_ID), config.get(ConfigId.S3_ACCESS_KEY));
        s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.fromName(config.get(ConfigId.S3_REGION, "us-east-1"))).withCredentials(new AWSStaticCredentialsProvider(credentials)).build();

    }

    public static void main(String[] args) {

    }


    /**
     * Provide a list of objects from a given bucket w/prefix (folder).
     *
     * @param bucketName S3 bucket name
     * @param prefix     S3 folder within the bucket
     * @return List of {@link S3ObjectSummary} sorted by date
     */
    public List<S3ObjectSummary> getAllObjects(String bucketName, String prefix) {

        try {
            logger.debug("Listing S3 objects for s3://{}/{}", bucketName, prefix);
            final ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withPrefix(prefix);

            ListObjectsV2Result result;
            List<S3ObjectSummary> allObjects = new ArrayList<>();

            do {
                result = s3Client.listObjectsV2(req);

                allObjects.addAll(result.getObjectSummaries());

                req.setContinuationToken(result.getNextContinuationToken());
            } while (result.isTruncated());


            allObjects.sort(Comparator.comparing(S3ObjectSummary::getLastModified));

            return allObjects;
        } catch (AmazonServiceException ase) {
            logger.error("Caught an AmazonServiceException, " +
                    "which means your request made it " +
                    "to Amazon S3, but was rejected with an error response " +
                    "for some reason.");
            logger.error("Error Message:    " + ase.getMessage());
            logger.error("HTTP Status Code: " + ase.getStatusCode());
            logger.error("AWS Error Code:   " + ase.getErrorCode());
            logger.error("Error Type:       " + ase.getErrorType());
            logger.error("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            logger.error("Caught an AmazonClientException, " +
                    "which means the client encountered " +
                    "an internal error while trying to communicate" +
                    " with S3, " +
                    "such as not being able to access the network.");
            logger.error("Error Message: " + ace.getMessage());
        }

        return null;
    }


    public InputStream getObject(S3ObjectSummary objectSummary) {

        S3Object object = null;

        try {
            logger.debug("Retrieving object inputstream for s3://{}/{}", objectSummary.getBucketName(), objectSummary.getKey());
            object = s3Client.getObject(
                    new GetObjectRequest(objectSummary.getBucketName(), objectSummary.getKey()));


        } catch (AmazonServiceException ase) {
            logger.error("Caught an AmazonServiceException, " +
                    "which means your request made it " +
                    "to Amazon S3, but was rejected with an error response " +
                    "for some reason.");
            logger.error("Error Message:    " + ase.getMessage());
            logger.error("HTTP Status Code: " + ase.getStatusCode());
            logger.error("AWS Error Code:   " + ase.getErrorCode());
            logger.error("Error Type:       " + ase.getErrorType());
            logger.error("Request ID:       " + ase.getRequestId());

        } catch (AmazonClientException ace) {
            logger.error("Caught an AmazonClientException, " +
                    "which means the client encountered " +
                    "an internal error while trying to communicate" +
                    " with S3, " +
                    "such as not being able to access the network.");
            logger.error("Error Message: " + ace.getMessage());
        }
        return object == null ? null : object.getObjectContent();
    }

    public void putObject(String destBucket, String key, InputStream inputStream, ObjectMetadata metaData) {

        try {

            logger.info("Put object for s3://{}/{}", destBucket, key);
            byte[] bytes = IOUtils.toByteArray(inputStream);

            if (metaData == null)
                metaData = new ObjectMetadata();

            metaData.setContentLength(bytes.length);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

            s3Client.putObject(new PutObjectRequest(destBucket, key, byteArrayInputStream, metaData));


        } catch (AmazonServiceException ase) {
            logger.error("Caught an AmazonServiceException, " +
                    "which means your request made it " +
                    "to Amazon S3, but was rejected with an error response " +
                    "for some reason.");
            logger.error("Error Message:    " + ase.getMessage());
            logger.error("HTTP Status Code: " + ase.getStatusCode());
            logger.error("AWS Error Code:   " + ase.getErrorCode());
            logger.error("Error Type:       " + ase.getErrorType());
            logger.error("Request ID:       " + ase.getRequestId());

        } catch (AmazonClientException ace) {
            logger.error("Caught an AmazonClientException, " +
                    "which means the client encountered " +
                    "an internal error while trying to communicate" +
                    " with S3, " +
                    "such as not being able to access the network.");
            logger.error("Error Message: " + ace.getMessage());
        } catch (IOException e) {
            logger.error("Obtaining length", e);
        }

    }

    public void moveObject(S3ObjectSummary objectSummary, String destBucket, String destKey) {
        try {
            // Copying object
            CopyObjectRequest copyObjRequest = new CopyObjectRequest(
                    objectSummary.getBucketName(), objectSummary.getKey(), destBucket, destKey);

            s3Client.copyObject(copyObjRequest);

            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(objectSummary.getBucketName(), objectSummary.getKey());

            s3Client.deleteObject(deleteObjectRequest);
        } catch (AmazonServiceException ase) {
            logger.error("Caught an AmazonServiceException, " +
                    "which means your request made it " +
                    "to Amazon S3, but was rejected with an error response " +
                    "for some reason.");
            logger.error("Error Message:    " + ase.getMessage());
            logger.error("HTTP Status Code: " + ase.getStatusCode());
            logger.error("AWS Error Code:   " + ase.getErrorCode());
            logger.error("Error Type:       " + ase.getErrorType());
            logger.error("Request ID:       " + ase.getRequestId());

        } catch (AmazonClientException ace) {
            logger.error("Caught an AmazonClientException, " +
                    "which means the client encountered " +
                    "an internal error while trying to communicate" +
                    " with S3, " +
                    "such as not being able to access the network.");
            logger.error("Error Message: " + ace.getMessage());
        }
    }

    public void deleteObject(S3ObjectSummary objectSummary) {

        deleteObject(objectSummary.getBucketName(), objectSummary.getKey());

    }


    public void deleteObject(String bucketName, String destKey) {
        try {

            logger.debug("Deleting S3 objects for s3://{}/{}", bucketName, destKey);

            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, destKey);

            s3Client.deleteObject(deleteObjectRequest);
        } catch (AmazonServiceException ase) {
            logger.error("Caught an AmazonServiceException, " +
                    "which means your request made it " +
                    "to Amazon S3, but was rejected with an error response " +
                    "for some reason.");
            logger.error("Error Message:    " + ase.getMessage());
            logger.error("HTTP Status Code: " + ase.getStatusCode());
            logger.error("AWS Error Code:   " + ase.getErrorCode());
            logger.error("Error Type:       " + ase.getErrorType());
            logger.error("Request ID:       " + ase.getRequestId());

        } catch (AmazonClientException ace) {
            logger.error("Caught an AmazonClientException, " +
                    "which means the client encountered " +
                    "an internal error while trying to communicate" +
                    " with S3, " +
                    "such as not being able to access the network.");
            logger.error("Error Message: " + ace.getMessage());
        }
    }


    private void displayTextInputStream(InputStream input)
            throws IOException {

        if (input == null) {
            logger.error("InputStream was null..");
            return;
        }
        // Read one text line at a time and display.
        BufferedReader reader = new BufferedReader(new
                InputStreamReader(input));
        while (true) {
            String line = reader.readLine();
            if (line == null) break;

            logger.info("    {}", line);
        }
        logger.info("");
    }
}
