///*
// *
// *
// *  Copyright (C) 2018 IHS Markit.
// *  All Rights Reserved
// *
// *
// *  NOTICE:  All information contained herein is, and remains
// *  the property of IHS Markit and its suppliers,
// *  if any.  The intellectual and technical concepts contained
// *  herein are proprietary to IHS Markit and its suppliers
// *  and may be covered by U.S. and Foreign Patents, patents in
// *  process, and are protected by trade secret or copyright law.
// *  Dissemination of this information or reproduction of this material
// *  is strictly forbidden unless prior written permission is obtained
// *  from IHS Markit.
// */
//
//package org.fdc3.appd.poc.filter;
//
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.annotation.Priority;
//import javax.annotation.security.DenyAll;
//import javax.annotation.security.PermitAll;
//import javax.ws.rs.Priorities;
//import javax.ws.rs.container.ContainerRequestContext;
//import javax.ws.rs.container.ResourceInfo;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.MultivaluedMap;
//import javax.ws.rs.core.Response;
//import javax.ws.rs.ext.Provider;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.List;
//
//
///**
// * The AuthenticationFilter verifies the access permissions for a user based on the provided jwt token
// * and role annotations
// **/
//@Provider
//@Priority( Priorities.AUTHENTICATION )
//public class AuthenticationFilterOrig implements javax.ws.rs.container.ContainerRequestFilter
//{
//
//    private Logger logger = LoggerFactory.getLogger(AuthenticationFilterOrig.class);
//
//    @Context
//    private ResourceInfo resourceInfo;
//
//    public static final String HEADER_PROPERTY_ID = "id";
//    public static final String AUTHORIZATION_PROPERTY = "x-access-token";
//
//    // Do not use static responses, rebuild reponses every time
//    private static final String ACCESS_REFRESH = "Token expired. Please authenticate again!";
//    private static final String ACCESS_INVALID_TOKEN = "Token invalid. Please authenticate again!";
//    private static final String ACCESS_DENIED = "Not allowed to access this resource!";
//    private static final String ACCESS_FORBIDDEN = "Access forbidden!";
//
//    public AuthenticationFilterOrig(ResourceInfo resourceInfo) {
//        super();
//        this.resourceInfo = resourceInfo;
//    }
//
//    public AuthenticationFilterOrig() {
//
//    }
//
//    @Override
//    public void filter( ContainerRequestContext requestContext )
//    {
//        Method method = resourceInfo.getResourceMethod();
//        // everybody can access (e.g. user/create or user/authenticate)
//        if( !method.isAnnotationPresent( PermitAll.class ) )
//        {
//            // nobody can access
//            if( method.isAnnotationPresent( DenyAll.class ) )
//            {
//                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN.getStatusCode(),ACCESS_FORBIDDEN).build());
//                return;
//            }
//
//            // get request headers to extract jwt token
//            final MultivaluedMap<String, String> headers = requestContext.getHeaders();
//            final List<String> authProperty = headers.get( AUTHORIZATION_PROPERTY );
//
//            // block access if no authorization information is provided
//            if( authProperty == null || authProperty.isEmpty() )
//            {
//                logger.warn("No token provided!");
//            //    requestContext.abortWith(Response.status( Response.Status.UNAUTHORIZED.getStatusCode(), ACCESS_DENIED ).build() );
//           //     return;
//            }
//
//            String id = "user@company.com" ;
////            String jwt = authProperty.get(0);
////
////            logger.debug("Authorization (x-access-token): {}", jwt);
//
//            //try to decode the jwt - deny access if no valid token provided
////            try {
////                id = TokenSecurity.validateJwtToken( jwt );
////            } catch ( InvalidJwtException e ) {
////                logger.warn("Invalid token provided!");
////                requestContext.abortWith(
////                        ResponseBuilder.createResponse( Response.Status.UNAUTHORIZED, ACCESS_INVALID_TOKEN )
////                );
////                return;
////            }
//
//            // check if token matches an user token (set in user/authenticate)
////            UserDAO userDao = UserDAOFactory.getUserDAO();
////            User user = null;
////            try {
////                user = userDao.getUser( id );
////            }
////            catch ( UserNotFoundException e ) {
////                logger.warn("Token missmatch!");
////                requestContext.abortWith(
////                        ResponseBuilder.createResponse( Response.Status.UNAUTHORIZED, ACCESS_DENIED )
////                );
////                return;
////            }
////
////            UserSecurity userSecurity = userDao.getUserAuthentication( user.getId() );
////
////            // token does not match with token stored in database - enforce re authentication
////            if( !userSecurity.getToken().equals( jwt ) ) {
////                logger.warn("Token expired!");
////                requestContext.abortWith(
////                        ResponseBuilder.createResponse( Response.Status.UNAUTHORIZED, ACCESS_REFRESH )
////                );
////                return;
////            }
////
////            // verify user access from provided roles ("admin", "user", "guest")
////            if( method.isAnnotationPresent( RolesAllowed.class ) )
////            {
////                // get annotated roles
////                RolesAllowed rolesAnnotation = method.getAnnotation( RolesAllowed.class );
////                Set<String> rolesSet = new HashSet<String>( Arrays.asList( rolesAnnotation.value() ) );
////
////                // user valid?
////                if( !isUserAllowed( userSecurity.getRole(), rolesSet ) )
////                {
////                    logger.warn("User does not have the rights to acces this resource!");
////                    requestContext.abortWith(
////                            ResponseBuilder.createResponse( Response.Status.UNAUTHORIZED, ACCESS_DENIED )
////                    );
////                    return;
////                }
////            }
//
//            // set header param email for user identification in rest service - do not decode jwt twice in rest services
//            List<String> idList = new ArrayList<String>();
//            idList.add( id );
//            headers.put( HEADER_PROPERTY_ID, idList );
//        }
//    }
//
//
//}