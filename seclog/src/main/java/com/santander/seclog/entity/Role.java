package com.santander.seclog.entity;

import com.santander.seclog.entity.enums.ROLE_NAME;

import javax.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private ROLE_NAME name;

	public Role() {

	}

	public Role(ROLE_NAME name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ROLE_NAME getName() {
		return name;
	}

	public void setName(ROLE_NAME name) {
		this.name = name;
	}
}