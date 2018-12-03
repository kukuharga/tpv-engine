package com.nuvola.tpv.model;




import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.security.core.GrantedAuthority;


/**
 * This class is used to represent available roles in the database.
 *
 * 
 */

//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
@NoArgsConstructor
public @Data class Role implements GrantedAuthority {
	

	private static final long serialVersionUID = -548810111180606468L;

	@Id
	private String name;

	private String description;
	
	private Set<String> allowedMenus = new HashSet<String>(); 
	
	
	public Role(final String name) {
		this.name = name;
	}


	@Override
	@Transient
	public String getAuthority() {
		return this.name;
	}


	

	

}
