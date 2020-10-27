package com.santander.seclog.repository;

import com.santander.seclog.entity.enums.ROLE_NAME;
import com.santander.seclog.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(ROLE_NAME name);
}
