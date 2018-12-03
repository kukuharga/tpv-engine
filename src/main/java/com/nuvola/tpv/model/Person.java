package com.nuvola.tpv.model;

import org.springframework.data.annotation.Id;
import java.util.List;

public class Person {

	@Id private String id;
	private String firstName;
	private String lastName;
    private List<String>favoriteColors;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
    
    public void setFavoriteColors(List<String>favoriteColors){
        this.favoriteColors = favoriteColors;
    }
    
    public List<String> getFavoriteColors(){
        return this.favoriteColors;
    }
}
