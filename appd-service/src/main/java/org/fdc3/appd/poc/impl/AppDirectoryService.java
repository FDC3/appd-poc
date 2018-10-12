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

package org.fdc3.appd.poc.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.fdc3.appd.poc.dao.AppsDAO;
import org.fdc3.appd.poc.exceptions.DaoException;
import org.fdc3.appd.poc.model.UserSecurity;
import org.fdc3.appd.server.api.NotFoundException;
import org.fdc3.appd.server.api.V1ApiService;
import org.fdc3.appd.server.model.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;

/**
 * The AppD directory delegate for core interface
 *
 * @author Frank Tarsillo on 7/5/18.
 */
public class AppDirectoryService extends V1ApiService {

    private AppsDAO appsDao = AppsDAO.get();

    private Logger logger = LoggerFactory.getLogger(AppDirectoryService.class);

    @Override
    public Response v1AppsAppIdGet(String appId, SecurityContext securityContext) throws NotFoundException {


        JsonObject jo = new JsonObject();
        Response.Status status = Response.Status.OK;
        Gson gson = new GsonBuilder().create();

        try {

            Application application = appsDao.getApp(appId);


            jo.add("application", gson.toJsonTree(application));
            jo.addProperty("message", "OK");

        } catch (DaoException e) {
            e.printStackTrace();
            jo.addProperty("message", "application record not found");
            status = Response.Status.NOT_FOUND;


        }

        return Response.ok().entity(jo.toString()).status(status).build();
    }


    @Override
    public Response v1AppsPost(Application application, SecurityContext securityContext) throws NotFoundException {

        JsonObject jo = new JsonObject();
        Response.Status status = Response.Status.OK;
        UserSecurity userSecurity = (UserSecurity) securityContext.getUserPrincipal();

        Gson gson = new GsonBuilder().create();

        try {
            //Check basic requirements
            if (application == null || application.getName() == null || application.getAppId() == null || application.getManifest() == null)
                throw new DaoException("Application or required attributes not provided");

            //Check if publisher is same as requester
            if (!application.getPublisher().equalsIgnoreCase(userSecurity.getCompany())) {
                jo.addProperty("message", "You are not administrator for company/publisher= " + application.getPublisher());
                return Response.status(Response.Status.UNAUTHORIZED).entity(jo.toString()).build();
            }


            Application tApp=null;

            try {
                 tApp = appsDao.getApp(application.getAppId());
            }catch (DaoException e){
                logger.debug("Application does not exist...");
            }

            //Check if existing and publisher/company match for update
            if(tApp != null){

                if(!tApp.getPublisher().equalsIgnoreCase(userSecurity.getCompany())){
                    jo.addProperty("message", "Unable to update application.  You are not administrator for company/publisher= " + tApp.getPublisher());
                    return Response.status(Response.Status.UNAUTHORIZED).entity(jo.toString()).build();
                }
            }

                //upsert
                appsDao.setApp(application);
                jo.add("application", gson.toJsonTree(application));
                jo.addProperty("message", "OK");



        } catch (DaoException e) {
            e.printStackTrace();
            jo.addProperty("message", "application not added or modified, make sure to provide an application model with required attributes (appId,Name,manifest)");
            status = Response.Status.NOT_MODIFIED;


        }

        return Response.ok().entity(jo.toString()).status(status).build();
    }

    @Override
    public Response v1AppsSearchGet(SecurityContext securityContext) throws NotFoundException {

        JsonObject jo = new JsonObject();
        Response.Status status = Response.Status.OK;
        Gson gson = new GsonBuilder().create();

        UserSecurity userSecurity = (UserSecurity) securityContext.getUserPrincipal();

        logger.debug("{}", userSecurity.toString());

        List<Application> applications = new ArrayList<>(appsDao.getApps().values());

        jo.add("applications", gson.toJsonTree(applications));
        jo.addProperty("message", "OK");


        return Response.ok().entity(jo.toString()).status(status).build();

    }


}
