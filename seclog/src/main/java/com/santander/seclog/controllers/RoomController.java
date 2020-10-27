package com.santander.seclog.controllers;

import com.santander.seclog.entity.Room;
import com.santander.seclog.entity.Room_User;
import com.santander.seclog.entity.enums.Response_Enum;
import com.santander.seclog.payload.response.MessageResponse;
import com.santander.seclog.repository.RoomRepository;
import com.santander.seclog.service.DeterminateBeer;
import com.santander.seclog.service.MeetingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.io.IOException;
import java.text.ParseException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/meetings")
public class RoomController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomController.class);

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    MeetingsService service;

    @Autowired
    DeterminateBeer determinateBeer;

    @GetMapping("/roomReserved")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    ResponseEntity<?> findRoomReserved() {
        return service.getAllRoomReserved();
    }

    @GetMapping("/rooms/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    ResponseEntity<?> findOne(@PathVariable @Min(1) Long id) {

        LOGGER.info("Request: id:{}" , id);
        return service.getOneRoom(id);
    }


    @GetMapping("/rooms")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    ResponseEntity<?> findAllRoom() {
        return ResponseEntity.ok().body(
                new MessageResponse(Response_Enum.SUCCESSFUL.getData(),
                        roomRepository.findAll().toString()));
    }

    @PostMapping("/reserve/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<?> reserveRoom(@RequestBody Room newRoom, @PathVariable Long id) throws ParseException {

        LOGGER.info("Request to new meeting: {}", newRoom);
        return service.reserveRoom(id, newRoom.getHourStart(), newRoom.getHourEnd());
    }

    @PutMapping("/inscript")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    ResponseEntity<?> suscribeMeeting(@RequestBody Room_User update) {

        LOGGER.info("Request to incript in the meeting: {}", update);
        return service.suscribeInMeeting(update.getRoom_id(), update.getUser_id());
    }

    @GetMapping("/beers/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<?> geetBeersNecesary(@PathVariable Long id) throws ParseException {

        LOGGER.info("Request to how beers needs the meeting: id: {}", id);
        return service.getBeersForMeeting(id);
    }


    @GetMapping("/weather/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    ResponseEntity<?> getWeather(@PathVariable Long id) throws IOException, InterruptedException {
        LOGGER.info("Request to weahter in the meeting: id: {}", id);
        return service.getWeather(id);
    }

    @PostMapping("/rooms")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<?> createRoom(@Valid @RequestBody Room newRoom){
        LOGGER.info("Request to create a new room: {}", newRoom);

        return service.createRoom(newRoom);
    }

    @PutMapping("/checkin")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    ResponseEntity<?> checkin(@Valid @RequestBody Room_User room_user){
        LOGGER.info("Request to update checkin: {}", room_user);

        return service.checkin(room_user.getUser_id(), room_user.getRoom_id());
    }

}
