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

package org.fdc3.appd.poc.dao;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.gson.Gson;
import org.fdc3.appd.poc.config.ConfigId;
import org.fdc3.appd.poc.config.Configuration;
import org.fdc3.appd.poc.exceptions.UserExistingException;
import org.fdc3.appd.poc.exceptions.UserNotFoundException;
import org.fdc3.appd.poc.model.User;
import org.fdc3.appd.poc.model.UserSecurity;
import org.fdc3.appd.poc.util.AwsS3Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Manage all user object access and persist through S3.
 * <p>
 * This DAO will preLoad all known users from S3 upon access and maintain cache from that point forward.
 * <p>
 * Currently, this does not support lazy loading.
 *
 * @author Frank Tarsillo on 8/28/18.
 */
public class UserS3DAO implements UserDAO {


    private Configuration config = Configuration.get();
    private Logger logger = LoggerFactory.getLogger(UserS3DAO.class);
    private ConcurrentMap<String, UserSecurity> users = new ConcurrentHashMap<>();

    //PreLoad all users from cache
    {
        preLoad();
    }


    /**
     * Create a new user if one doesn't already exist.
     *
     * @param user The full user object including security status.
     * @return True if user is created
     * @throws UserExistingException User already exists
     */
    @Override
    public boolean createUser(UserSecurity user) throws UserExistingException {


        if (user == null || user.getEmail() == null || user.getPassword() == null || user.getFirstname() == null || user.getLastname() == null) {
            logger.error("Insufficient attributes to create new user");
            return false;
        }


        //Need to set an internal GUID
        user.setId(java.util.UUID.randomUUID().toString());

        //Deep updates include updating the individual attributes
        return deepUpdateUser(user);


    }

    /**
     * Get a user ID by email address from cache
     *
     * @param email Email of user
     * @return GUID
     * @throws UserNotFoundException User is not in cache or defined
     */
    @Override
    public String getUserIdByEmail(String email) throws UserNotFoundException {

        if (email == null)
            return null;

        //Quick search of cache.  I'm sure this can be optimized, but this is only a POC
        UserSecurity userSecurity = users.values().stream().filter(userSecurity1 -> userSecurity1.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);

        if (userSecurity != null)
            return userSecurity.getId();

        //Not found, so throw it.
        throw new UserNotFoundException("User not found [" + email + "]");
    }

    /**
     * Return user object by ID from cache
     * <p>
     * User object is a subset of the full security state of the user
     *
     * @param id GUID for user, which was generated when the user was created
     * @return {@link User} for the given GUID
     * @throws UserNotFoundException User not found
     */
    @Override
    public User getUser(String id) throws UserNotFoundException {
        if (id == null)
            return null;

        //From cache
        return users.get(id);
    }

    /**
     * Retrieve list of users without security context
     *
     * @return List of {@link User} without any security context
     */
    @Override
    public List<User> getAllUsers() {

        //Convert from cache
        return new ArrayList<>(users.values());


    }


    /**
     * Get user with security context for authentication purposes
     *
     * @param id GUID of user
     * @return {@link UserSecurity} which includes security context
     * @throws UserNotFoundException User is not found
     */
    @Override
    public UserSecurity getUserAuthentication(String id) throws UserNotFoundException {


        //From cache
        if (users.get(id) != null)
            return users.get(id);

        throw new UserNotFoundException("User was not found from id [" + id + "]");
    }


    /**
     * Set user security context including token and password
     *
     * @param user User definition with security context
     * @return True if the user is updated
     * @throws UserNotFoundException User not found
     */
    @Override
    public boolean setUserAuthentication(UserSecurity user) throws UserNotFoundException {

        if (user == null)
            return false;


        if (users.get(user.getId()) != null) {

            //copy the token
            if (user.getToken() != null)
                users.get(user.getId()).setToken(user.getToken());

            //copy the password
            if (user.getPassword() != null)
                users.get(user.getId()).setPassword(user.getPassword());

            //Update user cache
            deepUpdateUser(users.get(user.getId()));
            return true;
        }

        return false;
    }

    /**
     * Update user attributes without security context
     * @param user User defintion
     * @return True if updated
     * @throws UserNotFoundException User not found
     */

    @Override
    public boolean updateUser(User user) throws UserNotFoundException {


        if (user == null || user.getEmail() == null || user.getFirstname() == null || user.getLastname() == null) {
            logger.error("Insufficient attributes to update new user");
            return false;

        }

        UserSecurity userSecurity = users.get(user.getId());

        if (userSecurity == null)
            return false;

        if (user.getEmail() != null)
            userSecurity.setEmail(user.getEmail());

        if (user.getLastname() != null)
            userSecurity.setLastname(user.getLastname());

        if (user.getFirstname() != null)
            userSecurity.setFirstname(user.getFirstname());


        return deepUpdateUser(userSecurity);
    }

    @Override
    public boolean deleteUser(String id) throws UserNotFoundException {
        return false;
    }


    /**
     * Pre-load all users from S3 into cache
     *
     */
    private void preLoad() {

        try {
            AwsS3Client awsS3Client = new AwsS3Client();

            Gson gson = new Gson();

            logger.debug("Attempting to load cache from S3 [{}/{}]", config.get(ConfigId.S3_BUCKET), config.get(ConfigId.S3_JSON_USERS_PREFIX));
            List<S3ObjectSummary> allObjects = awsS3Client.getAllObjects(config.get(ConfigId.S3_BUCKET, ""), config.get(ConfigId.S3_JSON_USERS_PREFIX, "json"));

            if (allObjects != null) {
                for (S3ObjectSummary objectSummary : allObjects) {

                    if (!objectSummary.getKey().contains(".json"))
                        continue;

                    UserSecurity user = gson.fromJson(new InputStreamReader(awsS3Client.getObject(objectSummary)), UserSecurity.class);
                    users.put(user.getId(), user);


                }
            }
        } catch (Exception e) {
            logger.error("S3 Exception loading all users from {}", ConfigId.S3_JSON_USERS_PREFIX, e);
        }

    }


    /**
     * Update user with security context updating both cache and persist (S3)
     * @param userSecurity User with security context
     * @return True if user updated to persist
     */
    private boolean deepUpdateUser(UserSecurity userSecurity) {


        Gson gson = new Gson();

        try {
            if (config.getBoolean(ConfigId.S3_ENABLED, false)) {

                AwsS3Client awsS3Client = new AwsS3Client();
                awsS3Client.putObject(
                        config.get(ConfigId.S3_BUCKET, ""),
                        Paths.get(config.get(ConfigId.S3_JSON_USERS_PREFIX, ""), userSecurity.getId() + ".json").toString(),
                        new ByteArrayInputStream(gson.toJson(userSecurity).getBytes(StandardCharsets.UTF_8)),
                        null);
            }

            users.putIfAbsent(userSecurity.getId(), userSecurity);
            return true;
        } catch (Exception e) {
            logger.error("S3 Exception writing user file for {}", userSecurity.getEmail(), e);
        }

        return false;
    }
}
