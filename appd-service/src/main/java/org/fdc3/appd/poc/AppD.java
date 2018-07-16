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

package org.fdc3.appd.poc;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.fdc3.appd.poc.config.ConfigId;
import org.fdc3.appd.poc.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AppD stands for Application Directory
 *
 * This is a reference implementation of the Application Directory specification which services the defined interfaces
 * and models.  This is intended to be a simple POC of a service to help establish the specification itself and should
 * not be used as a production capability.
 *
 * AppD initialization
 *      Run embedded Jetty server...
 *
 *
 * @author Frank Tarsillo on 7/5/18.
 */
public class AppD {

    private Configuration configuration = Configuration.get();
    private Logger logger = LoggerFactory.getLogger(AppD.class);

    public AppD() {

        init();


    }

    public static void main(String[] args) {
        new AppD();
    }


    /**
     * Init the jetty embedded server and use the configured war
     *
     */
    private void init() {
        Server server = new Server(8080);


        WebAppContext webapp = new WebAppContext();
        webapp.setWar(configuration.get(ConfigId.WAR_FILE, "lib/appd-service.war"));
        server.setHandler(webapp);


        try {
            logger.info("Starting Application Directory service....");
            server.start();
            server.join();
        } catch (Exception e) {

            logger.error("Failed to start service..", e);
        }

    }

}
