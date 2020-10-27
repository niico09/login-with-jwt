package com.santander.seclog.repository;

import com.santander.seclog.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByLocation(String location);

    Boolean existsByLocation(String location);

    boolean existsById(Long id);
}
