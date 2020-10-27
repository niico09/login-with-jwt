package com.santander.seclog.service;

import com.santander.seclog.entity.Room;
import com.santander.seclog.entity.User;
import com.santander.seclog.entity.enums.ROOM_STATE;
import com.santander.seclog.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


@Component
public class ScheduledMails {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledMails.class);
    private static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss";


    @Autowired
    RoomRepository repository;

    @Async
    @Scheduled(fixedRate = 1800000)
    public void scheduledFixRateTask() throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN);
        String strHour = simpleDateFormat.format(new Date());

        List<Room> filterReserved = addReservedRooms();
        LOGGER.info("Rooms Reserved: {}", filterReserved.toString());

        for (Room room : filterReserved) {
            Date dateStart = new SimpleDateFormat(PATTERN).parse(room.getHourStart());
            LOGGER.info("The room location {}, start at {}", room.getLocation(), room.getHourStart());

            Date timeNow = new SimpleDateFormat(PATTERN).parse(strHour);

            Long fisrtEpoch = dateStart.getTime();
            Long secondEpoch = timeNow.getTime();

            long minutes = TimeUnit.MILLISECONDS.toMinutes(fisrtEpoch - secondEpoch);

            if (minutes < 30) {
                //sendEmail(room.getParticipates(), minutes, room.getLocation());

                for (User person : room.getListUsers()) {
                    LOGGER.info("Send email to {}, . His emails is {} ",
                            person.getUsername(), person.getEmail());
                }

            } else {
                LOGGER.info("The meeting {} won't start soon, will start the day {}",
                        room.getLocation(), room.getHourStart());
            }
        }

    }

    @Async
    @Scheduled(fixedRate = 3600000)
    public void scheduledUpdateStateMeetings() throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN);
        String strHour = simpleDateFormat.format(new Date());

        List<Room> filterReserved = addReservedRooms();
        LOGGER.info("Rooms Reserved: {}", filterReserved.toString());

        for (Room room : filterReserved) {
            Date dateEnd = new SimpleDateFormat(PATTERN).parse(room.getHourEnd());
            Date dateStart = new SimpleDateFormat(PATTERN).parse(room.getHourStart());
            Date timeNow = new SimpleDateFormat(PATTERN).parse(strHour);

            if (timeNow.after(dateEnd)) {
                //sendEmail(room.getParticipates(), minutes, room.getLocation());
                LOGGER.info("The meeting {} is finish and start to update the state: {}",
                        room.toString(), ROOM_STATE.FINISHED.name());

                room.setState(ROOM_STATE.FINISHED.name());
                repository.save(room);
            } else if (timeNow.after(dateStart) && timeNow.before(dateEnd)){
                LOGGER.info("The meeting {} is finish and start to update the state: {}",
                        room.toString(), ROOM_STATE.STARTED.name());

                room.setState(ROOM_STATE.STARTED.name());
                repository.save(room);
            }
        }

    }

    private void sendEmail(List<User> participates, long minutes, String location) {

        String from = "";
        String host = "smtp.gmail.com";
        Properties properties = System.getProperties();

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, "*******");
            }

        });

        session.setDebug(true);

        for (User person : participates) {
            String to = person.getEmail();

            try {
                MimeMessage message = new MimeMessage(session);

                message.setFrom(new InternetAddress(from));

                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                message.setSubject("Meeting is coming");

                message.setText("The meeting start in: " + minutes + ", and located in the room: " + location);

                LOGGER.info("sending...");
                Transport.send(message);
                LOGGER.info("Sent message successfully....");
            } catch (MessagingException mex) {
                LOGGER.error("Error send email: {}", to, mex);
            }
        }
    }

    private List<Room> addReservedRooms() {

        List<Room> result = new ArrayList<>();
        List<Room> listRooms = repository.findAll();
        for (Room room : listRooms) {
            if (room.getState() != null &&
                    room.getState().equals(ROOM_STATE.RESERVED.name())) {
                result.add(room);
            }
        }
        return result;
    }


}
