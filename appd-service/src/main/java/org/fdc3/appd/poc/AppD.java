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

/**
 * @author Frank Tarsillo on 7/5/18.
 */
public class AppD  {

    Configuration configuration = Configuration.get();

    public AppD() {

        init();


    }

    public static void main(String[] args) {
        new AppD();
    }


    void init(){
        Server server = new Server(8080);


        WebAppContext webapp = new WebAppContext();
        webapp.setWar( configuration.get(ConfigId.WAR_FILE,"appd-service/target/appd-service.war"));
        server.setHandler(webapp);

        try {

            try {
                System.out.println("Starting server");
                server.start();
            }catch(IllegalStateException e){

             e.printStackTrace();
            }


            server.join();


        } catch (Exception e) {

            e.printStackTrace();

        }
    }

}
