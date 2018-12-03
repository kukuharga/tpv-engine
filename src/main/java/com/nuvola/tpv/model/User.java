package com.nuvola.tpv.model;


import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import lombok.Data;
import lombok.NoArgsConstructor;



//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class)
@NoArgsConstructor
public @Data class User {

	

	@Id
	private String username;

	private String password; // required
	

	private String passwordHint;

	private String firstName; // required

	private String lastName; // required

	private String email; // required; unique

	private String phoneNumber;
	
	private boolean enabled;
	
	private boolean accountExpired;

	private boolean accountLocked;

	private boolean credentialsExpired;
	
	private Set<String> roles;
	
	private String manager;
	
	private Set<String>groups = new HashSet<String>();

	public User(String username) {
		super();
		this.username = username;
	}
	
	public String getFullName() {
		return this.firstName + " " +this.lastName;
	}
	
	



}
