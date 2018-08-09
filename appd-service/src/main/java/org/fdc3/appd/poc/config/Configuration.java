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

import org.fdc3.appd.poc.exceptions.ProgramFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

/**
 * Configuration management
 * <p>
 * Will evaluate ENV and file properties
 *
 */
public class Configuration {
    private static Logger log = LoggerFactory.getLogger(Configuration.class);

    private Properties config = new Properties();

    private static Configuration configuration;

    public Configuration() {

        String configFile = get(ConfigId.CONFIG_FILE);

        if (configFile != null) {
            try (Reader reader = new FileReader(configFile)) {
                config.load(reader);
            } catch (FileNotFoundException e) {
                throw new ProgramFault("Config file \"" + configFile + "\" not found");
            } catch (IOException e) {
                throw new ProgramFault("Config file \"" + configFile + "\" cannot be loaded", e);
            }
        }

        StringBuilder s = null;

        for (ConfigId id : ConfigId.values()) {
            String v = get(id);
            if (v != null) {
                log.debug("{} = {}", id.getPropName(), v);
            } else if (id.isCore()) {
                if (s == null) {
                    s = new StringBuilder();
                } else {
                    s.append(",\n");
                }

                s.append(id);
            }
        }

        if (s != null)
            throw new ProgramFault("The following required properties are undefined:\n"
                    + s.toString());
    }

    public String get(ConfigId id) {
        String value = config.getProperty(id.getPropName());

        if (value == null)
            value = System.getProperty(id.getPropName());

        if (value == null)
            value = System.getenv(id.getEnvName());

        if (value == null && id.getAltName() != null)
            value = System.getProperty(id.getAltName());

        return value;
    }

    public String get(ConfigId id, String defaultValue) {
        String value = get(id);

        return (value == null) ? defaultValue : value;
    }

    public boolean getBoolean(ConfigId id, boolean  defaultValue) {
        String value = get(id);

        return (value == null) ? defaultValue : Boolean.valueOf(value);
    }

    public String getRequired(ConfigId id) {
        String value = get(id);

        if (value == null)
            throw new ProgramFault("Required config parameter \"" + id + "\" is undefined.");

        return value;
    }

    public String get(String id) {
        String value = config.getProperty(id);

        if (value == null)
            value = System.getProperty(ConfigId.toPropName(id));

        if (value == null)
            value = System.getenv(ConfigId.toEnvName(id));

        return value;
    }


    public String get(String id, String defaultValue) {
        String value = get(id);
        return (value == null) ? defaultValue : value;
    }


    public Integer getInt(ConfigId id, Integer defaultValue) {
        String value = get(id);
        return (value == null) ? defaultValue : Integer.valueOf(value);
    }


    public Integer getInt(String id, Integer defaultValue) {
        String value = get(id);
        return (value == null) ? defaultValue : Integer.valueOf(value);
    }


    public String getRequired(String id) {
        String value = get(id);

        if (value == null)
            throw new ProgramFault("Required config parameter \"" + id + "\" is undefined.");

        return value;
    }

    public static Configuration get() {

        if (configuration == null)
            configuration = new Configuration();

        return configuration;
    }

}
