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
