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

package org.fdc3.appd.poc.dao.impl;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.gson.Gson;
import org.fdc3.appd.poc.config.ConfigId;
import org.fdc3.appd.poc.config.Configuration;
import org.fdc3.appd.poc.exceptions.DaoException;
import org.fdc3.appd.poc.util.AwsS3Client;
import org.fdc3.appd.server.model.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * DAO for Applications supporting both retrieval and persist.
 * <p>
 * Persist supported through both local serialized json files and AWS S3 options
 *
 * @author Frank Tarsillo on 7/9/18.
 */
public class AppsDAOImpl implements org.fdc3.appd.poc.dao.AppsDAO {


    private ConcurrentMap<String, Application> apps = new ConcurrentHashMap<>();
    private Configuration config = Configuration.get();
    private String directory = config.get(ConfigId.JSON_APPS_DIR, "json/apps");

    private Logger logger = LoggerFactory.getLogger(AppsDAOImpl.class);
    private AwsS3Client awsS3Client;


    public AppsDAOImpl() {


        //Init S3 client
        if (config.getBoolean(ConfigId.S3_ENABLED, false))
            awsS3Client = new AwsS3Client();

        prime();
    }


    /**
     * Return the entire HashMap of known applications
     *
     * @return All known applications
     */
    @Override
    public ConcurrentMap<String, Application> getApps() {
        return apps;
    }

    /**
     * Define the known list of applications.  This will overwrite any existing cache of application defs
     *
     * @param apps All known apps.
     */
    @Override
    public void setApps(ConcurrentMap<String, Application> apps) {
        this.apps = apps;
    }


    /**
     * Retrieve application definition by AppID
     *
     * @param appId Application identification
     * @return Application definition
     * @throws DaoException General data management exception
     */
    @Override
    public Application getApp(String appId) throws DaoException {

        Application application = apps.get(appId);

        if (application == null)
            throw new DaoException("Application not found");

        return application;
    }

    /**
     * Upsert an application definition into cache.  This will also persist to disk or S3
     *
     * @param application Application to upsert
     * @throws DaoException General data management exception
     */
    @Override
    public void setApp(Application application) throws DaoException {

        if (application == null)
            throw new DaoException("No application provided");

        if (application.getAppId() == null)
            application.setAppId(java.util.UUID.randomUUID().toString());

        persist(application);
        apps.put(application.getAppId(), application);

    }


    /**
     * Prime the cache from disk or S3
     */
    @Override
    public void prime() {

        Gson gson = new Gson();


        File dir = new File(directory);

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                logger.error("Could not initialize Applications json directory {}", directory);

            }
        }


        File[] files = dir.listFiles();

        if (files == null) {
            logger.error("Failed to load locate directory [{}] for Applications json pre-load..exiting", directory);
            System.exit(1);
        }


        for (File file : files) {

            if (!file.getName().contains(".json"))
                continue;

            logger.info("Loading data from file [{}]", file.getName());
            try {
                Application application = gson.fromJson(new FileReader(file), Application.class);
                apps.put(application.getAppId(), application);

            } catch (IOException e) {
                logger.error("Could not load json {} ", file.getName(), e);
            }
        }

        //Load from S3
        if (config.getBoolean(ConfigId.S3_ENABLED, false)) {

            logger.debug("Attempting to load cache from S3 [{}/{}]", config.get(ConfigId.S3_BUCKET), config.get(ConfigId.S3_JSON_APPS_PREFIX));
            List<S3ObjectSummary> allObjects = awsS3Client.getAllObjects(config.get(ConfigId.S3_BUCKET, ""), config.get(ConfigId.S3_JSON_APPS_PREFIX, "json"));

            if (allObjects != null) {
                for (S3ObjectSummary objectSummary : allObjects) {

                    if (!objectSummary.getKey().contains(".json"))
                        continue;

                    Application application = gson.fromJson(new InputStreamReader(awsS3Client.getObject(objectSummary)), Application.class);

                    if (application.getAppId() != null) {
                        apps.put(application.getAppId(), application);
                    } else {
                        logger.error("Could not prime the following object [{}]", objectSummary.getKey());
                    }


                }
            }

        }
    }


    /**
     * Persist Application updates to cache
     *
     * @param application Application to persist
     * @throws DaoException General data exception while persisting data
     */
    private void persist(Application application) throws DaoException {


        String fileName = application.getAppId() + ".json";


        Gson gson = new Gson();

        try {
            FileWriter jsonFile = new FileWriter(Paths.get(directory, fileName).toString());


            gson.toJson(application, jsonFile);
            jsonFile.flush();
            jsonFile.close();
        } catch (IOException e) {
            logger.error("Could not write Application file [{}] to disk for {}", fileName, application.getName(), e);
            throw new DaoException("Could not write file [" + fileName + "] to disk", e);
        }


        if (config.getBoolean(ConfigId.S3_ENABLED, false)) {

            awsS3Client.putObject(
                    config.get(ConfigId.S3_BUCKET, ""),
                    Paths.get(config.get(ConfigId.S3_JSON_APPS_PREFIX, ""), fileName).toString(),
                    new ByteArrayInputStream(gson.toJson(application).getBytes(StandardCharsets.UTF_8)),
                    null);


        }


    }


}
