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


import org.fdc3.appd.poc.config.Configuration;
import org.fdc3.appd.poc.dao.impl.UserDAOImpl;

public class UserDAOFactory {
	Configuration config = Configuration.get();
	private static UserDAO userDAO = new UserDAOImpl();

    public static UserDAO getUserDAO() {

        return userDAO;

    }
}
