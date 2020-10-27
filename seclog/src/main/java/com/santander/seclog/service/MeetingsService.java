package com.santander.seclog.service;

import com.santander.seclog.entity.Room;
import com.santander.seclog.entity.Room_User;
import com.santander.seclog.entity.User;
import com.santander.seclog.entity.enums.ROOM_STATE;
import com.santander.seclog.entity.enums.Response_Enum;
import com.santander.seclog.payload.response.MessageResponse;
import com.santander.seclog.repository.RoomRepository;
import com.santander.seclog.repository.RoomUserRepository;
import com.santander.seclog.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class MeetingsService {

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    DeterminateBeer determinateBeer;

    @Autowired
    Weather weather;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoomUserRepository repository_compuesto;

    private final String pattern = "yyyy-MM-dd'T'HH:mm:ss";
    private static final Logger LOGGER = LoggerFactory.getLogger(MeetingsService.class);

    public ResponseEntity getWeather(final Long id) throws IOException, InterruptedException {

        Optional<Room> room = roomRepository.findById(id);

        if (room.isPresent()) {
            LOGGER.info("The room is: {}", room.get().toString());
            int temp = weather.getTemperatureDay(room.get().getHourStart());

            LOGGER.info("In the meeting it will be {} grades ", temp);

            return ResponseEntity.ok().body(new MessageResponse(Response_Enum.SUCCESSFUL.getData(),
                    "In the day, the meetings start at: " + room.get().getHourStart()
                            + " and it's " + temp + " grades"));
        } else {
            return ResponseEntity.badRequest().body(
                    new MessageResponse(Response_Enum.ERROR.getData(),
                            "The room id doesn't exist"));
        }

    }

    public ResponseEntity getAllRoomReserved() {

        List<Room> getRoomReserved = roomRepository.findAll();
        List<Room> listRoomReserved = new ArrayList<>();

        for (Room room : getRoomReserved) {
            if (room.getState() != null &&
                    room.getState().equals(ROOM_STATE.RESERVED.name())) {
                listRoomReserved.add(room);
            }
        }

        LOGGER.info("ListRoomReserver: {}", listRoomReserved);

        return ResponseEntity.ok().body(
                new MessageResponse(Response_Enum.SUCCESSFUL.getData(),
                        listRoomReserved.toString()));
    }

    public ResponseEntity<?> getOneRoom(Long id) {

        Optional<Room> room = roomRepository.findById(id);

        LOGGER.info("The room is: {}", room);

        if (room.isPresent()) {
            return ResponseEntity.ok().body(
                    new MessageResponse(Response_Enum.SUCCESSFUL.getData(),
                            room.get().toString()));
        } else {
            return ResponseEntity.badRequest().body(
                    new MessageResponse(Response_Enum.ERROR.getData(),
                            "This room doesn't exist"));
        }

    }

    public ResponseEntity<?> reserveRoom(Long id, String hourStart, String hourEnd) throws ParseException {

        Optional<Room> roomToReserved = roomRepository.findById(id);

        if (roomToReserved.isPresent()) {
            LOGGER.info("Room's reserved: {}", roomToReserved);

            Date dateStart = new SimpleDateFormat(pattern).parse(hourStart);
            String parse = dateStart.toString().substring(11, 19);

            if (checkHourStart(parse.replace(":", ""))) {
                List<Room> getRoomReserved = roomRepository.findAll();
                List<Room> listRoomReserved = new ArrayList<>();

                //obtiene la misma sala reservada pero en otro horario
                for (Room room : getRoomReserved) {
                    if ((room.getState() != null) && room.getState().equals(ROOM_STATE.RESERVED.name()) &&
                            room.getLocation().equals(roomToReserved.get().getLocation())) {
                        listRoomReserved.add(room);
                    }
                }

                LOGGER.info("ListRoomReserved: {}", listRoomReserved);

                try {
                    if (checkHourReserved(listRoomReserved, hourStart, hourEnd)) {
                        Room newMeeting = new Room();
                        newMeeting.setLocation(roomToReserved.get().getLocation());
                        newMeeting.setHourStart(hourStart);
                        newMeeting.setHourEnd(hourEnd);
                        newMeeting.setState(ROOM_STATE.RESERVED.name());

                        LOGGER.info("The new meeting is: {}", newMeeting.toString());

                        return ResponseEntity.ok().body(
                                new MessageResponse(Response_Enum.SUCCESSFUL.getData(),
                                        roomRepository.save(newMeeting).toString()));
                    } else {
                        return ResponseEntity.badRequest().body(
                                new MessageResponse(Response_Enum.ERROR.getData(),
                                        "This hour is already reserved, please retry in different hour"));
                    }
                } catch (ParseException e) {
                    LOGGER.warn("Error ocurred in the process, requestParameters: " +
                            "id={},hourStart={},hourEnd={}", id, hourStart, hourEnd, e);

                    return ResponseEntity.badRequest().body(
                            new MessageResponse(Response_Enum.ERROR.getData(),
                                    "Bad request: id=" + id + ",hourStart=" + hourStart
                                            + ", hourEnd=" + hourEnd));
                }
            } else {
                return null;
            }
        } else {
            return ResponseEntity.badRequest().body(
                    new MessageResponse(Response_Enum.ERROR.getData(),
                            "This room doesn't exist"));
        }
    }

    private Calendar calendar = Calendar.getInstance();

    @Value("${santander.enableWeekend}")
    Boolean weekend;

    private boolean checkHourStart(String parse) {

        if (Long.parseLong(parse) > 90000 && Long.parseLong(parse) < 180000) {
            return true;
        }

        return false;
    }

    private boolean checkHourReserved(List<Room> listRoomReserved, String hourStart, String hourEnd)
            throws ParseException {
        boolean accept = false;

        Date dateStart = new SimpleDateFormat(pattern).parse(hourStart);
        Date dateEnd = new SimpleDateFormat(pattern).parse(hourEnd);
        calendar.setTime(dateStart);


        if (!weekend && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return false;
        }

        if (listRoomReserved.isEmpty()) {
            return true;
        }
        if (dateStart.after(new Date())) {
            for (Room room : listRoomReserved) {
                Date meetingStart = new SimpleDateFormat(pattern).parse(room.getHourStart());
                Date meetingEnd = new SimpleDateFormat(pattern).parse(room.getHourEnd());

                if (dateStart.before(dateEnd) && dateStart.after(meetingEnd) || dateEnd.before(meetingStart)) {
                    accept = true;
                } else if (dateStart.before(meetingEnd) && dateStart.after(meetingStart)) {
                    accept = false;
                }
            }
        }


        return accept;
    }

    public ResponseEntity<?> suscribeInMeeting(final long room_id, final long user_id) {

        Optional<Room> room = roomRepository.findById(room_id);
        Optional<User> user = userRepository.findById(user_id);

        LOGGER.info("The user id: {}, and the room id {}", user, room);

        if (room.isPresent() && user.isPresent() &&
                room.get().getState() != null &&
                room.get().getState().equals(ROOM_STATE.RESERVED.name())) {

            Room_User room_user = new Room_User();
            room_user.setRoom_id(room_id);
            room_user.setUser_id(user_id);
            room_user.setId(1L);

            return ResponseEntity.ok().body(
                    new MessageResponse(Response_Enum.SUCCESSFUL.getData(),
                            repository_compuesto.save(room_user).toString()));
        } else {
            return ResponseEntity.badRequest().body
                    (new MessageResponse(Response_Enum.ERROR.getData(),
                            "Bad id room and id user, please check this information and retry again"));
        }

    }

    public ResponseEntity<?> getBeersForMeeting(final Long id) {

        Optional<Room> room = roomRepository.findById(id);

        if (room.isPresent()) {
            int beers = determinateBeer.getCantBeers(room.get().getListUsers().size(),
                    room.get().getHourEnd());

            return ResponseEntity.ok().body(
                    new MessageResponse(Response_Enum.SUCCESSFUL.getData(),
                            "The meeting need " + beers + " and start at " + room.get().getHourStart()));
        } else {
            return ResponseEntity.badRequest().body(
                    new MessageResponse(Response_Enum.ERROR.getData(),
                            "The meeting doesn't exist"));
        }

    }

    public ResponseEntity<?> checkin(final Long user_id, final Long room_id) {

        for (Room_User room_user : repository_compuesto.findAll()) {
            if (room_user.getUser_id() == user_id &&
                    room_user.getRoom_id() == room_id) {
                room_user.setCheckin(true);
                repository_compuesto.save(room_user);

                return ResponseEntity.ok().body(new MessageResponse(Response_Enum.SUCCESSFUL.getData(),
                        room_user.toString()));
            }
        }

        return ResponseEntity.badRequest().body(new MessageResponse(Response_Enum.ERROR.getData(),
                "The user id or the room id doesn't exist. Please check this data"));

    }

    public ResponseEntity<?> createRoom(Room newRoom) {

        newRoom.setState(ROOM_STATE.NOT_RESERVED.name());

        return ResponseEntity.ok().body(
                new MessageResponse(Response_Enum.SUCCESSFUL.getData(),
                        roomRepository.save(newRoom).toString()));

    }
}
