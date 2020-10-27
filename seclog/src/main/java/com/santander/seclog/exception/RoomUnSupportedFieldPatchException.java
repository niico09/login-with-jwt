package com.santander.seclog.exception;

import java.util.Set;

public class RoomUnSupportedFieldPatchException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RoomUnSupportedFieldPatchException(Set<String> keys) {
        super("Field " + keys.toString() + " update is not allow.");
    }


}
