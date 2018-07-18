# Application Directory POC

**This is NOT a ratified specification but rather a tool to evaluate both the use cases and technology implmentations.**


## Overview
This project is a POC focused on creation of an Application Directory service as defined through initial proposals and
ongoing discussions in the Application Directory FDC3 working group.  The purpose of this POC is to:

* Evaluate discoverability of the service through application identification
* Implementation of the [Application Directory API](https://github.com/FDC3/appd-api)
* Support general demonstrations


## Build

Compilation requires **jdk 8+** and **maven**

Build:

    #git clone..*:
    #cd cloned_directory
    #mvn clean install package


## Directory structure

* **appd-service**:
    The POC application directory service which implements the server stubs and exposes the interfaces.
    For the time being it will store all application data on local disk in JSON format.


## Run it in Docker!

    # clone..*
    # cd cloned_directory
    # mvn clean install package
    # cd appd-service
    # docker build . --tag=appd-service
    # docker run -p 8080:8080 appd-service

    # curl http://localhost:8080/appd/v1/apps/search
    {"applications":[{"appId":"353527c7-f765-452b-a5d4-a1ecaa0854f7","name":"TEST IT","appType":"string","version":"string","title":"string","tooltip":"string","description":"string","images":[{"url":"string"}],"contactEmail":"string","supportEmail":"string","publisher":"string","icons":[{"icon":"string"}],"customConfig":[{"name":"string","value":"string"}],"intents":[{"name":"string"}],"appDetails":{"name":"string","aType":"string"}}],"message":"OK"}


## Altering Configuration
Configuration can be provided through system properties, property file or environment variables.
The "Configuration" system will search in the same order.  The following are configurations properties
that can be set.

| Property |  ENV Name | default | Description |
| -------- | -------- | ------ | ----------- |
| config.file | CONFIG_FILE | NONE | Configuration properties file to load at startup. Not required to run |
| json.files | JSON_FILE | "json" | Directory to store all local json files |
| war.file | WAR_FILE | "lib/appd-service.war" | War file for AppD POD Service |
| http.port | HTTP_PORT | "8080" | Default interface listening port |
| s3.enabled | S3_ENABLED | "false" | Enable AWS S3 support |
| s3.access.key | S3_ACCESS_KEY | NONE | AWS S3 AccessKey |
| s3.key.id | S3_KEY_ID | NONE | AWS Key |
| s3.region | S3_REGION | "us-east-1" | AWS region to use |
| s3.bucket | S3_BUCKET | NONE | S3 bucket name (do not prefix with s3:// ) |
| s3.json.prefix | S3_JSON_PREFIX | NONE | Prefix to add to bucket where json files are stored |
