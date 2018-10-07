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

package org.fdc3.appd.poc.api;

import com.google.gson.Gson;
import org.fdc3.appd.poc.dao.UserDAO;
import org.fdc3.appd.poc.dao.UserDAOFactory;
import org.fdc3.appd.poc.exceptions.UserExistingException;
import org.fdc3.appd.poc.exceptions.UserNotFoundException;
import org.fdc3.appd.poc.filter.AuthenticationFilter;
import org.fdc3.appd.poc.model.Credentials;
import org.fdc3.appd.poc.model.User;
import org.fdc3.appd.poc.model.UserSecurity;
import org.fdc3.appd.poc.security.PasswordSecurity;
import org.fdc3.appd.poc.security.TokenSecurity;


import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This endpoint API supports all user level capabilities including CRUD and token management
 *
 * This class was derived from tutorial https://github.com/maltesander/rest-jersey2-json-jwt-authentication
 *
 */
@DeclareRoles({"admin", "user", "guest"})
@Path("/user")
public class UserRestService  {

    @POST
    @Path("/create")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(UserSecurity userSecurity) {
        UserDAO userDao = UserDAOFactory.getUserDAO();

        try {
            try {
                // check if user no registered already
                userDao.getUserIdByEmail(userSecurity.getEmail());
                throw new UserExistingException(userSecurity.getEmail());
            } catch (UserNotFoundException e) {
                // standard user role
                userSecurity.setRole("user");
                // store plain password
                String plainPassword = userSecurity.getPassword();
                // generate password
                userSecurity.setPassword(PasswordSecurity.generateHash(userSecurity.getPassword()));
                // create user
                userDao.createUser(userSecurity);
                // authenticate user
                return authenticate(new Credentials(userSecurity.getEmail(), plainPassword));
            }
        } catch (UserExistingException e) {
            return Response.status(Response.Status.CONFLICT).entity(Response.Status.CONFLICT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/authenticate")
    @PermitAll
    @Produces("application/json")
    @Consumes("application/json")
    public Response authenticate(Credentials credentials) {
        UserDAO userDao = UserDAOFactory.getUserDAO();

        try {
            String id = userDao.getUserIdByEmail(credentials.getEmail());
            UserSecurity userSecurity = userDao.getUserAuthentication(id);

            if (PasswordSecurity.validatePassword(credentials.getPassword(), userSecurity.getPassword()) == false) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            // generate a token for the user
            String token = TokenSecurity.generateJwtToken(id);

            // write the token to the database
            UserSecurity sec = new UserSecurity(null, token);
            sec.setId(id);
            userDao.setUserAuthentication(sec);

            Map<String, String> map = new HashMap<>();
            map.put(AuthenticationFilter.AUTHORIZATION_PROPERTY, token);


            // Return the token on the response
           // return Response.status(Response.Status.OK).entity(new Gson().toJsonTree(map)).build();
            return Response.status(Response.Status.OK).entity(token).build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }

    }

    @GET
    @Path("/get")
    @RolesAllowed({"admin", "user"})
    @Produces("application/json")
    public Response get(@Context HttpHeaders headers) {
        UserDAO userDao = UserDAOFactory.getUserDAO();

        try {
            String id = getId(headers);

            // use decoded email from jwt in header
            User user = userDao.getUser(id);

            // Return the user on the response
            return Response.status(Response.Status.OK).entity(new Gson().toJson(user)).build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/getAll")
    @RolesAllowed({"admin"}) // only an admin user should be allowed to request all users
    @Produces("application/json")
    public Response getAll(@Context HttpHeaders headers) {
        UserDAO userDao = UserDAOFactory.getUserDAO();

        try {
            List<User> usersJson = new ArrayList<>(userDao.getAllUsers());

            // Return the users on the response
            return Response.status(Response.Status.OK).entity(new Gson().toJsonTree(usersJson)).build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }

    }

    @POST
    @Path("/update")
    @RolesAllowed({"admin", "user"}) // only an admin user should be allowed to request all users
    @Produces("application/json")
    public Response update(@Context HttpHeaders headers, User user) {
        UserDAO userDao = UserDAOFactory.getUserDAO();

        try {
            String id = getId(headers);

            user.setId(id);
            userDao.updateUser(user);

            // Return the token on the response
            return Response.status(Response.Status.OK).entity("User updated").build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }

    }

    @DELETE
    @Path("/delete")
    @RolesAllowed({"admin", "user"})
    @Produces("application/json")
    public Response delete(@Context HttpHeaders headers) {
        UserDAO userDao = UserDAOFactory.getUserDAO();

        try {
            String id = getId(headers);

            userDao.deleteUser(id);

            // Return the response
            // Return the token on the response
            return Response.status(Response.Status.OK).entity("User deleted").build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }

    }

    private String getId(HttpHeaders headers) {
        // get the email we set in AuthenticationFilter
        List<String> id = headers.getRequestHeader(AuthenticationFilter.HEADER_PROPERTY_ID);

        if (id == null || id.size() != 1)
            throw new NotAuthorizedException("Unauthorized!");

        return id.get(0);
    }

}