package com.nishikatakagi.ProductDigital.model;

import java.util.Date;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "Users")
public class User {
	public User() {
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	 int id;
	 String username;
	 String password;
	 String email;
	 @Column(columnDefinition = "NCHAR(10)")
	 String phone;
	 String firstName;
	 String lastName;
	 int roleId;
	 boolean isDeleted;
	 boolean isVerified;
	 Date deletedDate;
	 Integer deletedBy;
	 Date createdDate;
	 Integer createdBy;
	 Date lastUpdated;
	 Integer updatedBy;

	 @Override
	 public String toString() {
		 return "id " + id + ", username " + username + ", password " + password + ", email " + email + ", phone " + phone;
	 }
}