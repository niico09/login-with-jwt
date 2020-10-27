package com.santander.seclog.entity.enums;

public enum Response_Enum {

    SUCCESSFUL("Operation successful!"),
    ERROR("An error ocurrer in the server!");

    private String data;

    Response_Enum(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
