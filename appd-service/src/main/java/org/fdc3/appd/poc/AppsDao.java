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

import org.fdc3.appd.poc.exceptions.DaoException;
import org.fdc3.appd.server.model.Application;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Frank Tarsillo on 7/9/18.
 */
public class AppsDao {

    private static AppsDao self;
    ConcurrentMap<String, Application> apps = new ConcurrentHashMap<>();


    public AppsDao() {
    }

    public static AppsDao get() {

        if (self == null) {
            self = new AppsDao();
        }

        return self;
    }


    public ConcurrentMap<String, Application> getApps()  {
        return apps;
    }

    public void setApps(ConcurrentMap<String, Application> apps) {
        this.apps = apps;
    }


    public Application getApp(String appId) throws DaoException {

        Application application = apps.get(appId);

        if (application == null)
            throw new DaoException("Application not found");

        return application;
    }

    public void setApp(Application application) throws DaoException {

        if (application == null)
            throw new DaoException("No application provided");

        if (application.getAppId() == null)
            application.setAppId(java.util.UUID.randomUUID().toString());

        apps.put(application.getAppId(), application);

    }




}
