package com.ahmet.androidserialcommunication.serial;

/**
 * @author Ahmet TOPAK
 * @version 1.0
 * @since 2/21/2024
 */

public class SerialException extends RuntimeException{
    public SerialException(String message) {
        super(message);
    }

    public SerialException(String message, Throwable cause) {
        super(message, cause);
    }
}
