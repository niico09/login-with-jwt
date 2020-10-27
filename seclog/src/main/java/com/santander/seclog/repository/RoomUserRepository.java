package com.santander.seclog.repository;

import com.santander.seclog.entity.Room_User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomUserRepository extends JpaRepository<Room_User, Long> {


}
