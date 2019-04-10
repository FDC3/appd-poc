[![Build Status](https://travis-ci.org/FDC3/appd-poc.svg)](https://travis-ci.org/FDC3/appd-poc/)
# Application Directory POC

**This is NOT a ratified specification but rather a tool to evaluate both the use cases and technology implmentations.**


## Overview
This project is a POC focused on creation of an Application Directory service as defined through initial proposals and
ongoing discussions in the Application Directory FDC3 working group.  The purpose of this POC is to:

* Evaluate discoverability of the service through application identification
* Implementation of the [Application Directory API](https://github.com/FDC3/FDC3/tree/master/src/app-directory) .  Please see [full specification](https://fdc3.finos.org) .
* Support general demonstrations


## Build

Compilation requires **jdk 8+** and **maven**

Build:

    #git clone..*:
    #cd cloned_directory
    #./make


## Directory structure

* **appd-service**:
    The POC application directory service which implements the server stubs and exposes the interfaces.
    For the time being it will store all application data on local disk in JSON format.


## Run it in Docker!
The default listening port will be 8080.

Consider mounting a local filesystem to /json (-v (local_json_dir):/json) to recover json data.

    # clone..*
    # cd cloned_directory
    # ./runAppDInDocker



## Altering Configuration
Configuration can be provided through system properties, property file or environment variables.
The "Configuration" system will search in the same order.  The following are configurations properties
that can be set.  Note: all serialized files will be written to local disk regardless of S3 configuration.

| Property |  ENV Name | default | Description |
| -------- | -------- | ------ | ----------- |
| config.file | CONFIG_FILE | NONE | Configuration properties file to load at startup. Not required to run |
| json.users.dir | JSON_USERS_DIR | "json/users" | Directory to store serialized user json files |
| json.apps.dir | JSON_APPS_DIR | "json/apps" | Directory to store serialized application json files |
| war.file | WAR_FILE | "lib/appd-service.war" | War file for AppD POD Service |
| http.port | HTTP_PORT | "8080" | Default interface listening port |
| s3.enabled | S3_ENABLED | "false" | Enable AWS S3 support |
| s3.access.key | S3_ACCESS_KEY | NONE | AWS S3 AccessKey |
| s3.key.id | S3_KEY_ID | NONE | AWS Key |
| s3.region | S3_REGION | "us-east-1" | AWS region to use |
| s3.bucket | S3_BUCKET | NONE | S3 bucket name (do not prefix with s3:// ) |
| s3.json.prefix | S3_JSON_PREFIX | NONE | Prefix to add to bucket where json files are stored |
| s3.json.users.prefix | S3_JSON_USERS_PREFIX | "json/users" | Prefix to add to bucket name where serialized json user files are stored |
| s3.json.apps.prefix | S3_JSON_APPS_PREFIX | "json/applications" |Prefix to add to bucket name where serialized application definitions are stored |