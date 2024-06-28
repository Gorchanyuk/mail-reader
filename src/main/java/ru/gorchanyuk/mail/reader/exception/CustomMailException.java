package ru.gorchanyuk.mail.reader.exception;

public class CustomMailException extends RuntimeException {
    public CustomMailException(String message) {
        super(message);
    }
}
