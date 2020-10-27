package com.santander.seclog.entity;

import com.santander.seclog.entity.enums.ROOM_STATE;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table( name  = "room", uniqueConstraints =  {
        @UniqueConstraint(columnNames = "location")
})
public class Room {

    public Room() {
    }

    public Room(String location) {
        this.location = location;
        this.state = ROOM_STATE.NOT_RESERVED.name();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull(message = "location is required")
    private String location;

    private String hourStart;

    private String hourEnd;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(	name = "room_users",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> listUsers = new HashSet<>();

   /* @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("person_id ASC")
    private List<User> participates = new ArrayList<>();
*/
    private String state;


    public void setHourEnd(String hourEnd) {
        this.hourEnd = hourEnd;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getHourEnd() {
        return hourEnd;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHourStart() {
        return hourStart;
    }

    public void setHourStart(String hourStart) {
        this.hourStart = hourStart;
    }

    public Set<User> getListUsers() {
        return listUsers;
    }

    public void setListUsers(Set<User> listUsers) {
        this.listUsers = listUsers;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", location='" + location + '\'' +
                ", hourStart='" + hourStart + '\'' +
                ", hourEnd='" + hourEnd + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}