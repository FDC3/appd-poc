# Application Directory POC

## Overview
This project is a POC focused on creation of an Application Directory service as defined through initial proposals and
ongoing discussions in the Application Directory FDC3 working group.  The purpose of this POC is to:
* Evaluate discoverability of the service through application identification
* Define an intial interface through an OpenAPI sepcification
* Start to ratify schema/models
* Generate example server stubs for java and nodejs
* Generate example client binding for java

This is NOT a ratified specification but rather a tool to evaluate both the use cases and technology implmentations.


## Build

Compilation requires **jdk 8+** and **maven**

Build:

    #git clone..*:
    #cd cloned_directory
    #mvn clean install package


## Directory structure

*  **appd-api/specification**:
    Contains the OpenAPI specification for interfaces and models
*  **appd-api/appd-java-server-stubs**:
    Contains the generated java server stubs (jaxrs/Jersey2) which the application server implements.  After build, you can reference the stubs using the following dependency.
     ```
     <dependency>
        <groupId>org.fdc3.appd</groupId>
        <artifactId>appd-server-stubs</artifactId>
        <version>0.0.1-SNAPSHOT</version>
     </dependency>
     ```
* **appd-api/appd-nodejs-server-stubs**:
    Contains the generated nodejs server stubs.
* **appd-api/appd-jersey-client**:
    Contains a java (jersey2) client binding to the application directory
* **appd-service**:
    The POC application directory service which implements the server stubs and exposes the interfaces.
    For the time being it will store all application data on local disk in JSON format.

