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

import org.fdc3.appd.server.api.ApiResponseMessage;
import org.fdc3.appd.server.api.NotFoundException;
import org.fdc3.appd.server.api.V1ApiService;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * @author Frank Tarsillo on 7/5/18.
 */
public class AppDirectoryService extends V1ApiService {

    @Override
    public Response v1AppsAppIdGet(Long appId , SecurityContext securityContext) throws NotFoundException {

        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "MyTest")).build();
    }



}
