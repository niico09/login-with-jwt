package com.santander.seclog.exception;

public class RoomNotFoundException extends RuntimeException {


    private static final long serialVersionUID = 1L;

    public RoomNotFoundException(Long id) {
        super("Room id not found : " + id);
    }
}
