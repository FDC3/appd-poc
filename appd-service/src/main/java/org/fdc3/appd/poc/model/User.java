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

package org.fdc3.appd.poc.model;

import java.security.Principal;

public class User implements Principal {
	
	// global unique identifier
	private String id = null;
	private String email = null;
	private String firstname = null;
	private String lastname = null;
	private String company = null;
	private String name = null;

	public User() {}
	
	public User( String email, String firstname, String lastname ) {			
		this.email = email;
		this.firstname = firstname;
		this.lastname = lastname;
	}
	
	public User( String id, String email, String firstname, String lastname, String company ) {
		this.id = id;
		this.email = email;
		this.firstname = firstname;
		this.lastname = lastname;
		this.company = company;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}


	public String getName() {
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", email=" + email + ", firstname=" + firstname + ", lastname=" + lastname + ", company=" + company + "]";
	}




}
