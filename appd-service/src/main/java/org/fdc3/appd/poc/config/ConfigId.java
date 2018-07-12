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

package org.fdc3.appd.poc.config;

public enum ConfigId {
    CONFIG_FILE(false),
    TRUSTSTORE_FILE("javax.net.ssl.trustStore",false),
    TRUSTSTORE_PASSWORD("javax.net.ssl.trustStorePassword",false),
    USER_CERT_FILE("javax.net.ssl.keyStore",false),
    USER_CERT_PASSWORD("javax.net.ssl.keyStorePassword",false),
    USER_EMAIL(false),
    RECEIVER_EMAIL(false),
    WAR_FILE(false),
    S3_ENABLED(false),
    S3_ACCESS_KEY(false),
    S3_KEY_ID(false),
    S3_BUCKET(false),
    S3_REGION(false),
    S3_COMPANY_PROFILE_PREFIX(false),
    JETTY_CONTEXTPATH(false),
    JETTY_PORT(false),
    S3_COMPANIES_PREFIX(false),
    S3_USER_GROUPS_PREFIX(false),
    S3_USER_GROUP_MAP_PREFIX(false),
    S3_TRANSCRIPTS_PREFIX(false);




    private final String altName;
    private final String propName;
    private final boolean core;

    ConfigId() {
        this(null, true);
    }

    ConfigId(String altName) {
        this(altName, true);
    }

    ConfigId(boolean core) {
        this(null, core);
    }

    ConfigId(String altName, boolean core) {
        this.altName = altName;
        propName = toPropName(name());
        this.core = core;
    }

    public static String toPropName(String name) {
        return name.toLowerCase().replaceAll("_", ".");
    }

    public static String toEnvName(String name) {
        return name.toUpperCase().replaceAll("\\.", "_");
    }

    public String getAltName() {
        return altName;
    }

    public String getPropName() {
        return propName;
    }

    public String getEnvName() {
        return name();
    }

    public boolean isCore() {
        return core;
    }

    @Override
    public String toString() {
        if (altName == null)
            return getPropName();

        return getPropName() + " (or " + getAltName() + ")";
    }
}
