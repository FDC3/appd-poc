#!/bin/sh
git clone https://github.com/FDC3/appd-api.git
cd appd-api
mvn clean install
cd ..
mvn clean install
