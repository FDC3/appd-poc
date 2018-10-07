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


import org.fdc3.appd.poc.exceptions.UserExistingException;
import org.fdc3.appd.poc.exceptions.UserNotFoundException;
import org.fdc3.appd.poc.model.User;
import org.fdc3.appd.poc.model.UserSecurity;

import java.util.List;

public interface UserDAO {
	 boolean createUser(UserSecurity user) throws UserExistingException;

	 String getUserIdByEmail(String email) throws UserNotFoundException;
	 User getUser(String id) throws UserNotFoundException;

	 List<User> getAllUsers();

	 UserSecurity getUserAuthentication(String id) throws UserNotFoundException;
	 boolean setUserAuthentication(UserSecurity user) throws UserNotFoundException;

	 boolean updateUser(User user) throws UserNotFoundException;
	 boolean deleteUser(String id) throws UserNotFoundException;
}
