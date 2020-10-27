package com.santander.seclog.entity;

import org.hibernate.procedure.spi.ParameterRegistrationImplementor;

import javax.persistence.*;

@Entity
@Table(name = "room_users")
public class Room_User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long user_id;

    private Long room_id;

    private Boolean checkin;

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getUser_id() {
        return user_id;
    }

    public Boolean getCheckin() {
        return checkin;
    }

    public void setCheckin(Boolean checkin) {
        this.checkin = checkin;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public long getRoom_id() {
        return room_id;
    }

    public void setRoom_id(Long room_id) {
        this.room_id = room_id;
    }

    @Override
    public String toString() {
        return "Room_User{" +
                "user_id=" + user_id +
                ", room_id=" + room_id +
                '}';
    }
}
